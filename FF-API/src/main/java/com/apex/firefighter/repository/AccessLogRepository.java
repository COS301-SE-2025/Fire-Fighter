package com.apex.firefighter.repository;

import com.apex.firefighter.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
    // Find logs by user Firebase UID
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId ORDER BY al.timestamp DESC")
    List<AccessLog> findByUserId(@Param("userId") String userId);
    
    // Find logs by action type
    List<AccessLog> findByActionOrderByTimestampDesc(String action);
    
    // Find logs by ticket ID
    List<AccessLog> findByTicketIdOrderByTimestampDesc(String ticketId);
    
    // Find logs within a time range
    @Query("SELECT al FROM AccessLog al WHERE al.timestamp BETWEEN :startTime AND :endTime ORDER BY al.timestamp DESC")
    List<AccessLog> findByTimestampBetween(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);
    
    // Find logs by user and action
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.action = :action ORDER BY al.timestamp DESC")
    List<AccessLog> findByUserIdAndAction(@Param("userId") String userId, @Param("action") String action);
    
    // Find recent logs (last N entries)
    @Query("SELECT al FROM AccessLog al ORDER BY al.timestamp DESC")
    List<AccessLog> findRecentLogs();
    
    // Find logs by session ID
    List<AccessLog> findBySessionIdOrderByTimestampDesc(Long sessionId);
    
    
    // Find most recent login for a user
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.action = 'LOGIN' ORDER BY al.timestamp DESC")
    List<AccessLog> findMostRecentLoginByUser(@Param("userId") String userId);
    
    // ========================================
    // ROLE-BASED QUERIES (for audit trail)
    // ========================================
    
    // Find all logs for a specific user role
    @Query("SELECT al FROM AccessLog al WHERE al.userRole = :role ORDER BY al.timestamp DESC")
    List<AccessLog> findByUserRole(@Param("role") String role);
    
    // Find logs by user and role (track what a specific user did while having a specific role)
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.userRole = :role ORDER BY al.timestamp DESC")
    List<AccessLog> findByUserIdAndRole(@Param("userId") String userId, @Param("role") String role);
    
    // Find logs by role and action (e.g., all LOGIN attempts by ADMIN role)
    @Query("SELECT al FROM AccessLog al WHERE al.userRole = :role AND al.action = :action ORDER BY al.timestamp DESC")
    List<AccessLog> findByRoleAndAction(@Param("role") String role, @Param("action") String action);
    
    // Find logs by role within a time range (for compliance reporting)
    @Query("SELECT al FROM AccessLog al WHERE al.userRole = :role AND al.timestamp BETWEEN :startTime AND :endTime ORDER BY al.timestamp DESC")
    List<AccessLog> findByRoleAndTimestampBetween(@Param("role") String role, @Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);
    
    // Find logs where user's role changed (by comparing consecutive logs for same user)
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId ORDER BY al.timestamp ASC")
    List<AccessLog> findAllByUserIdOrderedByTime(@Param("userId") String userId);
}
