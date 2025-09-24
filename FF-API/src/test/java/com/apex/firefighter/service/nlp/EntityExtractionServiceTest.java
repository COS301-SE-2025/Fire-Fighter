package com.apex.firefighter.service.nlp;

import com.apex.firefighter.config.NLPConfig;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntityExtractionServiceTest {

    @Mock
    private NLPConfig nlpConfig;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private EntityExtractionService entityExtractionService;

    @BeforeEach
    void setUp() {
        // Mock NLPConfig
        when(nlpConfig.getEntityConfidenceThreshold()).thenReturn(0.6);
        when(nlpConfig.isDebugEnabled()).thenReturn(true);

        // Mock TicketService
        when(ticketService.existsTicket("123")).thenReturn(true);
        when(ticketService.existsTicket("999")).thenReturn(false);
        when(ticketService.existsUser("john")).thenReturn(true);
        when(ticketService.existsUser("unknown")).thenReturn(false);

        // Reset ENTITY_PATTERNS
        ReflectionTestUtils.invokeMethod(entityExtractionService, "initializeEntityPatterns");
    }

    @ParameterizedTest
    @CsvSource({
        "update ticket #123 to open, TICKET_ID, #123",
        "create hr emergency ticket, EMERGENCY_TYPE, hr emergency",
        "show tickets for today, DATE, today",
        "assign to john, USER_NAME, john",
        "set status to closed, STATUS, closed"
    })
    void testExtractEntities(String query, EntityExtractionService.EntityType expectedType, String expectedValue) {
        EntityExtractionService.ExtractedEntities entities = entityExtractionService.extractEntities(query);

        List<EntityExtractionService.Entity> typeEntities = entities.getAllEntities().get(expectedType);
        assertThat(typeEntities).isNotEmpty();
        assertThat(typeEntities.get(0).getValue()).isEqualTo(expectedValue);
        assertThat(typeEntities.get(0).getConfidence()).isGreaterThanOrEqualTo(0.6);
    }

    @Test
    void testExtractEntities_emptyQuery() {
        EntityExtractionService.ExtractedEntities entities = entityExtractionService.extractEntities("");

        assertThat(entities.getAllEntities()).isEmpty();
        assertThat(entities.getTicketIds()).isEmpty();
    }

    @Test
    void testExtractSpecificEntities() {
        String query = "update ticket #123 to open on 2025-09-24";
        List<EntityExtractionService.EntityType> types = Arrays.asList(
            EntityExtractionService.EntityType.TICKET_ID,
            EntityExtractionService.EntityType.STATUS
        );
        var entities = entityExtractionService.extractSpecificEntities(query, types);

        assertThat(entities).containsKeys(EntityExtractionService.EntityType.TICKET_ID, EntityExtractionService.EntityType.STATUS);
        assertThat(entities.get(EntityExtractionService.EntityType.TICKET_ID)).anyMatch(e -> e.getValue().equals("#123"));
        assertThat(entities.get(EntityExtractionService.EntityType.STATUS)).anyMatch(e -> e.getValue().equals("open"));
        assertThat(entities.get(EntityExtractionService.EntityType.DATE)).isNull();
    }

    @Test
    void testValidateEntities() {
        EntityExtractionService.ExtractedEntities entities = new EntityExtractionService.ExtractedEntities();
        entities.setTicketIds(Arrays.asList(new EntityExtractionService.Entity(
            EntityExtractionService.EntityType.TICKET_ID, "#123", 0, 4)));
        entities.setStatuses(Arrays.asList(new EntityExtractionService.Entity(
            EntityExtractionService.EntityType.STATUS, "open", 10, 14)));
        entities.setUserNames(Arrays.asList(new EntityExtractionService.Entity(
            EntityExtractionService.EntityType.USER_NAME, "unknown", 20, 27)));
        entities.setAllEntities(Map.of(
            EntityExtractionService.EntityType.TICKET_ID, entities.getTicketIds(),
            EntityExtractionService.EntityType.STATUS, entities.getStatuses(),
            EntityExtractionService.EntityType.USER_NAME, entities.getUserNames()
        ));

        EntityExtractionService.ValidationResult result = entityExtractionService.validateEntities(entities);

        assertThat(result.isValid()).isTrue(); // Valid ticket and status, warning for user
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getWarnings()).contains("Unknown user name: unknown");
    }

    @Test
    void testGetSupportedEntityTypes() {
        List<EntityExtractionService.EntityType> types = entityExtractionService.getSupportedEntityTypes();

        assertThat(types).containsExactlyInAnyOrder(EntityExtractionService.EntityType.values());
    }
}
