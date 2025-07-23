package com.apex.firefighter.service;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * NotificationService handles notification management.
 * This service is responsible for:
 * - Creating and managing notifications
 * - Retrieving user-specific notifications
 * - Marking notifications as read
 * - Deleting read notifications
 * - Managing notification lifecycle
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Create a new notification for a user
     */
    public Notification createNotification(String userId, String type, String title, String message) {
        System.out.println("🔔 CREATE NOTIFICATION: Creating notification for user - " + userId + ", type - " + type);
        
        Notification notification = new Notification(userId, type, title, message);
        Notification savedNotification = notificationRepository.save(notification);
        
        System.out.println("✅ NOTIFICATION CREATED: " + savedNotification);
        return savedNotification;
    }

    /**
     * Create a new notification for a user with ticket reference
     */
    public Notification createNotification(String userId, String type, String title, String message, String ticketId) {
        System.out.println("🔔 CREATE NOTIFICATION: Creating notification for user - " + userId + ", type - " + type + ", ticket - " + ticketId);
        
        Notification notification = new Notification(userId, type, title, message, ticketId);
        Notification savedNotification = notificationRepository.save(notification);
        
        System.out.println("✅ NOTIFICATION CREATED: " + savedNotification);
        return savedNotification;
    }

    /**
     * Get all notifications for a user
     */
    public List<Notification> getNotificationsForUser(String userId) {
        System.out.println("📋 GET NOTIFICATIONS: Retrieving notifications for user - " + userId);
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        System.out.println("📋 GET UNREAD NOTIFICATIONS: Retrieving unread notifications for user - " + userId);
        return notificationRepository.findByUserIdAndReadFalseOrderByTimestampDesc(userId);
    }

    /**
     * Get read notifications for a user
     */
    public List<Notification> getReadNotificationsForUser(String userId) {
        System.out.println("📋 GET READ NOTIFICATIONS: Retrieving read notifications for user - " + userId);
        return notificationRepository.findByUserIdAndReadTrueOrderByTimestampDesc(userId);
    }

    /**
     * Get recent notifications for a user (limited count)
     */
    public List<Notification> getRecentNotificationsForUser(String userId, int limit) {
        System.out.println("📋 GET RECENT NOTIFICATIONS: Retrieving " + limit + " recent notifications for user - " + userId);
        return notificationRepository.findRecentNotificationsForUser(userId, limit);
    }

    /**
     * Mark a specific notification as read
     */
    public boolean markNotificationAsRead(Long notificationId, String userId) {
        System.out.println("✅ MARK AS READ: Marking notification " + notificationId + " as read for user - " + userId);
        
        int updatedRows = notificationRepository.markAsRead(notificationId, userId);
        boolean success = updatedRows > 0;
        
        if (success) {
            System.out.println("✅ NOTIFICATION MARKED AS READ: " + notificationId);
        } else {
            System.out.println("⚠️ NOTIFICATION NOT FOUND OR ALREADY READ: " + notificationId);
        }
        
        return success;
    }

    /**
     * Mark all notifications as read for a user
     */
    public int markAllNotificationsAsRead(String userId) {
        System.out.println("✅ MARK ALL AS READ: Marking all notifications as read for user - " + userId);
        
        int updatedRows = notificationRepository.markAllAsReadForUser(userId);
        System.out.println("✅ MARKED " + updatedRows + " NOTIFICATIONS AS READ for user - " + userId);
        
        return updatedRows;
    }

    /**
     * Delete all read notifications for a user
     */
    public int deleteReadNotifications(String userId) {
        System.out.println("🗑️ DELETE READ NOTIFICATIONS: Deleting read notifications for user - " + userId);
        
        int deletedRows = notificationRepository.deleteReadNotificationsForUser(userId);
        System.out.println("🗑️ DELETED " + deletedRows + " READ NOTIFICATIONS for user - " + userId);
        
        return deletedRows;
    }

    /**
     * Delete a specific notification (only if it belongs to the user)
     */
    public boolean deleteNotification(Long notificationId, String userId) {
        System.out.println("🗑️ DELETE NOTIFICATION: Deleting notification " + notificationId + " for user - " + userId);
        
        int deletedRows = notificationRepository.deleteNotificationForUser(notificationId, userId);
        boolean success = deletedRows > 0;
        
        if (success) {
            System.out.println("🗑️ NOTIFICATION DELETED: " + notificationId);
        } else {
            System.out.println("⚠️ NOTIFICATION NOT FOUND OR NOT OWNED BY USER: " + notificationId);
        }
        
        return success;
    }

    /**
     * Get notification counts for a user
     */
    public NotificationStats getNotificationStats(String userId) {
        System.out.println("📊 GET NOTIFICATION STATS: Getting stats for user - " + userId);
        
        long totalCount = notificationRepository.countByUserId(userId);
        long unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);
        long readCount = notificationRepository.countByUserIdAndReadTrue(userId);
        
        return new NotificationStats(totalCount, unreadCount, readCount);
    }

    /**
     * Get a specific notification if it belongs to the user
     */
    public Optional<Notification> getNotificationForUser(Long notificationId, String userId) {
        System.out.println("📋 GET NOTIFICATION: Retrieving notification " + notificationId + " for user - " + userId);
        return notificationRepository.findByIdAndUserId(notificationId, userId);
    }

    /**
     * Check if a notification exists and belongs to a user
     */
    public boolean notificationExistsForUser(Long notificationId, String userId) {
        return notificationRepository.existsByIdAndUserId(notificationId, userId);
    }

    /**
     * Get notifications by type for a user
     */
    public List<Notification> getNotificationsByType(String userId, String type) {
        System.out.println("📋 GET NOTIFICATIONS BY TYPE: Retrieving " + type + " notifications for user - " + userId);
        return notificationRepository.findByUserIdAndTypeOrderByTimestampDesc(userId, type);
    }

    /**
     * Get notifications related to a specific ticket for a user
     */
    public List<Notification> getNotificationsForTicket(String userId, String ticketId) {
        System.out.println("📋 GET TICKET NOTIFICATIONS: Retrieving notifications for ticket " + ticketId + " and user - " + userId);
        return notificationRepository.findByUserIdAndTicketIdOrderByTimestampDesc(userId, ticketId);
    }

    /**
     * Clean up old read notifications (older than specified days)
     */
    public int cleanupOldReadNotifications(int daysOld) {
        System.out.println("🧹 CLEANUP: Deleting read notifications older than " + daysOld + " days");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deletedRows = notificationRepository.deleteOldReadNotifications(cutoffDate);
        
        System.out.println("🧹 CLEANUP COMPLETE: Deleted " + deletedRows + " old read notifications");
        return deletedRows;
    }

    /**
     * Notification statistics data class
     */
    public static class NotificationStats {
        private final long total;
        private final long unread;
        private final long read;

        public NotificationStats(long total, long unread, long read) {
            this.total = total;
            this.unread = unread;
            this.read = read;
        }

        public long getTotal() { return total; }
        public long getUnread() { return unread; }
        public long getRead() { return read; }
    }
}
