package com.apex.firefighter.integration.registration;

import com.apex.firefighter.dto.registration.RegistrationRequestDto;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.registration.PendingApproval;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.registration.PendingApprovalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Registration API endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Registration Controller Integration Tests")
class RegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingApprovalRepository pendingApprovalRepository;

    private User adminUser;
    private static String testFirebaseUid = "integration-test-uid-" + System.currentTimeMillis();

    @BeforeEach
    void setUp() {
        // Create admin user for testing
        adminUser = new User();
        adminUser.setUserId("admin-integration-test");
        adminUser.setUsername("Admin Test User");
        adminUser.setEmail("admin@test.com");
        adminUser.setIsAdmin(true);
        adminUser.setIsAuthorized(true);
        userRepository.save(adminUser);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        pendingApprovalRepository.findByFirebaseUid(testFirebaseUid)
            .ifPresent(approval -> pendingApprovalRepository.delete(approval));
        userRepository.findByUserId(testFirebaseUid)
            .ifPresent(user -> userRepository.delete(user));
        userRepository.findByUserId("admin-integration-test")
            .ifPresent(user -> userRepository.delete(user));
    }

    // ========================================
    // SUBMIT REGISTRATION TESTS
    // ========================================

    @Test
    @Order(1)
    @DisplayName("POST /api/registration/submit - Should create pending approval")
    @Transactional
    void testSubmitRegistration_Success() throws Exception {
        // Arrange
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(testFirebaseUid);
        request.setUsername("Integration Test User");
        request.setEmail("integration@test.com");
        request.setDepartment("Engineering");
        request.setContactNumber("+27123456789");
        request.setRegistrationMethod("EMAIL");
        request.setRequestedAccessGroups(Arrays.asList("FINANCIAL", "LOGISTICS"));
        request.setBusinessJustification("Integration testing");
        request.setPriorityLevel("MEDIUM");

        // Act & Assert
        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firebaseUid").value(testFirebaseUid))
                .andExpect(jsonPath("$.username").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/registration/submit - Should reject duplicate Firebase UID")
    @Transactional
    void testSubmitRegistration_DuplicateFirebaseUid() throws Exception {
        // Arrange - Create existing pending approval
        PendingApproval existing = new PendingApproval();
        existing.setFirebaseUid(testFirebaseUid);
        existing.setUsername("Existing User");
        existing.setEmail("existing@test.com");
        existing.setRegistrationMethod("EMAIL");
        existing.setStatus("PENDING");
        pendingApprovalRepository.save(existing);

        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(testFirebaseUid);
        request.setUsername("Duplicate User");
        request.setEmail("duplicate@test.com");
        request.setRegistrationMethod("EMAIL");

        // Act & Assert
        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/registration/submit - Should reject invalid email")
    void testSubmitRegistration_InvalidEmail() throws Exception {
        // Arrange
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid("test-uid-invalid-email");
        request.setUsername("Test User");
        request.setEmail("invalid-email");  // Invalid email format
        request.setRegistrationMethod("EMAIL");

        // Act & Assert
        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // GET PENDING APPROVALS TESTS
    // ========================================

    @Test
    @Order(4)
    @DisplayName("GET /api/registration/admin/pending - Should return pending approvals for admin")
    @Transactional
    void testGetPendingApprovals_Success() throws Exception {
        // Arrange - Create pending approval
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(testFirebaseUid);
        approval.setUsername("Pending User");
        approval.setEmail("pending@test.com");
        approval.setRegistrationMethod("EMAIL");
        approval.setStatus("PENDING");
        pendingApprovalRepository.save(approval);

        // Act & Assert
        mockMvc.perform(get("/api/registration/admin/pending")
                .header("X-Firebase-UID", "admin-integration-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.firebaseUid=='" + testFirebaseUid + "')].username")
                    .value(hasItem("Pending User")));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/registration/admin/pending - Should reject non-admin")
    void testGetPendingApprovals_NonAdmin() throws Exception {
        // Arrange - Create non-admin user
        User nonAdmin = new User();
        nonAdmin.setUserId("non-admin-test");
        nonAdmin.setUsername("Non Admin");
        nonAdmin.setEmail("nonadmin@test.com");
        nonAdmin.setIsAdmin(false);
        userRepository.save(nonAdmin);

        // Act & Assert
        mockMvc.perform(get("/api/registration/admin/pending")
                .header("X-Firebase-UID", "non-admin-test"))
                .andExpect(status().isForbidden());

        // Cleanup
        userRepository.delete(nonAdmin);
    }

    // ========================================
    // APPROVE USER TESTS
    // ========================================

    @Test
    @Order(6)
    @DisplayName("PUT /api/registration/admin/approve - Should approve user")
    @Transactional
    void testApproveUser_Success() throws Exception {
        // Arrange - Create pending approval
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(testFirebaseUid);
        approval.setUsername("User To Approve");
        approval.setEmail("approve@test.com");
        approval.setDepartment("Engineering");
        approval.setRegistrationMethod("EMAIL");
        approval.setStatus("PENDING");
        pendingApprovalRepository.save(approval);

        String requestBody = String.format("""
            {
                "firebaseUid": "%s",
                "assignedAccessGroups": ["FINANCIAL"],
                "department": "Engineering"
            }
            """, testFirebaseUid);

        // Act & Assert
        mockMvc.perform(put("/api/registration/admin/approve")
                .header("X-Firebase-UID", "admin-integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        // Verify user was created
        assert userRepository.findByUserId(testFirebaseUid).isPresent();
    }

    // ========================================
    // REJECT USER TESTS
    // ========================================

    @Test
    @Order(7)
    @DisplayName("PUT /api/registration/admin/reject - Should reject user")
    @Transactional
    void testRejectUser_Success() throws Exception {
        // Arrange - Create pending approval
        String rejectUid = "reject-test-uid-" + System.currentTimeMillis();
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(rejectUid);
        approval.setUsername("User To Reject");
        approval.setEmail("reject@test.com");
        approval.setRegistrationMethod("EMAIL");
        approval.setStatus("PENDING");
        pendingApprovalRepository.save(approval);

        String requestBody = String.format("""
            {
                "firebaseUid": "%s",
                "rejectionReason": "Incomplete information"
            }
            """, rejectUid);

        // Act & Assert
        mockMvc.perform(put("/api/registration/admin/reject")
                .header("X-Firebase-UID", "admin-integration-test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        // Cleanup
        pendingApprovalRepository.findByFirebaseUid(rejectUid)
            .ifPresent(a -> pendingApprovalRepository.delete(a));
    }

    // ========================================
    // DELETE PENDING APPROVAL TESTS
    // ========================================

    @Test
    @Order(8)
    @DisplayName("DELETE /api/registration/admin/pending/{uid} - Should delete pending approval")
    @Transactional
    void testDeletePendingApproval_Success() throws Exception {
        // Arrange - Create pending approval
        String deleteUid = "delete-test-uid-" + System.currentTimeMillis();
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(deleteUid);
        approval.setUsername("User To Delete");
        approval.setEmail("delete@test.com");
        approval.setRegistrationMethod("EMAIL");
        approval.setStatus("PENDING");
        pendingApprovalRepository.save(approval);

        // Act & Assert
        mockMvc.perform(delete("/api/registration/admin/pending/" + deleteUid)
                .header("X-Firebase-UID", "admin-integration-test"))
                .andExpect(status().isNoContent());

        // Verify deletion
        assert pendingApprovalRepository.findByFirebaseUid(deleteUid).isEmpty();
    }

    // ========================================
    // GET STATISTICS TESTS
    // ========================================

    @Test
    @Order(9)
    @DisplayName("GET /api/registration/admin/statistics - Should return user statistics")
    void testGetStatistics_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/registration/admin/statistics")
                .header("X-Firebase-UID", "admin-integration-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").isNumber())
                .andExpect(jsonPath("$.adminUsers").isNumber())
                .andExpect(jsonPath("$.regularUsers").isNumber())
                .andExpect(jsonPath("$.pendingApprovals").isNumber());
    }

    // ========================================
    // CHECK REGISTRATION STATUS TESTS
    // ========================================

    @Test
    @Order(10)
    @DisplayName("GET /api/registration/status/{uid} - Should return registration status")
    @Transactional
    void testCheckRegistrationStatus_Pending() throws Exception {
        // Arrange - Create pending approval
        PendingApproval approval = new PendingApproval();
        approval.setFirebaseUid(testFirebaseUid);
        approval.setUsername("Status Check User");
        approval.setEmail("status@test.com");
        approval.setRegistrationMethod("EMAIL");
        approval.setStatus("PENDING");
        pendingApprovalRepository.save(approval);

        // Act & Assert
        mockMvc.perform(get("/api/registration/status/" + testFirebaseUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/registration/status/{uid} - Should return not registered")
    void testCheckRegistrationStatus_NotRegistered() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/registration/status/non-existent-uid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_REGISTERED"));
    }
}
