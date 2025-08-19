package com.apex.firefighter.unit.services.access;

import com.apex.firefighter.model.AccessRequest;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AccessRequestRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.access.AccessRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceTest {

    @Mock
    private AccessRequestRepository accessRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessRequest mockAccessRequest;

    @InjectMocks
    private AccessRequestService accessRequestService;

    private User testUser;
    private AccessRequest testAccessRequest;
    private final String FIREBASE_UID = "test-firebase-uid";
    private final String TICKET_ID = "TICKET-001";
    private final Long REQUEST_ID = 1L;
    private final String APPROVED_BY = "admin-user";
    private final String DENIED_BY = "admin-user";
    private final String REVOKED_BY = "admin-user";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(FIREBASE_UID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDepartment("IT");
        testUser.setIsAuthorized(true);

        testAccessRequest = new AccessRequest();
        testAccessRequest.setId(REQUEST_ID);
        testAccessRequest.setTicketId(TICKET_ID);
        testAccessRequest.setUser(testUser);
        testAccessRequest.setStatus(AccessRequest.RequestStatus.PENDING);
        testAccessRequest.setRequestTime(ZonedDateTime.now());
    }

    // ==================== CREATE ACCESS REQUEST TESTS ====================

    @Test
    void createAccessRequest_WithValidUser_ShouldCreateAndReturnRequest() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(testAccessRequest);

        // Act
        AccessRequest result = accessRequestService.createAccessRequest(FIREBASE_UID, TICKET_ID);

        // Assert
        assertThat(result).isEqualTo(testAccessRequest);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    @Test
    void createAccessRequest_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.createAccessRequest(FIREBASE_UID, TICKET_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: " + FIREBASE_UID);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void createAccessRequest_WithNullFirebaseUid_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByUserId(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.createAccessRequest(null, TICKET_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with Firebase UID: null");

        verify(userRepository).findByUserId(null);
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void createAccessRequest_WithNullTicketId_ShouldCreateRequestWithNullTicket() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(testAccessRequest);

        // Act
        AccessRequest result = accessRequestService.createAccessRequest(FIREBASE_UID, null);

        // Assert
        assertThat(result).isEqualTo(testAccessRequest);
        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    // ==================== APPROVE ACCESS REQUEST TESTS ====================

    @Test
    void approveAccessRequest_WithValidRequest_ShouldApproveAndReturnRequest() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.approveAccessRequest(REQUEST_ID, APPROVED_BY);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).approve(APPROVED_BY);
    }

    @Test
    void approveAccessRequest_WithNonExistentRequest_ShouldThrowException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.approveAccessRequest(REQUEST_ID, APPROVED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access request not found with ID: " + REQUEST_ID);

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void approveAccessRequest_WithNullApprovedBy_ShouldStillApprove() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.approveAccessRequest(REQUEST_ID, null);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).approve(null);
    }

    // ==================== DENY ACCESS REQUEST TESTS ====================

    @Test
    void denyAccessRequest_WithValidRequest_ShouldDenyAndReturnRequest() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.denyAccessRequest(REQUEST_ID, DENIED_BY);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).deny(DENIED_BY);
    }

    @Test
    void denyAccessRequest_WithNonExistentRequest_ShouldThrowException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.denyAccessRequest(REQUEST_ID, DENIED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access request not found with ID: " + REQUEST_ID);

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void denyAccessRequest_WithNullDeniedBy_ShouldStillDeny() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.denyAccessRequest(REQUEST_ID, null);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).deny(null);
    }

    // ==================== REVOKE ACCESS REQUEST TESTS ====================

    @Test
    void revokeAccessRequest_WithValidRequest_ShouldRevokeAndReturnRequest() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.revokeAccessRequest(REQUEST_ID, REVOKED_BY);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).revoke(REVOKED_BY);
    }

    @Test
    void revokeAccessRequest_WithNonExistentRequest_ShouldThrowException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.revokeAccessRequest(REQUEST_ID, REVOKED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Access request not found with ID: " + REQUEST_ID);

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
    }

    @Test
    void revokeAccessRequest_WithNullRevokedBy_ShouldStillRevoke() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act
        AccessRequest result = accessRequestService.revokeAccessRequest(REQUEST_ID, null);

        // Assert
        assertThat(result).isEqualTo(mockAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(mockAccessRequest);
        verify(mockAccessRequest).revoke(null);
    }

    // ==================== QUERY OPERATIONS TESTS ====================

    @Test
    void getAllAccessRequests_ShouldReturnAllRequests() {
        // Arrange
        List<AccessRequest> requests = Arrays.asList(testAccessRequest);
        when(accessRequestRepository.findAll()).thenReturn(requests);

        // Act
        List<AccessRequest> result = accessRequestService.getAllAccessRequests();

        // Assert
        assertThat(result).isEqualTo(requests);
        assertThat(result).hasSize(1);
        verify(accessRequestRepository).findAll();
    }

    @Test
    void getAllAccessRequests_WithEmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(accessRequestRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AccessRequest> result = accessRequestService.getAllAccessRequests();

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findAll();
    }

    @Test
    void getAccessRequestById_WithExistingId_ShouldReturnRequest() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));

        // Act
        Optional<AccessRequest> result = accessRequestService.getAccessRequestById(REQUEST_ID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testAccessRequest);
        verify(accessRequestRepository).findById(REQUEST_ID);
    }

    @Test
    void getAccessRequestById_WithNonExistentId_ShouldReturnEmpty() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        // Act
        Optional<AccessRequest> result = accessRequestService.getAccessRequestById(REQUEST_ID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findById(REQUEST_ID);
    }

    @Test
    void getAccessRequestsByUser_ShouldReturnUserRequests() {
        // Arrange
        List<AccessRequest> userRequests = Arrays.asList(testAccessRequest);
        when(accessRequestRepository.findByUserId(FIREBASE_UID)).thenReturn(userRequests);

        // Act
        List<AccessRequest> result = accessRequestService.getAccessRequestsByUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEqualTo(userRequests);
        assertThat(result).hasSize(1);
        verify(accessRequestRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getAccessRequestsByUser_WithNoRequests_ShouldReturnEmptyList() {
        // Arrange
        when(accessRequestRepository.findByUserId(FIREBASE_UID)).thenReturn(Collections.emptyList());

        // Act
        List<AccessRequest> result = accessRequestService.getAccessRequestsByUser(FIREBASE_UID);

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void getAccessRequestsByStatus_ShouldReturnRequestsWithStatus() {
        // Arrange
        AccessRequest.RequestStatus status = AccessRequest.RequestStatus.PENDING;
        List<AccessRequest> statusRequests = Arrays.asList(testAccessRequest);
        when(accessRequestRepository.findByStatus(status)).thenReturn(statusRequests);

        // Act
        List<AccessRequest> result = accessRequestService.getAccessRequestsByStatus(status);

        // Assert
        assertThat(result).isEqualTo(statusRequests);
        assertThat(result).hasSize(1);
        verify(accessRequestRepository).findByStatus(status);
    }

    @Test
    void getAccessRequestsByStatus_WithNoRequestsForStatus_ShouldReturnEmptyList() {
        // Arrange
        AccessRequest.RequestStatus status = AccessRequest.RequestStatus.APPROVED;
        when(accessRequestRepository.findByStatus(status)).thenReturn(Collections.emptyList());

        // Act
        List<AccessRequest> result = accessRequestService.getAccessRequestsByStatus(status);

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findByStatus(status);
    }

    @Test
    void getPendingAccessRequests_ShouldReturnPendingRequests() {
        // Arrange
        List<AccessRequest> pendingRequests = Arrays.asList(testAccessRequest);
        when(accessRequestRepository.findByStatus(AccessRequest.RequestStatus.PENDING)).thenReturn(pendingRequests);

        // Act
        List<AccessRequest> result = accessRequestService.getPendingAccessRequests();

        // Assert
        assertThat(result).isEqualTo(pendingRequests);
        assertThat(result).hasSize(1);
        verify(accessRequestRepository).findByStatus(AccessRequest.RequestStatus.PENDING);
    }

    @Test
    void getPendingAccessRequests_WithNoPendingRequests_ShouldReturnEmptyList() {
        // Arrange
        when(accessRequestRepository.findByStatus(AccessRequest.RequestStatus.PENDING)).thenReturn(Collections.emptyList());

        // Act
        List<AccessRequest> result = accessRequestService.getPendingAccessRequests();

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findByStatus(AccessRequest.RequestStatus.PENDING);
    }

    @Test
    void getApprovedAccessRequests_ShouldReturnApprovedRequests() {
        // Arrange
        List<AccessRequest> approvedRequests = Arrays.asList(testAccessRequest);
        when(accessRequestRepository.findByStatus(AccessRequest.RequestStatus.APPROVED)).thenReturn(approvedRequests);

        // Act
        List<AccessRequest> result = accessRequestService.getApprovedAccessRequests();

        // Assert
        assertThat(result).isEqualTo(approvedRequests);
        assertThat(result).hasSize(1);
        verify(accessRequestRepository).findByStatus(AccessRequest.RequestStatus.APPROVED);
    }

    @Test
    void getApprovedAccessRequests_WithNoApprovedRequests_ShouldReturnEmptyList() {
        // Arrange
        when(accessRequestRepository.findByStatus(AccessRequest.RequestStatus.APPROVED)).thenReturn(Collections.emptyList());

        // Act
        List<AccessRequest> result = accessRequestService.getApprovedAccessRequests();

        // Assert
        assertThat(result).isEmpty();
        verify(accessRequestRepository).findByStatus(AccessRequest.RequestStatus.APPROVED);
    }

    // ==================== DELETE OPERATIONS TESTS ====================

    @Test
    void deleteAccessRequest_WithExistingRequest_ShouldReturnTrue() {
        // Arrange
        when(accessRequestRepository.existsById(REQUEST_ID)).thenReturn(true);
        doNothing().when(accessRequestRepository).deleteById(REQUEST_ID);

        // Act
        boolean result = accessRequestService.deleteAccessRequest(REQUEST_ID);

        // Assert
        assertThat(result).isTrue();
        verify(accessRequestRepository).existsById(REQUEST_ID);
        verify(accessRequestRepository).deleteById(REQUEST_ID);
    }

    @Test
    void deleteAccessRequest_WithNonExistentRequest_ShouldReturnFalse() {
        // Arrange
        when(accessRequestRepository.existsById(REQUEST_ID)).thenReturn(false);

        // Act
        boolean result = accessRequestService.deleteAccessRequest(REQUEST_ID);

        // Assert
        assertThat(result).isFalse();
        verify(accessRequestRepository).existsById(REQUEST_ID);
        verify(accessRequestRepository, never()).deleteById(REQUEST_ID);
    }

    @Test
    void deleteAccessRequest_WithNullId_ShouldHandleGracefully() {
        // Arrange
        when(accessRequestRepository.existsById(null)).thenReturn(false);

        // Act
        boolean result = accessRequestService.deleteAccessRequest(null);

        // Assert
        assertThat(result).isFalse();
        verify(accessRequestRepository).existsById(null);
        verify(accessRequestRepository, never()).deleteById(null);
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void createAccessRequest_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.createAccessRequest(FIREBASE_UID, TICKET_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(accessRequestRepository).save(any(AccessRequest.class));
    }

    @Test
    void approveAccessRequest_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.approveAccessRequest(REQUEST_ID, APPROVED_BY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).save(testAccessRequest);
    }

    @Test
    void getAllAccessRequests_WithRepositoryException_ShouldPropagateException() {
        // Arrange
        when(accessRequestRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThatThrownBy(() -> accessRequestService.getAllAccessRequests())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(accessRequestRepository).findAll();
    }

    // ==================== COMPREHENSIVE INTEGRATION TESTS ====================

    @Test
    void fullWorkflow_CreateApproveAndQuery_ShouldWorkCorrectly() {
        // Arrange
        when(userRepository.findByUserId(FIREBASE_UID)).thenReturn(Optional.of(testUser));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(testAccessRequest);
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(testAccessRequest));
        when(accessRequestRepository.findByUserId(FIREBASE_UID)).thenReturn(Arrays.asList(testAccessRequest));

        // Act
        AccessRequest created = accessRequestService.createAccessRequest(FIREBASE_UID, TICKET_ID);
        AccessRequest approved = accessRequestService.approveAccessRequest(REQUEST_ID, APPROVED_BY);
        List<AccessRequest> userRequests = accessRequestService.getAccessRequestsByUser(FIREBASE_UID);

        // Assert
        assertThat(created).isEqualTo(testAccessRequest);
        assertThat(approved).isEqualTo(testAccessRequest);
        assertThat(userRequests).hasSize(1);
        assertThat(userRequests.get(0)).isEqualTo(testAccessRequest);

        verify(userRepository).findByUserId(FIREBASE_UID);
        verify(accessRequestRepository, times(2)).save(any(AccessRequest.class));
        verify(accessRequestRepository).findById(REQUEST_ID);
        verify(accessRequestRepository).findByUserId(FIREBASE_UID);
    }

    @Test
    void allStatusOperations_ShouldWorkCorrectly() {
        // Arrange
        when(accessRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(mockAccessRequest));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(mockAccessRequest);

        // Act & Assert - All status operations should work
        AccessRequest approved = accessRequestService.approveAccessRequest(REQUEST_ID, APPROVED_BY);
        AccessRequest denied = accessRequestService.denyAccessRequest(REQUEST_ID, DENIED_BY);
        AccessRequest revoked = accessRequestService.revokeAccessRequest(REQUEST_ID, REVOKED_BY);

        assertThat(approved).isEqualTo(mockAccessRequest);
        assertThat(denied).isEqualTo(mockAccessRequest);
        assertThat(revoked).isEqualTo(mockAccessRequest);

        verify(accessRequestRepository, times(3)).findById(REQUEST_ID);
        verify(accessRequestRepository, times(3)).save(mockAccessRequest);
        verify(mockAccessRequest).approve(APPROVED_BY);
        verify(mockAccessRequest).deny(DENIED_BY);
        verify(mockAccessRequest).revoke(REVOKED_BY);
    }
}
