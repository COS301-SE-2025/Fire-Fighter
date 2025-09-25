package com.apex.firefighter.service.nlp;

import com.apex.firefighter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Natural Language Processing service that orchestrates all NLP operations
 * for ticket management and querying through natural language.
 *
 * This service follows a clean pipeline architecture:
 * 1. Input validation and normalization
 * 2. User authorization and role determination
 * 3. Intent recognition from natural language
 * 4. Entity extraction and validation
 * 5. Query processing and business logic execution
 * 6. Response generation and formatting
 */
@Service
public class NLPService {

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Autowired
    private EntityExtractionService entityExtractionService;

    @Autowired
    private QueryProcessingService queryProcessingService;

    @Autowired
    private ResponseGenerationService responseGenerationService;

    @Autowired
    private UserService userService;

    // Setter methods for testing
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setIntentRecognitionService(IntentRecognitionService intentRecognitionService) {
        this.intentRecognitionService = intentRecognitionService;
    }

    public void setEntityExtractionService(EntityExtractionService entityExtractionService) {
        this.entityExtractionService = entityExtractionService;
    }

    public void setQueryProcessingService(QueryProcessingService queryProcessingService) {
        this.queryProcessingService = queryProcessingService;
    }

    public void setResponseGenerationService(ResponseGenerationService responseGenerationService) {
        this.responseGenerationService = responseGenerationService;
    }



    /**
     * Process a natural language query from a user
     *
     * @param query The natural language query
     * @param userId The Firebase UID of the user
     * @return NLPResponse containing the processed result
     */
    public NLPResponse processQuery(String query, String userId) {
        return processQuery(query, userId, null);
    }

