package com.apex.firefighter.service.registration;

import com.apex.firefighter.dto.registration.*;
import com.apex.firefighter.model.User;
import com.apex.firefighter.model.registration.PendingApproval;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.registration.PendingApprovalRepository;
import com.apex.firefighter.service.DolibarrUserGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Registration Service Unit Tests")
class RegistrationServiceTest {

    @Mock
    private PendingApprovalRepository pendingApprovalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationNotificationService notificationService;

    @Mock
    private DolibarrUserGroupService dolibarrUserGroupService;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequestDto validRequest;
    private PendingApproval mockApproval;
    private User mockUser;
    private User mockAdminUser;

    @BeforeEach
    void setUp() {
        // Setup valid registration request
        validRequest = new RegistrationRequestDto();
        validRequest.setFirebaseUid("test-uid-001");
        validRequest.setUsername("John Doe");
        validRequest.setEmail("john@example.com");
        validRequest.setDepartment("Engineering");
        validRequest.setContactNumber("+27123456789");
        validRequest.setRegistrationMethod("EMAIL");
        validRequest.setRequestedAccessGroups(Arrays.asList("FINANCIAL", "LOGISTICS"));
        validRequest.setBusinessJustification("Need access for project work");
        validRequest.setPriorityLevel("HIGH");
        validRequest.setDolibarrId("123");

        // Setup mock pending approval
        mockApproval = new PendingApproval();
        mockApproval.setId(1L);
        mockApproval.setFirebaseUid("test-uid-001");
        mockApproval.setUsername("John Doe");
        mockApproval.setEmail("john@example.com");
        mockApproval.setDepartment("Engineering");
        mockApproval.setStatus("PENDING");
        mockApproval.setCreatedAt(ZonedDateTime.now());

        // Setup mock user
        mockUser = new User();
        mockUser.setUserId("test-uid-001");
        mockUser.setUsername("John Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setIsAuthorized(true);
        mockUser.setIsAdmin(false);

        // Setup mock admin user
        mockAdminUser = new User();
        mockAdminUser.setUserId("admin-uid");
        mockAdminUser.setUsername("Admin User");
        mockAdminUser.setIsAdmin(true);
    }

    // ========================================
    // SUBMIT REGISTRATION TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully submit new registration request")
    void testSubmitRegistrationRequest_Success() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(pendingApprovalRepository.existsByFirebaseUid(anyString())).thenReturn(false);
        when(pendingApprovalRepository.existsByEmail(anyString())).thenReturn(false);
        when(pendingApprovalRepository.save(any(PendingApproval.class))).thenReturn(mockApproval);
        doNothing().when(notificationService).notifyAdminsOfNewRegistration(any());

        // Act
        PendingApprovalDto result = registrationService.submitRegistrationRequest(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test-uid-001", result.getFirebaseUid());
        assertEquals("John Doe", result.getUsername());
        assertEquals("PENDING", result.getStatus());
        
        verify(pendingApprovalRepository, times(1)).save(any(PendingApproval.class));
        verify(notificationService, times(1)).notifyAdminsOfNewRegistration(any());
    }

    @Test
    @DisplayName("Should throw exception when Firebase UID already exists as user")
    void testSubmitRegistrationRequest_DuplicateFirebaseUid() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> registrationService.submitRegistrationRequest(validRequest));
        
        assertEquals("User already registered with this Firebase UID", exception.getMessage());
        verify(pendingApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testSubmitRegistrationRequest_DuplicateEmail() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> registrationService.submitRegistrationRequest(validRequest));
        
        assertEquals("User already registered with this email", exception.getMessage());
        verify(pendingApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when pending approval already exists for Firebase UID")
    void testSubmitRegistrationRequest_PendingApprovalExists() {
        // Arrange
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(pendingApprovalRepository.existsByFirebaseUid(anyString())).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> registrationService.submitRegistrationRequest(validRequest));
        
        assertEquals("Registration request already pending for this Firebase UID", exception.getMessage());
    }

    // ========================================
    // GET PENDING APPROVALS TESTS
    // ========================================

    @Test
    @DisplayName("Should return pending approvals for admin")
    void testGetPendingApprovals_Success() {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByStatusOrderByCreatedAtDesc("PENDING"))
            .thenReturn(Arrays.asList(mockApproval));

        // Act
        List<PendingApprovalDto> result = registrationService.getPendingApprovals("admin-uid");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-uid-001", result.get(0).getFirebaseUid());
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to get pending approvals")
    void testGetPendingApprovals_NonAdmin() {
        // Arrange
        when(userRepository.findByUserId("test-uid-001")).thenReturn(Optional.of(mockUser));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> registrationService.getPendingApprovals("test-uid-001"));
        
