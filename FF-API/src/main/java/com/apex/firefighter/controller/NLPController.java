package com.apex.firefighter.controller;

import com.apex.firefighter.service.nlp.NLPService;
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
            // TODO: Extract user ID from JWT token
            String userId = extractUserIdFromRequest(httpRequest);
            
            // Validate request
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse("Query cannot be empty", false));
            }

            // Process the query
            NLPService.NLPResponse response = nlpService.processQuery(request.getQuery(), userId);
            
            return ResponseEntity.ok(response);
            
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
            // TODO: Extract user ID from JWT token and verify admin role
            String userId = extractUserIdFromRequest(httpRequest);
            
            // Validate request
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new NLPService.NLPResponse("Query cannot be empty", false));
            }

            // Process the admin query
            NLPService.NLPResponse response = nlpService.processAdminQuery(request.getQuery(), userId);
            
            return ResponseEntity.ok(response);
            
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
            // TODO: Verify user authorization
            
            NLPService.NLPCapabilities capabilities = nlpService.getCapabilities(userId);
            
            if (capabilities == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(capabilities);
            
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
            // TODO: Verify user authorization
            
            NLPService.NLPSuggestions suggestions = nlpService.getSuggestions(userId);
            
            if (suggestions == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(suggestions);
            
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
     * TODO: Implement proper JWT token extraction
     */
    private String extractUserIdFromRequest(HttpServletRequest request) {
        // TODO: Implement JWT token extraction and validation
        // This should extract the Firebase UID from the JWT token
        return "placeholder-user-id";
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
