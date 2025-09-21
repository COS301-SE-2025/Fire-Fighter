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
    
    // Find login events for a user within a time period (for dormant user detection)
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.action = 'LOGIN' AND al.timestamp >= :since ORDER BY al.timestamp DESC")
    List<AccessLog> findLoginEventsByUserSince(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    // Count actions by user within a time period (for dormant user detection)
    @Query("SELECT COUNT(al) FROM AccessLog al WHERE al.user.userId = :userId AND al.timestamp >= :since")
    long countActionsByUserSince(@Param("userId") String userId, @Param("since") LocalDateTime since);
    
    // Find most recent login for a user
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.action = 'LOGIN' ORDER BY al.timestamp DESC")
    List<AccessLog> findMostRecentLoginByUser(@Param("userId") String userId);
}
