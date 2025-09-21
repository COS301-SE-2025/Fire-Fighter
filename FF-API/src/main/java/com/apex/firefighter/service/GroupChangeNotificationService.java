package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.util.List;

@Service
public class GroupChangeNotificationService {

    private final GmailEmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public GroupChangeNotificationService(GmailEmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Notifies all admin users about a group change in the Dolibarr system
     * 
     * @param user The user whose group was changed
     * @param ticketId The ticket that triggered the group change
     * @param oldGroup The previous group (can be null if user had no group)
     * @param newGroup The new group assigned to the user
     * @param reason The reason for the group change (e.g., emergency type)
     */
    public void notifyAdminsOfGroupChange(User user, String ticketId, String oldGroup, String newGroup, String reason) {
        try {
            // Get all admin users (only checking isAdmin field)
            List<User> adminUsers = userRepository.findByIsAdminTrue();
            
            if (adminUsers.isEmpty()) {
                System.out.println("⚠️ GROUP CHANGE NOTIFICATION: No admin users found to notify");
                return;
            }

            System.out.println("📧 GROUP CHANGE NOTIFICATION: Notifying " + adminUsers.size() + " admin(s) about group change for user " + user.getUsername());
            
            // Send notification to each admin
            for (User admin : adminUsers) {
                try {
                    emailService.sendGroupChangeNotificationEmail(
                        admin.getEmail(),
                        user,
                        ticketId,
                        oldGroup,
                        newGroup,
                        reason
                    );
                    System.out.println("✅ GROUP CHANGE NOTIFICATION: Sent notification to admin " + admin.getUsername() + " (" + admin.getEmail() + ")");
                } catch (MessagingException e) {
                    System.err.println("❌ GROUP CHANGE NOTIFICATION: Failed to send notification to admin " + admin.getUsername() + ": " + e.getMessage());
                    // Continue with other admins even if one fails
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ GROUP CHANGE NOTIFICATION: Failed to notify admins of group change: " + e.getMessage());
        }
    }

    /**
     * Notifies admins of a group change with automatic group name resolution
     * This method attempts to resolve group names from group IDs
     * 
     * @param user The user whose group was changed
     * @param ticketId The ticket that triggered the group change
     * @param oldGroupId The previous group ID (can be null)
     * @param newGroupId The new group ID
     * @param reason The reason for the group change
     */
    public void notifyAdminsOfGroupChangeById(User user, String ticketId, Integer oldGroupId, Integer newGroupId, String reason) {
        String oldGroup = resolveGroupName(oldGroupId);
        String newGroup = resolveGroupName(newGroupId);
        
        notifyAdminsOfGroupChange(user, ticketId, oldGroup, newGroup, reason);
    }

    /**
     * Resolves a group ID to a human-readable group name
     * This method maps the group IDs used in the Dolibarr system to descriptive names
     * 
     * @param groupId The group ID to resolve
     * @return A human-readable group name
     */
    private String resolveGroupName(Integer groupId) {
        if (groupId == null) {
            return null;
        }
        
        // Map group IDs to human-readable names
        // These mappings should match the configuration in DoliGroupConfig
        return switch (groupId) {
            case 1 -> "HR Emergency Group";
            case 2 -> "Financial Emergency Group";
            case 3 -> "Management Emergency Group";
            case 4 -> "Logistics Emergency Group";
            default -> "Group ID: " + groupId;
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
