package com.apex.firefighter.service;

import com.apex.firefighter.model.User;
import com.apex.firefighter.model.Role;
import com.apex.firefighter.model.UserRole;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * FIREBASE USER VERIFICATION
     * Called when a Firebase-authenticated user accesses the system
     * This effectively serves as our "login" since Firebase handles authentication
     */
    public User verifyOrCreateUser(String firebaseUid, String username, String email, String department) {
        System.out.println("🔵 VERIFY: Checking user with Firebase UID - " + firebaseUid);
        
        Optional<User> existingUser = userRepository.findByUserId(firebaseUid);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Always update last login when user accesses the system
            user.updateLastLogin();
            User updatedUser = userRepository.save(user);
            System.out.println("✅ VERIFIED: Existing user accessed system - " + updatedUser.getUsername() + " (Last login updated)");
            return updatedUser;
        } else {
            // Create new user from Firebase auth
            User newUser = new User(firebaseUid, username, email, department);
            // New users get their "first login" timestamp set in constructor
            User savedUser = userRepository.save(newUser);
            System.out.println("✅ CREATED: New user from Firebase - " + savedUser.getUsername() + " (First login recorded)");
            return savedUser;
        }
    }

    /**
     * USER AUTHORIZATION CHECK
     * Verify if a Firebase user is authorized to access protected resources
     */
    public boolean isUserAuthorized(String firebaseUid) {
        System.out.println("🔵 AUTH CHECK: Verifying authorization for UID - " + firebaseUid);
        
        Optional<User> user = userRepository.findByUserId(firebaseUid);
        if (user.isPresent()) {
            boolean authorized = user.get().isAuthorized();
            System.out.println("✅ AUTH RESULT: User authorization status - " + authorized);
            return authorized;
        }
        
        System.out.println("❌ AUTH FAILED: User not found for UID - " + firebaseUid);
        return false;
    }

    /**
     * ROLE VERIFICATION
     * Check if user has specific role
     */
    public boolean hasRole(String firebaseUid, String roleName) {
        System.out.println("🔵 ROLE CHECK: Verifying role '" + roleName + "' for UID - " + firebaseUid);
        
        Optional<User> user = userRepository.findByUserId(firebaseUid);
        if (user.isPresent()) {
            boolean hasRole = user.get().hasRole(roleName);
            System.out.println("✅ ROLE RESULT: User has role '" + roleName + "' - " + hasRole);
            return hasRole;
        }
        
        System.out.println("❌ ROLE FAILED: User not found for UID - " + firebaseUid);
        return false;
    }

    /**
     * ADMIN OPERATIONS - Manage user authorization
     */
    public User authorizeUser(String firebaseUid, String authorizedBy) {
        System.out.println("🔵 AUTHORIZE: Authorizing user with UID - " + firebaseUid);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsAuthorized(true);
            User authorizedUser = userRepository.save(user);
            System.out.println("✅ AUTHORIZED: User authorized by " + authorizedBy + " - " + authorizedUser);
            return authorizedUser;
        } else {
            System.out.println("❌ AUTHORIZE FAILED: User not found for UID - " + firebaseUid);
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
    }

    public User revokeUserAuthorization(String firebaseUid, String revokedBy) {
        System.out.println("🔵 REVOKE: Revoking authorization for UID - " + firebaseUid);
        
        Optional<User> userOpt = userRepository.findByUserId(firebaseUid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsAuthorized(false);
            User revokedUser = userRepository.save(user);
            System.out.println("✅ REVOKED: User authorization revoked by " + revokedBy + " - " + revokedUser);
            return revokedUser;
        } else {
            System.out.println("❌ REVOKE FAILED: User not found for UID - " + firebaseUid);
            throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
        }
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
     * QUERY OPERATIONS
     */
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByUserId(firebaseUid);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAuthorizedUsers() {
        return userRepository.findByIsAuthorizedTrue();
    }

    public List<User> getUsersByDepartment(String department) {
        return userRepository.findByDepartment(department);
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public List<User> getAuthorizedUsersByRole(String roleName) {
        return userRepository.findAuthorizedUsersByRoleName(roleName);
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
} 