package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DolibarrUserGroupService {
    private final DolibarrDatabaseService dolibarrDatabaseService;
    private final DolibarrGroupAllocater groupAllocater;
    private final GroupChangeNotificationService notificationService;
    private final UserRepository userRepository;

    public DolibarrUserGroupService(DolibarrDatabaseService dolibarrDatabaseService, 
                                  DolibarrGroupAllocater groupAllocater,
                                  GroupChangeNotificationService notificationService,
                                  UserRepository userRepository) {
        this.dolibarrDatabaseService = dolibarrDatabaseService;
        this.groupAllocater = groupAllocater;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        System.out.println("✅ DolibarrUserGroupService initialized with notification support");
    }

    /**
     * Adds a user to the firefighter group using direct database access.
     * This method uses the DolibarrDatabaseService to directly insert the user-group
     * association into the llx_usergroup_user table in the Dolibarr database.
     * The entity ID is set to 1 as requested.
     *
     * @param userId The Dolibarr user ID
     * @param description The emergency description that determines the group
     * @throws SQLException if database operation fails
     */
    public void addUserToGroup(String userId, String description) throws SQLException {
        addUserToGroup(userId, description, null);
    }

    /**
     * Adds a user to the firefighter group and sends notifications to admins
     * 
     * @param userId The Dolibarr user ID
     * @param description The emergency description that determines the group
     * @param ticketId The ticket ID that triggered this group change (for notifications)
     * @throws SQLException if database operation fails
     */
    public void addUserToGroup(String userId, String description, String ticketId) throws SQLException {
        try {
            Integer firefighterGroupId = groupAllocater.allocateByDescription(description);
            System.out.println("🔵 DOLIBARR SERVICE: Starting to add user " + userId + " to firefighter group " + firefighterGroupId);

            dolibarrDatabaseService.addUserToFirefighterGroup(userId, firefighterGroupId);
            System.out.println("✅ DOLIBARR SERVICE: Successfully added user " + userId + " to firefighter group using database method");

            // Send notification to admins if ticketId is provided
            if (ticketId != null) {
                notifyAdminsOfGroupChange(userId, ticketId, null, firefighterGroupId, description);
            }
        } catch (SQLException e) {
            System.err.println("❌ DOLIBARR SERVICE: Failed to add user " + userId + " to firefighter group: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ DOLIBARR SERVICE: Unexpected error adding user " + userId + " to firefighter group: " + e.getMessage());
            throw new RuntimeException("Failed to add user to firefighter group", e);
        }
    }

    /**
     * Removes a user from the firefighter group using direct database access.
     * This method uses the DolibarrDatabaseService to directly delete the user-group
     * association from the llx_usergroup_user table in the Dolibarr database.
     *
     * @param userId The Dolibarr user ID
     * @param description The emergency description that determines the group
     * @throws SQLException if database operation fails
     */
    public void removeUserFromGroup(String userId, String description) throws SQLException {
        removeUserFromGroup(userId, description, null);
    }

    /**
     * Removes a user from the firefighter group and sends notifications to admins
     * 
     * @param userId The Dolibarr user ID
     * @param description The emergency description that determines the group
     * @param ticketId The ticket ID that triggered this group change (for notifications)
     * @throws SQLException if database operation fails
     */
    public void removeUserFromGroup(String userId, String description, String ticketId) throws SQLException {
        try {
            Integer firefighterGroupId = groupAllocater.allocateByDescription(description);
            dolibarrDatabaseService.removeUserFromFirefighterGroup(userId, firefighterGroupId);
            System.out.println("✅ DOLIBARR SERVICE: Successfully removed user " + userId + " from firefighter group " + firefighterGroupId);

            // Send notification to admins if ticketId is provided
            if (ticketId != null) {
                notifyAdminsOfGroupChange(userId, ticketId, firefighterGroupId, null, "Group removed - " + description);
            }
        } catch (SQLException e) {
            System.err.println("❌ DOLIBARR SERVICE: Failed to remove user " + userId + " from firefighter group: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ DOLIBARR SERVICE: Unexpected error removing user " + userId + " from firefighter group: " + e.getMessage());
            throw new RuntimeException("Failed to remove user from firefighter group", e);
        }
    }

    /**
     * Helper method to notify admins of group changes
     * 
     * @param dolibarrUserId The Dolibarr user ID
     * @param ticketId The ticket that triggered the change
     * @param oldGroupId The previous group ID (null if no previous group)
     * @param newGroupId The new group ID (null if group was removed)
     * @param reason The reason for the change
     */
    private void notifyAdminsOfGroupChange(String dolibarrUserId, String ticketId, Integer oldGroupId, Integer newGroupId, String reason) {
        try {
            // Find the user by Dolibarr ID
            Optional<User> userOpt = userRepository.findAll().stream()
                .filter(user -> dolibarrUserId.equals(user.getDolibarrId()))
                .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                notificationService.notifyAdminsOfGroupChangeById(user, ticketId, oldGroupId, newGroupId, reason);
            } else {
                System.err.println("⚠️ DOLIBARR SERVICE: Could not find user with Dolibarr ID " + dolibarrUserId + " for notification");
            }
        } catch (Exception e) {
            System.err.println("❌ DOLIBARR SERVICE: Failed to send group change notification: " + e.getMessage());
            // Don't throw the exception as this is a notification failure, not a core operation failure
        }
    }

}
