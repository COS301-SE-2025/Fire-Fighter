package com.apex.firefighter.controller;

import com.apex.firefighter.service.nlp.NLPService;
import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * REST Controller for Natural Language Processing operations.
 * Handles ticket queries and management through natural language.
 */
@RestController
@RequestMapping("/api/nlp")
@Tag(name = "Natural Language Processing", description = "NLP-powered ticket queries and management through natural language")
public class NLPController {

    @Autowired
    private NLPService nlpService;

    @Autowired
    private UserService userService;

    // Setter methods for testing
    public void setNlpService(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Process a natural language query from a user
     */
    @PostMapping("/query")
    @Operation(summary = "Process natural language query", 
               description = "Process a user's natural language query for ticket operations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Query processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query or parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPResponse> processQuery(
            @RequestBody NLPQueryRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Extract and verify user authentication
            String userId = extractAndVerifyUserId(httpRequest);

            // Validate request
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse("Query cannot be empty", false));
            }

            // Get admin flag from JWT
            Boolean isAdminFromJWT = (Boolean) httpRequest.getAttribute("isAdmin");

            // Process the query with admin flag
            NLPService.NLPResponse response = nlpService.processQuery(request.getQuery(), userId, isAdminFromJWT);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle authentication/authorization errors
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401)
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            } else {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new NLPService.NLPResponse("Error processing query: " + e.getMessage(), false));
        }
    }

    /**
     * Process an admin-level natural language query
     */
    @PostMapping("/admin/query")
    @Operation(summary = "Process admin natural language query", 
               description = "Process an admin user's natural language query with elevated privileges")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin query processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query or parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin privileges required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPResponse> processAdminQuery(
            @RequestBody NLPQueryRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Extract and verify admin user authentication
            String userId = extractAndVerifyAdminUser(httpRequest);

            // Validate request
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse("Query cannot be empty", false));
            }

            // Process the admin query
            NLPService.NLPResponse response = nlpService.processAdminQuery(request.getQuery(), userId);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Handle authentication/authorization errors
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401)
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            } else if (e.getMessage().contains("Admin privileges required")) {
                return ResponseEntity.status(403)
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            } else {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse(e.getMessage(), false));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new NLPService.NLPResponse("Error processing admin query: " + e.getMessage(), false));
        }
    }

    /**
     * Get NLP capabilities for the current user
     */
    @GetMapping("/capabilities/{userId}")
    @Operation(summary = "Get NLP capabilities", 
               description = "Get available NLP capabilities based on user role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPCapabilities> getCapabilities(
            @PathVariable String userId,
            HttpServletRequest httpRequest) {
        
        try {
            // Verify user authorization and ensure they can only access their own capabilities
            String authenticatedUserId = extractAndVerifyUserId(httpRequest);

            // Users can only access their own capabilities unless they're admin
            if (!authenticatedUserId.equals(userId)) {
                Boolean isAdmin = (Boolean) httpRequest.getAttribute("isAdmin");
                if (isAdmin == null || !isAdmin) {
                    return ResponseEntity.status(403)
                        .body(null); // Forbidden - can't access other user's capabilities
                }
            }

            NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

            if (capabilities == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(capabilities);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401).build();
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get suggested queries for the current user
     */
    @GetMapping("/suggestions/{userId}")
    @Operation(summary = "Get query suggestions", 
               description = "Get suggested natural language queries based on user role and context")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPSuggestions> getSuggestions(
            @PathVariable String userId,
            HttpServletRequest httpRequest) {
        
        try {
            // Verify user authorization and ensure they can only access their own suggestions
            String authenticatedUserId = extractAndVerifyUserId(httpRequest);

            // Users can only access their own suggestions unless they're admin
            if (!authenticatedUserId.equals(userId)) {
                Boolean isAdmin = (Boolean) httpRequest.getAttribute("isAdmin");
                if (isAdmin == null || !isAdmin) {
                    return ResponseEntity.status(403)
                        .body(null); // Forbidden - can't access other user's suggestions
                }
            }

            NLPService.NLPSuggestions suggestions = nlpService.getSuggestions(userId);

            if (suggestions == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(suggestions);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401).build();
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check for NLP service
     */
    @GetMapping("/health")
    @Operation(summary = "NLP service health check", 
               description = "Check the health and status of the NLP service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy"),
        @ApiResponse(responseCode = "503", description = "Service is unhealthy")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        
        try {
            boolean isHealthy = nlpService.isServiceHealthy();
            
            Map<String, Object> health = Map.of(
                "status", isHealthy ? "healthy" : "unhealthy",
                "service", "Natural Language Processing",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "version", "1.0.0",
                "components", Map.of(
                    "intentRecognition", "UP",
                    "entityExtraction", "UP", 
                    "queryProcessing", "UP",
                    "responseGeneration", "UP"
                )
            );
            
            if (!isHealthy) {
                return ResponseEntity.status(503).body(health);
            }
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            Map<String, Object> health = Map.of(
                "status", "unhealthy",
                "service", "Natural Language Processing",
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.status(503).body(health);
        }
    }

    /**
     * Extract user ID from HTTP request (JWT token)
     * The JwtAuthenticationFilter already extracts and validates the token,
     * setting the Firebase UID as a request attribute.
     *
     * This method follows the same pattern as other working controllers
     * and trusts the JWT authentication filter for security.
     */
    private String extractUserIdFromRequest(HttpServletRequest request) {
        // Get Firebase UID from request attribute (set by JwtAuthenticationFilter)
        String firebaseUid = (String) request.getAttribute("firebaseUid");

        if (firebaseUid == null || firebaseUid.trim().isEmpty()) {
            throw new RuntimeException("Authentication required: No valid Firebase UID found in request");
        }

        return firebaseUid;
    }

    /**
     * Extract user ID with basic validation (simplified to match other controllers)
     * This follows the same pattern as TicketController and other working endpoints
     */
    private String extractAndVerifyUserId(HttpServletRequest request) {
        String userId = extractUserIdFromRequest(request);

        // Only verify user exists - don't check authorization status
        // The JWT filter already handles authentication, and we should trust it
        if (!userService.userExists(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        return userId;
    }

    /**
     * Verify admin privileges (follows the same pattern as other controllers)
     */
    private String extractAndVerifyAdminUser(HttpServletRequest request) {
        String userId = extractAndVerifyUserId(request);

        // Check if user has admin privileges using the same pattern as other controllers
        Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
        String userRole = userService.getUserRole(userId);

        if (!"ADMIN".equals(userRole) && (isAdmin == null || !isAdmin)) {
            throw new RuntimeException("Admin privileges required for user: " + userId);
        }

        return userId;
    }

    /**
     * Get capabilities for current authenticated user (convenience endpoint)
     */
    @GetMapping("/capabilities/current")
    @Operation(summary = "Get current user NLP capabilities",
               description = "Get available NLP capabilities for the current authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPCapabilities> getCurrentUserCapabilities(HttpServletRequest httpRequest) {

        try {
            // Extract and verify user authentication
            String userId = extractAndVerifyUserId(httpRequest);

            NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);

            if (capabilities == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(capabilities);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401).build();
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get suggestions for current authenticated user (convenience endpoint)
     */
    @GetMapping("/suggestions/current")
    @Operation(summary = "Get current user query suggestions",
               description = "Get suggested natural language queries for the current authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NLPService.NLPSuggestions> getCurrentUserSuggestions(HttpServletRequest httpRequest) {

        try {
            // Extract and verify user authentication
            String userId = extractAndVerifyUserId(httpRequest);

            NLPService.NLPSuggestions suggestions = nlpService.getSuggestions(userId);

            if (suggestions == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(suggestions);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Authentication required") || e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(401).build();
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Request object for NLP queries
     */
    public static class NLPQueryRequest {
        private String query;
        private Map<String, Object> context;
        private String preferredResponseStyle;

        public NLPQueryRequest() {}

        public NLPQueryRequest(String query) {
            this.query = query;
        }

        // Getters and setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        
        public String getPreferredResponseStyle() { return preferredResponseStyle; }
        public void setPreferredResponseStyle(String preferredResponseStyle) { 
            this.preferredResponseStyle = preferredResponseStyle; 
        }
    }
}
