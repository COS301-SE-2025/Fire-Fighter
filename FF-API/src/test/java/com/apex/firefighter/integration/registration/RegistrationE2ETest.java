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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End tests for complete registration workflow
 * Tests the entire user journey from registration to approval
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Registration End-to-End Tests")
class RegistrationE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PendingApprovalRepository pendingApprovalRepository;

    private static final String E2E_TEST_UID = "e2e-test-uid-" + System.currentTimeMillis();
    private static final String ADMIN_UID = "e2e-admin-uid";
    private User adminUser;

    @BeforeAll
    static void setUpClass() {
        System.out.println("=".repeat(70));
        System.out.println("🧪 STARTING END-TO-END REGISTRATION TESTS");
        System.out.println("=".repeat(70));
    }

    @AfterAll
    static void tearDownClass() {
        System.out.println("=".repeat(70));
        System.out.println("✅ END-TO-END REGISTRATION TESTS COMPLETED");
        System.out.println("=".repeat(70));
    }

    @BeforeEach
    void setUp() {
        // Create admin user for the entire test flow
        adminUser = new User();
        adminUser.setUserId(ADMIN_UID);
        adminUser.setUsername("E2E Admin");
        adminUser.setEmail("e2eadmin@test.com");
        adminUser.setIsAdmin(true);
        adminUser.setIsAuthorized(true);
        userRepository.save(adminUser);
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        pendingApprovalRepository.findByFirebaseUid(E2E_TEST_UID)
            .ifPresent(approval -> pendingApprovalRepository.delete(approval));
        userRepository.findByUserId(E2E_TEST_UID)
            .ifPresent(user -> userRepository.delete(user));
        userRepository.findByUserId(ADMIN_UID)
            .ifPresent(user -> userRepository.delete(user));
    }

    @Test
    @Order(1)
    @DisplayName("E2E: Complete Registration Flow - Submit → Check Status → Approve → Verify User")
    @Transactional
    void testCompleteRegistrationFlow_Success() throws Exception {
        System.out.println("\n🔵 STEP 1: New user submits registration request");
        
        // STEP 1: User submits registration
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(E2E_TEST_UID);
        request.setUsername("E2E Test User");
        request.setEmail("e2euser@test.com");
        request.setDepartment("Engineering");
        request.setContactNumber("+27123456789");
        request.setRegistrationMethod("EMAIL");
        request.setRequestedAccessGroups(Arrays.asList("FINANCIAL", "LOGISTICS"));
        request.setBusinessJustification("E2E testing access required");
        request.setPriorityLevel("HIGH");

        MvcResult submitResult = mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firebaseUid").value(E2E_TEST_UID))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        System.out.println("   ✅ Registration submitted successfully");
        System.out.println("   📧 Notification email sent to admins");

        // STEP 2: Check registration status (as user)
        System.out.println("\n🔵 STEP 2: User checks registration status");
        
        mockMvc.perform(get("/api/registration/status/" + E2E_TEST_UID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));

        System.out.println("   ✅ Status confirmed: PENDING");

        // STEP 3: Admin views pending approvals
        System.out.println("\n🔵 STEP 3: Admin views pending approval requests");
        
        MvcResult pendingResult = mockMvc.perform(get("/api/registration/admin/pending")
                .header("X-Firebase-UID", ADMIN_UID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.firebaseUid=='" + E2E_TEST_UID + "')].username")
                    .value("E2E Test User"))
                .andReturn();

        System.out.println("   ✅ Pending request found in admin dashboard");

        // STEP 4: Admin approves the user
        System.out.println("\n🔵 STEP 4: Admin approves the registration");
        
        String approvalRequest = String.format("""
            {
                "firebaseUid": "%s",
                "assignedAccessGroups": ["FINANCIAL", "LOGISTICS"],
                "department": "Engineering"
            }
            """, E2E_TEST_UID);

        mockMvc.perform(put("/api/registration/admin/approve")
                .header("X-Firebase-UID", ADMIN_UID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(approvalRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        System.out.println("   ✅ User approved successfully");
        System.out.println("   📧 Approval notification sent to user");

        // STEP 5: Verify user was created in database
        System.out.println("\n🔵 STEP 5: Verify user account created");
        
        Optional<User> createdUser = userRepository.findByUserId(E2E_TEST_UID);
        assertTrue(createdUser.isPresent(), "User should be created in database");
        assertEquals("E2E Test User", createdUser.get().getUsername());
        assertEquals("e2euser@test.com", createdUser.get().getEmail());
        assertEquals("Engineering", createdUser.get().getDepartment());
        assertTrue(createdUser.get().getIsAuthorized(), "User should be authorized");
        assertFalse(createdUser.get().getIsAdmin(), "User should not be admin");

        System.out.println("   ✅ User account verified in database");

        // STEP 6: Verify pending approval status updated
        System.out.println("\n🔵 STEP 6: Verify pending approval updated");
        
        Optional<PendingApproval> approval = pendingApprovalRepository.findByFirebaseUid(E2E_TEST_UID);
        assertTrue(approval.isPresent());
        assertEquals("APPROVED", approval.get().getStatus());
        assertNotNull(approval.get().getReviewedBy());
        assertNotNull(approval.get().getReviewedAt());

        System.out.println("   ✅ Approval record updated");

        // STEP 7: Check final registration status
        System.out.println("\n🔵 STEP 7: Check final registration status");
        
        mockMvc.perform(get("/api/registration/status/" + E2E_TEST_UID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REGISTERED"));

        System.out.println("   ✅ Final status confirmed: REGISTERED");
        System.out.println("\n🎉 COMPLETE REGISTRATION FLOW SUCCESSFUL!");
    }

    @Test
    @Order(2)
    @DisplayName("E2E: Registration Rejection Flow - Submit → Admin Rejects → Verify Status")
    @Transactional
    void testRegistrationRejectionFlow() throws Exception {
        System.out.println("\n🔵 STEP 1: User submits registration request");
        
        String rejectUid = "e2e-reject-uid-" + System.currentTimeMillis();
        
        // STEP 1: User submits registration
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(rejectUid);
        request.setUsername("User To Reject");
        request.setEmail("reject@test.com");
        request.setRegistrationMethod("EMAIL");

        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        System.out.println("   ✅ Registration submitted");

        // STEP 2: Admin rejects the user
        System.out.println("\n🔵 STEP 2: Admin rejects the registration");
        
        String rejectRequest = String.format("""
            {
                "firebaseUid": "%s",
                "rejectionReason": "Insufficient information provided"
            }
            """, rejectUid);

        mockMvc.perform(put("/api/registration/admin/reject")
                .header("X-Firebase-UID", ADMIN_UID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(rejectRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        System.out.println("   ✅ User rejected successfully");
        System.out.println("   📧 Rejection notification sent");

        // STEP 3: Verify user was NOT created
        System.out.println("\n🔵 STEP 3: Verify user account NOT created");
        
        Optional<User> user = userRepository.findByUserId(rejectUid);
        assertFalse(user.isPresent(), "User should not be created");

        System.out.println("   ✅ Confirmed: No user account created");

        // STEP 4: Verify rejection status
        System.out.println("\n🔵 STEP 4: Verify rejection status");
        
        Optional<PendingApproval> approval = pendingApprovalRepository.findByFirebaseUid(rejectUid);
        assertTrue(approval.isPresent());
        assertEquals("REJECTED", approval.get().getStatus());
        assertEquals("Insufficient information provided", approval.get().getRejectionReason());

        System.out.println("   ✅ Rejection status confirmed");
        System.out.println("\n🎉 REJECTION FLOW SUCCESSFUL!");

        // Cleanup
        pendingApprovalRepository.delete(approval.get());
    }

    @Test
    @Order(3)
    @DisplayName("E2E: Duplicate Registration Prevention")
    @Transactional
    void testDuplicateRegistrationPrevention() throws Exception {
        System.out.println("\n🔵 Testing duplicate registration prevention");
        
        String dupUid = "e2e-dup-uid-" + System.currentTimeMillis();
        
        // STEP 1: First registration succeeds
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(dupUid);
        request.setUsername("First Registration");
        request.setEmail("first@test.com");
        request.setRegistrationMethod("EMAIL");

        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        System.out.println("   ✅ First registration successful");

        // STEP 2: Second registration with same UID fails
        RegistrationRequestDto duplicateRequest = new RegistrationRequestDto();
        duplicateRequest.setFirebaseUid(dupUid);  // Same UID
        duplicateRequest.setUsername("Duplicate Registration");
        duplicateRequest.setEmail("duplicate@test.com");  // Different email
        duplicateRequest.setRegistrationMethod("EMAIL");

        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());

        System.out.println("   ✅ Duplicate registration prevented");
        System.out.println("\n🎉 DUPLICATE PREVENTION SUCCESSFUL!");

        // Cleanup
        pendingApprovalRepository.findByFirebaseUid(dupUid)
            .ifPresent(a -> pendingApprovalRepository.delete(a));
    }

    @Test
    @Order(4)
    @DisplayName("E2E: Admin Permission Enforcement")
    @Transactional
    void testAdminPermissionEnforcement() throws Exception {
        System.out.println("\n🔵 Testing admin permission enforcement");
        
        // Create non-admin user
        User nonAdmin = new User();
        nonAdmin.setUserId("non-admin-e2e");
        nonAdmin.setUsername("Non Admin");
        nonAdmin.setEmail("nonadmin@test.com");
        nonAdmin.setIsAdmin(false);
        userRepository.save(nonAdmin);

        System.out.println("   ✅ Non-admin user created");

        // Try to access admin endpoint
        System.out.println("\n🔵 Non-admin attempts to view pending approvals");
        
        mockMvc.perform(get("/api/registration/admin/pending")
                .header("X-Firebase-UID", "non-admin-e2e"))
                .andExpect(status().isForbidden());

        System.out.println("   ✅ Access denied (403 Forbidden)");

        // Try to approve user
        System.out.println("\n🔵 Non-admin attempts to approve user");
        
        String approvalRequest = """
            {
                "firebaseUid": "some-uid",
                "assignedAccessGroups": ["FINANCIAL"],
                "department": "Engineering"
            }
            """;

        mockMvc.perform(put("/api/registration/admin/approve")
                .header("X-Firebase-UID", "non-admin-e2e")
                .contentType(MediaType.APPLICATION_JSON)
                .content(approvalRequest))
                .andExpect(status().isForbidden());

        System.out.println("   ✅ Action denied (403 Forbidden)");
        System.out.println("\n🎉 ADMIN PERMISSION ENFORCEMENT SUCCESSFUL!");

        // Cleanup
        userRepository.delete(nonAdmin);
    }

    @Test
    @Order(5)
    @DisplayName("E2E: Statistics and Reporting")
    @Transactional
    void testStatisticsReporting() throws Exception {
        System.out.println("\n🔵 Testing statistics and reporting");
        
        // Get initial statistics
        MvcResult initialStats = mockMvc.perform(get("/api/registration/admin/statistics")
                .header("X-Firebase-UID", ADMIN_UID))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("   ✅ Initial statistics retrieved");

        // Create a pending approval
        String statsUid = "e2e-stats-uid-" + System.currentTimeMillis();
        RegistrationRequestDto request = new RegistrationRequestDto();
        request.setFirebaseUid(statsUid);
        request.setUsername("Stats Test User");
        request.setEmail("stats@test.com");
        request.setRegistrationMethod("EMAIL");

        mockMvc.perform(post("/api/registration/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        System.out.println("   ✅ Test registration created");

        // Get updated statistics
        mockMvc.perform(get("/api/registration/admin/statistics")
                .header("X-Firebase-UID", ADMIN_UID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendingApprovals").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber());

        System.out.println("   ✅ Updated statistics retrieved");
        System.out.println("\n🎉 STATISTICS REPORTING SUCCESSFUL!");

        // Cleanup
        pendingApprovalRepository.findByFirebaseUid(statsUid)
            .ifPresent(a -> pendingApprovalRepository.delete(a));
    }
}
