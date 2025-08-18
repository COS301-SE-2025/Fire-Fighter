package com.apex.firefighter.controller;

import com.apex.firefighter.model.Notification;
import com.apex.firefighter.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "User notification management operations")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200", "http://localhost:8100", "http://127.0.0.1:8100", "ionic://localhost", "capacitor://localhost"})
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get all notifications for the current user",
               description = "Retrieves all notifications for the authenticated user, ordered by timestamp descending")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error retrieving notifications for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get unread notifications for the current user",
               description = "Retrieves only unread notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("Error retrieving unread notifications for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get notification statistics for the current user",
               description = "Retrieves notification counts (total, unread, read) for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats(
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            NotificationService.NotificationStats stats = notificationService.getNotificationStats(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", stats.getTotal());
            response.put("unread", stats.getUnread());
            response.put("read", stats.getRead());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error retrieving notification stats for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Mark a specific notification as read",
               description = "Marks a specific notification as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found or doesn't belong to user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markNotificationAsRead(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable Long notificationId,
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            boolean success = notificationService.markNotificationAsRead(notificationId, userId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Notification marked as read");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Notification not found or doesn't belong to user");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Mark all notifications as read for the current user",
               description = "Marks all unread notifications as read for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All notifications marked as read successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllNotificationsAsRead(
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            int updatedCount = notificationService.markAllNotificationsAsRead(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All notifications marked as read");
            response.put("updatedCount", updatedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error marking all notifications as read for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Delete all read notifications for the current user",
               description = "Deletes all read notifications for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Read notifications deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/read")
    public ResponseEntity<Map<String, Object>> deleteReadNotifications(
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            int deletedCount = notificationService.deleteReadNotifications(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Read notifications deleted successfully");
            response.put("deletedCount", deletedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error deleting read notifications for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Delete a specific notification",
               description = "Deletes a specific notification for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found or doesn't belong to user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable Long notificationId,
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            boolean success = notificationService.deleteNotification(notificationId, userId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Notification deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Notification not found or doesn't belong to user");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get a specific notification",
               description = "Retrieves a specific notification for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found or doesn't belong to user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<Notification> getNotification(
            @Parameter(description = "Notification ID", required = true)
            @PathVariable Long notificationId,
            @Parameter(description = "User ID (Firebase UID)", required = true)
            @RequestParam String userId) {
        
        try {
            Optional<Notification> notification = notificationService.getNotificationForUser(notificationId, userId);
            
            if (notification.isPresent()) {
                return ResponseEntity.ok(notification.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error retrieving notification " + notificationId + " for user " + userId + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
