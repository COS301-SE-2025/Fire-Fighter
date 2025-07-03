package com.apex.firefighter.controller;

import com.apex.firefighter.model.ApiKey;
import com.apex.firefighter.service.ApiKeyService;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ApiKeyService apiKeyService;


    @Autowired
    public UserController(UserService userService, ApiKeyService apiKeyService) {
        this.userService = userService;
        this.apiKeyService = apiKeyService;
    }

    /**
     * REQUEST API KEY ENDPOINT
     * POST /api/users/{firebaseUid}/apikey
     * Request an API key for a user
     */ 
    @PostMapping("/{firebaseUid}/apikey")
    public ResponseEntity<String> requestApiKey(@PathVariable String firebaseUid) {
        try {
            ApiKey apiKey = apiKeyService.generateApiKeyForUser(firebaseUid);
            // Return the API key value directly (only show once)
            return ResponseEntity.ok(apiKey.getApiKey());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating API key");
        }
    }

    /**
     * FIREBASE USER VERIFICATION ENDPOINT
     * POST /api/users/verify
     * Called when a Firebase-authenticated user accesses the system
     */
    @PostMapping("/verify")
    public ResponseEntity<User> verifyUser(
            @RequestParam String firebaseUid,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String department) {
        
        try {
            System.out.println("🔵 VERIFY USER REQUEST:");
            System.out.println("  Firebase UID: " + firebaseUid);
            System.out.println("  Username: " + username);
            System.out.println("  Email: " + email);
            System.out.println("  Department: " + department);
            
            User user = userService.verifyOrCreateUser(firebaseUid, username, email, department);
            
            System.out.println("✅ USER VERIFICATION SUCCESS: " + user.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("❌ USER VERIFICATION FAILED:");
            System.err.println("  Error Type: " + e.getClass().getSimpleName());
            System.err.println("  Error Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * AUTHORIZATION CHECK ENDPOINT
     * GET /api/users/{firebaseUid}/authorized
     * Check if a Firebase user is authorized to access protected resources
     */
    @GetMapping("/{firebaseUid}/authorized")
    public ResponseEntity<Boolean> checkAuthorization(@PathVariable String firebaseUid) {
        boolean isAuthorized = userService.isUserAuthorized(firebaseUid);
        return ResponseEntity.ok(isAuthorized);
    }

    /**
     * ROLE CHECK ENDPOINT
     * GET /api/users/{firebaseUid}/roles/{roleName}
     * Check if user has specific role
     */
    @GetMapping("/{firebaseUid}/roles/{roleName}")
    public ResponseEntity<Boolean> checkRole(
            @PathVariable String firebaseUid,
            @PathVariable String roleName) {
        
        boolean hasRole = userService.hasRole(firebaseUid, roleName);
        return ResponseEntity.ok(hasRole);
    }

    /**
     * GET USER INFO ENDPOINT
     * GET /api/users/{firebaseUid}
     * Get complete user information including roles
     */
    @GetMapping("/{firebaseUid}")
    public ResponseEntity<User> getUserInfo(@PathVariable String firebaseUid) {
        Optional<User> user = userService.getUserWithRoles(firebaseUid);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET USER BY EMAIL
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ADMIN ENDPOINTS - Manage Authorization
     */
    
    /**
     * AUTHORIZE USER
     * PUT /api/users/{firebaseUid}/authorize
     * Admin endpoint to authorize a user
     */
    @PutMapping("/{firebaseUid}/authorize")
    public ResponseEntity<User> authorizeUser(
            @PathVariable String firebaseUid,
            @RequestParam String authorizedBy) {
        
        try {
            User authorizedUser = userService.authorizeUser(firebaseUid, authorizedBy);
            return ResponseEntity.ok(authorizedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * REVOKE AUTHORIZATION
     * PUT /api/users/{firebaseUid}/revoke
     * Admin endpoint to revoke user authorization
     */
    @PutMapping("/{firebaseUid}/revoke")
    public ResponseEntity<User> revokeAuthorization(
            @PathVariable String firebaseUid,
            @RequestParam String revokedBy) {
        
        try {
            User revokedUser = userService.revokeUserAuthorization(firebaseUid, revokedBy);
            return ResponseEntity.ok(revokedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ASSIGN ROLE
     * POST /api/users/{firebaseUid}/roles
     * Admin endpoint to assign role to user
     */
    @PostMapping("/{firebaseUid}/roles")
    public ResponseEntity<User> assignRole(
            @PathVariable String firebaseUid,
            @RequestParam String roleName,
            @RequestParam String assignedBy) {
        
        try {
            User updatedUser = userService.assignRole(firebaseUid, roleName, assignedBy);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * QUERY ENDPOINTS
     */
    
    /**
     * GET AUTHORIZED USERS
     * GET /api/users/authorized
     */
    @GetMapping("/authorized")
    public ResponseEntity<List<User>> getAuthorizedUsers() {
        List<User> authorizedUsers = userService.getAuthorizedUsers();
        return ResponseEntity.ok(authorizedUsers);
    }

    /**
     * GET USERS BY DEPARTMENT
     * GET /api/users/department/{department}
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<User>> getUsersByDepartment(@PathVariable String department) {
        List<User> users = userService.getUsersByDepartment(department);
        return ResponseEntity.ok(users);
    }

    /**
     * GET USERS BY ROLE
     * GET /api/users/role/{roleName}
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }

    /**
     * GET AUTHORIZED USERS BY ROLE
     * GET /api/users/authorized/role/{roleName}
     */
    @GetMapping("/authorized/role/{roleName}")
    public ResponseEntity<List<User>> getAuthorizedUsersByRole(@PathVariable String roleName) {
        List<User> users = userService.getAuthorizedUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }
} 