    /**
     * Process a natural language query with admin flag from JWT
     *
     * @param query The natural language query
     * @param userId The Firebase UID of the user
     * @param isAdminFromJWT Admin flag from JWT token (null to use database lookup)
     * @return NLPResponse containing the processed result
     */
    public NLPResponse processQuery(String query, String userId, Boolean isAdminFromJWT) {
        try {
            // Step 1: Validate input
            NLPResponse validationResult = validateInput(query, userId);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }

            // Step 2: Recognize intent
            IntentRecognitionService.Intent intent = recognizeIntent(query);
            if (intent == null) {
                return new NLPResponse("Could not understand query: " + query, false);
            }

            // Step 3: Determine user authorization
            String userRole = determineUserRole(userId, isAdminFromJWT);
            if (!isIntentAllowed(intent, userRole)) {
                return new NLPResponse("Permission denied for intent: " + intent.getType().getCode(), false);
            }

            // Step 4: Extract and validate entities
            EntityExtractionService.ExtractedEntities entities = extractAndValidateEntities(query, intent);
            if (entities == null) {
                return new NLPResponse("Failed to extract or validate entities", false);
            }

            // Step 5: Process query
            QueryProcessingService.QueryResult result = processQueryLogic(intent, entities, userId, userRole);
            if (result == null || !result.isSuccess()) {
                String message = result != null ? result.getMessage() : "Query processing failed";
                return new NLPResponse(message, false);
            }

            // Step 6: Generate response
            String responseText = generateResponse(result);
            return new NLPResponse(responseText, true, result.getData());

        } catch (IllegalArgumentException e) {
            return new NLPResponse("Invalid input: " + e.getMessage(), false);
        } catch (SecurityException e) {
            return new NLPResponse("Access denied: " + e.getMessage(), false);
        } catch (Exception e) {
            return new NLPResponse("Error processing query: " + e.getMessage(), false);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Validate input parameters
     */
    private NLPResponse validateInput(String query, String userId) {
        if (query == null || query.trim().isEmpty()) {
            return new NLPResponse("Query cannot be null or empty", false);
        }
        if (userId == null || userId.trim().isEmpty()) {
            return new NLPResponse("User ID cannot be null or empty", false);
        }
        return new NLPResponse("Input validation passed", true);
    }

    /**
     * Recognize intent from query
     */
    private IntentRecognitionService.Intent recognizeIntent(String query) {
        if (intentRecognitionService == null) {
            return null;
        }

        IntentRecognitionService.Intent intent = intentRecognitionService.recognizeIntent(query);
        System.out.println("🔵 NLP SERVICE: Intent recognition result: " +
            (intent != null ? intent.getType() : "null"));

        return intent;
    }

    /**
     * Determine user role with JWT admin flag priority
     */
    private String determineUserRole(String userId, Boolean isAdminFromJWT) {
        // First priority: JWT admin flag
        if (isAdminFromJWT != null && isAdminFromJWT) {
            System.out.println("🔐 NLP SERVICE: Using JWT admin flag - User role: ADMIN");
            return "ADMIN";
        }

        // Second priority: Database role
        try {
            String userRole = userService.getUserRole(userId);
            System.out.println("🔐 NLP SERVICE: Database role for " + userId + ": " + userRole);
            if (userRole != null && !userRole.trim().isEmpty()) {
                return userRole;
            }
        } catch (Exception e) {
            System.out.println("⚠️ NLP SERVICE: Failed to get database role: " + e.getMessage());
        }

        // Third priority: Authorization service check
        try {
            boolean isAuthorized = userService.isUserAuthorized(userId);
            if (isAuthorized) {
                System.out.println("🔐 NLP SERVICE: User authorized via service - Role: ADMIN");
                return "ADMIN";
            }
        } catch (Exception e) {
            System.out.println("⚠️ NLP SERVICE: Authorization check failed: " + e.getMessage());
        }

        // Default to USER
        System.out.println("🔐 NLP SERVICE: Defaulting to USER role");
        return "USER";
    }

    /**
     * Check if intent is allowed for user role
     */
    private boolean isIntentAllowed(IntentRecognitionService.Intent intent, String userRole) {
        boolean allowed = intentRecognitionService.isIntentAllowed(intent.getType(), userRole);
        System.out.println("🔐 NLP SERVICE: Intent " + intent.getType().getCode() +
            " allowed for role " + userRole + ": " + allowed);
        return allowed;
    }

    /**
     * Extract and validate entities from query
     */
    private EntityExtractionService.ExtractedEntities extractAndValidateEntities(
            String query, IntentRecognitionService.Intent intent) {

        if (entityExtractionService == null) {
            return null;
        }

        EntityExtractionService.ExtractedEntities entities = entityExtractionService.extractEntities(query);
        if (entities == null) {
            return null;
        }

        // Log extracted entities
        logExtractedEntities(entities);

        // Skip validation for CREATE_TICKET operations
        if (intent.getType() == IntentRecognitionService.IntentType.CREATE_TICKET) {
            System.out.println("🔵 NLP SERVICE: Skipping entity validation for CREATE_TICKET intent");
            return entities;
        }

        // Validate entities for other operations
        EntityExtractionService.ValidationResult validation = entityExtractionService.validateEntities(entities);
        if (validation == null || !validation.isValid()) {
            System.out.println("❌ NLP SERVICE: Entity validation failed");
            return null;
        }

        return entities;
    }

    /**
     * Process query logic
     */
    private QueryProcessingService.QueryResult processQueryLogic(
            IntentRecognitionService.Intent intent,
            EntityExtractionService.ExtractedEntities entities,
            String userId, String userRole) {

        if (queryProcessingService == null) {
            return null;
        }

        boolean isAdmin = "ADMIN".equals(userRole);
        System.out.println("🔵 QUERY PROCESSING: Processing intent: " + intent.getType());

        QueryProcessingService.QueryResult result = queryProcessingService.processQuery(intent, entities, userId, isAdmin);
        System.out.println("🔵 NLP SERVICE: Query processing result: " +
            (result != null ? "success=" + result.isSuccess() : "null"));

        return result;
    }

    /**
     * Generate response from query result
     */
    private String generateResponse(QueryProcessingService.QueryResult result) {
        if (responseGenerationService == null) {
            return "Response generation service is not available";
        }

        String responseText = responseGenerationService.generateResponse(result);
        if (responseText == null || responseText.trim().isEmpty()) {
            responseText = "Query processed successfully but no response generated";
        }

        return responseText;
    }

    /**
     * Log extracted entities for debugging
     */
    private void logExtractedEntities(EntityExtractionService.ExtractedEntities entities) {
        System.out.println("🔵 NLP SERVICE: Extracted entities:");
        if (entities.getAllEntities() != null) {
            for (EntityExtractionService.EntityType type : entities.getAllEntities().keySet()) {
                List<EntityExtractionService.Entity> entitiesOfType = entities.getAllEntities().get(type);
                if (entitiesOfType != null && !entitiesOfType.isEmpty()) {
                    System.out.println("  - " + type + ": " + entitiesOfType.size() + " items");
                    for (EntityExtractionService.Entity entity : entitiesOfType) {
                        System.out.println("    * '" + entity.getValue() +
                            "' (normalized: '" + entity.getNormalizedValue() + "')");
                    }
                }
            }
        }
    }

    /**
     * Process an admin-level natural language query with elevated privileges
     * 
     * @param query The natural language query
     * @param userId The Firebase UID of the admin user
     * @return NLPResponse containing the processed result
     */
    public NLPResponse processAdminQuery(String query, String userId) {
        // Input validation
        if (query == null || query.trim().isEmpty()) {
            return new NLPResponse("Admin query cannot be null or empty", false);
        }

        if (userId == null || userId.trim().isEmpty()) {
            return new NLPResponse("User ID cannot be null or empty", false);
        }

        try {
            // Step 1: Trust the controller's admin verification
            // The NLPController.extractAndVerifyAdminUser() already verified admin privileges
            // This follows the same pattern as other services that trust controller authentication

            // Step 2: Recognize intent
            if (intentRecognitionService == null) {
                return new NLPResponse("Intent recognition service is not available", false);
            }

            IntentRecognitionService.Intent intent = intentRecognitionService.recognizeIntent(query);
            if (intent == null) {
                return new NLPResponse("Failed to recognize intent from admin query", false);
            }

            if (!intent.isSuccess()) {
                return new NLPResponse("Could not understand admin query: " + query, false);
            }

            // Step 3: Extract entities
            if (entityExtractionService == null) {
                return new NLPResponse("Entity extraction service is not available", false);
            }

            EntityExtractionService.ExtractedEntities entities = entityExtractionService.extractEntities(query);
            if (entities == null) {
                return new NLPResponse("Failed to extract entities from admin query", false);
            }

            // Skip entity validation for CREATE_TICKET operations since ticket ID is auto-generated
            if (intent.getType() != IntentRecognitionService.IntentType.CREATE_TICKET) {
                EntityExtractionService.ValidationResult validation = entityExtractionService.validateEntities(entities);
                if (validation == null) {
                    return new NLPResponse("Failed to validate extracted entities", false);
                }

                if (!validation.isValid()) {
                    String errorMessage = "Invalid entities in admin query";
                    if (validation.getErrors() != null && !validation.getErrors().isEmpty()) {
                        errorMessage += ": " + String.join(", ", validation.getErrors());
                    }
                    return new NLPResponse(errorMessage, false);
                }
            }

            // Step 4: Process the query with admin privileges
            if (queryProcessingService == null) {
                return new NLPResponse("Query processing service is not available", false);
            }

            QueryProcessingService.QueryResult result = queryProcessingService.processQuery(intent, entities, userId, true);
            if (result == null) {
                return new NLPResponse("Failed to process admin query", false);
            }

            // Step 5: Generate response
            if (responseGenerationService == null) {
                return new NLPResponse("Response generation service is not available", false);
            }

            String responseText = responseGenerationService.generateResponse(result);
            if (responseText == null || responseText.trim().isEmpty()) {
                responseText = "Admin query processed successfully but no response generated";
            }

            return new NLPResponse(responseText, true, result.getData());

        } catch (IllegalArgumentException e) {
            return new NLPResponse("Invalid admin query input: " + e.getMessage(), false);
        } catch (SecurityException e) {
            return new NLPResponse("Admin access denied: " + e.getMessage(), false);
        } catch (Exception e) {
            return new NLPResponse("Error processing admin query: " + e.getMessage(), false);
        }
    }

    /**
     * Get NLP capabilities based on user role
     * 
     * @param userId The Firebase UID of the user
     * @return NLPCapabilities describing what the user can do
     */
    public NLPCapabilities getCapabilities(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }

        try {
            if (userService == null) {
                return null;
            }

            String userRole = userService.getUserRole(userId);
            if (userRole == null) {
                userRole = "GUEST";
            }

            // Create capabilities based on user role
            NLPCapabilities capabilities = new NLPCapabilities();
            capabilities.setAvailable(true);
            capabilities.setAdminAccess("ADMIN".equals(userRole));
            capabilities.setAccessLevel(userRole);

            // Set available intents based on role
            List<String> availableIntents = new ArrayList<>();

            // Basic intents available to all users
            availableIntents.add("SHOW_ACTIVE_TICKETS");
            availableIntents.add("SHOW_CLOSED_TICKETS");
            availableIntents.add("CREATE_TICKET");
            availableIntents.add("GET_HELP");

            // Admin-only intents
            if ("ADMIN".equals(userRole)) {
                availableIntents.add("SHOW_ALL_TICKETS");
                availableIntents.add("UPDATE_TICKET_STATUS");
                availableIntents.add("ASSIGN_TICKET");
                availableIntents.add("EXPORT_DATA");
            }

            capabilities.setSupportedIntents(availableIntents.toArray(new String[0]));

            // Set supported entities
            String[] supportedEntities = {"TICKET_ID", "STATUS", "DATE", "EMERGENCY_TYPE", "USER_NAME", "DESCRIPTION"};
            capabilities.setSupportedEntities(supportedEntities);

            return capabilities;

        } catch (Exception e) {
            // Return null on error - caller should handle gracefully
            return null;
        }
    }

