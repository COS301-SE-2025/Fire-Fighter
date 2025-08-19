package com.apex.firefighter.service.ai;

import com.apex.firefighter.model.User;
import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ChatbotService {

    @Autowired
    private GeminiAIService geminiAIService;

    @Autowired
    private TicketQueryService ticketQueryService;

    @Autowired
    private UserService userService;

    /**
     * Process user query and generate AI response
     */
    public ChatbotResponse processQuery(String query, String userId) {
        try {
            System.out.println("🤖 CHATBOT SERVICE: Processing query");
            System.out.println("🤖 CHATBOT SERVICE: Query: " + query);
            System.out.println("🤖 CHATBOT SERVICE: User ID: " + userId);
            
            // Validate input
            if (query == null || query.trim().isEmpty()) {
                System.out.println("🤖 CHATBOT SERVICE: Query is empty");
                return new ChatbotResponse("Please ask me a question about tickets or the emergency response system.", false);
            }

            if (userId == null || userId.trim().isEmpty()) {
                System.out.println("🤖 CHATBOT SERVICE: User ID is empty");
                return new ChatbotResponse("User authentication required to access ticket information.", false);
            }

            // Check if AI service is configured
            if (!geminiAIService.isConfigured()) {
                System.out.println("🤖 CHATBOT SERVICE: AI service not configured");
                return new ChatbotResponse("AI service is not properly configured. Please contact your administrator.", false);
            }

            System.out.println("🤖 CHATBOT SERVICE: Getting user information");
            // Get user information
            Optional<User> userOpt = userService.getUserByFirebaseUid(userId);
            if (userOpt.isEmpty()) {
                System.out.println("🤖 CHATBOT SERVICE: User not found");
                return new ChatbotResponse("User not found. Please ensure you are properly authenticated.", false);
            }

            User user = userOpt.get();
            boolean isAdmin = user.isAdmin();
            String userRole = isAdmin ? "Administrator" : "User";
            
            System.out.println("🤖 CHATBOT SERVICE: User found - " + user.getUsername() + " (admin: " + isAdmin + ")");

            // Focus ONLY on ticket data - no navigation guidance
            String ticketContext = "";

            // Add request creation context if query is about creating requests
            if (containsRequestCreationKeywords(query)) {
                System.out.println("🤖 CHATBOT SERVICE: Getting request creation context");
                String requestContext = ticketQueryService.getRequestCreationContext(query);
                if (!requestContext.isEmpty()) {
                    ticketContext = requestContext;
                }
            }
            // For ALL other queries, show user's ticket data
            else {
                System.out.println("🤖 CHATBOT SERVICE: Getting user ticket context");
                // BOTH admin and regular users see their own ticket data
                ticketContext = ticketQueryService.getUserTicketContext(query, userId);
                System.out.println("🔍 DEBUG: Generated ticket context for query '" + query + "': " + ticketContext.substring(0, Math.min(100, ticketContext.length())) + "...");

                // Add admin note if user is admin
                if (isAdmin) {
                    ticketContext = "Note: You have admin privileges for system-wide access.\n\n" + ticketContext;
                }
            }

            System.out.println("🤖 CHATBOT SERVICE: Generating AI response");
            // Generate AI response with context
            String aiResponse = geminiAIService.generateResponseWithContext(query, userRole, ticketContext);
            System.out.println("🤖 CHATBOT SERVICE: AI response generated: " + aiResponse.substring(0, Math.min(100, aiResponse.length())) + "...");

            // Create response with metadata
            return new ChatbotResponse(aiResponse, true, userRole, LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("Error processing chatbot query: " + e.getMessage());
            return new ChatbotResponse("I'm experiencing technical difficulties. Please try again later.", false);
        }
    }

    /**
     * Process admin-specific query with enhanced privileges
     */
    public ChatbotResponse processAdminQuery(String query, String userId) {
        try {
            Optional<User> userOpt = userService.getUserByFirebaseUid(userId);
            if (userOpt.isEmpty() || !userOpt.get().isAdmin()) {
                return new ChatbotResponse("Administrator privileges required for this query.", false);
            }

            // Enhanced context for admin queries
            String enhancedContext = buildAdminContext(query, userId);
            String aiResponse = geminiAIService.generateResponseWithContext(query, "Administrator", enhancedContext);

            return new ChatbotResponse(aiResponse, true, "Administrator", LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("Error processing admin chatbot query: " + e.getMessage());
            return new ChatbotResponse("Error processing admin query. Please try again.", false);
        }
    }

    /**
     * Get chatbot capabilities based on user role
     */
    public ChatbotCapabilities getCapabilities(String userId) {
        try {
            Optional<User> userOpt = userService.getUserByFirebaseUid(userId);
            if (userOpt.isEmpty()) {
                return new ChatbotCapabilities(false, false, "Authentication required");
            }

            User user = userOpt.get();
            boolean isAdmin = user.isAdmin();

            return new ChatbotCapabilities(
                true,
                isAdmin,
                isAdmin ? "Full system access" : "Personal ticket access",
                getSuggestedQueries(isAdmin)
            );

        } catch (Exception e) {
            return new ChatbotCapabilities(false, false, "Error determining capabilities");
        }
    }

    /**
     * Build enhanced context for admin queries
     */
    private String buildAdminContext(String query, String userId) {
        StringBuilder context = new StringBuilder();
        
        // Include user ticket context - admin sees their own tickets
        String userTicketContext = ticketQueryService.getUserTicketContext(query, userId);
        context.append("Admin Query - Your Personal Tickets:\n");
        context.append(userTicketContext);
        
        // Add admin-specific context
        context.append("\nAdmin Functions Available:\n");
        context.append("- View all tickets and statistics\n");
        context.append("- Export ticket data\n");
        context.append("- Manage user access\n");
        context.append("- System monitoring\n");
        
        return context.toString();
    }

    /**
     * Check if query contains request creation keywords (token-efficient detection)
     */
    private boolean containsRequestCreationKeywords(String query) {
        String lowerQuery = query.toLowerCase();
        return (lowerQuery.contains("create") || lowerQuery.contains("new") ||
                lowerQuery.contains("submit") || lowerQuery.contains("form")) &&
               (lowerQuery.contains("request") || lowerQuery.contains("emergency") ||
                lowerQuery.contains("access"));
    }



    /**
     * Get suggested queries based on user role
     */
    private String[] getSuggestedQueries(boolean isAdmin) {
        if (isAdmin) {
            return new String[]{
                "Show system-wide access logs",
                "Show recent access activity",
                "What's the current access summary?",
                "Show active access tickets",
                "Show my access tickets",
                "What access tickets need review?",
                "Show latest system activity",
                "How do I create a new emergency request?"
            };
        } else {
            return new String[]{
                "Show my access tickets",
                "How much time is remaining on my tickets?",
                "Show my recent access activity",
                "What elevated access do I currently have?",
                "When do my active tickets expire?",
                "Show my active tickets",
                "Do I have any security incident access?",
                "How do I create a new emergency request?"
            };
        }
    }

    /**
     * Debug method to see what context is being generated
     */
    public String getDebugContext(String query, String userId) {
        try {
            Optional<User> userOpt = userService.getUserByFirebaseUid(userId);
            if (userOpt.isEmpty()) {
                return "DEBUG: User not found for ID: " + userId;
            }

            User user = userOpt.get();
            boolean isAdmin = user.isAdmin();

            // Generate the same context as the main query method
            String ticketContext = "";
            if (containsRequestCreationKeywords(query)) {
                String requestContext = ticketQueryService.getRequestCreationContext(query);
                if (!requestContext.isEmpty()) {
                    ticketContext = requestContext;
                }
            } else {
                ticketContext = ticketQueryService.getUserTicketContext(query, userId);
                if (isAdmin) {
                    ticketContext = "Note: You have admin privileges for system-wide access.\n\n" + ticketContext;
                }
            }

            StringBuilder debug = new StringBuilder();
            debug.append("DEBUG CONTEXT GENERATION:\n");
            debug.append("Query: ").append(query).append("\n");
            debug.append("User ID: ").append(userId).append("\n");
            debug.append("Is Admin: ").append(isAdmin).append("\n");
            debug.append("Request Creation Keywords: ").append(containsRequestCreationKeywords(query)).append("\n");
            debug.append("Generated Context Length: ").append(ticketContext.length()).append("\n");
            debug.append("Generated Context:\n").append(ticketContext).append("\n");

            return debug.toString();

        } catch (Exception e) {
            return "DEBUG ERROR: " + e.getMessage();
        }
    }

    /**
     * Response object for chatbot interactions
     */
    public static class ChatbotResponse {
        private String message;
        private boolean success;
        private String userRole;
        private LocalDateTime timestamp;

        public ChatbotResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
            this.timestamp = LocalDateTime.now();
        }

        public ChatbotResponse(String message, boolean success, String userRole, LocalDateTime timestamp) {
            this.message = message;
            this.success = success;
            this.userRole = userRole;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getUserRole() { return userRole; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getFormattedTimestamp() {
            return timestamp != null ? timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        }
    }

    /**
     * Check if Gemini AI service is properly configured
     * Used by health check endpoint to verify service availability
     */
    public boolean isGeminiConfigured() {
        return geminiAIService.isConfigured();
    }

    /**
     * Capabilities object for chatbot features
     */
    public static class ChatbotCapabilities {
        private boolean available;
        private boolean adminAccess;
        private String accessLevel;
        private String[] suggestedQueries;

        public ChatbotCapabilities(boolean available, boolean adminAccess, String accessLevel) {
            this.available = available;
            this.adminAccess = adminAccess;
            this.accessLevel = accessLevel;
        }

        public ChatbotCapabilities(boolean available, boolean adminAccess, String accessLevel, String[] suggestedQueries) {
            this.available = available;
            this.adminAccess = adminAccess;
            this.accessLevel = accessLevel;
            this.suggestedQueries = suggestedQueries;
        }

        // Getters and setters
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public boolean isAdminAccess() { return adminAccess; }
        public void setAdminAccess(boolean adminAccess) { this.adminAccess = adminAccess; }
        
        public String getAccessLevel() { return accessLevel; }
        public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
        
        public String[] getSuggestedQueries() { return suggestedQueries; }
        public void setSuggestedQueries(String[] suggestedQueries) { this.suggestedQueries = suggestedQueries; }
    }
}
