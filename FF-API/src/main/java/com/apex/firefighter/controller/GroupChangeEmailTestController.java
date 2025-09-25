package com.apex.firefighter.controller;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test controller for sending group change notification emails with mock data
 * This controller tests the suspicious group change detection email functionality
 */
@RestController
@RequestMapping("/api/test/group-change-emails")
@CrossOrigin(origins = "*")
public class GroupChangeEmailTestController {

    private final GmailEmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public GroupChangeEmailTestController(GmailEmailService emailService,
                                        UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Test endpoint to send all types of suspicious group change emails
     */
    @PostMapping("/send-all-risk-levels")
    public ResponseEntity<Map<String, Object>> sendAllRiskLevels() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                response.put("success", false);
                response.put("message", "No admin users found in the system");
                response.put("adminCount", 0);
                return ResponseEntity.badRequest().body(response);
            }

            int totalEmailsSent = 0;
            Map<String, Integer> emailsByRiskLevel = new HashMap<>();

            // Test HIGH RISK - Financial Emergency Group change
            User mockUserFinancial = createMockUserForGroup("Financial");
            totalEmailsSent += sendGroupChangeEmailsToAdmins(adminUsers, mockUserFinancial, 
                "TICKET-FIN-001", "None", "Financial Emergency Group", 
                "Ticket creation - Financial emergency access", "HIGH");
            emailsByRiskLevel.put("HIGH_RISK_FINANCIAL", adminUsers.size());

            // Test HIGH RISK - Management Emergency Group change
            User mockUserManagement = createMockUserForGroup("Management");
            totalEmailsSent += sendGroupChangeEmailsToAdmins(adminUsers, mockUserManagement, 
                "TICKET-MGT-002", "None", "Management Emergency Group", 
                "Ticket creation - Management emergency access", "HIGH");
            emailsByRiskLevel.put("HIGH_RISK_MANAGEMENT", adminUsers.size());

            // Test MEDIUM RISK - HR Emergency Group change
            User mockUserHR = createMockUserForGroup("HR");
            totalEmailsSent += sendGroupChangeEmailsToAdmins(adminUsers, mockUserHR, 
                "TICKET-HR-003", "None", "HR Emergency Group", 
                "Ticket creation - HR emergency access", "MEDIUM");
            emailsByRiskLevel.put("MEDIUM_RISK_HR", adminUsers.size());

            // Test MEDIUM RISK - Moving from HR to another group
            User mockUserHRTransfer = createMockUserForGroup("IT");
            totalEmailsSent += sendGroupChangeEmailsToAdmins(adminUsers, mockUserHRTransfer, 
                "TICKET-TRANSFER-004", "HR Emergency Group", "Financial Emergency Group", 
                "Group transfer - Suspicious cross-department access", "MEDIUM");
            emailsByRiskLevel.put("MEDIUM_RISK_HR_TRANSFER", adminUsers.size());

