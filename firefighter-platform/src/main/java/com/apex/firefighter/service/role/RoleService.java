package com.apex.firefighter.service.role;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * RoleService handles simple role management using the role column.
 * This service is responsible for:
 * - Assigning roles to users (simple string-based roles)
 * - Removing roles from users
 * - Querying users by roles
 * 
 * Note: This is a simplified version that uses the single 'role' column
 * instead of the complex Role/UserRole entity relationships.
 */
@Service
@Transactional
public class RoleService {

    private final UserRepository userRepository;

    @Autowired
    public RoleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * SIMPLE ROLE MANAGEMENT
     */
    public User assignRole(String firebaseUid, String roleName, String assignedBy) {
        System.out.println("🔵 ASSIGN ROLE: Assigning role '" + roleName + "' to UID - " + firebaseUid);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Simply set the role in the role column
            user.setRole(roleName);
            User updatedUser = userRepository.save(user);
            System.out.println("✅ ROLE ASSIGNED: Role '" + roleName + "' assigned to user by " + assignedBy);
            return updatedUser;
        } else {
            System.out.println("❌ ASSIGN FAILED: User not found");
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * Remove role from user
     */
    public User removeRole(String firebaseUid, String roleName) {
        System.out.println("🔵 REMOVE ROLE: Removing role '" + roleName + "' from UID - " + firebaseUid);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Clear the role if it matches
            if (roleName.equals(user.getRole())) {
                user.setRole(null);
                User updatedUser = userRepository.save(user);
                System.out.println("✅ ROLE REMOVED: Role '" + roleName + "' removed from user");
                return updatedUser;
            } else {
                System.out.println("⚠️ ROLE MISMATCH: User doesn't have role '" + roleName + "'");
                return user;
            }
        } else {
            System.out.println("❌ REMOVE FAILED: User not found");
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * QUERY OPERATIONS
     */
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRole(roleName);
    }

    public List<User> getAuthorizedUsersByRole(String roleName) {
        return userRepository.findByRoleAndIsAuthorizedTrue(roleName);
    }
} 