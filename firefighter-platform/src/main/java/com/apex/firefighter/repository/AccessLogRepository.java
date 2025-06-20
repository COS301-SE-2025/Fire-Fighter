package com.apex.firefighter.repository;

import com.apex.firefighter.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<LogEntry, Long> {
    
    // Find logs by user Firebase UID
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId ORDER BY al.timestamp DESC")
    List<LogEntry> findByUserId(@Param("userId") String userId);
    
    // Find logs by action type
    List<LogEntry> findByActionOrderByTimestampDesc(String action);
    
    // Find logs by ticket ID
    List<LogEntry> findByTicketIdOrderByTimestampDesc(String ticketId);
    
    // Find logs within a time range
    @Query("SELECT al FROM AccessLog al WHERE al.timestamp BETWEEN :startTime AND :endTime ORDER BY al.timestamp DESC")
    List<LogEntry> findByTimestampBetween(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);
    
    // Find logs by user and action
    @Query("SELECT al FROM AccessLog al WHERE al.user.userId = :userId AND al.action = :action ORDER BY al.timestamp DESC")
    List<LogEntry> findByUserIdAndAction(@Param("userId") String userId, @Param("action") String action);
    
    // Find recent logs (last N entries)
    @Query("SELECT al FROM AccessLog al ORDER BY al.timestamp DESC")
    List<LogEntry> findRecentLogs();
    
    // Find logs by session ID
    List<LogEntry> findBySessionIdOrderByTimestampDesc(Long sessionId);
}
