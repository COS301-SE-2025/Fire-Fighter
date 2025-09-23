package com.apex.firefighter.service.nlp;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for processing natural language queries and converting them
 * into database operations and business logic execution.
 */
@Service
public class QueryProcessingService {

    @Autowired
    private TicketService ticketService;

    /**
     * Process a query based on recognized intent and extracted entities
     * 
     * @param intent The recognized intent from the query
     * @param entities The extracted entities from the query
     * @param userId The user making the query
     * @param isAdmin Whether the user has admin privileges
     * @return QueryResult containing the processed data and metadata
     */
    public QueryResult processQuery(IntentRecognitionService.Intent intent, 
                                   EntityExtractionService.ExtractedEntities entities, 
                                   String userId, 
                                   boolean isAdmin) {
        
        if (intent == null || intent.getType() == IntentRecognitionService.IntentType.UNKNOWN) {
            return new QueryResult(QueryResultType.ERROR, 
                "Sorry, I couldn't understand your request.");
        }

        switch (intent.getType()) {
            // ----------- Ticket Queries -----------
            case SHOW_ACTIVE_TICKETS:
                return executeTicketQuery(TicketQueryType.ACTIVE_TICKETS,
                        buildQueryFilters(entities), userId, isAdmin);

            case SHOW_COMPLETED_TICKETS:
                return executeTicketQuery(TicketQueryType.COMPLETED_TICKETS,
                        buildQueryFilters(entities), userId, isAdmin);

            case SHOW_ALL_TICKETS:
            case SHOW_TICKETS:
                return executeTicketQuery(TicketQueryType.USER_TICKETS,
                        buildQueryFilters(entities), userId, isAdmin);
            
            case GET_SYSTEM_STATS:
                return executeTicketQuery(TicketQueryType.SYSTEM_STATS,
                        buildQueryFilters(entities), userId, isAdmin);

            case EXPORT_TICKETS:
                return executeTicketQuery(TicketQueryType.EXPORT_DATA,
                        buildQueryFilters(entities), userId, isAdmin);

            case HELP:
                return executeTicketQuery(TicketQueryType.HELP,
                        buildQueryFilters(entities), userId, isAdmin);
            
            // ----------- Ticket Operations -----------
            case CREATE_TICKET:
                return executeTicketOperation(TicketOperation.CREATE_TICKET,
                        entities, userId, isAdmin);

            case CLOSE_TICKET:
                return executeTicketOperation(TicketOperation.CLOSE_TICKET,
                        entities, userId, isAdmin);

            case UPDATE_STATUS:
                return executeTicketOperation(TicketOperation.UPDATE_STATUS,
                        entities, userId, isAdmin);

            case ASSIGN_TICKET:
                return executeTicketOperation(TicketOperation.ASSIGN_TICKET,
                        entities, userId, isAdmin);

            case ADD_COMMENT:
                return executeTicketOperation(TicketOperation.ADD_COMMENT,
                        entities, userId, isAdmin);

            case UPDATE_PRIORITY:
                return executeTicketOperation(TicketOperation.UPDATE_PRIORITY,
                        entities, userId, isAdmin);

            // ----------- Fallback -----------
            default:
                return new QueryResult(QueryResultType.ERROR, 
                    "Intent recognized but not yet supported.");
        }
        return null;
    }

    /**
     * Execute a ticket query operation
     * 
     * @param queryType The type of ticket query to execute
     * @param filters The filters to apply to the query
     * @param userId The user making the query
     * @param isAdmin Whether the user has admin privileges
     * @return QueryResult containing the ticket data
     */
    public QueryResult executeTicketQuery(TicketQueryType queryType, 
                                         Map<String, Object> filters, 
                                         String userId, 
                                         boolean isAdmin) {
        try {
            switch (queryType) {
                case ACTIVE_TICKETS: {
                    List<Ticket> tickets = ticketService.getActiveTicketsForUser(userId, isAdmin);
                    return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                }

                case COMPLETED_TICKETS: {
                    List<Ticket> tickets = ticketService.getCompletedTicketsForUser(userId, isAdmin);
                    return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                }

                case USER_TICKETS: {
                    List<Ticket> tickets = ticketService.getTicketsForUser(userId, isAdmin);
                    return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                }

                case SEARCH_TICKETS: {
                    // Use filters (status, priority, etc.)
                    List<Ticket> tickets = ticketService.searchTickets(filters, userId, isAdmin);
                    return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                }

                case SYSTEM_STATS: {
                    Map<String, Object> stats = ticketService.getSystemStatistics();
                    return new QueryResult(QueryResultType.STATISTICS, stats, stats.size());
                }

                case EXPORT_DATA: {
                    byte[] export = ticketService.exportTickets(filters, userId, isAdmin);
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("exportFormat", "csv");
                    metadata.put("size", export.length);
                    return new QueryResult(QueryResultType.OPERATION_RESULT, export, 1, metadata);
                }

                case HELP: {
                    String helpText = "Supported commands: show tickets, create ticket, update status, close ticket, assign ticket, add comment, update priority, export tickets, system stats.";
                    return new QueryResult(QueryResultType.HELP, helpText, 1);
                }

                default:
                    return new QueryResult(QueryResultType.ERROR, "Unsupported query type: " + queryType);
            }

        } catch (Exception e) {
            return new QueryResult(QueryResultType.ERROR, "Error while executing query: " + e.getMessage());
        }
    }

    /**
     * Execute a ticket management operation
     * 
     * @param operation The management operation to execute
     * @param parameters The parameters for the operation
     * @param userId The user performing the operation
     * @param isAdmin Whether the user has admin privileges
     * @return QueryResult containing the operation result
     */
    public QueryResult executeTicketOperation(TicketOperation operation, 
                                             Map<String, Object> parameters, 
                                             String userId, 
                                             boolean isAdmin) {
        // TODO: Implement ticket operation execution logic
        return null;
    }

