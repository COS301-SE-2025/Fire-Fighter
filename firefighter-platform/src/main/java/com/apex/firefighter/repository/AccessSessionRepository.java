package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessSessionRepository extends JpaRepository<AccessSession, Long> {
    
    // Find sessions by user Firebase UID
    @Query("SELECT as FROM AccessSession as WHERE as.user.userId = :userId")
    List<AccessSession> findByUserId(@Param("userId") String userId);
    
    // Find session by access request ID
    Optional<AccessSession> findByAccessRequestId(Long accessRequestId);
    
    // Find active sessions for a user
    @Query("SELECT as FROM AccessSession as WHERE as.user.userId = :userId AND as.active = true")
    List<AccessSession> findActiveByUserId(@Param("userId") String userId);
    
    // Find all active sessions
    List<AccessSession> findByActiveTrue();
    
    // Find sessions by ticket ID through access request
    @Query("SELECT as FROM AccessSession as WHERE as.accessRequest.ticketId = :ticketId")
    List<AccessSession> findByTicketId(@Param("ticketId") String ticketId);
    
    // Find session by session token
    Optional<AccessSession> findBySessionToken(String sessionToken);
}