        assertEquals("Access denied: Admin privileges required", exception.getMessage());
    }

    // ========================================
    // APPROVE USER TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully approve user without Dolibarr")
    void testApproveUser_SuccessWithoutDolibarr() throws SQLException {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByFirebaseUidAndStatus("test-uid-001", "PENDING"))
            .thenReturn(Optional.of(mockApproval));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(pendingApprovalRepository.save(any(PendingApproval.class))).thenReturn(mockApproval);
        doNothing().when(notificationService).notifyUserOfApproval(any(), anyString());

        // Act
        PendingApprovalDto result = registrationService.approveUser(
            "admin-uid", 
            "test-uid-001", 
            Arrays.asList("FINANCIAL"), 
            "Engineering", 
            null
        );

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(notificationService, times(1)).notifyUserOfApproval(any(), anyString());
        verify(dolibarrUserGroupService, never()).addUserToGroup(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should successfully approve user with Dolibarr sync")
    void testApproveUser_SuccessWithDolibarr() throws Exception {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByFirebaseUidAndStatus("test-uid-001", "PENDING"))
            .thenReturn(Optional.of(mockApproval));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(pendingApprovalRepository.save(any(PendingApproval.class))).thenReturn(mockApproval);
        doNothing().when(notificationService).notifyUserOfApproval(any(), anyString());
        doNothing().when(dolibarrUserGroupService).addUserToGroup(anyString(), anyString(), anyString());

        // Act
        PendingApprovalDto result = registrationService.approveUser(
            "admin-uid", 
            "test-uid-001", 
            Arrays.asList("FINANCIAL", "LOGISTICS"), 
            "Engineering", 
            "123"
        );

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(dolibarrUserGroupService, times(2)).addUserToGroup(eq("123"), anyString(), eq("admin-uid"));
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to approve")
    void testApproveUser_NonAdmin() {
        // Arrange
        when(userRepository.findByUserId("test-uid-001")).thenReturn(Optional.of(mockUser));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, 
            () -> registrationService.approveUser("test-uid-001", "target-uid", List.of(), null, null));
        
        assertEquals("Access denied: Admin privileges required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when no pending approval found")
    void testApproveUser_NoPendingApproval() {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByFirebaseUidAndStatus("test-uid-001", "PENDING"))
            .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> registrationService.approveUser("admin-uid", "test-uid-001", List.of(), null, null));
        
        assertTrue(exception.getMessage().contains("No pending approval found"));
    }

    // ========================================
    // REJECT USER TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully reject user")
    void testRejectUser_Success() {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByFirebaseUidAndStatus("test-uid-001", "PENDING"))
            .thenReturn(Optional.of(mockApproval));
        when(pendingApprovalRepository.save(any(PendingApproval.class))).thenReturn(mockApproval);
        doNothing().when(notificationService).notifyUserOfRejection(any(), anyString(), anyString());

        // Act
        PendingApprovalDto result = registrationService.rejectUser(
            "admin-uid", 
            "test-uid-001", 
            "Incomplete information"
        );

        // Assert
        assertNotNull(result);
        verify(pendingApprovalRepository, times(1)).save(any(PendingApproval.class));
        verify(notificationService, times(1)).notifyUserOfRejection(any(), anyString(), eq("Incomplete information"));
    }

    // ========================================
    // DELETE PENDING APPROVAL TESTS
    // ========================================

    @Test
    @DisplayName("Should successfully delete pending approval")
    void testDeletePendingApproval_Success() {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(pendingApprovalRepository.findByFirebaseUid("test-uid-001"))
            .thenReturn(Optional.of(mockApproval));
        doNothing().when(pendingApprovalRepository).delete(any(PendingApproval.class));

        // Act
        assertDoesNotThrow(() -> registrationService.deletePendingApproval("admin-uid", "test-uid-001"));

        // Assert
        verify(pendingApprovalRepository, times(1)).delete(mockApproval);
    }

    // ========================================
    // GET USER STATISTICS TESTS
    // ========================================

    @Test
    @DisplayName("Should return user statistics for admin")
    void testGetUserStatistics_Success() {
        // Arrange
        when(userRepository.findByUserId("admin-uid")).thenReturn(Optional.of(mockAdminUser));
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByIsAdmin(true)).thenReturn(50L);
        when(userRepository.countByIsAuthorized(true)).thenReturn(80L);
        when(pendingApprovalRepository.countByStatus("PENDING")).thenReturn(30L);

        // Act
        UserManagementStatisticsDto result = registrationService.getUserStatistics("admin-uid");

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(50L, result.getAdminUsers());
        assertEquals(50L, result.getRegularUsers());
        assertEquals(30L, result.getPendingApprovals());
        assertEquals(80L, result.getActiveUsers());
        assertEquals(20L, result.getInactiveUsers());
    }

    // ========================================
    // CHECK REGISTRATION STATUS TESTS
    // ========================================

    @Test
    @DisplayName("Should return registration status when approval exists")
    void testCheckRegistrationStatus_PendingExists() {
        // Arrange
        when(pendingApprovalRepository.findByFirebaseUid("test-uid-001"))
            .thenReturn(Optional.of(mockApproval));

        // Act
        Map<String, Object> result = registrationService.getRegistrationStatus("test-uid-001");

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.get("status"));
        assertNotNull(result.get("createdAt"));
    }

    @Test
    @DisplayName("Should return not registered when no approval exists")
    void testCheckRegistrationStatus_NotRegistered() {
        // Arrange
        when(pendingApprovalRepository.findByFirebaseUid("test-uid-001"))
            .thenReturn(Optional.empty());
        when(userRepository.findByUserId("test-uid-001")).thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = registrationService.getRegistrationStatus("test-uid-001");

        // Assert
        assertNotNull(result);
        assertEquals("NOT_REGISTERED", result.get("status"));
    }

    @Test
    @DisplayName("Should return registered when user exists")
    void testCheckRegistrationStatus_AlreadyRegistered() {
        // Arrange
        when(userRepository.findByUserId("test-uid-registered")).thenReturn(Optional.of(mockUser));

        // Act
        Map<String, Object> result = registrationService.getRegistrationStatus("test-uid-registered");

        // Assert
        assertNotNull(result);
        assertEquals("REGISTERED", result.get("status"));
        assertNotNull(result.get("authorized"));
        assertNotNull(result.get("isAdmin"));
    }
}
