package com.apex.firefighter.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DolibarrUserGroupService {
    private final String firefighterGroupId;
    private final DolibarrDatabaseService dolibarrDatabaseService;

    public DolibarrUserGroupService(
            @Value("${dolibarr.ff.hr.id}") String firefighterGroupId,
            DolibarrDatabaseService dolibarrDatabaseService) {
        this.firefighterGroupId = firefighterGroupId;
        this.dolibarrDatabaseService = dolibarrDatabaseService;

        System.out.println("✅ DolibarrUserGroupService initialized with firefighter group ID: " + firefighterGroupId);
    }

    /**
     * Adds a user to the firefighter group using direct database access.
     * This method uses the DolibarrDatabaseService to directly insert the user-group
     * association into the llx_usergroup_user table in the Dolibarr database.
     * The entity ID is set to 1 as requested.
     *
     * @param userId The Dolibarr user ID
     * @throws SQLException if database operation fails
     */
    public void addUserToGroup(String userId) throws SQLException {
        System.out.println("🔵 DOLIBARR SERVICE: Starting to add user " + userId + " to firefighter group " + firefighterGroupId);

        try {
            dolibarrDatabaseService.addUserToFirefighterGroup(userId);
            System.out.println("✅ DOLIBARR SERVICE: Successfully added user " + userId + " to firefighter group using database method");
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
     * @throws SQLException if database operation fails
     */
    public void removeUserFromGroup(String userId) throws SQLException {
        try {
            dolibarrDatabaseService.removeUserFromFirefighterGroup(userId);
            System.out.println("✅ DOLIBARR SERVICE: Successfully removed user " + userId + " from firefighter group");
        } catch (SQLException e) {
            System.err.println("❌ DOLIBARR SERVICE: Failed to remove user " + userId + " from firefighter group: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ DOLIBARR SERVICE: Unexpected error removing user " + userId + " from firefighter group: " + e.getMessage());
            throw new RuntimeException("Failed to remove user from firefighter group", e);
        }
    }

}
