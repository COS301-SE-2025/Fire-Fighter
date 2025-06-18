package com.apex.firefighter.service.user;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Autowired
    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
} 