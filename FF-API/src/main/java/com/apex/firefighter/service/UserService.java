package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.service.auth.AuthorizationService;
import com.apex.firefighter.service.role.RoleService;
import com.apex.firefighter.service.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserService serves as a facade for user-related operations.
 * This service delegates to specialized services for:
 * - Authentication (AuthenticationService)
 * - Authorization (AuthorizationService) 
 * - Role Management (RoleService)
 * - User Profile Management (UserProfileService)
 * 
 * This provides backward compatibility while maintaining modular architecture.
 */
@Service
@Transactional
public class UserService {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final RoleService roleService;
    private final UserProfileService userProfileService;

    @Autowired
    public UserService(AuthenticationService authenticationService,
                      AuthorizationService authorizationService,
                      RoleService roleService,
                      UserProfileService userProfileService) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.roleService = roleService;
        this.userProfileService = userProfileService;
    }

    // DELEGATION METHODS FOR BACKWARD COMPATIBILITY

    /**
     * Find user by Firebase UID - Delegates to AuthenticationService
     */
    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return authenticationService.getUserByFirebaseUid(firebaseUid);
    }

    /**
     * FIREBASE USER VERIFICATION - Delegates to AuthenticationService
     */
    public User verifyOrCreateUser(String firebaseUid, String username, String email, String department) {
        return authenticationService.verifyOrCreateUser(firebaseUid, username, email, department);
    }

    /**
     * USER AUTHORIZATION CHECK - Delegates to AuthorizationService
     */
    public boolean isUserAuthorized(String firebaseUid) {
        return authorizationService.isUserAuthorized(firebaseUid);
    }

    /**
     * ROLE VERIFICATION - Delegates to AuthorizationService
     */
    public boolean hasRole(String firebaseUid, String roleName) {
        return authorizationService.hasRole(firebaseUid, roleName);
    }

    /**
     * ADMIN OPERATIONS - Delegates to AuthorizationService
     */
    public User authorizeUser(String firebaseUid, String authorizedBy) {
        return authorizationService.authorizeUser(firebaseUid, authorizedBy);
    }

    public User revokeUserAuthorization(String firebaseUid, String revokedBy) {
        return authorizationService.revokeUserAuthorization(firebaseUid, revokedBy);
    }

    /**
     * ROLE MANAGEMENT - Delegates to RoleService
     */
    public User assignRole(String firebaseUid, String roleName, String assignedBy) {
        return roleService.assignRole(firebaseUid, roleName, assignedBy);
    }

    /**
     * QUERY OPERATIONS - Delegates to UserProfileService
     */
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return authenticationService.getUserByFirebaseUid(firebaseUid);
    }

    public Optional<User> getUserByEmail(String email) {
        return userProfileService.getUserByEmail(email);
    }

    public List<User> getAuthorizedUsers() {
        return userProfileService.getAuthorizedUsers();
    }

    public List<User> getUsersByDepartment(String department) {
        return userProfileService.getUsersByDepartment(department);
    }

    public List<User> getUsersByRole(String roleName) {
        return roleService.getUsersByRole(roleName);
    }

    public List<User> getAuthorizedUsersByRole(String roleName) {
        return roleService.getAuthorizedUsersByRole(roleName);
    }

    /**
     * COMPREHENSIVE USER INFO - Delegates to UserProfileService
     */
    public Optional<User> getUserWithRoles(String firebaseUid) {
        return userProfileService.getUserWithRoles(firebaseUid);
    }

    // ADDITIONAL METHODS EXPOSING MODULAR FUNCTIONALITY

    /**
     * Update user profile - Delegates to UserProfileService
     */
    public User updateUserProfile(String firebaseUid, String username, String email, String department) {
        return userProfileService.updateUserProfile(firebaseUid, username, email, department);
    }

    /**
     * Update user contact number - Delegates to UserProfileService
     */
    public User updateContactNumber(String firebaseUid, String contactNumber) {
        return userProfileService.updateContactNumber(firebaseUid, contactNumber);
    }

    /**
     * Remove role from user - Delegates to RoleService
     */
    public User removeRole(String firebaseUid, String roleName) {
        return roleService.removeRole(firebaseUid, roleName);
    }

    /**
     * Get all users - Delegates to UserProfileService
     */
    public List<User> getAllUsers() {
        return userProfileService.getAllUsers();
    }

    /**
     * Check if user exists - Delegates to AuthenticationService
     */
    public boolean userExists(String firebaseUid) {
        return authenticationService.userExists(firebaseUid);
    }

    /**
     * Get user count - Delegates to UserProfileService
     */
    public long getUserCount() {
        return userProfileService.getUserCount();
    }

    /**
     * Get authorized user count - Delegates to UserProfileService
     */
    public long getAuthorizedUserCount() {
        return userProfileService.getAuthorizedUserCount();
    }

    // REMOVED: User self-management Dolibarr UID methods
    // These methods have been removed for security reasons.
    // Only administrators can now manage Dolibarr UIDs through admin-only service methods.

    /**
     * ADMIN-ONLY DOLIBARR UID MANAGEMENT
     */

    /**
     * Update user Dolibarr ID as admin - Delegates to UserProfileService with admin check
     */
    public User updateUserDolibarrIdAsAdmin(String adminFirebaseUid, String targetFirebaseUid, String dolibarrId) {
        return userProfileService.updateUserDolibarrIdAsAdmin(adminFirebaseUid, targetFirebaseUid, dolibarrId);
    }

    /**
     * Get user Dolibarr ID as admin - Delegates to UserProfileService with admin check
     */
    public String getUserDolibarrIdAsAdmin(String adminFirebaseUid, String targetFirebaseUid) {
        return userProfileService.getUserDolibarrIdAsAdmin(adminFirebaseUid, targetFirebaseUid);
    }

    /**
     * Get all users as admin - Delegates to UserProfileService with admin check
     */
    public Map<String, Object> getAllUsersAsAdmin(String adminFirebaseUid) {
        return userProfileService.getAllUsersAsAdmin(adminFirebaseUid);
    }
} 