    /**
     * Get suggested queries for the user based on their role and current context
     * 
     * @param userId The Firebase UID of the user
     * @return NLPSuggestions containing suggested queries
     */
    public NLPSuggestions getSuggestions(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }

        try {
            if (userService == null) {
                return null;
            }

            String userRole = userService.getUserRole(userId);
            if (userRole == null) {
                userRole = "GUEST";
            }

            // Create suggestions based on user role
            NLPSuggestions suggestions = new NLPSuggestions();
            suggestions.setAvailable(true);
            suggestions.setUserRole(userRole);

            // Basic suggestions for all users
            List<String> suggestedQueries = new ArrayList<>();
            suggestedQueries.add("Show my active tickets");
            suggestedQueries.add("Show my closed tickets");
            suggestedQueries.add("Create a new ticket");
            suggestedQueries.add("Help me with ticket management");

            // Admin-specific suggestions
            if ("ADMIN".equals(userRole)) {
                suggestedQueries.add("Show all tickets");
                suggestedQueries.add("Show tickets by status");
                suggestedQueries.add("Update ticket status");
                suggestedQueries.add("Assign ticket to user");
                suggestedQueries.add("Export ticket data");
            }

            suggestions.setSuggestedQueries(suggestedQueries.toArray(new String[0]));

            // Set examples
            String[] examples = {
                "Show my tickets from last week",
                "Create a ticket for HR emergency",
                "Update ticket T123 to closed",
                "Show all open tickets"
            };
            suggestions.setExamples(examples);

            // Set quick actions
            List<String> quickActions = new ArrayList<>();
            quickActions.add("View active tickets");
            quickActions.add("Create ticket");

            if ("ADMIN".equals(userRole)) {
                quickActions.add("View all tickets");
                quickActions.add("Manage tickets");
            }

            suggestions.setQuickActions(quickActions.toArray(new String[0]));

            return suggestions;

        } catch (Exception e) {
            // Return null on error - caller should handle gracefully
            return null;
        }
    }

    /**
     * Check if the NLP service is properly configured and operational
     * 
     * @return true if service is ready, false otherwise
     */
    public boolean isServiceHealthy() {
        try {
            // Check if all required services are available
            if (intentRecognitionService == null) {
                return false;
            }

            if (entityExtractionService == null) {
                return false;
            }

            if (queryProcessingService == null) {
                return false;
            }

            if (responseGenerationService == null) {
                return false;
            }

            if (userService == null) {
                return false;
            }

            // Test basic functionality with a simple query
            try {
                IntentRecognitionService.Intent testIntent = intentRecognitionService.recognizeIntent("help");
                if (testIntent == null) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            // Test entity extraction
            try {
                EntityExtractionService.ExtractedEntities testEntities = entityExtractionService.extractEntities("test query");
                if (testEntities == null) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Response object for NLP interactions
     */
    public static class NLPResponse {
        private String message;
        private boolean success;
        private String userRole;
        private LocalDateTime timestamp;
        private String queryType;
        private Object data; // Additional structured data if needed

        public NLPResponse() {}

        public NLPResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
            this.timestamp = LocalDateTime.now();
        }

        public NLPResponse(String message, boolean success, String userRole, String queryType) {
            this.message = message;
            this.success = success;
            this.userRole = userRole;
            this.queryType = queryType;
            this.timestamp = LocalDateTime.now();
        }

        public NLPResponse(String message, boolean success, Object data) {
            this.message = message;
            this.success = success;
            this.data = data;
            this.timestamp = LocalDateTime.now();
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
        
        public String getQueryType() { return queryType; }
        public void setQueryType(String queryType) { this.queryType = queryType; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

    /**
     * Capabilities object for NLP features
     */
    public static class NLPCapabilities {
        private boolean available;
        private boolean adminAccess;
        private String accessLevel;
        private String[] supportedIntents;
        private String[] supportedEntities;

        public NLPCapabilities() {}

        public NLPCapabilities(boolean available, boolean adminAccess, String accessLevel) {
            this.available = available;
            this.adminAccess = adminAccess;
            this.accessLevel = accessLevel;
        }

        // Getters and setters
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public boolean isAdminAccess() { return adminAccess; }
        public void setAdminAccess(boolean adminAccess) { this.adminAccess = adminAccess; }
        
        public String getAccessLevel() { return accessLevel; }
        public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
        
        public String[] getSupportedIntents() { return supportedIntents; }
        public void setSupportedIntents(String[] supportedIntents) { this.supportedIntents = supportedIntents; }
        
        public String[] getSupportedEntities() { return supportedEntities; }
        public void setSupportedEntities(String[] supportedEntities) { this.supportedEntities = supportedEntities; }
    }

    /**
     * Suggestions object for query recommendations
     */
    public static class NLPSuggestions {
        private boolean available;
        private String userRole;
        private String[] suggestedQueries;
        private String[] examples;
        private String[] quickActions;

        public NLPSuggestions() {}

        public NLPSuggestions(boolean available, String userRole, String[] suggestedQueries) {
            this.available = available;
            this.userRole = userRole;
            this.suggestedQueries = suggestedQueries;
        }

        // Getters and setters
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public String getUserRole() { return userRole; }
        public void setUserRole(String userRole) { this.userRole = userRole; }
        
        public String[] getSuggestedQueries() { return suggestedQueries; }
        public void setSuggestedQueries(String[] suggestedQueries) { this.suggestedQueries = suggestedQueries; }
        
        public String[] getExamples() { return examples; }
        public void setExamples(String[] examples) { this.examples = examples; }
        
        public String[] getQuickActions() { return quickActions; }
        public void setQuickActions(String[] quickActions) { this.quickActions = quickActions; }
    }
}
