package com.apex.firefighter.service.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.nlp.EntityExtractionService;
import com.apex.firefighter.service.nlp.IntentRecognitionService;
import com.apex.firefighter.service.nlp.QueryProcessingService;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.config.NLPConfig;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryProcessingServiceTest {

    private QueryProcessingService queryProcessingService;
    private TicketService ticketService;
    private IntentRecognitionService intentRecognitionService;
    private NLPConfig nlpConfig;

    @BeforeEach
    void setUp() {
        ticketService = Mockito.mock(TicketService.class);
        intentRecognitionService = Mockito.mock(IntentRecognitionService.class);
        nlpConfig = Mockito.mock(NLPConfig.class);

        queryProcessingService = new QueryProcessingService();
        queryProcessingService.setTicketService(ticketService);
        queryProcessingService.setIntentRecognitionService(intentRecognitionService);
        queryProcessingService.setNlpConfig(nlpConfig);

        // Mock default behavior
        when(nlpConfig.getMaxQueryLength()).thenReturn(500);
        when(intentRecognitionService.isIntentAllowed(any(), any())).thenReturn(true);
    }

    private EntityExtractionService.Entity makeEntity(EntityExtractionService.EntityType type, String value) {
        return new EntityExtractionService.Entity(type, value, 0, 1);
    }

    @Test   // ----- Process Query Tests -----
    void testProcessQuery_ShowActiveTickets() {
        List<Ticket> mockTickets = Arrays.asList(new Ticket("1", "Show Active Tickets test", "Active", "user1", "high", "0123456789"));
        when(ticketService.getTicketsByUserId("user1")).thenReturn(mockTickets);

        IntentRecognitionService.Intent intent =
                new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS, 0.95);

        QueryProcessingService.QueryResult result =
                queryProcessingService.processQuery(intent, null, "user1", false);

        assertEquals(QueryProcessingService.QueryResultType.TICKET_LIST, result.getResultType());
        assertEquals(1, result.getRecordCount());
        assertEquals(mockTickets, result.getData());
    }

    @Test   // ----- Build Query Filters Tests -----
    void testBuildQueryFilters_WithEntities() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();

        // Build the entities explicitly
        EntityExtractionService.Entity ticket =
                new EntityExtractionService.Entity(
                        EntityExtractionService.EntityType.TICKET_ID, "123", 0, 3);
        ticket.setNormalizedValue("123"); // Set normalized value for filter building

        EntityExtractionService.Entity status =
                new EntityExtractionService.Entity(
                        EntityExtractionService.EntityType.STATUS, "Open", 0, 4);
        // If your filter builder prefers normalized values, set it explicitly:
        status.setNormalizedValue("open");

        entities.setTicketIds(Arrays.asList(ticket));
        entities.setStatuses(Arrays.asList(status));

        Map<String, Object> filters = queryProcessingService.buildQueryFilters(entities);

        assertEquals("123", filters.get("ticketId"));
        assertEquals("open", filters.get("status")); // normalized to lowercase
    }

    @Test   // ----- Validate User Operation Tests -----
    void testValidateUserOperation_CreateTicket_UserAllowed() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        boolean allowed = queryProcessingService.validateUserOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET, entities, "user1", false);
        assertTrue(allowed);
    }

    @Test
    void testValidateUserOperation_UpdatePriority_UserNotOwner() {
        Ticket ticket = new Ticket("123", "Update priority test (not owned)", "open", "otherUser", "high", "0123456789");
        when(ticketService.getTicketByTicketId("123")).thenReturn(Optional.of(ticket));

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.Entity ticketEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.TICKET_ID, "123", 0, 3);
        entities.setTicketIds(Arrays.asList(ticketEntity));
        boolean allowed = queryProcessingService.validateUserOperation(QueryProcessingService.TicketOperation.UPDATE_PRIORITY, entities, "user1", false);
        
        assertFalse(allowed);
    }

    @Test
    void testValidateUserOperation_CreateTicket_Admin() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        boolean allowed = queryProcessingService.validateUserOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET, entities, "user1", true);
        assertTrue(allowed);
    }

    @Test
    void testValidateUserOperation_UpdatePriority_UserOwnsTicket() {
        Ticket ticket = new Ticket("123", "Update priority test", "open", "user1", "high", "0123456789");
        when(ticketService.getTicketByTicketId("123")).thenReturn(Optional.of(ticket));

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.Entity ticketEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.TICKET_ID, "123", 0, 3);
        ticketEntity.setNormalizedValue("123"); // Set normalized value for validation
        entities.setTicketIds(Arrays.asList(ticketEntity));
        boolean allowed = queryProcessingService.validateUserOperation(QueryProcessingService.TicketOperation.UPDATE_PRIORITY, entities, "user1", false);
        
        assertTrue(allowed);
    }    

    @Test   // ----- Execute Ticket Query Tests -----
    void testExecuteTicketQuery_SystemStats() {
        // Mock tickets
        List<Ticket> allTickets = Arrays.asList(
                new Ticket("1", "Issue A", "Active", "user1", "high", "0123456789"),
                new Ticket("2", "Issue B", "Active", "user2", "low", "0123456789"),
                new Ticket("3", "Issue C", "Closed", "user3", "medium", "0123456789"),
                new Ticket("4", "Issue D", "Completed", "user4", "high", "0123456789"),
                new Ticket("5", "Issue E", "Active", "user5", "low", "0123456789")
        );

        when(ticketService.getAllTickets()).thenReturn(allTickets);
        when(ticketService.getTicketsByStatus("Active")).thenReturn(Arrays.asList(allTickets.get(0), allTickets.get(1), allTickets.get(4)));
        when(ticketService.getTicketsByStatus("Closed")).thenReturn(Arrays.asList(allTickets.get(2)));
        when(ticketService.getTicketsByStatus("Completed")).thenReturn(Arrays.asList(allTickets.get(3)));
        when(ticketService.getTicketsByStatus("Rejected")).thenReturn(Arrays.asList());

        QueryProcessingService.QueryResult result =
                queryProcessingService.executeTicketQuery(
                        QueryProcessingService.TicketQueryType.SYSTEM_STATS,
                        Collections.emptyMap(), "admin", true);

        assertEquals(QueryProcessingService.QueryResultType.STATISTICS, result.getResultType());

        Map<String, Object> stats = (Map<String, Object>) result.getData();
        assertEquals(5, stats.get("totalTickets"));
        assertEquals(3, stats.get("active")); // matches tickets with "Active" status
    }

    @Test
    void testProcessQuery_NullIntent() {
        QueryProcessingService.QueryResult result = queryProcessingService.processQuery(null, null, "user1", false);
        
        assertFalse(result.isSuccess());
        assertEquals("Invalid or unknown intent", result.getMessage());
        assertEquals(QueryProcessingService.QueryResultType.ERROR, result.getResultType());
    }

    @Test
    void testProcessQuery_UnauthorizedIntent() {
        IntentRecognitionService.Intent intent = 
            new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.SHOW_ALL_TICKETS, 0.95);
        when(intentRecognitionService.isIntentAllowed(any(), any())).thenReturn(false);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.processQuery(intent, null, "user1", false);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Permission denied for intent"));
        assertEquals(QueryProcessingService.QueryResultType.ERROR, result.getResultType());
    }

    @Test
    void testProcessQuery_ExceedsMaxLength() {
        IntentRecognitionService.Intent intent = 
            new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.CREATE_TICKET, 0.95);
        when(nlpConfig.getMaxQueryLength()).thenReturn(10);
        intent.setOriginalQuery("This is a very long query that exceeds the maximum length");

        QueryProcessingService.QueryResult result = 
            queryProcessingService.processQuery(intent, null, "user1", false);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Query exceeds maximum length"));
        assertEquals(QueryProcessingService.QueryResultType.ERROR, result.getResultType());
    }

    @Test
    void testExecuteTicketQuery_ActiveTickets_Admin() {
        List<Ticket> activeTickets = Arrays.asList(
            new Ticket("1", "Active ticket 1", "Active", "user1", "high", "0123456789"),
            new Ticket("2", "Active ticket 2", "Active", "user2", "medium", "0123456789")
        );
        when(ticketService.getTicketsByStatus("Active")).thenReturn(activeTickets);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketQuery(
                QueryProcessingService.TicketQueryType.ACTIVE_TICKETS,
                new HashMap<>(), "admin", true);

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.TICKET_LIST, result.getResultType());
        assertEquals(activeTickets, result.getData());
        assertEquals(2, result.getRecordCount());
    }

    @Test
    void testExecuteTicketQuery_CompletedTickets_User() {
        List<Ticket> userTickets = Arrays.asList(
            new Ticket("1", "Completed ticket", "Completed", "user1", "high", "0123456789"),
            new Ticket("2", "Active ticket", "Active", "user1", "medium", "0123456789"),
            new Ticket("3", "Closed ticket", "Closed", "user1", "low", "0123456789")
        );
        when(ticketService.getTicketsByUserId("user1")).thenReturn(userTickets);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketQuery(
                QueryProcessingService.TicketQueryType.COMPLETED_TICKETS,
                new HashMap<>(), "user1", false);

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.TICKET_LIST, result.getResultType());
        List<Ticket> resultTickets = (List<Ticket>) result.getData();
        assertEquals(2, resultTickets.size());
        assertTrue(resultTickets.stream().allMatch(t -> 
            t.getStatus().equals("Completed") || t.getStatus().equals("Closed")));
    }

    @Test
    void testExecuteTicketQuery_TicketDetails_NotFound() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("ticketId", "nonexistent");
        when(ticketService.getTicketByTicketId("nonexistent")).thenReturn(Optional.empty());

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketQuery(
                QueryProcessingService.TicketQueryType.TICKET_DETAILS,
                filters, "user1", false);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Ticket not found"));
    }

    @Test
    void testExecuteTicketQuery_TicketDetails_Unauthorized() {
        Ticket ticket = new Ticket("123", "Test ticket", "Active", "other-user", "high", "0123456789");
        Map<String, Object> filters = new HashMap<>();
        filters.put("ticketId", "123");
        when(ticketService.getTicketByTicketId("123")).thenReturn(Optional.of(ticket));

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketQuery(
                QueryProcessingService.TicketQueryType.TICKET_DETAILS,
                filters, "user1", false);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not allowed to view this ticket"));
    }

    @Test
    void testExecuteTicketOperation_CreateTicket() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        
        // Add description
        EntityExtractionService.Entity descEntity = 
            new EntityExtractionService.Entity(EntityExtractionService.EntityType.DESCRIPTION, "office fire", 0, 10);
        entityMap.put(EntityExtractionService.EntityType.DESCRIPTION, Arrays.asList(descEntity));
        
        // Add duration
        EntityExtractionService.Entity durationEntity = 
            new EntityExtractionService.Entity(EntityExtractionService.EntityType.DURATION, "30", 0, 2);
        entityMap.put(EntityExtractionService.EntityType.DURATION, Arrays.asList(durationEntity));
        
        // Add emergency type
        EntityExtractionService.Entity emergencyEntity = 
            new EntityExtractionService.Entity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "hr-emergency", 0, 11);
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE, Arrays.asList(emergencyEntity));
        
        // Set all entities
        entities.setAllEntities(entityMap);

        Ticket createdTicket = new Ticket("new-id", "office fire", "Active", "user1", "hr-emergency", "0123456789");
        when(ticketService.createTicket(anyString(), anyString(), anyString(), any(), anyInt()))
            .thenReturn(createdTicket);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET,
                entities, "user1", false, "create hr-emergency ticket for office fire duration 30 minutes");

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.OPERATION_RESULT, result.getResultType());
        assertEquals(createdTicket, result.getData());
    }

    @Test
    void testExecuteTicketOperation_CreateTicket_MissingRequiredFields() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        
        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET,
                entities, "user1", false, "create ticket");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("need the following information"));
    }

    @Test   // ----- Execute Ticket Operation Tests -----
    void testExecuteTicketOperation_UpdateStatus() {
        Ticket original = new Ticket("123", "Update status test", "Active", "user1", "high", "0123456789");
        Ticket updated = new Ticket("123", "Update status test", "Completed", "user1", "high", "0123456789");
        when(ticketService.updateTicketStatus("123", "Completed")).thenReturn(updated);
        when(ticketService.getTicketByTicketId("123")).thenReturn(Optional.of(original));

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.Entity ticketEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.TICKET_ID, "123", 0, 3);
        ticketEntity.setNormalizedValue("123"); // Set normalized value for ticket ID
        EntityExtractionService.Entity statusEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.STATUS, "completed", 0, 9);
        statusEntity.setNormalizedValue("completed"); // ensure lowercase normalization if needed
        entities.setTicketIds(Arrays.asList(ticketEntity));
        entities.setStatuses(Arrays.asList(statusEntity));
        QueryProcessingService.QueryResult result = queryProcessingService.executeTicketOperation(QueryProcessingService.TicketOperation.UPDATE_TICKET_STATUS, entities, "user1", false, "update ticket status");

        assertEquals(QueryProcessingService.QueryResultType.OPERATION_RESULT, result.getResultType());
        assertEquals(updated, result.getData());
    }

    @Test
    void testGenerateHelpResponse() {
        IntentRecognitionService.Intent intent =
            new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.GET_HELP, 0.95);

        QueryProcessingService.QueryResult result =
            queryProcessingService.processQuery(intent, null, "user1", false);

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.HELP, result.getResultType());
        assertNotNull(result.getData());
        assertTrue(result.getMessage().contains("FireFighter Emergency Management Help"));
    }

    @Test
    void testGenerateCapabilitiesResponse() {
        IntentRecognitionService.Intent intent =
            new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.SHOW_CAPABILITIES, 0.95);

        QueryProcessingService.QueryResult result =
            queryProcessingService.processQuery(intent, null, "user1", false);

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.HELP, result.getResultType());
        assertNotNull(result.getData());
        assertTrue(result.getMessage().contains("Ada Capabilities"));
    }

    @Test
    void testGenerateEmergencyTypesResponse() {
        IntentRecognitionService.Intent intent =
            new IntentRecognitionService.Intent(IntentRecognitionService.IntentType.SHOW_EMERGENCY_TYPES, 0.95);

        QueryProcessingService.QueryResult result =
            queryProcessingService.processQuery(intent, null, "user1", false);

        assertTrue(result.isSuccess());
        assertEquals(QueryProcessingService.QueryResultType.HELP, result.getResultType());
        assertNotNull(result.getData());
        assertTrue(result.getMessage().contains("Available Emergency Types"));
    }

    @Test
    void testBuildQueryFilters_NullEntities() {
        Map<String, Object> filters = queryProcessingService.buildQueryFilters(null);
        assertNotNull(filters);
        assertTrue(filters.isEmpty());
    }

    @Test
    void testBuildQueryFilters_AllEntityTypes() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();

        // Add all types of entities
        EntityExtractionService.Entity ticketId = makeEntity(EntityExtractionService.EntityType.TICKET_ID, "123");
        EntityExtractionService.Entity status = makeEntity(EntityExtractionService.EntityType.STATUS, "active");
        EntityExtractionService.Entity userName = makeEntity(EntityExtractionService.EntityType.USER_NAME, "john");
        EntityExtractionService.Entity priority = makeEntity(EntityExtractionService.EntityType.PRIORITY, "high");
        EntityExtractionService.Entity date = makeEntity(EntityExtractionService.EntityType.DATE, "2025-09-27");
        EntityExtractionService.Entity time = makeEntity(EntityExtractionService.EntityType.TIME, "14:30");
        EntityExtractionService.Entity duration = makeEntity(EntityExtractionService.EntityType.DURATION, "30");
        EntityExtractionService.Entity emergencyType = makeEntity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "hr-emergency");
        EntityExtractionService.Entity number = makeEntity(EntityExtractionService.EntityType.NUMBER, "42");
        EntityExtractionService.Entity location = makeEntity(EntityExtractionService.EntityType.LOCATION, "office");

        // Set normalized values
        ticketId.setNormalizedValue("123");
        status.setNormalizedValue("active");
        userName.setNormalizedValue("john");
        priority.setNormalizedValue("high");
        date.setNormalizedValue("2025-09-27");
        time.setNormalizedValue("14:30");
        duration.setNormalizedValue("30");
        emergencyType.setNormalizedValue("hr-emergency");
        number.setNormalizedValue("42");
        location.setNormalizedValue("office");

        // Add all entities to the map
        entityMap.put(EntityExtractionService.EntityType.TICKET_ID, Arrays.asList(ticketId));
        entityMap.put(EntityExtractionService.EntityType.STATUS, Arrays.asList(status));
        entityMap.put(EntityExtractionService.EntityType.USER_NAME, Arrays.asList(userName));
        entityMap.put(EntityExtractionService.EntityType.PRIORITY, Arrays.asList(priority));
        entityMap.put(EntityExtractionService.EntityType.DATE, Arrays.asList(date));
        entityMap.put(EntityExtractionService.EntityType.TIME, Arrays.asList(time));
        entityMap.put(EntityExtractionService.EntityType.DURATION, Arrays.asList(duration));
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE, Arrays.asList(emergencyType));
        entityMap.put(EntityExtractionService.EntityType.NUMBER, Arrays.asList(number));
        entityMap.put(EntityExtractionService.EntityType.LOCATION, Arrays.asList(location));

        entities.setAllEntities(entityMap);

        Map<String, Object> filters = queryProcessingService.buildQueryFilters(entities);

        assertEquals("123", filters.get("ticketId"));
        assertEquals("active", filters.get("status"));
        assertEquals("john", filters.get("assigned"));
        assertEquals("high", filters.get("priority"));
        assertEquals("2025-09-27", filters.get("date"));
        assertEquals("14:30", filters.get("time"));
        assertEquals("30", filters.get("duration"));
        assertEquals("hr-emergency", filters.get("emergencyType"));
        assertEquals("42", filters.get("number"));
        assertEquals("office", filters.get("location"));
    }

    @Test
    void testCreateTicket_InvalidDuration() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        
        // Add description and emergency type
        entityMap.put(EntityExtractionService.EntityType.DESCRIPTION, 
            Arrays.asList(makeEntity(EntityExtractionService.EntityType.DESCRIPTION, "test ticket")));
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE,
            Arrays.asList(makeEntity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "hr-emergency")));
        
        // Add invalid duration (less than 15 minutes)
        EntityExtractionService.Entity durationEntity = makeEntity(EntityExtractionService.EntityType.DURATION, "10");
        durationEntity.setNormalizedValue("10");
        entityMap.put(EntityExtractionService.EntityType.DURATION, Arrays.asList(durationEntity));
        
        entities.setAllEntities(entityMap);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET,
                entities, "user1", false, "create hr-emergency ticket test duration 10 minutes");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Duration must be between 15 and 120 minutes"));
    }

    @Test
    void testCreateTicket_InvalidEmergencyType() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        Map<EntityExtractionService.EntityType, List<EntityExtractionService.Entity>> entityMap = new HashMap<>();
        
        // Add description and duration
        entityMap.put(EntityExtractionService.EntityType.DESCRIPTION,
            Arrays.asList(makeEntity(EntityExtractionService.EntityType.DESCRIPTION, "test ticket")));
        EntityExtractionService.Entity durationEntity = makeEntity(EntityExtractionService.EntityType.DURATION, "30");
        durationEntity.setNormalizedValue("30");
        entityMap.put(EntityExtractionService.EntityType.DURATION, Arrays.asList(durationEntity));
        
        // Add invalid emergency type
        EntityExtractionService.Entity emergencyEntity = 
            makeEntity(EntityExtractionService.EntityType.EMERGENCY_TYPE, "invalid-emergency");
        emergencyEntity.setNormalizedValue("invalid-emergency");
        entityMap.put(EntityExtractionService.EntityType.EMERGENCY_TYPE, Arrays.asList(emergencyEntity));
        
        entities.setAllEntities(entityMap);

        QueryProcessingService.QueryResult result = 
            queryProcessingService.executeTicketOperation(
                QueryProcessingService.TicketOperation.CREATE_TICKET,
                entities, "user1", false, "create invalid-emergency ticket test duration 30 minutes");

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid emergency type"));
    }
}