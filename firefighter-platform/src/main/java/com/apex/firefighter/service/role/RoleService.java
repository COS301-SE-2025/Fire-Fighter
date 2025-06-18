package com.apex.firefighter.service.role;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.Role;
import com.apex.firefighter.model.UserRole;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * RoleService handles role management and assignment.
 * This service is responsible for:
 * - Managing roles (create, update, delete)
 * - Assigning roles to users
 * - Removing roles from users
 * - Querying users by roles
 */
@Service
@Transactional
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * ROLE MANAGEMENT
     */
    public User assignRole(String firebaseUid, String roleName, String assignedBy) {
        System.out.println("🔵 ASSIGN ROLE: Assigning role '" + roleName + "' to UID - " + firebaseUid);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        
        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            Role role = roleOpt.get();
            
            // Check if user already has this role
            if (!user.hasRole(roleName)) {
                UserRole userRole = new UserRole(user, role, assignedBy);
                user.addUserRole(userRole);
                User updatedUser = userRepository.save(user);
                System.out.println("✅ ROLE ASSIGNED: Role '" + roleName + "' assigned to user by " + assignedBy);
                return updatedUser;
            } else {
                System.out.println("⚠️ ROLE EXISTS: User already has role '" + roleName + "'");
                return user;
            }
        } else {
            System.out.println("❌ ASSIGN FAILED: User or role not found");
            throw new RuntimeException("User or role not found");
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
            
            // Find and remove the UserRole
            user.getUserRoles().removeIf(userRole -> 
                userRole.getRole().getName().equals(roleName));
                
            User updatedUser = userRepository.save(user);
            System.out.println("✅ ROLE REMOVED: Role '" + roleName + "' removed from user");
            return updatedUser;
        } else {
            System.out.println("❌ REMOVE FAILED: User not found");
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    /**
     * Create a new role
     */
    public Role createRole(String roleName) {
        System.out.println("🔵 CREATE ROLE: Creating new role '" + roleName + "'");
        
        // Check if role already exists
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if (existingRole.isPresent()) {
            System.out.println("⚠️ ROLE EXISTS: Role '" + roleName + "' already exists");
            return existingRole.get();
        }
        
        Role newRole = new Role(roleName);
        Role savedRole = roleRepository.save(newRole);
        System.out.println("✅ ROLE CREATED: New role '" + roleName + "' created");
        return savedRole;
    }

    /**
     * Get all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Get role by name
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    /**
     * QUERY OPERATIONS
     */
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public List<User> getAuthorizedUsersByRole(String roleName) {
        return userRepository.findAuthorizedUsersByRoleName(roleName);
    }

    /**
     * Check if role exists
     */
    public boolean roleExists(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
} 