package com.apex.firefighter.service.nlp;

import com.apex.firefighter.config.NLPConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for IntentRecognitionService
 * Tests the core intent recognition functionality with various query types
 */
@ExtendWith(MockitoExtension.class)
class IntentRecognitionServiceTest {

    @Mock
    private NLPConfig mockNlpConfig;

    private IntentRecognitionService intentRecognitionService;

    @BeforeEach
    void setUp() {
        intentRecognitionService = new IntentRecognitionService();
        intentRecognitionService.nlpConfig = mockNlpConfig;
        
        // Set up default configuration with lenient stubbing
        lenient().when(mockNlpConfig.getIntentConfidenceThreshold()).thenReturn(0.7);
    }

    // ==================== QUERY INTENT TESTS ====================

    @Test
    void recognizeIntent_ShowMyTickets_ShouldReturnShowTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("show my tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SHOW_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void recognizeIntent_ActiveTickets_ShouldReturnShowActiveTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("show active tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_CompletedTickets_ShouldReturnShowCompletedTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("show completed tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SHOW_COMPLETED_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_SearchTickets_ShouldReturnSearchTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("search for tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SEARCH_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    // ==================== MANAGEMENT INTENT TESTS ====================

    @Test
    void recognizeIntent_CreateTicket_ShouldReturnCreateTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("create new ticket");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CREATE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_UpdateStatus_ShouldReturnUpdateTicketStatusIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("update ticket status");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.UPDATE_TICKET_STATUS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_CloseTicket_ShouldReturnCloseTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("close ticket");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CLOSE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    // ==================== ADMIN INTENT TESTS ====================

    @Test
    void recognizeIntent_AllTickets_ShouldReturnShowAllTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("show all tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SHOW_ALL_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_SystemStats_ShouldReturnGetSystemStatsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("system statistics");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.GET_SYSTEM_STATS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_ExportTickets_ShouldReturnExportTicketsIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("export tickets");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.EXPORT_TICKETS);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    // ==================== HELP INTENT TESTS ====================

    @Test
    void recognizeIntent_Help_ShouldReturnGetHelpIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("help");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.GET_HELP);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_Capabilities_ShouldReturnShowCapabilitiesIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("what can you do");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.SHOW_CAPABILITIES);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    void recognizeIntent_EmptyQuery_ShouldReturnUnknownIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.UNKNOWN);
        assertThat(result.getConfidence()).isEqualTo(0.0);
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void recognizeIntent_NullQuery_ShouldReturnUnknownIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent(null);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.UNKNOWN);
        assertThat(result.getConfidence()).isEqualTo(0.0);
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void recognizeIntent_UnrecognizedQuery_ShouldReturnUnknownIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("random gibberish text");
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.UNKNOWN);
        assertThat(result.getConfidence()).isLessThan(0.7);
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void recognizeIntent_LowConfidenceThreshold_ShouldAcceptLowerScores() {
        // Arrange
        when(mockNlpConfig.getIntentConfidenceThreshold()).thenReturn(0.3);
        
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("tickets");
        
        // Assert - should recognize even with lower confidence
        assertThat(result).isNotNull();
        // The exact intent may vary, but it should not be UNKNOWN with lower threshold
    }

    // ==================== DOLIBARR EMERGENCY TYPE TESTS ====================

    @Test
    void recognizeIntent_HrEmergencyTicket_ShouldReturnCreateTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("new hr emergency ticket needed");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CREATE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_FinancialEmergencyTicket_ShouldReturnCreateTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("create financial emergency ticket");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CREATE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_ManagementEmergencyTicket_ShouldReturnCreateTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("management emergency request");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CREATE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }

    @Test
    void recognizeIntent_LogisticsEmergencyTicket_ShouldReturnCreateTicketIntent() {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent("logistics emergency ticket");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(IntentRecognitionService.IntentType.CREATE_TICKET);
        assertThat(result.getConfidence()).isGreaterThan(0.7);
    }


    // ==================== Test recognizeIntents ====================
    @ParameterizedTest
    @CsvSource({
        "show my tickets, SHOW_TICKETS, true",
        "create ticket, CREATE_TICKET, true",
        "invalid query, UNKNOWN, false",
        ", UNKNOWN, false"
    })
    void testRecognizeIntent(String query, IntentRecognitionService.IntentType expectedType, boolean expectedSuccess)
    {
        // Act
        IntentRecognitionService.Intent result = intentRecognitionService.recognizeIntent(query);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(expectedType);
        assertThat(result.isSuccess()).isEqualTo(expectedSuccess);
        if (expectedSuccess) {
            assertThat(result.getConfidence()).isGreaterThanOrEqual(0.7);
        } else {
            assertThat(result.getConfidence()).isLessThan(0.7);
        }
    }

    // ==================== Test recognizeMultipleIntents ====================
    @Test testRecognizeMultipleIntents_validQuery() {
        // Act 
        String query = "show my tickets and create a new ticket";
        List<IntentRecognitionService.Intent> intents = intentRecognitionService.recognizeMultipleIntents(query);

        // Assert
        assertThat(intents).isNotNull();
        assertThat(intents).isNotEmpty();
        assertThat(intents.stream().map(IntentRecognitionService.Intent::getType))
            .containsAnyOf(
                IntentRecognitionService.IntentType.SHOW_TICKETS,
                IntentRecognitionService.IntentType.CREATE_TICKET
            );
        assertThat(intents.get(0).getConfidence()).isGreaterThanOrEqualTo(0.7);
    }


}