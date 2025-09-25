package com.apex.firefighter.service.nlp;

import org.springframework.stereotype.Service;
import com.apex.firefighter.model.Ticket;

import java.util.*;

/**
 * Service responsible for generating natural language responses from query results.
 * Converts structured data back into human-readable text.
 */
@SuppressWarnings("unchecked")
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
            return generateErrorResponse(queryResult, context, preferences);
        }

        String response;

        switch (queryResult.getResultType()) {
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
            case INFORMATION:
                response = generateInformationResponse(queryResult, context, preferences);
                break;
            case ERROR:
            default:
                response = generateErrorResponse(queryResult, context, preferences);
                break;
        }

        return customizeResponse(response, context, preferences);
    }

    public String generateResponse(QueryProcessingService.QueryResult result) {
        if (result == null) {
            return generateErrorResponse(ErrorType.QUERY_NOT_UNDERSTOOD);
        }

        switch (result.getResultType()) {
            case TICKET_LIST:
                List<Ticket> tickets = (List<Ticket>) result.getData();
                if (tickets == null || tickets.isEmpty()) {
                    return "No tickets found.";
                }
                StringBuilder listResponse = new StringBuilder("Here are your tickets:\n");
                for (Ticket t : tickets) {
                    listResponse.append("• [").append(t.getTicketId()).append("] ")
                                .append(t.getStatus()).append(" - ")
                                .append(t.getDescription()).append("\n");
                }
                return listResponse.toString();

            case TICKET_DETAILS:
                Ticket ticket = (Ticket) result.getData();
                if (ticket == null) {
                    return "No details found for that ticket.";
                }
                return "Ticket [" + ticket.getTicketId() + "]\n"
                    + "Status: " + ticket.getStatus() + "\n"
                    + "Owner: " + ticket.getUserId() + "\n"
                    + "Description: " + ticket.getDescription() + "\n"
                    + (ticket.getEmergencyType() != null ? "Emergency Type: " + ticket.getEmergencyType() + "\n" : "")
                    + (ticket.getDuration() != null ? "Duration: " + ticket.getDuration() + " minutes\n" : "");

            case OPERATION_RESULT:
                if (!result.isSuccess()) {
                    return "❌ Operation failed: " + (result.getMessage() != null ? result.getMessage() : "Unknown error");
                }
                if (result.getData() instanceof Ticket) {
                    Ticket updated = (Ticket) result.getData();
                    return "✅ Operation successful. Ticket [" + updated.getTicketId() + "] is now " + updated.getStatus() + ".";
                }
                if (result.getData() instanceof List<?>) {
                    return "✅ Operation successful on " + ((List<?>) result.getData()).size() + " ticket(s).";
                }
                return "✅ Operation completed successfully.";

            case STATISTICS:
                return "Statistics: " + result.getData();

            case HELP:
                // Return the actual message from the QueryResult instead of hardcoded text
                return result.getMessage() != null ? result.getMessage() : "You can ask me to show, create, update, or close tickets, get system stats, or export data.";

            case INFORMATION:
                // Return the actual message from the QueryResult for information responses
                return result.getMessage() != null ? result.getMessage() : "Information not available.";

            case ERROR:
            default:
                return generateErrorResponse(ErrorType.SYSTEM_ERROR);
        }
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
        // Check metadata first for operation status
        if (queryResult.getMetadata() != null) {
            Object successFlag = queryResult.getMetadata().get("success");
            if (successFlag instanceof Boolean && !(Boolean) successFlag) {
                String reason = (String) queryResult.getMetadata().getOrDefault("reason", "Unknown error");
                return "❌ Operation failed: " + reason;
            }
        }

        // Handle success cases                                       
    
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

        if (data == null) {
        return "⚠️ Operation completed, but no result was returned.";
        }

        return "✅ The operation was completed successfully.";
    }

    /**
     * Generate error responses in natural language
     * 
     * @param queryResult The query result containing error information
     * @param context Context about the original query
     * @param preference User preferences for response style
     * @return User-friendly error message
     */
    public String generateErrorResponse(QueryProcessingService.QueryResult queryResult,
                                        QueryContext context,
                                        ResponsePreferences preferences) {
        ErrorType errorType = ErrorType.SYSTEM_ERROR;

        if (queryResult.getMetadata() != null &&
            queryResult.getMetadata().containsKey("errorType")) {
            errorType = (ErrorType) queryResult.getMetadata().get("errorType");
        }

        switch (errorType) {
            case INSUFFICIENT_PERMISSIONS:
                return "❌ You don't have permission to perform this action.";
            case DATA_NOT_FOUND:
                return "⚠️ I couldn't find any matching data.";
            case QUERY_NOT_UNDERSTOOD:
                return "🤔 I didn't understand that request. Try rephrasing.";
            case INVALID_PARAMETERS:
                return "⚠️ The input provided was invalid. Please check and try again.";
            case OPERATION_FAILED:
                return "❌ The operation could not be completed.";
            case SYSTEM_ERROR:
            default:
                return "⚠️ Something went wrong on our side. Please try again later.";
        }
    }

    private String generateErrorResponse(ErrorType type) {
        switch (type) {
            case QUERY_NOT_UNDERSTOOD:
                return "Sorry, I couldn't understand your query.";
            case SYSTEM_ERROR:
            default:
                return "An internal error occurred.";
        }
    }

    /**
     * Generate information responses
     *
     * @param queryResult The query result containing information
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Information in natural language
     */
    public String generateInformationResponse(QueryProcessingService.QueryResult queryResult,
                                            QueryContext context,
                                            ResponsePreferences preferences) {
        // For information responses, return the message directly from the QueryResult
        return queryResult.getMessage() != null ? queryResult.getMessage() : "Information not available.";
    }

    /**
     * Generate help responses
     *
     * @param queryResult The query result containing help information
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Help information in natural language
     */
    public String generateHelpResponse(QueryProcessingService.QueryResult queryResult,
                                        QueryContext context,
                                        ResponsePreferences preferences) {
        HelpType helpType = HelpType.GENERAL;
        if (queryResult.getMetadata() != null &&
            queryResult.getMetadata().containsKey("helpType")) {
            helpType = (HelpType) queryResult.getMetadata().get("helpType");
        }

        switch (helpType) {
            case COMMANDS:
                return "Available commands:\n" +
                        "- show my tickets\n" +
                        "- create ticket <description>\n" +
                        "- update status of <ticketId>\n" +
                        "- close ticket <ticketId>";
            case EXAMPLES:
                return "Examples:\n" +
                        "“show active tickets”\n" +
                        "“create ticket printer not working”";
            case FEATURES:
                return "Features:\n" +
                        "- Ticket tracking\n" +
                        "- Priority management\n" +
                        "- System statistics\n" +
                        "- Data export";
            case TROUBLESHOOTING:
                return "Troubleshooting:\n" +
                        "- If commands aren’t recognized, try rephrasing.\n" +
                        "- Check your permissions for admin actions.";
            case GENERAL:
            default:
                return "You can ask me to show, create, update, or close tickets, " +
                    "get system stats, or export data. Try “help commands” for more.";
        }
    }

    /**
     * Generate statistical summary responses
     * 
     * @param queryResult The query result containing statistics
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Natural language summary of statistics
     */
    public String generateStatisticsResponse(QueryProcessingService.QueryResult queryResult,
                                            QueryContext context,
                                            ResponsePreferences preferences) {
        Map<String, Object> stats = (Map<String, Object>) queryResult.getData();
        if (stats == null || stats.isEmpty()) {
            return "No statistics available.";
        }

        StringBuilder sb = new StringBuilder("📊 System Statistics:\n");
        stats.forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("\n"));
        return sb.toString();
    }

    /**
     * Customize response based on user preferences and role
     * 
     * @param baseResponse The base response text
     * @param context Context about the original query
     * @param preferences User preferences for response style
     * @return Customized response
     */
    public String customizeResponse(String baseResponse, 
                                QueryContext context, 
                                ResponsePreferences preferences) {
        String response = baseResponse == null ? "" : baseResponse.trim();
        if (preferences == null) preferences = new ResponsePreferences();

        if (preferences.isIncludeEmojis()) {
            String emoji = pickEmojiForIntent(context != null ? context.getIntent() : null);
            if (emoji != null && !response.startsWith(emoji)) {
                response = emoji + " " + response;
            }
        }

        if (preferences.isVerboseMode() && context != null) {
            StringBuilder meta = new StringBuilder();

            if (context.getOriginalQuery() != null && !context.getOriginalQuery().isBlank()) {
                meta.append("\n\nQuery: ").append(context.getOriginalQuery());
            }

            if (context.getFilters() != null && !context.getFilters().isEmpty()) {
                meta.append("\nFilters: ").append(humanizeFilters(context.getFilters()));
            }

            if (context.getTimeframe() != null && !context.getTimeframe().isBlank()) {
                meta.append("\nTimeframe: ").append(context.getTimeframe());
            }

            response += meta.toString();
        }

        switch (preferences.getStyle()) {
            case CONCISE:
                response = toConcise(response);
                break;
            case CASUAL:
                response = toCasual(response, preferences.isIncludeEmojis());
                break;
            case TECHNICAL:
                response = toTechnical(response);
                break;
            case PROFESSIONAL:
            default:
                response = toProfessional(response);
                break;
        }

        int max = Math.max(80, preferences.getMaxResponseLength()); // keep sane minimum
        if (response.length() > max) {
            response = response.substring(0, Math.max(0, max - 1)) + "…";
        }

        return response;
    }

    /* ----------Helpers---------- */
    private String pickEmojiForIntent(IntentRecognitionService.IntentType intent) {
        if (intent == null) return null;
        switch (intent) {
            case SHOW_TICKETS:
            case SHOW_ACTIVE_TICKETS:
            case SHOW_COMPLETED_TICKETS:
            case SEARCH_TICKETS:
                return "📋";
            case GET_TICKET_DETAILS:
                return "🔎";
            case CREATE_TICKET:
                return "🆕";
            case UPDATE_TICKET_STATUS:
            case CLOSE_TICKET:
            case ASSIGN_TICKET:
                return "✅";
            case SHOW_ALL_TICKETS:
            case GET_SYSTEM_STATS:
                return "📊";
            case EXPORT_TICKETS:
                return "⬇️";
            case GET_HELP:
            case SHOW_CAPABILITIES:
                return "💡";
            case SHOW_RECENT_ACTIVITY:
                return "📊";
            case SHOW_EMERGENCY_TYPES:
                return "🚨";
            case REQUEST_EMERGENCY_ACCESS_HELP:
                return "🔐";
            case SHOW_MY_ACCESS_LEVEL:
                return "👤";
            default:
                return null;
        }
    }

    private String humanizeFilters(Map<String, Object> filters) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : filters.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(": ").append(String.valueOf(e.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private String toConcise(String s) {
        // Keep first ~8 lines or 300 chars, whichever is shorter
        String[] lines = s.split("\\R");
        StringBuilder out = new StringBuilder();
        int limitLines = Math.min(lines.length, 8);
        for (int i = 0; i < limitLines; i++) {
            if (out.length() + lines[i].length() + 1 > 300) break;
            if (i > 0) out.append("\n");
            out.append(lines[i]);
        }
        return out.toString();
    }

    private String toCasual(String s, boolean withEmojis) {
        // Gentle tone tweak without altering semantics
        if (s.isBlank()) return s;
        String lead = withEmojis ? "🙂 " : "";
        // If it already starts with an emoji/prefix, don’t double it
        if (s.startsWith("📋") || s.startsWith("🔎") || s.startsWith("🆕") ||
            s.startsWith("✅") || s.startsWith("📊") || s.startsWith("⬇️") ||
            s.startsWith("💡") || s.startsWith("🙂")) {
            return s;
        }
        return lead + s;
    }

    private String toTechnical(String s) {
        // Keep as-is; future spot for adding keys/values or code blocks
        return s;
    }

    private String toProfessional(String s) {
        // Ensure no leading whitespace; keep punctuation clean
        return s.strip();
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