            response.put("success", true);
            response.put("message", "All group change risk level emails sent successfully");
            response.put("adminCount", adminUsers.size());
            response.put("totalEmailsSent", totalEmailsSent);
            response.put("emailsByRiskLevel", emailsByRiskLevel);
            response.put("riskLevelExplanation", Map.of(
                "HIGH", "Financial or Management Emergency Group changes - Always suspicious",
                "MEDIUM", "HR Emergency Group changes or transfers involving HR - Potentially suspicious",
                "LOW", "Logistics Emergency Group changes - Generally not suspicious (not tested here)"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send group change emails: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test endpoint to send a specific risk level group change email
     */
    @PostMapping("/send-specific-risk/{riskLevel}")
    public ResponseEntity<Map<String, Object>> sendSpecificRiskLevel(@PathVariable String riskLevel) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                response.put("success", false);
                response.put("message", "No admin users found in the system");
                return ResponseEntity.badRequest().body(response);
            }

            // Create scenario based on risk level
            GroupChangeScenario scenario = createScenarioForRiskLevel(riskLevel);
            
            if (scenario == null) {
                response.put("success", false);
                response.put("message", "Invalid risk level: " + riskLevel);
                response.put("validRiskLevels", List.of("HIGH", "MEDIUM", "LOW"));
                return ResponseEntity.badRequest().body(response);
            }

            int emailsSent = sendGroupChangeEmailsToAdmins(adminUsers, scenario.user, 
                scenario.ticketId, scenario.oldGroup, scenario.newGroup, scenario.reason, scenario.riskLevel);

            response.put("success", true);
            response.put("message", "Group change emails sent successfully for risk level: " + riskLevel);
            response.put("riskLevel", riskLevel);
            response.put("adminCount", adminUsers.size());
            response.put("emailsSent", emailsSent);
            response.put("scenario", Map.of(
                "oldGroup", scenario.oldGroup,
                "newGroup", scenario.newGroup,
                "reason", scenario.reason,
                "ticketId", scenario.ticketId
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send group change emails: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test endpoint to simulate group change scenarios with custom data
     */
    @PostMapping("/send-custom-scenario")
    public ResponseEntity<Map<String, Object>> sendCustomScenario(
            @RequestParam String oldGroup,
            @RequestParam String newGroup,
            @RequestParam String reason,
            @RequestParam String riskLevel) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                response.put("success", false);
                response.put("message", "No admin users found in the system");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate risk level
            if (!List.of("HIGH", "MEDIUM", "LOW").contains(riskLevel)) {
                response.put("success", false);
                response.put("message", "Invalid risk level. Must be HIGH, MEDIUM, or LOW");
                return ResponseEntity.badRequest().body(response);
            }

            // Create mock user
            User mockUser = createMockUserForGroup("Custom Test");
            String ticketId = "TICKET-CUSTOM-" + System.currentTimeMillis();

            int emailsSent = sendGroupChangeEmailsToAdmins(adminUsers, mockUser, 
                ticketId, oldGroup, newGroup, reason, riskLevel);

            response.put("success", true);
            response.put("message", "Custom group change scenario emails sent successfully");
            response.put("adminCount", adminUsers.size());
            response.put("emailsSent", emailsSent);
            response.put("customScenario", Map.of(
                "oldGroup", oldGroup,
                "newGroup", newGroup,
                "reason", reason,
                "riskLevel", riskLevel,
                "ticketId", ticketId
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send custom scenario emails: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get group change risk assessment information
     */
    @GetMapping("/risk-assessment-info")
    public ResponseEntity<Map<String, Object>> getRiskAssessmentInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("success", true);
        response.put("riskLevels", Map.of(
            "HIGH", Map.of(
                "description", "Always suspicious - Financial or Management Emergency Groups",
                "groups", List.of("Financial Emergency Group (ID: 2)", "Management Emergency Group (ID: 3)"),
                "action", "Immediate investigation required"
            ),
            "MEDIUM", Map.of(
                "description", "Potentially suspicious - HR Emergency Group or transfers involving HR",
                "groups", List.of("HR Emergency Group (ID: 1)", "Any transfer from/to HR"),
                "action", "Review and verify legitimacy"
            ),
            "LOW", Map.of(
                "description", "Generally not suspicious - Logistics Emergency Group only",
                "groups", List.of("Logistics Emergency Group (ID: 4)"),
                "action", "Standard monitoring"
            )
        ));
        
        response.put("detectionLogic", Map.of(
            "highRisk", "Any changes involving Financial (ID: 2) or Management (ID: 3) groups",
            "mediumRisk", "Any changes involving HR (ID: 1) group or moving from/to HR",
            "lowRisk", "Changes involving only Logistics (ID: 4) group"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to send group change emails to all admin users
     */
    private int sendGroupChangeEmailsToAdmins(List<User> adminUsers, User user, String ticketId, 
                                            String oldGroup, String newGroup, String reason, String riskLevel) {
        int emailsSent = 0;

        for (User admin : adminUsers) {
            try {
                emailService.sendSuspiciousGroupChangeNotificationEmail(
                    admin.getEmail(),
                    user,
                    ticketId,
                    oldGroup,
                    newGroup,
                    reason,
                    riskLevel
                );
                emailsSent++;
                System.out.println("✅ TEST EMAIL: Sent " + riskLevel + " risk group change email to " + admin.getEmail());
            } catch (Exception e) {
                System.err.println("❌ TEST EMAIL: Failed to send group change email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }

        return emailsSent;
    }

    /**
     * Create mock user data for specific group testing
     */
    private User createMockUserForGroup(String department) {
        User mockUser = new User();
        mockUser.setUserId("test-user-" + department.toLowerCase() + "-" + System.currentTimeMillis());
        mockUser.setUsername("jane.smith." + department.toLowerCase());
        mockUser.setEmail("jane.smith." + department.toLowerCase() + "@company.com");
        mockUser.setDepartment(department);
        mockUser.setRole("Employee");
        mockUser.setIsAuthorized(true);
        mockUser.setIsAdmin(false);
        mockUser.setDolibarrId("54321");
        return mockUser;
    }

    /**
     * Create scenario based on risk level
     */
    private GroupChangeScenario createScenarioForRiskLevel(String riskLevel) {
        return switch (riskLevel) {
            case "HIGH" -> new GroupChangeScenario(
                createMockUserForGroup("Security"),
                "TICKET-HIGH-RISK-" + System.currentTimeMillis(),
                "None",
                "Financial Emergency Group",
                "Emergency ticket creation - Financial system access required",
                "HIGH"
            );
            case "MEDIUM" -> new GroupChangeScenario(
                createMockUserForGroup("HR"),
                "TICKET-MEDIUM-RISK-" + System.currentTimeMillis(),
                "None",
                "HR Emergency Group",
                "Emergency ticket creation - HR system access required",
                "MEDIUM"
            );
            case "LOW" -> new GroupChangeScenario(
                createMockUserForGroup("Operations"),
                "TICKET-LOW-RISK-" + System.currentTimeMillis(),
                "None",
                "Logistics Emergency Group",
                "Emergency ticket creation - Logistics system access required",
                "LOW"
            );
            default -> null;
        };
    }

    /**
     * Helper class to represent group change scenarios
     */
    private static class GroupChangeScenario {
        final User user;
        final String ticketId;
        final String oldGroup;
        final String newGroup;
        final String reason;
        final String riskLevel;

        GroupChangeScenario(User user, String ticketId, String oldGroup, String newGroup, String reason, String riskLevel) {
            this.user = user;
            this.ticketId = ticketId;
            this.oldGroup = oldGroup;
            this.newGroup = newGroup;
            this.reason = reason;
            this.riskLevel = riskLevel;
        }
    }
}
