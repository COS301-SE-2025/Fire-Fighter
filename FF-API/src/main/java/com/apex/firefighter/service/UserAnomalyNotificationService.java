package com.apex.firefighter.service;

import com.apex.firefighter.dto.AnomalyNotificationDTO;
import com.apex.firefighter.model.AnomalyNotification;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.AnomalyNotificationRepository;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAnomalyNotificationService {

    @Autowired
    private AnomalyNotificationRepository anomalyNotificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new anomaly notification for a user
     */
    public void createAnomalyNotification(String userId, String username, String anomalyType, String message) {
        AnomalyNotification notification = new AnomalyNotification(userId, username, anomalyType, message);
        anomalyNotificationRepository.save(notification);
        System.out.println("📧 ANOMALY NOTIFICATION CREATED: " + anomalyType + " for user " + username);
    }

    /**
     * Get unread notifications for a specific user
     */
    public List<AnomalyNotificationDTO> getUnreadNotificationsForUser(String username) {
        List<AnomalyNotification> notifications = anomalyNotificationRepository
                .findByUsernameAndIsReadFalseOrderByCreatedAtDesc(username);
        
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark a notification as read
     */
    public void markNotificationAsRead(Long notificationId, String username) {
        AnomalyNotification notification = anomalyNotificationRepository.findByIdAndUsername(notificationId, username);
        if (notification != null) {
            notification.setRead(true);
            anomalyNotificationRepository.save(notification);
        }
    }

    /**
     * Get count of unread notifications for a user
     */
    public long getUnreadNotificationCount(String username) {
        return anomalyNotificationRepository.countByUsernameAndIsReadFalse(username);
    }

    /**
     * Convert entity to DTO
     */
    private AnomalyNotificationDTO convertToDTO(AnomalyNotification notification) {
        return new AnomalyNotificationDTO(
                notification.getId(),
                notification.getAnomalyType(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead(),
                notification.getUsername()
        );
    }

    /**
     * Create anomaly notification when anomaly is detected
     */
    public void notifyUserOfAnomaly(User user, String anomalyType, String anomalyDetails) {
        String message = "Anomaly detected: " + anomalyDetails;
        createAnomalyNotification(user.getUserId(), user.getUsername(), anomalyType, message);
    }
}