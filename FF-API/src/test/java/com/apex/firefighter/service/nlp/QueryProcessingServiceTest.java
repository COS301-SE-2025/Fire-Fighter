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
}
