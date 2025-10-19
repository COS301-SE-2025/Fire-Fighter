package com.apex.firefighter.service.user;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.registration.RegistrationNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserProfileService handles user profile operations and queries.
 * This service is responsible for:
 * - Getting user information
 * - Updating user profiles
 * - Querying users by various criteria
 */
@Service
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;
    private final RegistrationNotificationService notificationService;

    @Autowired
    public UserProfileService(UserRepository userRepository,
                             RegistrationNotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * COMPREHENSIVE USER INFO
     * Get complete user information including roles
     */
    public Optional<User> getUserWithRoles(String firebaseUid) {
        System.out.println("🔵 GET USER: Fetching complete user info for UID - " + firebaseUid);
        
        Optional<User> user = userRepository.findByUserId(firebaseUid);
        if (user.isPresent()) {
            System.out.println("✅ FOUND USER: " + user.get());
            return user;
        } else {
            System.out.println("❌ USER NOT FOUND: UID - " + firebaseUid);
            return Optional.empty();
        }
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Update user profile information
     */
    public User updateUserProfile(String firebaseUid, String username, String email, String department) {
        System.out.println("🔵 UPDATE PROFILE: Updating profile for UID - " + firebaseUid);

        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (department != null) user.setDepartment(department);

            User updatedUser = userRepository.save(user);
            System.out.println("✅ PROFILE UPDATED: " + updatedUser);
            return updatedUser;
        } else {
            System.out.println("❌ UPDATE FAILED: User not found for UID - " + firebaseUid);
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * Update user contact number
     */
    public User updateContactNumber(String firebaseUid, String contactNumber) {
        System.out.println("🔵 UPDATE CONTACT: Updating contact number for UID - " + firebaseUid);

        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setContactNumber(contactNumber);

            User updatedUser = userRepository.save(user);
            System.out.println("✅ CONTACT UPDATED: " + updatedUser.getContactNumber());
            return updatedUser;
        } else {
            System.out.println("❌ CONTACT UPDATE FAILED: User not found for UID - " + firebaseUid);
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * QUERY OPERATIONS
     */
    public List<User> getAuthorizedUsers() {
        return userRepository.findByIsAuthorizedTrue();
    }

    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    public List<User> getUsersByAuthorizationStatus(Boolean isAuthorized) {
        return userRepository.findByIsAuthorized(isAuthorized);
    }

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Check if user exists by email
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if user exists by username
     */
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Get user count
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Get authorized user count
     */
    public long getAuthorizedUserCount() {
        return userRepository.findByIsAuthorizedTrue().size();
    }

    // REMOVED: User self-management Dolibarr UID methods
    // These methods have been removed for security reasons.
    // Only administrators can now manage Dolibarr UIDs through admin-only service methods.

    /**
     * ADMIN-ONLY DOLIBARR UID MANAGEMENT
     */

    /**
     * Update user Dolibarr ID as admin with proper authorization checks
     */
    public User updateUserDolibarrIdAsAdmin(String adminFirebaseUid, String targetFirebaseUid, String dolibarrId) {
        System.out.println("🔵 ADMIN UPDATE DOLIBARR ID: Admin " + adminFirebaseUid + " updating Dolibarr ID for " + targetFirebaseUid);

        // First, verify that the requesting user is an admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty()) {
            System.err.println("❌ ADMIN UPDATE DOLIBARR ID: Admin user not found - " + adminFirebaseUid);
            throw new RuntimeException("Admin user not found with Firebase UID: " + adminFirebaseUid);
        }

        User adminUser = adminUserOpt.get();
        if (!adminUser.isAdmin()) {
            System.err.println("❌ ADMIN UPDATE DOLIBARR ID: User is not an admin - " + adminFirebaseUid);
            throw new SecurityException("Administrator privileges required to manage Dolibarr UIDs");
        }

        // Now find and update the target user
        Optional<User> targetUserOpt = userRepository.findByUserId(targetFirebaseUid);
        if (targetUserOpt.isEmpty()) {
            System.err.println("❌ ADMIN UPDATE DOLIBARR ID: Target user not found - " + targetFirebaseUid);
            throw new RuntimeException("Target user not found with Firebase UID: " + targetFirebaseUid);
        }

        User targetUser = targetUserOpt.get();
        String oldDolibarrId = targetUser.getDolibarrId();
        targetUser.setDolibarrId(dolibarrId);

        User updatedUser = userRepository.save(targetUser);

        System.out.println("✅ ADMIN DOLIBARR ID UPDATED:");
        System.out.println("  Admin: " + adminUser.getUsername() + " (" + adminFirebaseUid + ")");
        System.out.println("  Target User: " + targetUser.getUsername() + " (" + targetFirebaseUid + ")");
        System.out.println("  Old Dolibarr ID: " + oldDolibarrId);
        System.out.println("  New Dolibarr ID: " + dolibarrId);

        return updatedUser;
    }

    /**
     * Get user Dolibarr ID as admin with proper authorization checks
     */
    public String getUserDolibarrIdAsAdmin(String adminFirebaseUid, String targetFirebaseUid) {
        System.out.println("🔵 ADMIN GET DOLIBARR ID: Admin " + adminFirebaseUid + " fetching Dolibarr ID for " + targetFirebaseUid);

        // First, verify that the requesting user is an admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty()) {
            System.err.println("❌ ADMIN GET DOLIBARR ID: Admin user not found - " + adminFirebaseUid);
            throw new RuntimeException("Admin user not found with Firebase UID: " + adminFirebaseUid);
        }

        User adminUser = adminUserOpt.get();
        if (!adminUser.isAdmin()) {
            System.err.println("❌ ADMIN GET DOLIBARR ID: User is not an admin - " + adminFirebaseUid);
            throw new SecurityException("Administrator privileges required to access Dolibarr UIDs");
        }

        // Now find the target user
        Optional<User> targetUserOpt = userRepository.findByUserId(targetFirebaseUid);
        if (targetUserOpt.isEmpty()) {
            System.err.println("❌ ADMIN GET DOLIBARR ID: Target user not found - " + targetFirebaseUid);
            throw new RuntimeException("Target user not found with Firebase UID: " + targetFirebaseUid);
        }

        User targetUser = targetUserOpt.get();
        String dolibarrId = targetUser.getDolibarrId();

        System.out.println("✅ ADMIN DOLIBARR ID RETRIEVED:");
        System.out.println("  Admin: " + adminUser.getUsername() + " (" + adminFirebaseUid + ")");
        System.out.println("  Target User: " + targetUser.getUsername() + " (" + targetFirebaseUid + ")");
        System.out.println("  Dolibarr ID: " + dolibarrId);

        return dolibarrId;
    }

    /**
     * Get all users as admin with statistics
     * Only administrators can access this endpoint
     */
    public Map<String, Object> getAllUsersAsAdmin(String adminFirebaseUid) {
        System.out.println("🔵 ADMIN GET ALL USERS: Admin " + adminFirebaseUid + " requesting all users");

        // Verify admin user exists and has admin privileges
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty()) {
            System.err.println("❌ ADMIN GET ALL USERS: Admin user not found - " + adminFirebaseUid);
            throw new RuntimeException("Admin user not found with Firebase UID: " + adminFirebaseUid);
        }

        User adminUser = adminUserOpt.get();
        if (!adminUser.isAdmin()) {
            System.err.println("❌ ADMIN GET ALL USERS: User is not an admin - " + adminFirebaseUid);
            throw new SecurityException("Administrator privileges required to access all users");
        }

        // Get all users and filter to only show authorized users (is_authorized = true)
        List<User> allUsers = userRepository.findAll();
        List<User> authorizedUsers = allUsers.stream()
                .filter(user -> user.getIsAuthorized() != null && user.getIsAuthorized())
                .collect(java.util.stream.Collectors.toList());

        // Calculate statistics (only from authorized users)
        long normalUsers = authorizedUsers.stream().filter(user -> !user.isAdmin()).count();
        long adminUsers = authorizedUsers.stream().filter(User::isAdmin).count();

        // Create response with users and statistics
        Map<String, Object> response = new HashMap<>();
        response.put("users", authorizedUsers);
        response.put("statistics", Map.of(
            "normalUsers", normalUsers,
            "adminUsers", adminUsers,
            "totalUsers", authorizedUsers.size()
        ));

        System.out.println("✅ ADMIN ALL USERS RETRIEVED (authorized only):");
        System.out.println("  Admin: " + adminUser.getUsername() + " (" + adminFirebaseUid + ")");
        System.out.println("  Total Authorized Users: " + authorizedUsers.size());
        System.out.println("  Normal Users: " + normalUsers);
        System.out.println("  Admin Users: " + adminUsers);
        System.out.println("  Filtered out: " + (allUsers.size() - authorizedUsers.size()) + " unauthorized users");

        return response;
    }

    /**
     * ENHANCED USER MANAGEMENT (Admin Only)
     */

    /**
     * Update user department as admin
     */
    public User updateUserDepartment(String adminFirebaseUid, String targetFirebaseUid, String department) {
        System.out.println("🔵 ADMIN UPDATE DEPARTMENT:");
        System.out.println("  Admin UID: " + adminFirebaseUid);
        System.out.println("  Target UID: " + targetFirebaseUid);
        System.out.println("  New Department: " + department);

        // Verify admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty() || !adminUserOpt.get().isAdmin()) {
            throw new SecurityException("Administrator privileges required");
        }

        // Find target user
        Optional<User> targetUserOpt = userRepository.findByUserId(targetFirebaseUid);
        if (targetUserOpt.isEmpty()) {
            throw new RuntimeException("Target user not found with Firebase UID: " + targetFirebaseUid);
        }

        // Update department
        User targetUser = targetUserOpt.get();
        String oldDepartment = targetUser.getDepartment();
        targetUser.setDepartment(department);
        User updatedUser = userRepository.save(targetUser);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminFirebaseUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfDepartmentChange(updatedUser, oldDepartment, department, adminName);

        System.out.println("✅ DEPARTMENT UPDATED: " + updatedUser.getDepartment());
        return updatedUser;
    }

    /**
     * Update user account status (authorized/unauthorized) as admin
     */
    public User updateUserAccountStatus(String adminFirebaseUid, String targetFirebaseUid, Boolean isAuthorized) {
        System.out.println("🔵 ADMIN UPDATE ACCOUNT STATUS:");
        System.out.println("  Admin UID: " + adminFirebaseUid);
        System.out.println("  Target UID: " + targetFirebaseUid);
        System.out.println("  New Status: " + (isAuthorized ? "AUTHORIZED" : "UNAUTHORIZED"));

        // Verify admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty() || !adminUserOpt.get().isAdmin()) {
            throw new SecurityException("Administrator privileges required");
        }

        // Prevent admin from disabling themselves
        if (adminFirebaseUid.equals(targetFirebaseUid) && !isAuthorized) {
            throw new SecurityException("Cannot disable your own admin account");
        }

        // Find target user
        Optional<User> targetUserOpt = userRepository.findByUserId(targetFirebaseUid);
        if (targetUserOpt.isEmpty()) {
            throw new RuntimeException("Target user not found with Firebase UID: " + targetFirebaseUid);
        }

        // Update status
        User targetUser = targetUserOpt.get();
        targetUser.setIsAuthorized(isAuthorized);
        User updatedUser = userRepository.save(targetUser);

        // Send notification to user
        Optional<User> adminUser = userRepository.findByUserId(adminFirebaseUid);
        String adminName = adminUser.map(User::getUsername).orElse("Administrator");
        notificationService.notifyUserOfStatusChange(updatedUser, isAuthorized, adminName);

        System.out.println("✅ ACCOUNT STATUS UPDATED: " + (isAuthorized ? "AUTHORIZED" : "UNAUTHORIZED"));
        return updatedUser;
    }
}
