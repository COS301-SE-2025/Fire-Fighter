package com.apex.firefighter.service.auth;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * AuthorizationService handles user authorization and role verification.
 * This service is responsible for:
 * - Checking if users are authorized to access protected resources
 * - Verifying user roles
 * - Managing authorization status
 */
@Service
@Transactional
public class AuthorizationService {

    private final UserRepository userRepository;

    @Autowired
    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
} 