package com.apex.firefighter.service.nlp;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service responsible for processing natural language queries and converting them
 * into database operations and business logic execution.
 */
@Service
public class QueryProcessingService {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private EntityExtractionService entityExtractor;

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
            return new QueryResult(false, "Sorry, I couldn't understand your request.");
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
                return new QueryResult(false, "Intent recognized but not yet supported.");
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
            if (filters == null) filters = new HashMap<>();
            final String filterTicketId   = (String) filters.get("ticketId");
            final String filterStatusRaw  = (String) filters.get("status");
            final String filterUserId     = (String) filters.getOrDefault("userId", userId);
            final String filterEmergency  = (String) filters.get("emergencyType");

            switch (queryType) {
                case TICKET_DETAILS: {
                    if (filterTicketId == null || filterTicketId.isEmpty()) {
                        return new QueryResult(false, "Missing ticketId for TICKET_DETAILS.");
                    }
                    Optional<Ticket> opt = ticketService.getTicketByTicketId(filterTicketId);
                    if (opt.isEmpty()) {
                        return new QueryResult(false, "Ticket not found: " + filterTicketId);
                    }
                    Ticket t = opt.get();
                    if (!isAdmin && !userId.equals(t.getUserId())) {
                        return new QueryResult(false, "You are not allowed to view this ticket.");
                    }
                    return new QueryResult(QueryResultType.TICKET_DETAILS, t, 1);
                }

                case USER_TICKETS: {
                    // If admin and a userId filter is present, show that user's tickets; otherwise:
                    List<Ticket> tickets = ticketService.getTicketsByUserId(filterUserId);
                    return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                }

                case ACTIVE_TICKETS: {
                    if (isAdmin) {
                        List<Ticket> tickets = ticketService.getTicketsByStatus("Active");
                        return new QueryResult(QueryResultType.TICKET_LIST, tickets, tickets.size());
                    } else {
                        List<Ticket> mine = ticketService.getTicketsByUserId(userId);
                        List<Ticket> active = filterByStatuses(mine, "Active");
                        return new QueryResult(QueryResultType.TICKET_LIST, active, active.size());
                    }
                }

                case COMPLETED_TICKETS: {
                    if (isAdmin) {
                        List<Ticket> completed = ticketService.getTicketsByStatus("Completed");
                        List<Ticket> closed    = ticketService.getTicketsByStatus("Closed");
                        List<Ticket> all = new ArrayList<>(completed);
                        all.addAll(closed);
                        return new QueryResult(QueryResultType.TICKET_LIST, all, all.size());
                    } else {
                        List<Ticket> mine = ticketService.getTicketsByUserId(userId);
                        List<Ticket> done = filterByStatuses(mine, "Completed", "Closed");
                        return new QueryResult(QueryResultType.TICKET_LIST, done, done.size());
                    }
                }

                case SEARCH_TICKETS: {
                    // Start from scope (admin = all, user = own)
                    List<Ticket> base = isAdmin ? ticketService.getAllTickets()
                                                : ticketService.getTicketsByUserId(userId);

                    String normStatus = normalizeStatus(filterStatusRaw); // map "open"->Active, etc.

                    List<Ticket> out = base.stream()
                        .filter(t -> filterTicketId == null || filterTicketId.equals(t.getTicketId()))
                        .filter(t -> normStatus == null || normStatus.equalsIgnoreCase(t.getStatus())
                                || (normStatus.equals("CompletedOrClosed")
                                    && ("Completed".equalsIgnoreCase(t.getStatus())
                                        || "Closed".equalsIgnoreCase(t.getStatus()))))
                        .filter(t -> filterEmergency == null || equalsIgnoreCaseSafe(t.getEmergencyType(), filterEmergency))
                        .collect(java.util.stream.Collectors.toList());

                    return new QueryResult(QueryResultType.TICKET_LIST, out, out.size());
                }

                case SYSTEM_STATS: {
                    Map<String, Object> stats = new HashMap<>();
                    if (isAdmin) {
                        int total      = ticketService.getAllTickets().size();
                        int active     = ticketService.getTicketsByStatus("Active").size();
                        int completed  = ticketService.getTicketsByStatus("Completed").size();
                        int closed     = ticketService.getTicketsByStatus("Closed").size();
                        int rejected   = ticketService.getTicketsByStatus("Rejected").size();

                        stats.put("scope", "system");
                        stats.put("totalTickets", total);
                        stats.put("active", active);
                        stats.put("completed", completed);
                        stats.put("closed", closed);
                        stats.put("rejected", rejected);
                    } else {
                        List<Ticket> mine = ticketService.getTicketsByUserId(userId);
                        stats.put("scope", "user");
                        stats.put("totalTickets", mine.size());
                        stats.put("active", countByStatus(mine, "Active"));
                        stats.put("completed", countByStatus(mine, "Completed"));
                        stats.put("closed", countByStatus(mine, "Closed"));
                        stats.put("rejected", countByStatus(mine, "Rejected"));
                    }
                    return new QueryResult(QueryResultType.STATISTICS, stats, 1);
                }

                case EXPORT_DATA: {
                    // Not implemented in TicketService; return an explicit error to caller.
                    return new QueryResult(false, "Export is not supported by the current TicketService.");
                }

                case HELP: {
                    String helpText = "Supported queries: my tickets, active tickets, completed tickets, ticket details, system stats, search by status/ticketId/emergency type.";
                    return new QueryResult(QueryResultType.HELP, helpText, 1);
                }

                default:
                    return new QueryResult(false, "Unsupported query type: " + queryType);
            }

        } catch (Exception e) {
            return new QueryResult(false, "Error while executing query: " + e.getMessage());
        }
    }


    /**
     * Execute a ticket management operation
     * 
     * @param operation The management operation to execute
     * @param entities 
     * @param userId The user performing the operation
     * @param isAdmin Whether the user has admin privileges
     * @return QueryResult containing the operation result
     */
    public QueryResult executeTicketOperation(TicketOperation operation,
                                          EntityExtractionService.ExtractedEntities entities,
                                          String userId,
                                          boolean isAdmin) {
        try {
            // Permission gate
            if (!validateUserOperation(operation, entities, userId, isAdmin)) {
                return new QueryResult(false, "You are not allowed to perform this operation.");
            }

            switch (operation) {
                case CREATE_TICKET: {
                    String description      = firstNormalized(entities, EntityExtractionService.EntityType.NUMBER);
                    if (description == null || description.isEmpty()) {
                        description = "Emergency assistance request";
                    }

                    String emergencyType    = firstNormalized(entities, EntityExtractionService.EntityType.EMERGENCY_TYPE);
                    String emergencyContact = firstNormalized(entities, EntityExtractionService.EntityType.PHONE);
                    Integer duration        = parseIntegerSafe(firstNormalized(entities, EntityExtractionService.EntityType.DURATION));

                    Ticket created = ticketService.createTicket(
                            description, userId, emergencyType, emergencyContact, duration);
                    return new QueryResult(QueryResultType.OPERATION_RESULT, created, 1);
                }

                case UPDATE_STATUS: {
                    String ticketId = firstNormalized(entities, EntityExtractionService.EntityType.TICKET_ID);
                    String status   = firstNormalized(entities, EntityExtractionService.EntityType.STATUS);
                    if (ticketId == null || status == null) {
                        return new QueryResult(false, "Missing ticketId or status.");
                    }
                    if (!isAdmin) {
                        Optional<Ticket> t = ticketService.getTicketByTicketId(ticketId);
                        if (t.isEmpty() || !userId.equals(t.get().getUserId())) {
                            return new QueryResult(false, "You can only update your own tickets.");
                        }
                    }
                    Ticket updated = ticketService.updateTicketStatus(ticketId,
                            normalizeStatusForWrite(status));
                    return new QueryResult(QueryResultType.OPERATION_RESULT, updated, 1);
                }

                case CLOSE_TICKET: {
                    String ticketId = firstNormalized(entities, EntityExtractionService.EntityType.TICKET_ID);
                    if (ticketId == null) {
                        return new QueryResult(false, "Missing ticketId.");
                    }
                    if (!isAdmin) {
                        Optional<Ticket> t = ticketService.getTicketByTicketId(ticketId);
                        if (t.isEmpty() || !userId.equals(t.get().getUserId())) {
                            return new QueryResult(false, "You can only close your own tickets.");
                        }
                    }
                    Ticket updated = ticketService.updateTicketStatus(ticketId, "Completed");
                    return new QueryResult(QueryResultType.OPERATION_RESULT, updated, 1);
                }

                case ASSIGN_TICKET:
                case ADD_COMMENT:
                case UPDATE_PRIORITY:
                    return new QueryResult(false, "Operation not supported: " + operation);

                default:
                    return new QueryResult(false, "Unsupported operation: " + operation);
            }

        } catch (Exception e) {
            return new QueryResult(false, "Error while executing operation: " + e.getMessage());
        }
    }


    /* ----------------------- helpers ----------------------- */

    private List<Ticket> filterByStatuses(List<Ticket> tickets, String... statuses) {
        if (tickets == null || tickets.isEmpty() || statuses == null || statuses.length == 0) return Collections.emptyList();
        java.util.Set<String> set = new java.util.HashSet<>();
        for (String s : statuses) set.add(s);
        return tickets.stream().filter(t -> set.contains(t.getStatus())).collect(java.util.stream.Collectors.toList());
    }

    private int countByStatus(List<Ticket> tickets, String status) {
        if (tickets == null) return 0;
        int c = 0;
        for (Ticket t : tickets) if (status.equalsIgnoreCase(t.getStatus())) c++;
        return c;
    }

    private String normalizeStatus(String status) {
        if (status == null) return null;
        String s = status.trim().toLowerCase();
        if (s.equals("open") || s.equals("active")) return "Active";
        if (s.equals("done") || s.equals("completed")) return "CompletedOrClosed"; // query mode: treat both
        if (s.equals("closed")) return "CompletedOrClosed";
        if (s.equals("rejected") || s.equals("revoked")) return "Rejected";
        // fall back to capitalized as-is for exact match attempts
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String normalizeStatusForWrite(String status) {
        if (status == null) return null;
        String s = status.trim().toLowerCase();
        if (s.equals("open") || s.equals("active")) return "Active";
        if (s.equals("done") || s.equals("complete") || s.equals("completed")) return "Completed";
        if (s.equals("closed") || s.equals("close")) return "Closed";
        if (s.equals("rejected") || s.equals("revoked")) return "Rejected";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private boolean equalsIgnoreCaseSafe(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    private Integer parseIntegerSafe(String s) {
        try { return (s == null) ? null : Integer.parseInt(s); }
        catch (NumberFormatException nfe) { return null; }
    }

    private String firstNormalized(EntityExtractionService.ExtractedEntities entities,
                                EntityExtractionService.EntityType type) {
        if (entities == null) return null;
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> all = entities.getAllEntities();
        if (all != null) {
            List<EntityExtractionService.Entity> list = all.get(type);
            if (list != null && !list.isEmpty()) {
                String v = list.get(0).getNormalizedValue();
                return (v == null || v.isEmpty()) ? list.get(0).getValue() : v;
            }
        }
        return null;
    }

    private String listFirstNormOrVal(List<EntityExtractionService.Entity> list) {
        if (list == null || list.isEmpty()) return null;
        String v = list.get(0).getNormalizedValue();
        return (v == null || v.isEmpty()) ? list.get(0).getValue() : v;
    }


    /**
     * Build query filters from extracted entities
     * 
     * @param entities The extracted entities
     * @return Map of filter criteria
     */
    public Map<String, Object> buildQueryFilters(EntityExtractionService.ExtractedEntities entities) {
        Map<String, Object> filters = new HashMap<>();

            if (entities == null || entities.getAllEntities() == null) {
                return filters;
            }

            for (EntityExtractionService.Entity entity : entities.getAllEntities().values().stream().flatMap(List::stream).toList()) {
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
                    for (EntityExtractionService.EntityType entity : entities.getEntities()) {
                        if (entity.getType() == TICKET_ID) {
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

        public QueryResult(QueryResultType resultType, Object data, int recordCount) {
            this.success = true;
            this.resultType = resultType;
            this.data = data;
            this.recordCount = recordCount;
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
