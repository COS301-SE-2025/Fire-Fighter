package com.apex.firefighter.service.auth;

import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * AuthenticationService handles Firebase user verification and login tracking.
 * This service is responsible for:
 * - Verifying Firebase authenticated users
 * - Creating new users from Firebase authentication
 * - Tracking user login times
 */
@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
     * Get user by Firebase UID for authentication purposes
     */
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByUserId(firebaseUid);
    }

    /**
     * Check if user exists by Firebase UID
     */
    public boolean userExists(String firebaseUid) {
        return userRepository.existsByUserId(firebaseUid);
    }
} 