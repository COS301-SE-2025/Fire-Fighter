package com.apex.firefighter.controller;

import com.apex.firefighter.model.ApiKey;
import com.apex.firefighter.service.auth.ApiKeyService;
import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management and authentication operations")
public class UserController {

    private final UserService userService;
    private final ApiKeyService apiKeyService;

    @Autowired
    public UserController(UserService userService, ApiKeyService apiKeyService) {
        this.userService = userService;
        this.apiKeyService = apiKeyService;
    }

    @Operation(summary = "Verify Firebase user",
               description = "Verifies and registers a Firebase-authenticated user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify")
    public ResponseEntity<User> verifyUser(
            @Parameter(description = "Firebase UID") @RequestParam String firebaseUid,
            @Parameter(description = "Username") @RequestParam String username,
            @Parameter(description = "Email address") @RequestParam String email,
            @Parameter(description = "Department (optional)") @RequestParam(required = false) String department) {
        
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

    @PostMapping("/{firebaseUid}/apikey")
    public ResponseEntity<String> requestApiKey(@PathVariable String firebaseUid, Authentication authentication) {
        String authenticatedUid = authentication.getName();
        if (!firebaseUid.equals(authenticatedUid)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only request an API key for yourself.");
        }

        try {
            ApiKey apiKey = apiKeyService.generateApiKeyForUser(firebaseUid);
            return ResponseEntity.ok(apiKey.getApiKey());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating API key");
        }
    }

    @GetMapping("/{firebaseUid}")
    public ResponseEntity<User> getUserByFirebaseUid(@PathVariable String firebaseUid) {
        try {
            Optional<User> user = userService.findByFirebaseUid(firebaseUid);
            return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{firebaseUid}/authorized")
    public ResponseEntity<Boolean> isUserAuthorized(@PathVariable String firebaseUid) {
        try {
            boolean isAuthorized = userService.isUserAuthorized(firebaseUid);
            return ResponseEntity.ok(isAuthorized);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{firebaseUid}/roles/{roleName}")
    public ResponseEntity<Boolean> hasUserRole(@PathVariable String firebaseUid, @PathVariable String roleName) {
        try {
            boolean hasRole = userService.hasRole(firebaseUid, roleName);
            return ResponseEntity.ok(hasRole);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
