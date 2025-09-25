package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for testing all types of email notifications with mock data
 * This service provides comprehensive email testing capabilities for the FireFighter platform
 */
@Service
public class EmailTestingService {

    private final GmailEmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public EmailTestingService(GmailEmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Send all types of anomaly detection emails to all admin users
     */
    public EmailTestResult sendAllAnomalyEmails() {
        EmailTestResult result = new EmailTestResult();
        
        try {
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("No admin users found in the system");
                return result;
            }

            User mockUser = createMockUser("anomaly.test", "Anomaly Test User", "Security");
            Ticket mockTicket = createMockTicket("ANOMALY-TEST");

            // Test all anomaly types
            Map<String, Integer> emailCounts = new HashMap<>();
            
            // Frequent Requests Anomaly
            int frequentCount = sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "FREQUENT_REQUESTS", "User has made 8 requests in the last hour (threshold: 5)");
            emailCounts.put("FREQUENT_REQUESTS", frequentCount);

            // Dormant User Activity Anomaly
            int dormantCount = sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "DORMANT_USER_ACTIVITY", "User was dormant for 5+ days, logged in at " + 
                LocalDateTime.now().minusMinutes(10) + " and made 3 actions within 15 minutes");
            emailCounts.put("DORMANT_USER_ACTIVITY", dormantCount);

            // Off-Hours Activity Anomaly
            int offHoursCount = sendAnomalyEmailsToAdmins(adminUsers, mockUser, mockTicket, 
                "OFF_HOURS_ACTIVITY", "User made a request at 23:00 which is outside of regular work hours! (allowed: 7:00 AM - 17:00 PM)");
            emailCounts.put("OFF_HOURS_ACTIVITY", offHoursCount);

