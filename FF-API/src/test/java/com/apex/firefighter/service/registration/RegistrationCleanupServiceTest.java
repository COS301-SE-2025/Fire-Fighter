package com.apex.firefighter.service.registration;

import com.apex.firefighter.model.registration.PendingApproval;
import com.apex.firefighter.repository.registration.PendingApprovalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationCleanupService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Registration Cleanup Service Unit Tests")
class RegistrationCleanupServiceTest {

    @Mock
    private PendingApprovalRepository pendingApprovalRepository;

    @InjectMocks
    private RegistrationCleanupService cleanupService;

    private PendingApproval oldRejectedApproval;
    private PendingApproval oldPendingApproval;
    private PendingApproval recentApproval;

    @BeforeEach
    void setUp() {
        // Setup old rejected approval (35 days old)
        oldRejectedApproval = new PendingApproval();
        oldRejectedApproval.setId(1L);
        oldRejectedApproval.setFirebaseUid("old-rejected");
        oldRejectedApproval.setEmail("rejected@test.com");
        oldRejectedApproval.setUsername("Old Rejected");
        oldRejectedApproval.setRegistrationMethod("EMAIL");
        oldRejectedApproval.setStatus("REJECTED");
        oldRejectedApproval.setCreatedAt(ZonedDateTime.now().minusDays(35));

        // Setup old pending approval (95 days old)
        oldPendingApproval = new PendingApproval();
        oldPendingApproval.setId(2L);
        oldPendingApproval.setFirebaseUid("old-pending");
        oldPendingApproval.setEmail("pending@test.com");
        oldPendingApproval.setUsername("Old Pending");
        oldPendingApproval.setRegistrationMethod("EMAIL");
        oldPendingApproval.setStatus("PENDING");
        oldPendingApproval.setCreatedAt(ZonedDateTime.now().minusDays(95));

        // Setup recent approval (5 days old)
        recentApproval = new PendingApproval();
        recentApproval.setId(3L);
        recentApproval.setFirebaseUid("recent");
        recentApproval.setEmail("recent@test.com");
        recentApproval.setUsername("Recent User");
        recentApproval.setRegistrationMethod("EMAIL");
        recentApproval.setStatus("PENDING");
        recentApproval.setCreatedAt(ZonedDateTime.now().minusDays(5));
    }

    // ========================================
    // CLEANUP EXPIRED APPROVALS TESTS
    // ========================================

    @Test
    @DisplayName("Should delete old rejected and pending approvals")
    void testCleanupExpiredApprovals_DeletesOldRecords() {
        // Arrange
        List<PendingApproval> oldRejected = Arrays.asList(oldRejectedApproval);
        List<PendingApproval> oldPending = Arrays.asList(oldPendingApproval);

        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(oldRejected);
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(oldPending);

        doNothing().when(pendingApprovalRepository).deleteAll(anyList());

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert
        verify(pendingApprovalRepository, times(1))
            .findByStatusAndCreatedAtBefore(eq("REJECTED"), any(ZonedDateTime.class));
        verify(pendingApprovalRepository, times(1))
            .findByStatusAndCreatedAtBefore(eq("PENDING"), any(ZonedDateTime.class));
        verify(pendingApprovalRepository, times(2)).deleteAll(anyList());
    }

    @Test
    @DisplayName("Should handle case when no old records exist")
    void testCleanupExpiredApprovals_NoOldRecords() {
        // Arrange
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert
        verify(pendingApprovalRepository, times(1))
            .findByStatusAndCreatedAtBefore(eq("REJECTED"), any(ZonedDateTime.class));
        verify(pendingApprovalRepository, times(1))
            .findByStatusAndCreatedAtBefore(eq("PENDING"), any(ZonedDateTime.class));
        verify(pendingApprovalRepository, never()).deleteAll(anyList());
    }

    @Test
    @DisplayName("Should delete only rejected approvals if pending list is empty")
    void testCleanupExpiredApprovals_OnlyRejectedRecords() {
        // Arrange
        List<PendingApproval> oldRejected = Arrays.asList(oldRejectedApproval);

        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(oldRejected);
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());

        doNothing().when(pendingApprovalRepository).deleteAll(oldRejected);

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert
        verify(pendingApprovalRepository, times(1)).deleteAll(oldRejected);
    }

    @Test
    @DisplayName("Should delete only pending approvals if rejected list is empty")
    void testCleanupExpiredApprovals_OnlyPendingRecords() {
        // Arrange
        List<PendingApproval> oldPending = Arrays.asList(oldPendingApproval);

        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(oldPending);

        doNothing().when(pendingApprovalRepository).deleteAll(oldPending);

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert
        verify(pendingApprovalRepository, times(1)).deleteAll(oldPending);
    }

    @Test
    @DisplayName("Should delete multiple old records")
    void testCleanupExpiredApprovals_MultipleRecords() {
        // Arrange
        PendingApproval oldRejected2 = new PendingApproval();
        oldRejected2.setId(4L);
        oldRejected2.setFirebaseUid("old-rejected-2");
        oldRejected2.setEmail("rejected2@test.com");
        oldRejected2.setUsername("Old Rejected 2");
        oldRejected2.setRegistrationMethod("EMAIL");
        oldRejected2.setStatus("REJECTED");
        oldRejected2.setCreatedAt(ZonedDateTime.now().minusDays(40));

        PendingApproval oldPending2 = new PendingApproval();
        oldPending2.setId(5L);
        oldPending2.setFirebaseUid("old-pending-2");
        oldPending2.setEmail("pending2@test.com");
        oldPending2.setUsername("Old Pending 2");
        oldPending2.setRegistrationMethod("EMAIL");
        oldPending2.setStatus("PENDING");
        oldPending2.setCreatedAt(ZonedDateTime.now().minusDays(100));

        List<PendingApproval> oldRejected = Arrays.asList(oldRejectedApproval, oldRejected2);
        List<PendingApproval> oldPending = Arrays.asList(oldPendingApproval, oldPending2);

        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(oldRejected);
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(oldPending);

        doNothing().when(pendingApprovalRepository).deleteAll(anyList());

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert
        verify(pendingApprovalRepository, times(1)).deleteAll(oldRejected);
        verify(pendingApprovalRepository, times(1)).deleteAll(oldPending);
    }

    @Test
    @DisplayName("Should use correct time thresholds for deletion")
    void testCleanupExpiredApprovals_CorrectTimeThresholds() {
        // Arrange
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("REJECTED"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());
        
        when(pendingApprovalRepository.findByStatusAndCreatedAtBefore(
            eq("PENDING"), any(ZonedDateTime.class)))
            .thenReturn(Collections.emptyList());

        // Act
        cleanupService.cleanupExpiredApprovals();

        // Assert - Verify the method was called with dates approximately 30 and 90 days ago
        verify(pendingApprovalRepository).findByStatusAndCreatedAtBefore(
            eq("REJECTED"), 
            argThat(date -> {
                ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().minusDays(30);
                // Allow 1 second tolerance
                return Math.abs(date.toEpochSecond() - thirtyDaysAgo.toEpochSecond()) < 1;
            })
        );

        verify(pendingApprovalRepository).findByStatusAndCreatedAtBefore(
            eq("PENDING"), 
            argThat(date -> {
                ZonedDateTime ninetyDaysAgo = ZonedDateTime.now().minusDays(90);
                // Allow 1 second tolerance
                return Math.abs(date.toEpochSecond() - ninetyDaysAgo.toEpochSecond()) < 1;
            })
        );
    }
}
