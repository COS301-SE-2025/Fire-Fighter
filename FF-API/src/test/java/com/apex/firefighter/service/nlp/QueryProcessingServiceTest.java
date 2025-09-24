import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.nlp.EntityExtractionService;
import com.apex.firefighter.service.nlp.IntentRecognitionService;
import com.apex.firefighter.service.nlp.QueryProcessingService;


import com.apex.firefighter.service.ticket.TicketService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryProcessingServiceTest {

    private QueryProcessingService queryProcessingService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = Mockito.mock(TicketService.class);
        queryProcessingService = new QueryProcessingService();
    }

    private EntityExtractionService.Entity makeEntity(EntityExtractionService.EntityType type, String value) {
        return new EntityExtractionService.Entity(type, value, 0, 1);
    }

    @Test   // ----- Process Query Tests -----
    void testProcessQuery_ShowActiveTickets() {
        List<Ticket> mockTickets = Arrays.asList(new Ticket("1", "Show Active Tickets test", "open", "user1", "high", "0123456789"));
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
        entities.setTicketIds(Arrays.asList(ticketEntity));
        boolean allowed = queryProcessingService.validateUserOperation(QueryProcessingService.TicketOperation.UPDATE_PRIORITY, entities, "user1", false);
        
        assertTrue(allowed);
    }    

    @Test   // ----- Execute Ticket Query Tests -----
    void testExecuteTicketQuery_SystemStats() {
        // Mock tickets
        List<Ticket> allTickets = Arrays.asList(
                new Ticket("1", "Issue A", "open", "user1", "high", "0123456789"),
                new Ticket("2", "Issue B", "open", "user2", "low", "0123456789"),
                new Ticket("3", "Issue C", "closed", "user3", "medium", "0123456789"),
                new Ticket("4", "Issue D", "in progress", "user4", "high", "0123456789"),
                new Ticket("5", "Issue E", "open", "user5", "low", "0123456789")
        );

        when(ticketService.getAllTickets()).thenReturn(allTickets);

        QueryProcessingService.QueryResult result =
                queryProcessingService.executeTicketQuery(
                        QueryProcessingService.TicketQueryType.SYSTEM_STATS,
                        Collections.emptyMap(), "admin", true);

        assertEquals(QueryProcessingService.QueryResultType.STATISTICS, result.getResultType());

        Map<String, Object> stats = (Map<String, Object>) result.getData();
        assertEquals(5, stats.get("totalTickets"));
        assertEquals(3, stats.get("openTickets")); // matches tickets with "open" status
    }

    @Test   // ----- Execute Ticket Operation Tests -----
    void testExecuteTicketOperation_UpdateStatus() {
        Ticket updated = new Ticket("123", "Update status test", "closed", "otherUser", "high", "0123456789");
        when(ticketService.updateTicketStatus("123", "closed")).thenReturn(updated);

        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        EntityExtractionService.Entity ticketEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.TICKET_ID, "123", 0, 3);
        EntityExtractionService.Entity statusEntity = new EntityExtractionService.Entity(EntityExtractionService.EntityType.STATUS, "closed", 0, 6);
        statusEntity.setNormalizedValue("closed"); // ensure lowercase normalization if needed
        entities.setTicketIds(Arrays.asList(ticketEntity));
        entities.setStatuses(Arrays.asList(statusEntity));
        QueryProcessingService.QueryResult result = queryProcessingService.executeTicketOperation(QueryProcessingService.TicketOperation.UPDATE_TICKET_STATUS, entities, "user1", false);

        assertEquals(QueryProcessingService.QueryResultType.OPERATION_RESULT, result.getResultType());
        assertEquals(updated, result.getData());
    }

}
