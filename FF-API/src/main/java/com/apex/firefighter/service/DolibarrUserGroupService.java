package com.apex.firefighter.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DolibarrUserGroupService {
    private final DolibarrDatabaseService dolibarrDatabaseService;
    private final DolibarrGroupAllocater groupAllocater;

    public DolibarrUserGroupService(DolibarrDatabaseService dolibarrDatabaseService, DolibarrGroupAllocater groupAllocater) {
        this.dolibarrDatabaseService = dolibarrDatabaseService;
        this.groupAllocater = groupAllocater;
        System.out.println("✅ DolibarrUserGroupService initialized");
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
    public void addUserToGroup(String userId, String description) throws SQLException {
        try {
            Integer firefighterGroupId = groupAllocater.allocateByDescription(description);
            System.out.println("🔵 DOLIBARR SERVICE: Starting to add user " + userId + " to firefighter group " + firefighterGroupId);

            dolibarrDatabaseService.addUserToFirefighterGroup(userId, firefighterGroupId);
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
    public void removeUserFromGroup(String userId, String description) throws SQLException {
        try {
            Integer firefighterGroupId = groupAllocater.allocateByDescription(description);
            dolibarrDatabaseService.removeUserFromFirefighterGroup(userId, firefighterGroupId);
            System.out.println("✅ DOLIBARR SERVICE: Successfully removed user " + userId + " from firefighter group " + firefighterGroupId);
        } catch (SQLException e) {
            System.err.println("❌ DOLIBARR SERVICE: Failed to remove user " + userId + " from firefighter group: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ DOLIBARR SERVICE: Unexpected error removing user " + userId + " from firefighter group: " + e.getMessage());
            throw new RuntimeException("Failed to remove user from firefighter group", e);
        }
    }

}
