package com.apex.firefighter.service;

import com.apex.firefighter.dto.GroupChangeNotificationDTO;
import com.apex.firefighter.model.GroupChangeNotification;
import com.apex.firefighter.repository.GroupChangeNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupNotificationService {

    @Autowired
    private GroupChangeNotificationRepository groupChangeNotificationRepository;

    /**
     * Create a new group change notification for a user
     */
    public void createGroupChangeNotification(String userId, String username, String securityLevel, String groupName, String changeType) {
        GroupChangeNotification notification = new GroupChangeNotification(userId, username, securityLevel, groupName, changeType);
        groupChangeNotificationRepository.save(notification);
        System.out.println("🔐 GROUP CHANGE NOTIFICATION CREATED: " + securityLevel + " level for user " + username);
    }

    /**
     * Get unread notifications for a specific user
     */
    public List<GroupChangeNotificationDTO> getUnreadNotificationsForUser(String username) {
        List<GroupChangeNotification> notifications = groupChangeNotificationRepository
                .findByUsernameAndIsReadFalseOrderByCreatedAtDesc(username);
        
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mark a notification as read
     */
    public void markNotificationAsRead(Long notificationId, String username) {
        GroupChangeNotification notification = groupChangeNotificationRepository.findByIdAndUsername(notificationId, username);
        if (notification != null) {
            notification.setRead(true);
            groupChangeNotificationRepository.save(notification);
        }
    }

    /**
     * Get count of unread notifications for a user
     */
    public long getUnreadNotificationCount(String username) {
        return groupChangeNotificationRepository.countByUsernameAndIsReadFalse(username);
    }

    /**
     * Convert entity to DTO
     */
    private GroupChangeNotificationDTO convertToDTO(GroupChangeNotification notification) {
        return new GroupChangeNotificationDTO(
                notification.getId(),
                notification.getSecurityLevel(),
                notification.getGroupName(),
                notification.getChangeType(),
                notification.getCreatedAt(),
                notification.isRead(),
                notification.getUsername()
        );
    }

    /**
     * Determine security level based on group name
     */
    public String determineSecurityLevel(String groupName) {
        if (groupName == null) return "LOW";
        
        String group = groupName.toLowerCase();
        if (group.contains("financial") || group.contains("management") || group.contains("admin")) {
            return "HIGH";
        } else if (group.contains("hr") || group.contains("supervisor")) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}