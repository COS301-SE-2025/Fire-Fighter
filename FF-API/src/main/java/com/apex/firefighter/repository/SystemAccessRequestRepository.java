package com.apex.firefighter.repository;

import com.apex.firefighter.model.registration.SystemAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SystemAccessRequest entity
 * Primary repository for managing new user registration/access requests
 */
@Repository
public interface SystemAccessRequestRepository extends JpaRepository<SystemAccessRequest, Long> {
    
    /**
     * Find access request by Firebase UID
     */
    Optional<SystemAccessRequest> findByFirebaseUid(String firebaseUid);
    
    /**
     * Check if access request exists for Firebase UID
     */
    boolean existsByFirebaseUid(String firebaseUid);
    
    /**
     * Check if access request exists for email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find access request by email
     */
    Optional<SystemAccessRequest> findByEmail(String email);
    
    /**
     * Find all access requests by status
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of requests with the specified status
     */
    java.util.List<SystemAccessRequest> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * Find access request by Firebase UID and status
     */
    Optional<SystemAccessRequest> findByFirebaseUidAndStatus(String firebaseUid, String status);
    
    /**
     * Count requests by status
     */
    long countByStatus(String status);
    
    /**
     * Delete access request by Firebase UID
     */
    void deleteByFirebaseUid(String firebaseUid);
}
