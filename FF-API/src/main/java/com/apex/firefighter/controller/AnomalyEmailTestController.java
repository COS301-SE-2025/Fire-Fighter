package com.apex.firefighter.controller;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.AnomalyNotificationService;
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
 * Test controller for sending anomaly detection emails with mock data
 * This controller is designed for testing email functionality and templates
 */
@RestController
@RequestMapping("/api/test/anomaly-emails")
@CrossOrigin(origins = "*")
public class AnomalyEmailTestController {

    private final AnomalyNotificationService anomalyNotificationService;
    private final GmailEmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public AnomalyEmailTestController(AnomalyNotificationService anomalyNotificationService,
                                    GmailEmailService emailService,
                                    UserRepository userRepository) {
        this.anomalyNotificationService = anomalyNotificationService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Test endpoint to send all types of anomaly emails to all admin users
     */
    @PostMapping("/send-all-anomaly-types")
    public ResponseEntity<Map<String, Object>> sendAllAnomalyTypes() {
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

            // Create mock user and ticket data
            User mockUser = createMockUser();
            Ticket mockTicket = createMockTicket();

            int totalEmailsSent = 0;
            Map<String, Integer> emailsByType = new HashMap<>();

            // Test Frequent Requests Anomaly
            String frequentRequestsDetails = "User has made 8 requests in the last hour (threshold: 5)";
            totalEmailsSent += sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "FREQUENT_REQUESTS", frequentRequestsDetails);
            emailsByType.put("FREQUENT_REQUESTS", adminUsers.size());

            // Test Dormant User Activity Anomaly
            String dormantUserDetails = "User was dormant for 5+ days, logged in at " + 
                LocalDateTime.now().minusMinutes(10) + " and made 3 actions within 15 minutes";
            totalEmailsSent += sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "DORMANT_USER_ACTIVITY", dormantUserDetails);
            emailsByType.put("DORMANT_USER_ACTIVITY", adminUsers.size());

