package com.apex.firefighter.service.nlp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResponseGenerationServiceTest {

    private ResponseGenerationService service;
    private ResponseGenerationService.QueryContext context;
    private ResponseGenerationService.ResponsePreferences prefs;

    @BeforeEach
    void setUp() {
        service = new ResponseGenerationService();

        context = new ResponseGenerationService.QueryContext();
        context.setOriginalQuery("show my active tickets");
        context.setIntent(IntentRecognitionService.IntentType.SHOW_ACTIVE_TICKETS);
        Map<String,Object> filters = new HashMap<>();
        filters.put("status", "open");
        context.setFilters(filters);
        context.setTimeframe("last 7 days");
        context.setIncludeDetails(false);

        prefs = new ResponseGenerationService.ResponsePreferences();
        prefs.setIncludeEmojis(false);   // keep stable for assertions
        prefs.setVerboseMode(false);
        prefs.setStyle(ResponseGenerationService.ResponseStyle.PROFESSIONAL);
        prefs.setMaxResponseLength(500);
    }

    // ----- Helpers -----

    @SuppressWarnings("unchecked")
    private Ticket mockedTicket(String id, String status, String desc, String user, String priority) {
        Ticket t = mock(Ticket.class);
        when(t.getTicketId()).thenReturn(id);
        when(t.getStatus()).thenReturn(status);
        when(t.getDescription()).thenReturn(desc);
        when(t.getUserId()).thenReturn(user);
        when(t.getPriority()).thenReturn(priority);
        // optional fields:
        when(t.getEmergencyType()).thenReturn(null);
        when(t.getDuration()).thenReturn(null);
        return t;
    }

    private QueryProcessingService.QueryResult mockedResult(
            QueryProcessingService.QueryResultType type,
            Object data,
            Map<String, Object> metadata) {

        QueryProcessingService.QueryResult r = mock(QueryProcessingService.QueryResult.class);
        when(r.getResultType()).thenReturn(type);
        when(r.getData()).thenReturn(data);
        when(r.getMetadata()).thenReturn(metadata);
        when(r.getRecordCount()).thenReturn(
                data instanceof Collection ? ((Collection<?>) data).size() : (data == null ? 0 : 1));
        return r;
    }

    // ----- generateResponse dispatcher -----

    @Test
    void generateResponse_routesToTicketList() {
        Ticket t = mockedTicket("T-1", "open", "Printer jam", "user1", "high");
        List<Ticket> tickets = List.of(t);

        var result = mockedResult(QueryProcessingService.QueryResultType.TICKET_LIST, tickets, Map.of());
        String out = service.generateResponse(result, context, prefs);

        assertTrue(out.contains("Here are your tickets:"), "Should include header");
        assertTrue(out.contains("[T-1] open - Printer jam"), "Should list ticket line");
        // implementations: list formatting per file lines L66-L83
        // Ref: generateTicketListResponse
    }

}
