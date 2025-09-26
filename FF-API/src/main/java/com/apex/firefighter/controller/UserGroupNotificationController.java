package com.apex.firefighter.controller;

import com.apex.firefighter.service.UserGroupNotificationService;
import com.apex.firefighter.dto.GroupChangeNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-group-notifications")
@CrossOrigin(origins = "*")
public class UserGroupNotificationController {

    @Autowired
    private UserGroupNotificationService userGroupNotificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<GroupChangeNotificationDTO>> getUnreadNotifications(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.ok(List.of()); // Return empty list if not authenticated
            }
            
            String username = authentication.getName();
            List<GroupChangeNotificationDTO> notifications = userGroupNotificationService.getUnreadNotificationsForUser(username);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error fetching unread group change notifications: " + e.getMessage());
            return ResponseEntity.ok(List.of()); // Return empty list on error to prevent frontend issues
        }
    }

    @PutMapping("/{notificationId}/mark-read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Authentication required"));
            }
            
            String username = authentication.getName();
            userGroupNotificationService.markNotificationAsRead(notificationId, username);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (Exception e) {
            System.err.println("Error marking group change notification as read: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to mark notification as read"));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.ok(Map.of("count", 0L));
            }
            
            String username = authentication.getName();
            long count = userGroupNotificationService.getUnreadNotificationCount(username);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            System.err.println("Error getting unread group notification count: " + e.getMessage());
            return ResponseEntity.ok(Map.of("count", 0L));
        }
    }
}