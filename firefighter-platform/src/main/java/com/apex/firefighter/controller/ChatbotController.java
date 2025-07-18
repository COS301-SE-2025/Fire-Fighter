package com.apex.firefighter.controller;

import com.apex.firefighter.service.ai.ChatbotService;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotResponse;
import com.apex.firefighter.service.ai.ChatbotService.ChatbotCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @Operation(summary = "Send query to AI chatbot", 
               description = "Process user query and get AI-powered response about tickets and emergency operations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Query processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid query or missing parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
        @ApiResponse(responseCode = "500", description = "AI service error")
    })
    @PostMapping("/query")
    public ResponseEntity<ChatbotResponse> processQuery(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            String userId = request.get("userId");

            if (query == null || query.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("Query cannot be empty", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userId == null || userId.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("User ID is required", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            ChatbotResponse response = chatbotService.processQuery(query, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in chatbot query endpoint: " + e.getMessage());
            ChatbotResponse errorResponse = new ChatbotResponse("Internal server error", false);
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
            String userId = request.get("userId");

            if (query == null || query.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("Query cannot be empty", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userId == null || userId.trim().isEmpty()) {
                ChatbotResponse errorResponse = new ChatbotResponse("User ID is required", false);
                return ResponseEntity.badRequest().body(errorResponse);
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
               description = "Retrieve available chatbot features and suggested queries based on user role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Capabilities retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/capabilities/{userId}")
    public ResponseEntity<ChatbotCapabilities> getCapabilities(
            @Parameter(description = "User ID to check capabilities for") @PathVariable String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
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
               description = "Get a list of suggested queries based on user role and current system state")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<Map<String, Object>> getSuggestions(
            @Parameter(description = "User ID to get personalized suggestions") @PathVariable String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
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
}