            result.setSuccess(true);
            result.setMessage("All anomaly emails sent successfully");
            result.setAdminCount(adminUsers.size());
            result.setTotalEmailsSent(frequentCount + dormantCount + offHoursCount);
            result.setEmailCounts(emailCounts);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Failed to send anomaly emails: " + e.getMessage());
            result.setError(e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Send all types of group change notification emails to all admin users
     */
    public EmailTestResult sendAllGroupChangeEmails() {
        EmailTestResult result = new EmailTestResult();
        
        try {
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("No admin users found in the system");
                return result;
            }

            Map<String, Integer> emailCounts = new HashMap<>();
            int totalEmails = 0;

            // HIGH RISK - Financial Emergency Group
            User financialUser = createMockUser("financial.test", "Financial Test User", "Finance");
            int financialCount = sendGroupChangeEmailsToAdmins(adminUsers, financialUser, 
                "TICKET-FIN-TEST", "None", "Financial Emergency Group", 
                "Emergency ticket creation - Financial system access", "HIGH");
            emailCounts.put("HIGH_RISK_FINANCIAL", financialCount);
            totalEmails += financialCount;

            // HIGH RISK - Management Emergency Group
            User managementUser = createMockUser("management.test", "Management Test User", "Management");
            int managementCount = sendGroupChangeEmailsToAdmins(adminUsers, managementUser, 
                "TICKET-MGT-TEST", "None", "Management Emergency Group", 
                "Emergency ticket creation - Management system access", "HIGH");
            emailCounts.put("HIGH_RISK_MANAGEMENT", managementCount);
            totalEmails += managementCount;

            // MEDIUM RISK - HR Emergency Group
            User hrUser = createMockUser("hr.test", "HR Test User", "Human Resources");
            int hrCount = sendGroupChangeEmailsToAdmins(adminUsers, hrUser, 
                "TICKET-HR-TEST", "None", "HR Emergency Group", 
                "Emergency ticket creation - HR system access", "MEDIUM");
            emailCounts.put("MEDIUM_RISK_HR", hrCount);
            totalEmails += hrCount;

            result.setSuccess(true);
            result.setMessage("All group change emails sent successfully");
            result.setAdminCount(adminUsers.size());
            result.setTotalEmailsSent(totalEmails);
            result.setEmailCounts(emailCounts);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Failed to send group change emails: " + e.getMessage());
            result.setError(e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Send all types of ticket notification emails to all admin users
     */
    public EmailTestResult sendAllTicketEmails() {
        EmailTestResult result = new EmailTestResult();
        
        try {
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("No admin users found in the system");
                return result;
            }

            User mockUser = createMockUser("ticket.test", "Ticket Test User", "Operations");
            Map<String, Integer> emailCounts = new HashMap<>();
            int totalEmails = 0;

            // Ticket Creation Emails
            Ticket creationTicket = createMockTicket("TICKET-CREATE-TEST");
            int creationCount = sendTicketEmailsToAdmins(adminUsers, mockUser, creationTicket, "CREATION");
            emailCounts.put("TICKET_CREATION", creationCount);
            totalEmails += creationCount;

            // Ticket Completion Emails
            Ticket completionTicket = createMockTicket("TICKET-COMPLETE-TEST");
            completionTicket.setStatus("COMPLETED");
            completionTicket.setDateCompleted(LocalDateTime.now());
            int completionCount = sendTicketEmailsToAdmins(adminUsers, mockUser, completionTicket, "COMPLETION");
            emailCounts.put("TICKET_COMPLETION", completionCount);
            totalEmails += completionCount;

            // Ticket Revocation Emails
            Ticket revocationTicket = createMockTicket("TICKET-REVOKE-TEST");
            revocationTicket.setStatus("REVOKED");
            int revocationCount = sendTicketEmailsToAdmins(adminUsers, mockUser, revocationTicket, "REVOCATION");
            emailCounts.put("TICKET_REVOCATION", revocationCount);
            totalEmails += revocationCount;

            // Five Minute Warning Emails
            Ticket warningTicket = createMockTicket("TICKET-WARNING-TEST");
            warningTicket.setDateCreated(LocalDateTime.now().minusMinutes(55)); // Almost expired
            int warningCount = sendTicketEmailsToAdmins(adminUsers, mockUser, warningTicket, "WARNING");
            emailCounts.put("TICKET_WARNING", warningCount);
            totalEmails += warningCount;

            result.setSuccess(true);
            result.setMessage("All ticket notification emails sent successfully");
            result.setAdminCount(adminUsers.size());
            result.setTotalEmailsSent(totalEmails);
            result.setEmailCounts(emailCounts);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Failed to send ticket emails: " + e.getMessage());
            result.setError(e.getClass().getSimpleName());
        }

        return result;
    }

    /**
     * Send comprehensive test of all email types
     */
    public EmailTestResult sendComprehensiveEmailTest() {
        EmailTestResult result = new EmailTestResult();
        
        try {
            System.out.println("🚀 COMPREHENSIVE EMAIL TEST: Starting complete email system test...");
            
            // Test anomaly emails
            EmailTestResult anomalyResult = sendAllAnomalyEmails();
            
            // Test group change emails
            EmailTestResult groupChangeResult = sendAllGroupChangeEmails();
            
            // Test ticket emails
            EmailTestResult ticketResult = sendAllTicketEmails();

            // Combine results
            Map<String, Object> combinedResults = new HashMap<>();
            combinedResults.put("anomalyEmails", Map.of(
                "success", anomalyResult.isSuccess(),
                "emailsSent", anomalyResult.getTotalEmailsSent(),
                "details", anomalyResult.getEmailCounts()
            ));
            combinedResults.put("groupChangeEmails", Map.of(
                "success", groupChangeResult.isSuccess(),
                "emailsSent", groupChangeResult.getTotalEmailsSent(),
                "details", groupChangeResult.getEmailCounts()
            ));
            combinedResults.put("ticketEmails", Map.of(
                "success", ticketResult.isSuccess(),
                "emailsSent", ticketResult.getTotalEmailsSent(),
                "details", ticketResult.getEmailCounts()
            ));

            int totalEmails = anomalyResult.getTotalEmailsSent() + 
                            groupChangeResult.getTotalEmailsSent() + 
                            ticketResult.getTotalEmailsSent();

            boolean allSuccessful = anomalyResult.isSuccess() && 
                                  groupChangeResult.isSuccess() && 
                                  ticketResult.isSuccess();

            result.setSuccess(allSuccessful);
            result.setMessage(allSuccessful ? 
                "Comprehensive email test completed successfully" : 
                "Some email tests failed - check individual results");
            result.setTotalEmailsSent(totalEmails);
            result.setAdminCount(Math.max(anomalyResult.getAdminCount(), 
                                Math.max(groupChangeResult.getAdminCount(), ticketResult.getAdminCount())));
            result.setAdditionalData(combinedResults);

            System.out.println("✅ COMPREHENSIVE EMAIL TEST: Completed - " + totalEmails + " emails sent");

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Comprehensive email test failed: " + e.getMessage());
            result.setError(e.getClass().getSimpleName());
            System.err.println("❌ COMPREHENSIVE EMAIL TEST: Failed - " + e.getMessage());
        }

        return result;
    }

    /**
     * Get admin user information for testing
     */
    public Map<String, Object> getAdminUserInfo() {
        List<User> adminUsers = userRepository.findByIsAdminTrue();
        
        return Map.of(
            "adminCount", adminUsers.size(),
            "adminUsers", adminUsers.stream().map(admin -> Map.of(
                "username", admin.getUsername(),
                "email", admin.getEmail(),
                "department", admin.getDepartment() != null ? admin.getDepartment() : "N/A",
                "isAuthorized", admin.getIsAuthorized()
            )).toList()
        );
    }

    // Helper methods

    private int sendAnomalyEmailsToAdmins(List<User> adminUsers, User user, Ticket ticket, 
                                        String anomalyType, String anomalyDetails) {
        int emailsSent = 0;
        String riskLevel = determineAnomalyRiskLevel(anomalyType);

        for (User admin : adminUsers) {
            try {
                emailService.sendAnomalyDetectionNotificationEmail(
                    admin.getEmail(), user, ticket, anomalyType, anomalyDetails, riskLevel);
                emailsSent++;
            } catch (Exception e) {
                System.err.println("Failed to send anomaly email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }

        return emailsSent;
    }

    private int sendGroupChangeEmailsToAdmins(List<User> adminUsers, User user, String ticketId, 
                                            String oldGroup, String newGroup, String reason, String riskLevel) {
        int emailsSent = 0;

        for (User admin : adminUsers) {
            try {
                emailService.sendSuspiciousGroupChangeNotificationEmail(
                    admin.getEmail(), user, ticketId, oldGroup, newGroup, reason, riskLevel);
                emailsSent++;
            } catch (Exception e) {
                System.err.println("Failed to send group change email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }

        return emailsSent;
    }

    private int sendTicketEmailsToAdmins(List<User> adminUsers, User user, Ticket ticket, String emailType) {
        int emailsSent = 0;

        for (User admin : adminUsers) {
            try {
                switch (emailType) {
                    case "CREATION" -> emailService.sendTicketCreationEmail(admin.getEmail(), ticket, user);
                    case "COMPLETION" -> emailService.sendTicketCompletionEmail(admin.getEmail(), ticket, user);
                    case "REVOCATION" -> emailService.sendTicketRevocationEmail(admin.getEmail(), ticket, user, "Test revocation reason");
                    case "WARNING" -> emailService.sendFiveMinuteWarningEmail(admin.getEmail(), ticket, user);
                }
                emailsSent++;
            } catch (Exception e) {
                System.err.println("Failed to send " + emailType + " email to " + admin.getEmail() + ": " + e.getMessage());
            }
        }

        return emailsSent;
    }

    private User createMockUser(String username, String displayName, String department) {
        User user = new User();
        user.setUserId("test-" + username + "-" + System.currentTimeMillis());
        user.setUsername(username);
        user.setEmail(username + "@company.com");
        user.setDepartment(department);
        user.setRole("Employee");
        user.setIsAuthorized(true);
        user.setIsAdmin(false);
        user.setDolibarrId(String.valueOf((int) (Math.random() * 100000)));
        return user;
    }

    private Ticket createMockTicket(String ticketPrefix) {
        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketPrefix + "-" + System.currentTimeMillis());
        ticket.setUserId("test-user-123");
        ticket.setEmergencyType("Fire Emergency");
        ticket.setDescription("Test emergency scenario - smoke detected in server room");
        ticket.setStatus("ACTIVE");
        ticket.setDateCreated(LocalDateTime.now().minusMinutes(5));
        ticket.setDuration(60);
        ticket.setEmergencyContact("Emergency Services: 911");
        return ticket;
    }

    private String determineAnomalyRiskLevel(String anomalyType) {
        return switch (anomalyType) {
            case "DORMANT_USER_ACTIVITY" -> "HIGH";
            case "FREQUENT_REQUESTS" -> "MEDIUM";
            case "OFF_HOURS_ACTIVITY" -> "LOW";
            default -> "MEDIUM";
        };
    }

    /**
     * Result class for email testing operations
     */
    public static class EmailTestResult {
        private boolean success;
        private String message;
        private String error;
        private int adminCount;
        private int totalEmailsSent;
        private Map<String, Integer> emailCounts;
        private Map<String, Object> additionalData;

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public int getAdminCount() { return adminCount; }
        public void setAdminCount(int adminCount) { this.adminCount = adminCount; }
        
        public int getTotalEmailsSent() { return totalEmailsSent; }
        public void setTotalEmailsSent(int totalEmailsSent) { this.totalEmailsSent = totalEmailsSent; }
        
        public Map<String, Integer> getEmailCounts() { return emailCounts; }
        public void setEmailCounts(Map<String, Integer> emailCounts) { this.emailCounts = emailCounts; }
        
        public Map<String, Object> getAdditionalData() { return additionalData; }
        public void setAdditionalData(Map<String, Object> additionalData) { this.additionalData = additionalData; }
    }
}
