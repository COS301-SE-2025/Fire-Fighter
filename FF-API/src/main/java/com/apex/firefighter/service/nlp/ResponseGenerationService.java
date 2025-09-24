package com.apex.firefighter.service.nlp;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for generating natural language responses from query results.
 * Converts structured data back into human-readable text.
 */
@Service
public class ResponseGenerationService {

    /**
     * Generate a natural language response from query results
     * 
     * @param queryResult The result of query processing
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Generated natural language response
     */
    public String generateResponse(QueryProcessingService.QueryResult queryResult, 
                                  QueryContext context,
                                  ResponsePreferences preferences) {
        if (queryResult == null) {
            return generateErrorResponse(ErrorType.QUERY_NOT_UNDERSTOOD, context, preferences);
        }

        String response;

        switch (result.getResultType()) {
            case TICKET_LIST:
                response = generateTicketListResponse(queryResult, context, preferences);
                break;
            case TICKET_DETAILS:
                response = generateTicketDetailsResponse(queryResult, context, preferences);
                break;
            case OPERATION_RESULT:
                response = generateOperationResponse(queryResult, context, preferences);
                break;
            case STATISTICS:
                response = generateStatisticsResponse(queryResult, context, preferences);
                break;
            case HELP:
                response = generateHelpResponse(queryResult, context, preferences);
                break;
            case ERROR:
            default:
                response = generateErrorResponse(ErrorType.INTERNAL_ERROR, context, preferences);
                break;
        }

        return customizeResponse(response, preferences);
    }

