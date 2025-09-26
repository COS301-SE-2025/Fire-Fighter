package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.anomaly.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.List;

@Service
public class AnomalyNotificationService {

    private final GmailEmailService emailService;
    private final UserRepository userRepository;
    private final AnomalyDetectionService anomalyDetectionService;
    private final UserAnomalyNotificationService userAnomalyNotificationService;

    @Autowired
    public AnomalyNotificationService(GmailEmailService emailService, 
                                    UserRepository userRepository,
                                    AnomalyDetectionService anomalyDetectionService,
                                    UserAnomalyNotificationService userAnomalyNotificationService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.anomalyDetectionService = anomalyDetectionService;
        this.userAnomalyNotificationService = userAnomalyNotificationService;
    }

    /**
     * Notifies all admin users about detected anomalous behavior
     * 
     * @param user The user who exhibited anomalous behavior
     * @param ticket The ticket that triggered the anomaly detection
     * @param anomalyType The type of anomaly detected
     * @param anomalyDetails Detailed description of the anomaly
     */
    public void notifyAdminsOfAnomaly(User user, Ticket ticket, String anomalyType, String anomalyDetails) {
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                System.out.println("⚠️ ANOMALY NOTIFICATION: No admin users found to notify");
                return;
            }

            String riskLevel = determineRiskLevel(anomalyType);
            System.out.println("🚨 ANOMALY NOTIFICATION: " + anomalyType + " detected (" + riskLevel + " risk) - sending notifications to " + adminUsers.size() + " admin(s)");
            
            // Create notification for the user to see in frontend
            userAnomalyNotificationService.notifyUserOfAnomaly(user, anomalyType, anomalyDetails);
            
            // Send notification to each admin
            for (User admin : adminUsers) {
                try {
                    emailService.sendAnomalyDetectionNotificationEmail(
                        admin.getEmail(),
                        user,
                        ticket,
                        anomalyType,
                        anomalyDetails,
                        riskLevel
                    );
                    System.out.println("✅ ANOMALY NOTIFICATION: Sent " + anomalyType + " notification to admin " + admin.getUsername() + " (" + admin.getEmail() + ")");
                } catch (MessagingException e) {
                    System.err.println("❌ ANOMALY NOTIFICATION: Failed to send notification to admin " + admin.getUsername() + ": " + e.getMessage());
                    // Continue with other admins even if one fails
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ ANOMALY NOTIFICATION: Failed to notify admins of anomaly: " + e.getMessage());
        }
    }

    /**
     * Comprehensive anomaly check and notification for a user after ticket creation
     * 
     * @param user The user to check for anomalies
     * @param ticket The ticket that was created
     */
    public void checkAndNotifyAnomalies(User user, Ticket ticket) {
        try {
            if (user == null) {
                System.err.println("❌ ANOMALY NOTIFICATION: Cannot check anomalies for null user");
                return;
            }
            
            String userId = user.getUserId();
            
            // Check for frequent request anomaly
            String frequencyDetails = anomalyDetectionService.getRequestFrequencyDetails(userId);
            if (frequencyDetails != null) {
                notifyAdminsOfAnomaly(user, ticket, "FREQUENT_REQUESTS", frequencyDetails);
            }
            
            // Check for dormant user anomaly
            String dormantDetails = anomalyDetectionService.getDormantUserAnomalyDetails(userId);
            if (dormantDetails != null) {
                notifyAdminsOfAnomaly(user, ticket, "DORMANT_USER_ACTIVITY", dormantDetails);
            }
            
            // Check for off-hours anomaly
            String offHoursDetails = anomalyDetectionService.getOffHoursAnomalyDetails(userId);
            if (offHoursDetails != null) {
                notifyAdminsOfAnomaly(user, ticket, "OFF_HOURS_ACTIVITY", offHoursDetails);
            }
            
        } catch (Exception e) {
            String username = (user != null) ? user.getUsername() : "unknown";
            System.err.println("❌ ANOMALY NOTIFICATION: Failed to check and notify anomalies for user " + username + ": " + e.getMessage());
        }
    }

    /**
     * Determines the risk level based on the anomaly type
     * 
     * @param anomalyType The type of anomaly detected
     * @return The risk level (HIGH, MEDIUM, LOW)
     */
    private String determineRiskLevel(String anomalyType) {
        return switch (anomalyType) {
            case "DORMANT_USER_ACTIVITY" -> "HIGH";     // Account takeover risk
            case "FREQUENT_REQUESTS" -> "MEDIUM";       // Potential abuse or automation
            case "OFF_HOURS_ACTIVITY" -> "LOW";         // Unusual but not necessarily malicious
            default -> "MEDIUM";
        };
    }

    /**
     * Get a human-readable description of the anomaly type
     * 
     * @param anomalyType The anomaly type code
     * @return Human-readable description
     */
    public String getAnomalyTypeDescription(String anomalyType) {
        if (anomalyType == null) {
            return "Unknown Anomaly Type";
        }
        
        return switch (anomalyType) {
            case "FREQUENT_REQUESTS" -> "Excessive Request Frequency";
            case "DORMANT_USER_ACTIVITY" -> "Dormant Account Sudden Activity";
            case "OFF_HOURS_ACTIVITY" -> "Off-Hours System Access";
            default -> "Unknown Anomaly Type";
        };
    }

    /**
     * Get the count of admin users who will receive notifications
     * 
     * @return The number of admin users
     */
    public long getAdminNotificationCount() {
        return userRepository.findByIsAdminTrue().size();
    }
}
