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
            // Validate input
            if (query == null || query.trim().isEmpty()) {
                return new ChatbotResponse("Please ask me a question about tickets or the emergency response system.", false);
            }

            if (userId == null || userId.trim().isEmpty()) {
                return new ChatbotResponse("User authentication required to access ticket information.", false);
            }

            // Check if AI service is configured
            if (!geminiAIService.isConfigured()) {
                return new ChatbotResponse("AI service is not properly configured. Please contact your administrator.", false);
            }

            // Get user information
            Optional<User> userOpt = userService.getUserByFirebaseUid(userId);
            if (userOpt.isEmpty()) {
                return new ChatbotResponse("User not found. Please ensure you are properly authenticated.", false);
            }

            User user = userOpt.get();
            boolean isAdmin = user.isAdmin();
            String userRole = isAdmin ? "Administrator" : "User";

            // For basic users, focus on their own tickets only
            String ticketContext;
            if (isAdmin) {
                // Admin can see system-wide information (to be implemented later)
                ticketContext = "Admin access available. Currently showing user-specific context for security.";
            } else {
                // Regular users see only their own tickets
                ticketContext = ticketQueryService.getUserTicketContext(query, userId);
            }

            // Generate AI response with context
            String aiResponse = geminiAIService.generateResponseWithContext(query, userRole, ticketContext);

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
        
        // Include user ticket context (for now, admin sees their own tickets too)
        context.append(ticketQueryService.getUserTicketContext(query, userId));
        
        // Add admin-specific context
        context.append("\nAdmin Functions Available:\n");
        context.append("- View all tickets and statistics\n");
        context.append("- Export ticket data\n");
        context.append("- Manage user access\n");
        context.append("- System monitoring\n");
        
        return context.toString();
    }

    /**
     * Get suggested queries based on user role
     */
    private String[] getSuggestedQueries(boolean isAdmin) {
        if (isAdmin) {
            return new String[]{
                "Show me active tickets",
                "What's the current ticket summary?",
                "How many fire emergencies are active?",
                "Show recent ticket activity",
                "What tickets need attention?",
                "Export ticket statistics"
            };
        } else {
            return new String[]{
                "Show my tickets",
                "What tickets am I assigned to?",
                "Do I have any active emergencies?",
                "Help with ticket status",
                "How do I update a ticket?"
            };
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
