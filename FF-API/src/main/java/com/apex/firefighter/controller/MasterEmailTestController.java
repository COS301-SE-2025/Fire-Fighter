package com.apex.firefighter.controller;

import com.apex.firefighter.service.EmailTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Master controller for testing all email notification functionality
 * This controller provides a unified interface for testing all email types in the FireFighter platform
 */
@RestController
@RequestMapping("/api/test/master-email-test")
@CrossOrigin(origins = "*")
public class MasterEmailTestController {

    private final EmailTestingService emailTestingService;

    @Autowired
    public MasterEmailTestController(EmailTestingService emailTestingService) {
        this.emailTestingService = emailTestingService;
    }

    /**
     * Run comprehensive test of ALL email types
     * This endpoint tests anomaly detection, group changes, and ticket notifications
     */
    @PostMapping("/run-comprehensive-test")
    public ResponseEntity<Map<String, Object>> runComprehensiveTest() {
        System.out.println("🚀 MASTER EMAIL TEST: Starting comprehensive email system test...");
        
        EmailTestingService.EmailTestResult result = emailTestingService.sendComprehensiveEmailTest();
        
        if (result.isSuccess()) {
            System.out.println("✅ MASTER EMAIL TEST: All email types tested successfully!");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result.getMessage(),
                "adminCount", result.getAdminCount(),
                "totalEmailsSent", result.getTotalEmailsSent(),
                "testResults", result.getAdditionalData(),
                "testTypes", Map.of(
                    "anomalyDetection", "Frequent requests, dormant user activity, off-hours activity",
                    "groupChanges", "High-risk financial/management, medium-risk HR changes",
                    "ticketNotifications", "Creation, completion, revocation, expiration warnings"
                )
            ));
        } else {
            System.err.println("❌ MASTER EMAIL TEST: Test failed - " + result.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", result.getMessage(),
                "error", result.getError(),
                "adminCount", result.getAdminCount()
            ));
        }
    }

    /**
     * Test only anomaly detection emails
     */
    @PostMapping("/test-anomaly-emails")
    public ResponseEntity<Map<String, Object>> testAnomalyEmails() {
        System.out.println("🔍 ANOMALY EMAIL TEST: Testing anomaly detection emails...");
        
        EmailTestingService.EmailTestResult result = emailTestingService.sendAllAnomalyEmails();
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result.getMessage(),
                "adminCount", result.getAdminCount(),
                "totalEmailsSent", result.getTotalEmailsSent(),
                "emailTypes", result.getEmailCounts(),
                "anomalyTypes", Map.of(
                    "FREQUENT_REQUESTS", "User exceeded request frequency thresholds",
                    "DORMANT_USER_ACTIVITY", "Previously inactive user suddenly active",
                    "OFF_HOURS_ACTIVITY", "System access outside business hours"
                )
            ));
        } else {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", result.getMessage(),
                "error", result.getError()
            ));
        }
    }

    /**
     * Test only group change notification emails
     */
    @PostMapping("/test-group-change-emails")
    public ResponseEntity<Map<String, Object>> testGroupChangeEmails() {
        System.out.println("👥 GROUP CHANGE EMAIL TEST: Testing suspicious group change emails...");
        
        EmailTestingService.EmailTestResult result = emailTestingService.sendAllGroupChangeEmails();
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result.getMessage(),
                "adminCount", result.getAdminCount(),
                "totalEmailsSent", result.getTotalEmailsSent(),
                "emailTypes", result.getEmailCounts(),
                "riskLevels", Map.of(
                    "HIGH", "Financial/Management emergency groups - Always suspicious",
                    "MEDIUM", "HR emergency group changes - Potentially suspicious",
                    "LOW", "Logistics emergency group - Generally not suspicious"
                )
            ));
        } else {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", result.getMessage(),
                "error", result.getError()
            ));
        }
    }

    /**
     * Test only ticket notification emails
     */
    @PostMapping("/test-ticket-emails")
    public ResponseEntity<Map<String, Object>> testTicketEmails() {
        System.out.println("🎫 TICKET EMAIL TEST: Testing ticket notification emails...");
        
        EmailTestingService.EmailTestResult result = emailTestingService.sendAllTicketEmails();
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result.getMessage(),
                "adminCount", result.getAdminCount(),
                "totalEmailsSent", result.getTotalEmailsSent(),
                "emailTypes", result.getEmailCounts(),
                "ticketNotificationTypes", Map.of(
                    "TICKET_CREATION", "New emergency ticket created",
                    "TICKET_COMPLETION", "Emergency ticket completed",
                    "TICKET_REVOCATION", "Emergency ticket revoked by admin",
                    "TICKET_WARNING", "Emergency ticket expiring soon (5-minute warning)"
                )
            ));
        } else {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", result.getMessage(),
                "error", result.getError()
            ));
        }
    }

    /**
     * Get information about admin users who will receive test emails
     */
    @GetMapping("/admin-info")
    public ResponseEntity<Map<String, Object>> getAdminInfo() {
        try {
            Map<String, Object> adminInfo = emailTestingService.getAdminUserInfo();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin user information retrieved successfully",
                "data", adminInfo,
                "note", "These are the admin users who will receive all test emails"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to get admin info: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * Get comprehensive information about all available email tests
     */
    @GetMapping("/test-info")
    public ResponseEntity<Map<String, Object>> getTestInfo() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Email testing system information",
            "availableTests", Map.of(
                "comprehensive", Map.of(
                    "endpoint", "/run-comprehensive-test",
                    "description", "Tests all email types: anomaly detection, group changes, and ticket notifications",
                    "method", "POST"
                ),
                "anomalyOnly", Map.of(
                    "endpoint", "/test-anomaly-emails",
                    "description", "Tests only anomaly detection emails (frequent requests, dormant users, off-hours)",
                    "method", "POST"
                ),
                "groupChangeOnly", Map.of(
                    "endpoint", "/test-group-change-emails",
                    "description", "Tests only suspicious group change notification emails",
                    "method", "POST"
                ),
                "ticketOnly", Map.of(
                    "endpoint", "/test-ticket-emails",
                    "description", "Tests only ticket notification emails (creation, completion, revocation, warnings)",
                    "method", "POST"
                )
            ),
            "emailTypes", Map.of(
                "anomalyDetection", Map.of(
                    "FREQUENT_REQUESTS", "User exceeded request frequency thresholds (MEDIUM risk)",
                    "DORMANT_USER_ACTIVITY", "Previously inactive user suddenly active (HIGH risk)",
                    "OFF_HOURS_ACTIVITY", "System access outside business hours (LOW risk)"
                ),
                "groupChanges", Map.of(
                    "HIGH_RISK", "Financial/Management emergency group changes",
                    "MEDIUM_RISK", "HR emergency group changes or transfers",
                    "LOW_RISK", "Logistics emergency group changes"
                ),
                "ticketNotifications", Map.of(
                    "CREATION", "New emergency ticket created",
                    "COMPLETION", "Emergency ticket completed",
                    "REVOCATION", "Emergency ticket revoked by admin",
                    "WARNING", "Emergency ticket expiring soon"
                )
            ),
            "notes", Map.of(
                "mockData", "All tests use realistic mock data for users and tickets",
                "realEmails", "Tests use the real email configuration and send actual emails",
                "adminTargets", "All emails are sent to actual admin users in the system",
                "logging", "Comprehensive logging shows email sending success/failure for each admin"
            )
        ));
    }
}
