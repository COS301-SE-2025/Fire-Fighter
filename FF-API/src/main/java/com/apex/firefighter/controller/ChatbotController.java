package com.apex.firefighter.controller;

import com.apex.firefighter.service.ai.ChatbotService;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotResponse;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "AI Chatbot", description = "AI-powered chatbot for ticket queries and emergency response assistance")
@ConditionalOnProperty(name = "GOOGLE_GEMINI_API_KEY", matchIfMissing = false)
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Operation(summary = "Send query to AI chatbot", 
               description = "Process user query and get AI-powered response about tickets and emergency operations",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Query processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query or missing parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "500", description = "AI service error")
    })
    @PostMapping("/query")
    public ResponseEntity<ChatbotResponse> processQuery(@RequestBody Map<String, String> request) {
        try {
            System.out.println("🤖 CHATBOT CONTROLLER: Received query request");
            System.out.println("🤖 CHATBOT CONTROLLER: Request body: " + request);
            
            String query = request.get("query");
            System.out.println("🤖 CHATBOT CONTROLLER: Extracted query: " + query);
            
            // Get authenticated user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("🤖 CHATBOT CONTROLLER: Authentication object: " + authentication);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("🤖 CHATBOT CONTROLLER: Authentication failed - no authentication or not authenticated");
                ChatbotResponse errorResponse = new ChatbotResponse("Authentication required", false);
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            String userId = authentication.getName(); // This will be the Firebase UID from our JWT
            System.out.println("🤖 CHATBOT CONTROLLER: Authenticated user ID: " + userId);
            System.out.println("🤖 CHATBOT CONTROLLER: Authentication principal: " + authentication.getPrincipal());
            System.out.println("🤖 CHATBOT CONTROLLER: Authentication authorities: " + authentication.getAuthorities());

            if (query == null || query.trim().isEmpty()) {
                System.out.println("🤖 CHATBOT CONTROLLER: Query is empty");
                ChatbotResponse errorResponse = new ChatbotResponse("Query cannot be empty", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userId == null || userId.trim().isEmpty()) {
                System.out.println("🤖 CHATBOT CONTROLLER: User ID is empty");
                ChatbotResponse errorResponse = new ChatbotResponse("User authentication failed", false);
                return ResponseEntity.status(401).body(errorResponse);
            }

            System.out.println("🤖 CHATBOT CONTROLLER: Processing query: '" + query + "' for user: " + userId);
            ChatbotResponse response = chatbotService.processQuery(query, userId);
            System.out.println("🤖 CHATBOT CONTROLLER: Response from service: " + response);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("🤖 CHATBOT CONTROLLER: Error in chatbot query endpoint: " + e.getMessage());
            e.printStackTrace();
            ChatbotResponse errorResponse = new ChatbotResponse("Internal server error: " + e.getMessage(), false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Send admin query to AI chatbot", 
               description = "Process admin-level query with enhanced system access and capabilities",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin query processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query or missing parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin privileges required"),
        @ApiResponse(responseCode = "500", description = "AI service error")
    })
    @PostMapping("/admin/query")
    public ResponseEntity<ChatbotResponse> processAdminQuery(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            
            // Get authenticated user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                ChatbotResponse errorResponse = new ChatbotResponse("Authentication required", false);
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            String userId = authentication.getName(); // This will be the Firebase UID from our JWT

            if (query == null || query.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("Query cannot be empty", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userId == null || userId.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("User authentication failed", false);
                return ResponseEntity.status(401).body(errorResponse);
            }

            ChatbotResponse response = chatbotService.processAdminQuery(query, userId);
            
            if (!response.isSuccess() && response.getMessage().contains("Administrator privileges required")) {
                return ResponseEntity.status(403).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in admin chatbot query endpoint: " + e.getMessage());
            ChatbotResponse errorResponse = new ChatbotResponse("Internal server error", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Get chatbot capabilities", 
               description = "Retrieve available chatbot features and suggested queries based on user role",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/capabilities")
    public ResponseEntity<ChatbotCapabilities> getCapabilities() {
        try {
            // Get authenticated user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }
            
            String userId = authentication.getName(); // This will be the Firebase UID from our JWT

            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);
            return ResponseEntity.ok(capabilities);

        } catch (Exception e) {
            System.err.println("Error getting chatbot capabilities: " + e.getMessage());
            ChatbotCapabilities errorCapabilities = new ChatbotCapabilities(false, false, "Error retrieving capabilities");
            return ResponseEntity.internalServerError().body(errorCapabilities);
        }
    }

    @Operation(summary = "Check chatbot health", 
               description = "Health check endpoint to verify AI service availability")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chatbot service is healthy"),
        @ApiResponse(responseCode = "503", description = "Chatbot service unavailable")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            // Simple health check - could be expanded to test AI service connectivity
            Map<String, Object> health = Map.of(
                "status", "healthy",
                "service", "AI Chatbot",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "version", "1.0.0"
            );
            
            return ResponseEntity.ok(health);

        } catch (Exception e) {
            Map<String, Object> health = Map.of(
                "status", "unhealthy",
                "service", "AI Chatbot",
                "error", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.status(503).body(health);
        }
    }

    @Operation(summary = "Get suggested queries", 
               description = "Get a list of suggested queries based on user role and current system state",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions() {
        try {
            // Get authenticated user ID from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }
            
            String userId = authentication.getName(); // This will be the Firebase UID from our JWT

            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            ChatbotCapabilities capabilities = chatbotService.getCapabilities(userId);
            
            Map<String, Object> suggestions = Map.of(
                "available", capabilities.isAvailable(),
                "userRole", capabilities.isAdminAccess() ? "Administrator" : "User",
                "accessLevel", capabilities.getAccessLevel(),
                "suggestedQueries", capabilities.getSuggestedQueries() != null ? capabilities.getSuggestedQueries() : new String[0],
                "examples", getExampleQueries(capabilities.isAdminAccess())
            );

            return ResponseEntity.ok(suggestions);

        } catch (Exception e) {
            System.err.println("Error getting chatbot suggestions: " + e.getMessage());
            Map<String, Object> errorSuggestions = Map.of(
                "available", false,
                "error", "Unable to retrieve suggestions"
            );
            return ResponseEntity.internalServerError().body(errorSuggestions);
        }
    }

    /**
     * Get example queries based on user role
     */
    private String[] getExampleQueries(boolean isAdmin) {
        if (isAdmin) {
            return new String[]{
                "How many active fire emergencies do we have?",
                "Show me today's ticket summary",
                "Which tickets need immediate attention?",
                "What's the average response time this week?",
                "Export current active tickets"
            };
        } else {
            return new String[]{
                "What tickets am I assigned to?",
                "Do I have any urgent tasks?",
                "How do I update my ticket status?",
                "Show my recent activity",
                "Help me with emergency procedures"
            };
        }
    }

    @GetMapping("/debug/context/{userId}")
    public ResponseEntity<String> getDebugContext(@PathVariable String userId, @RequestParam String query) {
        try {
            // This is a debug endpoint to see what context is being generated
            String context = chatbotService.getDebugContext(query, userId);
            return ResponseEntity.ok(context);
        } catch (Exception e) {
            System.err.println("Error getting debug context: " + e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
