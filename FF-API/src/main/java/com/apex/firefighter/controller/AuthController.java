package com.apex.firefighter.controller;

import com.apex.firefighter.service.auth.AuthenticationService;
import com.apex.firefighter.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

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

    public static class FirebaseLoginRequest {
        private String idToken;
        
        public String getIdToken() { return idToken; }
        public void setIdToken(String idToken) { this.idToken = idToken; }
    }
}
