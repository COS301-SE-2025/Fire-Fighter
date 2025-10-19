package com.apex.firefighter.repository.registration;

import com.apex.firefighter.model.registration.PendingApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PendingApproval entity
 */
@Repository
public interface PendingApprovalRepository extends JpaRepository<PendingApproval, Long> {

    /**
     * Find pending approval by Firebase UID
     */
    Optional<PendingApproval> findByFirebaseUid(String firebaseUid);

    /**
     * Find pending approval by Firebase UID and status
     */
    Optional<PendingApproval> findByFirebaseUidAndStatus(String firebaseUid, String status);

    /**
     * Find all pending approvals by status
     */
    List<PendingApproval> findByStatus(String status);

    /**
     * Find all pending approvals ordered by creation date
     */
    List<PendingApproval> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find pending approvals older than a specific date (for cleanup)
     */
    List<PendingApproval> findByStatusAndCreatedAtBefore(String status, ZonedDateTime date);

    /**
     * Count pending approvals by status
     */
    long countByStatus(String status);

    /**
     * Check if a pending approval exists for a Firebase UID
     */
    boolean existsByFirebaseUid(String firebaseUid);

    /**
     * Check if a pending approval exists for an email
     */
    boolean existsByEmail(String email);

    /**
     * Find by email
     */
    Optional<PendingApproval> findByEmail(String email);

    /**
     * Delete by Firebase UID
     */
    void deleteByFirebaseUid(String firebaseUid);
}
