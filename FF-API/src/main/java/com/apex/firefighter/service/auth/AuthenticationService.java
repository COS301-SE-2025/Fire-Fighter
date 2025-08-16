package com.apex.firefighter.service.auth;

import com.apex.firefighter.dto.AuthResponse;
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
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * FIREBASE USER VERIFICATION
     * Called when a Firebase-authenticated user accesses the system
     * This effectively serves as our "login" since Firebase handles authentication
     */
    public User verifyOrCreateUser(String firebaseUid, String username, String email, String department) {
        System.out.println("🔵 VERIFY: Checking user with Firebase UID - " + firebaseUid);
        
        // First, check if user exists by Firebase UID
        Optional<User> existingUserByUid = userRepository.findByUserId(firebaseUid);
        
        if (existingUserByUid.isPresent()) {
            User user = existingUserByUid.get();
            // Always update last login when user accesses the system
            user.updateLastLogin();
            User updatedUser = userRepository.save(user);
            System.out.println("✅ VERIFIED: Existing user accessed system - " + updatedUser.getUsername() + " (Last login updated)");
            return updatedUser;
        }
        
        // If not found by Firebase UID, check if user exists by email
        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        
        if (existingUserByEmail.isPresent()) {
            User user = existingUserByEmail.get();
            // Update the existing user's Firebase UID (user might have recreated Firebase account)
            System.out.println("🔄 UPDATING: Found existing user by email, updating Firebase UID from " + user.getUserId() + " to " + firebaseUid);
            user.setUserId(firebaseUid);
            
            // Update other fields if provided
            if (username != null && !username.trim().isEmpty()) {
                user.setUsername(username);
            }
            if (department != null && !department.trim().isEmpty()) {
                user.setDepartment(department);
            }
            
            // Update last login
            user.updateLastLogin();
            User updatedUser = userRepository.save(user);
            System.out.println("✅ UPDATED: User Firebase UID updated - " + updatedUser.getUsername() + " (Last login updated)");
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

    /**
     * Verify Firebase token and create JWT
     * This method combines Firebase verification with JWT creation
     */
    public AuthResponse verifyFirebaseTokenAndCreateJwt(String firebaseToken) throws Exception {
        // Verify the Firebase token using JwtService
        var token = jwtService.verifyFirebaseToken(firebaseToken);
        
        String firebaseUid = token.getUid();
        String email = token.getEmail();
        String username = token.getName();
        
        // Find or create user
        User user = verifyOrCreateUser(firebaseUid, username, email, null);
        
        // Generate JWT token with user's admin status
        String jwtToken = jwtService.generateToken(firebaseUid, email, user.getIsAdmin());
        
        return new AuthResponse(jwtToken, user);
    }
} 