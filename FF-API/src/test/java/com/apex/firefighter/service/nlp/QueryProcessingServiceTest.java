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

}