package com.apex.firefighter.service.registration;

import com.apex.firefighter.model.registration.PendingApproval;
import com.apex.firefighter.repository.registration.PendingApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service for cleaning up expired registration approvals
 */
@Service
public class RegistrationCleanupService {

    private final PendingApprovalRepository pendingApprovalRepository;

    @Autowired
    public RegistrationCleanupService(PendingApprovalRepository pendingApprovalRepository) {
        this.pendingApprovalRepository = pendingApprovalRepository;
    }

    /**
     * Scheduled task to clean up old pending approvals.
     * Runs daily at 2 AM to:
     * - Delete rejected approvals older than 30 days
     * - Delete pending approvals older than 90 days
     */
    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2 AM
    @Transactional
    public void cleanupExpiredApprovals() {
        ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().minusDays(30);
        ZonedDateTime ninetyDaysAgo = ZonedDateTime.now().minusDays(90);
        
        // Delete old rejected approvals
        List<PendingApproval> oldRejected = 
            pendingApprovalRepository.findByStatusAndCreatedAtBefore("REJECTED", thirtyDaysAgo);
        if (!oldRejected.isEmpty()) {
            System.out.println("Deleting " + oldRejected.size() + " rejected approvals older than 30 days");
            pendingApprovalRepository.deleteAll(oldRejected);
        }
        
        // Delete old pending approvals
        List<PendingApproval> oldPending = 
            pendingApprovalRepository.findByStatusAndCreatedAtBefore("PENDING", ninetyDaysAgo);
        if (!oldPending.isEmpty()) {
            System.out.println("Deleting " + oldPending.size() + " pending approvals older than 90 days");
            pendingApprovalRepository.deleteAll(oldPending);
        }
    }
}