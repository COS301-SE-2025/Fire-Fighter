package com.apex.firefighter.controller;

import com.apex.firefighter.service.UserAnomalyNotificationService;
import com.apex.firefighter.service.UserGroupNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test/user-notifications")
@CrossOrigin(origins = "*")
public class UserNotificationTestController {

    @Autowired
    private UserAnomalyNotificationService anomalyNotificationService;
    
    @Autowired
    private UserGroupNotificationService groupNotificationService;

    /**
     * Create test anomaly notification for a user
     */
    @PostMapping("/create-anomaly/{username}")
    public ResponseEntity<Map<String, String>> createTestAnomalyNotification(
            @PathVariable String username,
            @RequestParam(defaultValue = "FREQUENT_REQUESTS") String anomalyType) {
        
        try {
            String message = "Test anomaly notification: " + anomalyType;
            anomalyNotificationService.createAnomalyNotification("test-user-id", username, anomalyType, message);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test anomaly notification created successfully",
                "username", username,
                "anomalyType", anomalyType
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to create test anomaly notification: " + e.getMessage()
            ));
        }
    }

    /**
     * Create test group change notification for a user
     */
    @PostMapping("/create-group-change/{username}")
    public ResponseEntity<Map<String, String>> createTestGroupChangeNotification(
            @PathVariable String username,
            @RequestParam(defaultValue = "HIGH") String securityLevel,
            @RequestParam(defaultValue = "Test Group") String groupName) {
        
        try {
            groupNotificationService.createGroupChangeNotification("test-user-id", username, securityLevel, groupName, "PROMOTION");
            
            return ResponseEntity.ok(Map.of(
                "message", "Test group change notification created successfully",
                "username", username,
                "securityLevel", securityLevel,
                "groupName", groupName
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to create test group change notification: " + e.getMessage()
            ));
        }
    }

    /**
     * Create multiple test notifications for demo purposes
     */
    @PostMapping("/create-demo-notifications/{username}")
    public ResponseEntity<Map<String, Object>> createDemoNotifications(@PathVariable String username) {
        try {
            // Create various anomaly notifications
            anomalyNotificationService.createAnomalyNotification("test-user-id", username, "FREQUENT_REQUESTS", "Demo: Excessive requests detected");
            anomalyNotificationService.createAnomalyNotification("test-user-id", username, "OFF_HOURS_ACTIVITY", "Demo: Off-hours access detected");
            anomalyNotificationService.createAnomalyNotification("test-user-id", username, "DORMANT_USER_ACTIVITY", "Demo: Dormant account activity detected");
            
            // Create group change notifications
            groupNotificationService.createGroupChangeNotification("test-user-id", username, "HIGH", "Financial Management", "PROMOTION");
            groupNotificationService.createGroupChangeNotification("test-user-id", username, "MEDIUM", "HR Department", "TRANSFER");
            
            return ResponseEntity.ok(Map.of(
                "message", "Demo notifications created successfully",
                "username", username,
                "anomalyNotifications", 3,
                "groupChangeNotifications", 2,
                "totalNotifications", 5
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to create demo notifications: " + e.getMessage()
            ));
        }
    }
}