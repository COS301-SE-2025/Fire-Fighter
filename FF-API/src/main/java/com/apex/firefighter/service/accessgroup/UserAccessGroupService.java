package com.apex.firefighter.service.accessgroup;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.accessgroup.UserAccessGroup;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.accessgroup.UserAccessGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user access groups
 * Handles assignment and removal of users to/from access groups
 */
@Service
@Transactional
public class UserAccessGroupService {

    private final UserAccessGroupRepository accessGroupRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserAccessGroupService(UserAccessGroupRepository accessGroupRepository,
                                   UserRepository userRepository) {
        this.accessGroupRepository = accessGroupRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all access groups for a user
     */
    public List<String> getUserAccessGroups(String userId) {
        System.out.println("🔵 GET USER ACCESS GROUPS: userId=" + userId);
        
        List<UserAccessGroup> userGroups = accessGroupRepository.findByUserId(userId);
        List<String> groupIds = userGroups.stream()
            .map(UserAccessGroup::getGroupId)
            .collect(Collectors.toList());
        
        System.out.println("✅ Found " + groupIds.size() + " access groups for user");
        return groupIds;
    }

    /**
     * Update user access groups (Admin only)
     * Replaces all existing groups with the new list
     */
    public List<String> updateUserAccessGroups(String adminFirebaseUid, String targetUserId, List<String> groupIds) {
        System.out.println("🔵 UPDATE USER ACCESS GROUPS:");
        System.out.println("  Admin UID: " + adminFirebaseUid);
        System.out.println("  Target User: " + targetUserId);
        System.out.println("  New Groups: " + groupIds);

        // Verify admin user exists and has admin privileges
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty()) {
            System.err.println("❌ Admin user not found: " + adminFirebaseUid);
            throw new RuntimeException("Admin user not found with Firebase UID: " + adminFirebaseUid);
        }

        User adminUser = adminUserOpt.get();
        if (!adminUser.isAdmin()) {
            System.err.println("❌ User is not an admin: " + adminFirebaseUid);
            throw new SecurityException("Administrator privileges required to manage access groups");
        }

        // Verify target user exists
        Optional<User> targetUserOpt = userRepository.findByUserId(targetUserId);
        if (targetUserOpt.isEmpty()) {
            System.err.println("❌ Target user not found: " + targetUserId);
            throw new RuntimeException("Target user not found with Firebase UID: " + targetUserId);
        }

        // Remove all existing groups for this user
        System.out.println("🗑️ Removing existing access groups for user...");
        accessGroupRepository.deleteByUserId(targetUserId);

        // Add new groups
        System.out.println("➕ Adding new access groups...");
        for (String groupId : groupIds) {
            UserAccessGroup accessGroup = new UserAccessGroup(targetUserId, groupId, adminFirebaseUid);
            accessGroupRepository.save(accessGroup);
            System.out.println("  ✅ Added group: " + groupId);
        }

        System.out.println("✅ ACCESS GROUPS UPDATED SUCCESSFULLY");
        System.out.println("  User: " + targetUserOpt.get().getUsername());
        System.out.println("  Total Groups: " + groupIds.size());

        return groupIds;
    }

    /**
     * Add a single access group to a user
     */
    public void addAccessGroupToUser(String adminFirebaseUid, String userId, String groupId) {
        System.out.println("🔵 ADD ACCESS GROUP:");
        System.out.println("  Admin UID: " + adminFirebaseUid);
        System.out.println("  User ID: " + userId);
        System.out.println("  Group ID: " + groupId);

        // Verify admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty() || !adminUserOpt.get().isAdmin()) {
            throw new SecurityException("Administrator privileges required");
        }

        // Check if already assigned
        if (accessGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            System.out.println("⚠️ User already has this access group");
            return;
        }

        // Add the group
        UserAccessGroup accessGroup = new UserAccessGroup(userId, groupId, adminFirebaseUid);
        accessGroupRepository.save(accessGroup);

        System.out.println("✅ ACCESS GROUP ADDED");
    }

    /**
     * Remove a single access group from a user
     */
    public void removeAccessGroupFromUser(String adminFirebaseUid, String userId, String groupId) {
        System.out.println("🔵 REMOVE ACCESS GROUP:");
        System.out.println("  Admin UID: " + adminFirebaseUid);
        System.out.println("  User ID: " + userId);
        System.out.println("  Group ID: " + groupId);

        // Verify admin
        Optional<User> adminUserOpt = userRepository.findByUserId(adminFirebaseUid);
        if (adminUserOpt.isEmpty() || !adminUserOpt.get().isAdmin()) {
            throw new SecurityException("Administrator privileges required");
        }

        // Remove the group
        accessGroupRepository.deleteByUserIdAndGroupId(userId, groupId);

        System.out.println("✅ ACCESS GROUP REMOVED");
    }

    /**
     * Check if user has access to a specific group
     */
    public boolean userHasAccessGroup(String userId, String groupId) {
        return accessGroupRepository.existsByUserIdAndGroupId(userId, groupId);
    }

    /**
     * Get all users in a specific access group
     */
    public List<String> getUsersInAccessGroup(String groupId) {
        List<UserAccessGroup> groupUsers = accessGroupRepository.findByGroupId(groupId);
        return groupUsers.stream()
            .map(UserAccessGroup::getUserId)
            .collect(Collectors.toList());
    }
}