            // Test Off-Hours Activity Anomaly
            String offHoursDetails = "User made a request at 23:00 which is outside of regular work hours! (allowed: 7:00 AM - 17:00 PM)";
            totalEmailsSent += sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "OFF_HOURS_ACTIVITY", offHoursDetails);
            emailsByType.put("OFF_HOURS_ACTIVITY", adminUsers.size());

            response.put("success", true);
            response.put("message", "All anomaly type emails sent successfully");
            response.put("adminCount", adminUsers.size());
            response.put("totalEmailsSent", totalEmailsSent);
            response.put("emailsByType", emailsByType);
            response.put("testData", Map.of(
                "mockUser", Map.of(
                    "username", mockUser.getUsername(),
                    "email", mockUser.getEmail(),
                    "userId", mockUser.getUserId(),
                    "department", mockUser.getDepartment()
                ),
                "mockTicket", Map.of(
                    "ticketId", mockTicket.getTicketId(),
                    "emergencyType", mockTicket.getEmergencyType(),
                    "description", mockTicket.getDescription(),
                    "dateCreated", mockTicket.getDateCreated()
                )
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send anomaly emails: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test endpoint to send a specific anomaly type to all admin users
     */
    @PostMapping("/send-specific-anomaly/{anomalyType}")
    public ResponseEntity<Map<String, Object>> sendSpecificAnomalyType(@PathVariable String anomalyType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                response.put("success", false);
                response.put("message", "No admin users found in the system");
                return ResponseEntity.badRequest().body(response);
            }

            // Create mock user and ticket data
            User mockUser = createMockUser();
            Ticket mockTicket = createMockTicket();

            // Get anomaly details based on type
            String anomalyDetails = getAnomalyDetailsForType(anomalyType);
            
            if (anomalyDetails == null) {
                response.put("success", false);
                response.put("message", "Invalid anomaly type: " + anomalyType);
                response.put("validTypes", List.of("FREQUENT_REQUESTS", "DORMANT_USER_ACTIVITY", "OFF_HOURS_ACTIVITY"));
                return ResponseEntity.badRequest().body(response);
            }

            int emailsSent = sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, anomalyType, anomalyDetails);

            response.put("success", true);
            response.put("message", "Anomaly emails sent successfully for type: " + anomalyType);
            response.put("anomalyType", anomalyType);
            response.put("adminCount", adminUsers.size());
            response.put("emailsSent", emailsSent);
            response.put("anomalyDetails", anomalyDetails);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send anomaly emails: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get information about admin users who will receive notifications
     */
    @GetMapping("/admin-info")
    public ResponseEntity<Map<String, Object>> getAdminInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            response.put("success", true);
            response.put("adminCount", adminUsers.size());
            response.put("adminUsers", adminUsers.stream().map(admin -> Map.of(
                "username", admin.getUsername(),
                "email", admin.getEmail(),
                "department", admin.getDepartment() != null ? admin.getDepartment() : "N/A",
                "isAuthorized", admin.getIsAuthorized()
            )).toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get admin info: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test endpoint to send a single anomaly email to a specific admin
     */
    @PostMapping("/send-to-specific-admin")
    public ResponseEntity<Map<String, Object>> sendToSpecificAdmin(
            @RequestParam String adminEmail,
            @RequestParam String anomalyType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find the specific admin
            User admin = userRepository.findByEmail(adminEmail)
                .orElse(null);
            
            if (admin == null || !admin.getIsAdmin()) {
                response.put("success", false);
                response.put("message", "Admin user not found with email: " + adminEmail);
                return ResponseEntity.badRequest().body(response);
            }

            // Create mock data
            User mockUser = createMockUser();
            Ticket mockTicket = createMockTicket();
            String anomalyDetails = getAnomalyDetailsForType(anomalyType);
            
            if (anomalyDetails == null) {
                response.put("success", false);
                response.put("message", "Invalid anomaly type: " + anomalyType);
                return ResponseEntity.badRequest().body(response);
            }

            // Determine risk level
            String riskLevel = determineRiskLevel(anomalyType);

            // Send email
            emailService.sendAnomalyDetectionNotificationEmail(
                admin.getEmail(),
                mockUser,
                mockTicket,
                anomalyType,
                anomalyDetails,
                riskLevel
            );

            response.put("success", true);
            response.put("message", "Anomaly email sent successfully");
            response.put("recipient", Map.of(
                "username", admin.getUsername(),
                "email", admin.getEmail()
            ));
            response.put("anomalyType", anomalyType);
            response.put("riskLevel", riskLevel);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send anomaly email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Helper method to send anomaly emails to all admin users
     */
    private int sendAnomalyEmailsToAdmins(List<User> adminUsers, User mockUser, Ticket mockTicket, 
                                        String anomalyType, String anomalyDetails) {
        int emailsSent = 0;
        String riskLevel = determineRiskLevel(anomalyType);

        for (User admin : adminUsers) {
            try {
                emailService.sendAnomalyDetectionNotificationEmail(
                    admin.getEmail(),
                    mockUser,
                    mockTicket,
                    anomalyType,
                    anomalyDetails,
                    riskLevel
                );
                emailsSent++;
                System.out.println("✅ TEST EMAIL: Sent " + anomalyType + " anomaly email to " + admin.getEmail());
            } catch (Exception e) {
                System.err.println("❌ TEST EMAIL: Failed to send " + anomalyType + " email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }

        return emailsSent;
    }

    /**
     * Create mock user data for testing
     */
    private User createMockUser() {
        User mockUser = new User();
        mockUser.setUserId("test-user-123");
        mockUser.setUsername("john.doe");
        mockUser.setEmail("john.doe@company.com");
        mockUser.setDepartment("IT Security");
        mockUser.setRole("Employee");
        mockUser.setIsAuthorized(true);
        mockUser.setIsAdmin(false);
        mockUser.setDolibarrId("12345");
        return mockUser;
    }

    /**
     * Create mock ticket data for testing
     */
    private Ticket createMockTicket() {
        Ticket mockTicket = new Ticket();
        mockTicket.setTicketId("TICKET-TEST-001");
        mockTicket.setUserId("test-user-123");
        mockTicket.setEmergencyType("Fire Emergency");
        mockTicket.setDescription("Smoke detected in server room - immediate evacuation required");
        mockTicket.setStatus("ACTIVE");
        mockTicket.setDateCreated(LocalDateTime.now().minusMinutes(5));
        mockTicket.setDuration(60);
        mockTicket.setEmergencyContact("Emergency Services: 911");
        return mockTicket;
    }

    /**
     * Get anomaly details based on type
     */
    private String getAnomalyDetailsForType(String anomalyType) {
        return switch (anomalyType) {
            case "FREQUENT_REQUESTS" -> "User has made 8 requests in the last hour (threshold: 5)";
            case "DORMANT_USER_ACTIVITY" -> "User was dormant for 5+ days, logged in at " + 
                LocalDateTime.now().minusMinutes(10) + " and made 3 actions within 15 minutes";
            case "OFF_HOURS_ACTIVITY" -> "User made a request at 23:00 which is outside of regular work hours! (allowed: 7:00 AM - 17:00 PM)";
            default -> null;
        };
    }

    /**
     * Determine risk level based on anomaly type
     */
    private String determineRiskLevel(String anomalyType) {
        return switch (anomalyType) {
            case "DORMANT_USER_ACTIVITY" -> "HIGH";
            case "FREQUENT_REQUESTS" -> "MEDIUM";
            case "OFF_HOURS_ACTIVITY" -> "LOW";
            default -> "MEDIUM";
        };
    }
}