    /**
     * Generate a response for ticket list results
     * 
     * @param tickets The list of tickets to describe
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Natural language description of the tickets
     */
    public String generateTicketListResponse(QueryProcessingService.QueryResult result,
                                            QueryContext context,
                                            ResponsePreferences preferences) {
        List<Ticket> tickets = (List<Ticket>) result.getData();
        if (tickets == null || tickets.isEmpty()) {
            return "No tickets found matching your query.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Here are your tickets:\n");
        for (Ticket t : tickets) {
            sb.append("• [").append(t.getTicketId()).append("] ")
            .append(t.getStatus()).append(" - ")
            .append(t.getDescription()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate a response for single ticket details
     * 
     * @param queryResult The query result containing ticket details
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Natural language description of the ticket
     */
    public String generateTicketDetailsResponse(QueryProcessingService.QueryResult queryResult,
                                                QueryContext context,
                                                ResponsePreferences preferences) {
        List<Ticket> tickets = (List<Ticket>) queryResult.getData();
        if (tickets == null || tickets.isEmpty()) {
            return "No ticket details available.";
        }

        Ticket ticket = tickets.get(0); // assume single ticket for details
        StringBuilder sb = new StringBuilder();

        sb.append("Ticket [").append(ticket.getTicketId()).append("]\n");
        sb.append("Status: ").append(ticket.getStatus()).append("\n");
        sb.append("Priority: ").append(ticket.getPriority()).append("\n");
        sb.append("Owner: ").append(ticket.getUserId()).append("\n");
        sb.append("Description: ").append(ticket.getDescription()).append("\n");

        if (ticket.getEmergencyType() != null) {
            sb.append("Emergency Type: ").append(ticket.getEmergencyType()).append("\n");
        }
        if (ticket.getDuration() != null) {
            sb.append("Duration: ").append(ticket.getDuration()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate a response for operation results
     * 
     * @param queryResult The query result containing operation outcome
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Natural language description of the operation result
     */
    public String generateOperationResponse(QueryProcessingService.QueryResult queryResult,
                                            QueryContext context,
                                            ResponsePreferences preferences) {
        Object data = queryResult.getData();

        if (data instanceof Ticket) {
            Ticket ticket = (Ticket) data;
            return "Operation successful. Ticket [" + ticket.getTicketId() + "] is now "
                    + ticket.getStatus() + ".";
        }

        if (data instanceof List<?>) {
            List<Ticket> tickets = (List<Ticket>) data;
            return "Operation successful on " + tickets.size() + " ticket(s).";
        }

        return "The operation was completed successfully.";
    }

    /**
     * Generate error responses in natural language
     * 
     * @param errorType The type of error that occurred
     * @param errorDetails Additional error details
     * @param originalQuery The original query that caused the error
     * @return User-friendly error message
     */
    public String generateErrorResponse(ErrorType errorType, 
                                       String errorDetails, 
                                       String originalQuery) {
        // TODO: Implement error response generation
        return null;
    }

    /**
     * Generate help responses
     * 
     * @param helpType The type of help requested
     * @param userRole The user's role (affects available features)
     * @return Help information in natural language
     */
    public String generateHelpResponse(HelpType helpType, String userRole) {
        // TODO: Implement help response generation
        return null;
    }

    /**
     * Generate statistical summary responses
     * 
     * @param statistics The statistical data to summarize
     * @param userRole The user's role
     * @return Natural language summary of statistics
     */
    public String generateStatisticsResponse(Map<String, Object> statistics, String userRole) {
        // TODO: Implement statistics response generation
        return null;
    }

    /**
     * Customize response based on user preferences and role
     * 
     * @param baseResponse The base response text
     * @param userRole The user's role
     * @param preferences User preferences for response style
     * @return Customized response
     */
    public String customizeResponse(String baseResponse, 
                                   String userRole, 
                                   ResponsePreferences preferences) {
        // TODO: Implement response customization logic
        return null;
    }

    /**
     * Context information for query processing
     */
    public static class QueryContext {
        private String originalQuery;
        private IntentRecognitionService.IntentType intent;
        private Map<String, Object> filters;
        private String timeframe;
        private boolean includeDetails;

        public QueryContext() {}

        public QueryContext(String originalQuery, IntentRecognitionService.IntentType intent) {
            this.originalQuery = originalQuery;
            this.intent = intent;
        }

        // Getters and setters
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }
        
        public IntentRecognitionService.IntentType getIntent() { return intent; }
        public void setIntent(IntentRecognitionService.IntentType intent) { this.intent = intent; }
        
        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
        
        public String getTimeframe() { return timeframe; }
        public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
        
        public boolean isIncludeDetails() { return includeDetails; }
        public void setIncludeDetails(boolean includeDetails) { this.includeDetails = includeDetails; }
    }

    /**
     * User preferences for response generation
     */
    public static class ResponsePreferences {
        private ResponseStyle style;
        private boolean includeEmojis;
        private boolean verboseMode;
        private String language;
        private int maxResponseLength;

        public ResponsePreferences() {
            this.style = ResponseStyle.PROFESSIONAL;
            this.includeEmojis = false;
            this.verboseMode = false;
            this.language = "en";
            this.maxResponseLength = 500;
        }

        // Getters and setters
        public ResponseStyle getStyle() { return style; }
        public void setStyle(ResponseStyle style) { this.style = style; }
        
        public boolean isIncludeEmojis() { return includeEmojis; }
        public void setIncludeEmojis(boolean includeEmojis) { this.includeEmojis = includeEmojis; }
        
        public boolean isVerboseMode() { return verboseMode; }
        public void setVerboseMode(boolean verboseMode) { this.verboseMode = verboseMode; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public int getMaxResponseLength() { return maxResponseLength; }
        public void setMaxResponseLength(int maxResponseLength) { this.maxResponseLength = maxResponseLength; }
    }

    /**
     * Response style options
     */
    public enum ResponseStyle {
        PROFESSIONAL("professional", "Formal business language"),
        CASUAL("casual", "Informal conversational language"),
        TECHNICAL("technical", "Technical detailed language"),
        CONCISE("concise", "Brief and to the point");

        private final String code;
        private final String description;

        ResponseStyle(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Types of errors that can occur
     */
    public enum ErrorType {
        QUERY_NOT_UNDERSTOOD("query_not_understood", "Could not understand the query"),
        INSUFFICIENT_PERMISSIONS("insufficient_permissions", "User lacks required permissions"),
        INVALID_PARAMETERS("invalid_parameters", "Invalid parameters provided"),
        SYSTEM_ERROR("system_error", "Internal system error"),
        DATA_NOT_FOUND("data_not_found", "Requested data not found"),
        OPERATION_FAILED("operation_failed", "Operation could not be completed");

        private final String code;
        private final String description;

        ErrorType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Types of help that can be provided
     */
    public enum HelpType {
        GENERAL("general", "General help information"),
        COMMANDS("commands", "Available commands"),
        EXAMPLES("examples", "Example queries"),
        FEATURES("features", "Available features"),
        TROUBLESHOOTING("troubleshooting", "Common issues and solutions");

        private final String code;
        private final String description;

        HelpType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
}
