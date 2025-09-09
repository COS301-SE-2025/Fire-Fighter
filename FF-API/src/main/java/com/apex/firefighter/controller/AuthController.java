package com.apex.firefighter.controller;

import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200", "http://localhost:8100", "http://127.0.0.1:8100", "ionic://localhost", "capacitor://localhost"})
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        System.out.println("🔵 AUTHCONTROLLER: Constructor called - Controller is being instantiated!");
        System.out.println("✅ AUTHCONTROLLER: Constructor completed successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("🔵 FIREBASE AUTH CONTROLLER: Test endpoint called");
        return ResponseEntity.ok("Firebase auth controller is working!");
    }

    @PostMapping("/firebase-login")
    public ResponseEntity<?> firebaseLogin(@RequestBody FirebaseLoginRequest request) {
        try {
            System.out.println("🔵 FIREBASE LOGIN REQUEST: Processing token exchange...");
            String idToken = request.getIdToken();
            
            if (idToken == null || idToken.trim().isEmpty()) {
                System.out.println("❌ FIREBASE LOGIN ERROR: Missing or empty idToken");
                return ResponseEntity.badRequest().build();
            }
            
            // Use the existing AuthenticationService to handle the flow
            AuthResponse authResponse = authenticationService.verifyFirebaseTokenAndCreateJwt(idToken);
            
            System.out.println("✅ FIREBASE LOGIN SUCCESS: JWT created for user " + authResponse.getUser().getUsername());
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            System.out.println("❌ FIREBASE LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/dev-login")
    public ResponseEntity<?> devLogin(@RequestBody DevLoginRequest request) {
        try {
            System.out.println("🔵 DEV LOGIN REQUEST: Processing development authentication...");
            String firebaseUid = request.getFirebaseUid();
            String email = request.getEmail();
            String username = request.getUsername();

            if (firebaseUid == null || firebaseUid.trim().isEmpty()) {
                System.out.println("❌ DEV LOGIN ERROR: Firebase UID is missing");
                return ResponseEntity.badRequest().build();
            }

            if (email == null || email.trim().isEmpty()) {
                System.out.println("❌ DEV LOGIN ERROR: Email is missing");
                return ResponseEntity.badRequest().build();
            }

            // Create JWT token directly without Firebase verification (for development)
            AuthResponse authResponse = authenticationService.createJwtForDevelopment(firebaseUid, email, username);

            System.out.println("✅ DEV LOGIN SUCCESS: JWT token created for user: " + firebaseUid);
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            System.out.println("❌ DEV LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    public static class FirebaseLoginRequest {
        private String idToken;

        public String getIdToken() { return idToken; }
        public void setIdToken(String idToken) { this.idToken = idToken; }
    }

    public static class DevLoginRequest {
        private String firebaseUid;
        private String email;
        private String username;

        public String getFirebaseUid() { return firebaseUid; }
        public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    @GetMapping("/test-jwt")
    public ResponseEntity<Map<String, Object>> testJwtAuthentication(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> testResult = Map.of(
                "authHeaderPresent", authHeader != null,
                "authHeaderValue", authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "null",
                "authenticationPresent", authentication != null,
                "isAuthenticated", authentication != null ? authentication.isAuthenticated() : false,
                "userId", authentication != null ? authentication.getName() : "null",
                "authorities", authentication != null ? authentication.getAuthorities().toString() : "null",
                "timestamp", java.time.LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(testResult);

        } catch (Exception e) {
            Map<String, Object> errorResult = Map.of(
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            );
            return ResponseEntity.status(500).body(errorResult);
        }
    }
}