    /**
     * Build query filters from extracted entities
     * 
     * @param entities The extracted entities
     * @return Map of filter criteria
     */
    public Map<String, Object> buildQueryFilters(EntityExtractionService.ExtractedEntities entities) {
        Map<String, Object> filters = new HashMap<>();

        if (entities == null || entities.getEntities() == null) {
            return filters;
        }

        for (EntityExtractionService.Entity entity : entities.getEntities()) {
        switch (entity.getType()) {
            case TICKET_ID:
                filters.put("ticketId", entity.getNormalizedValue());
                break;

            case STATUS:
                filters.put("status", entity.getNormalizedValue().toLowerCase());
                break;

            case USER_NAME:
                filters.put("assigned", entity.getNormalizedValue());
                break;

            case PRIORITY:
                filters.put("priority", entity.getNormalizedValue().toLowerCase());
                break;

            case DATE:
                filters.put("date", entity.getNormalizedValue());
                break;

            case TIME:
                filters.put("time", entity.getNormalizedValue());
                break;

            case DURATION:
                filters.put("duration", entity.getNormalizedValue());
                break; 
            
            case EMERGENCY_TYPE:
                filters.put("emergencyType", entity.getNormalizedValue());
                break;

            case NUMBER:
                filters.put("number", entity.getNormalizedValue());
                break;

            case LOCATION:
                filters.put("location", entity.getNormalizedValue());
                break;

            default:
                // skip unknown or unneeded entity types
                break;
        }
    }

        return filters;
    }

    /**
     * Validate if a user can perform a specific operation
     * 
     * @param operation The operation to validate
     * @param userId The user attempting the operation
     * @param isAdmin Whether the user has admin privileges
     * @return true if operation is allowed, false otherwise
     */
    public boolean validateUserOperation(TicketOperation operation, String userId, boolean isAdmin) {
        // Admin can do everything
        if (isAdmin) {
            return true;
        }

        switch (operation) {
            case CREATE_TICKET:
            // Any user can create a ticket
            return true;

            case ADD_COMMENT:
            case UPDATE_STATUS:
            case CLOSE_TICKET:
            case UPDATE_PRIORITY:
                if (entities != null && entities.getEntities() != null) {
                    for (EntityExtractionService.Entity entity : entities.getEntities()) {
                        if (entity.getType() == EntityExtractionService.EntityType.TICKET_ID) {
                            String ticketId = entity.getNormalizedValue();
                            if (ticketId != null) {
                                Optional<Ticket> ticketOpt = ticketService.getTicketByTicketId(ticketId);
                                if (ticketOpt.isPresent()) {
                                    Ticket ticket = ticketOpt.get();
                                    // Only allow if the ticket belongs to the current user
                                    if (ticket.getUserId().equals(userId)) {
                                        return true;
                                    } else {
                                        return false; // ticket belongs to someone else
                                    }
                                } else {
                                    return false; // no such ticket
                                }
                            }
                        }
                    }
                }

            case ASSIGN_TICKET:
            // Only admins can reassign tickets
            return false;

            default:
                // Unknown/unsupported operation → reject
                return false;
        }
        return false;
    }

    /**
     * Result of query processing
     */
    public static class QueryResult {
        private boolean success;
        private String message;
        private Object data;
        private QueryResultType resultType;
        private int recordCount;
        private Map<String, Object> metadata;
        private List<String> errors;
        private List<String> warnings;

        public QueryResult() {}

        public QueryResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public QueryResult(boolean success, String message, Object data, QueryResultType resultType) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.resultType = resultType;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public QueryResultType getResultType() { return resultType; }
        public void setResultType(QueryResultType resultType) { this.resultType = resultType; }
        
        public int getRecordCount() { return recordCount; }
        public void setRecordCount(int recordCount) { this.recordCount = recordCount; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }

    /**
     * Types of ticket queries that can be executed
     */
    public enum TicketQueryType {
        USER_TICKETS("user_tickets", "Get tickets for a specific user"),
        ACTIVE_TICKETS("active_tickets", "Get all active tickets"),
        COMPLETED_TICKETS("completed_tickets", "Get completed tickets"),
        SEARCH_TICKETS("search_tickets", "Search tickets by criteria"),
        TICKET_DETAILS("ticket_details", "Get details of specific ticket"),
        SYSTEM_STATS("system_stats", "Get system statistics"),
        EXPORT_DATA("export_data", "Export ticket data");

        private final String code;
        private final String description;

        TicketQueryType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Types of ticket operations that can be executed
     */
    public enum TicketOperation {
        UPDATE_STATUS("update_status", "Update ticket status"),
        ASSIGN_TICKET("assign_ticket", "Assign ticket to user"),
        CREATE_TICKET("create_ticket", "Create new ticket"),
        CLOSE_TICKET("close_ticket", "Close existing ticket"),
        ADD_COMMENT("add_comment", "Add comment to ticket"),
        UPDATE_PRIORITY("update_priority", "Update ticket priority");

        private final String code;
        private final String description;

        TicketOperation(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Types of query results
     */
    public enum QueryResultType {
        TICKET_LIST("ticket_list", "List of tickets"),
        TICKET_DETAILS("ticket_details", "Single ticket details"),
        OPERATION_RESULT("operation_result", "Result of an operation"),
        STATISTICS("statistics", "Statistical data"),
        ERROR("error", "Error result"),
        HELP("help", "Help information");

        private final String code;
        private final String description;

        QueryResultType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
}
