package com.apex.firefighter.repository;

import com.apex.firefighter.model.registration.SystemAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SystemAccessRequest entity
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
     * Delete access request by Firebase UID
     */
    void deleteByFirebaseUid(String firebaseUid);
}
