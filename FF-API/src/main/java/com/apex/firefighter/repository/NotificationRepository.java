package com.apex.firefighter.repository;

import com.apex.firefighter.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific user, ordered by timestamp descending
     */
    List<Notification> findByUserIdOrderByTimestampDesc(String userId);
    
    /**
     * Find unread notifications for a specific user, ordered by timestamp descending
     */
    List<Notification> findByUserIdAndReadFalseOrderByTimestampDesc(String userId);
    
    /**
     * Find read notifications for a specific user, ordered by timestamp descending
     */
    List<Notification> findByUserIdAndReadTrueOrderByTimestampDesc(String userId);
    
    /**
     * Find notifications by type for a specific user
     */
    List<Notification> findByUserIdAndTypeOrderByTimestampDesc(String userId, String type);
    
    /**
     * Find notifications related to a specific ticket
     */
    List<Notification> findByTicketIdOrderByTimestampDesc(String ticketId);
    
    /**
     * Find notifications for a user related to a specific ticket
     */
    List<Notification> findByUserIdAndTicketIdOrderByTimestampDesc(String userId, String ticketId);
    
    /**
     * Count total notifications for a user
     */
    long countByUserId(String userId);
    
    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndReadFalse(String userId);
    
    /**
     * Count read notifications for a user
     */
    long countByUserIdAndReadTrue(String userId);
    
    /**
     * Mark a specific notification as read
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :notificationId AND n.userId = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") String userId);
    
    /**
     * Mark all notifications as read for a specific user
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllAsReadForUser(@Param("userId") String userId);
    
    /**
     * Delete all read notifications for a specific user
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.read = true")
    int deleteReadNotificationsForUser(@Param("userId") String userId);
    
    /**
     * Delete a specific notification (only if it belongs to the user)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.id = :notificationId AND n.userId = :userId")
    int deleteNotificationForUser(@Param("notificationId") Long notificationId, @Param("userId") String userId);
    
    /**
     * Find notifications created within a date range for a user
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.timestamp >= :startDate AND n.timestamp <= :endDate ORDER BY n.timestamp DESC")
    List<Notification> findByUserIdAndTimestampBetween(@Param("userId") String userId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent notifications for a user (last N notifications)
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.timestamp DESC LIMIT :limit")
    List<Notification> findRecentNotificationsForUser(@Param("userId") String userId, @Param("limit") int limit);
    
    /**
     * Check if a notification exists and belongs to a user
     */
    boolean existsByIdAndUserId(Long id, String userId);
    
    /**
     * Find a notification by ID that belongs to a specific user
     */
    Optional<Notification> findByIdAndUserId(Long id, String userId);
    
    /**
     * Delete old read notifications (older than specified date)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.read = true AND n.timestamp < :cutoffDate")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Get notification statistics for a user
     */
    @Query("SELECT " +
           "COUNT(*) as total, " +
           "COUNT(CASE WHEN n.read = false THEN 1 END) as unread, " +
           "COUNT(CASE WHEN n.read = true THEN 1 END) as read " +
           "FROM Notification n WHERE n.userId = :userId")
    Object[] getNotificationStatsForUser(@Param("userId") String userId);
}
