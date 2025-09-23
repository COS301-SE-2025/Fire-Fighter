import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryProcessingServiceTest {

    private QueryProcessingService queryProcessingService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = Mockito.mock(TicketService.class);
        queryProcessingService = new QueryProcessingService(ticketService);
    }

    private EntityExtractionService.Entity makeEntity(EntityExtractionService.EntityType type, String value) {
        return new EntityExtractionService.Entity(type, value, value, 0, 1.0);
    }

    @Test   // ----- Process Query Tests -----
    void testProcessQuery_ShowActiveTickets() {
        List<Ticket> mockTickets = Arrays.asList(new Ticket("1", "user1", "open"));
        when(ticketService.getActiveTicketsForUser("user1", false)).thenReturn(mockTickets);

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
        entities.addEntity(makeEntity(EntityExtractionService.EntityType.TICKET_ID, "123"));
        entities.addEntity(makeEntity(EntityExtractionService.EntityType.STATUS, "Open"));

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

    


}
