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

    @Test
    void generateResponse_routesToTicketDetails() {
        Ticket t = mockedTicket("T-2", "closed", "Reset router", "user2", "medium");
        var result = mockedResult(QueryProcessingService.QueryResultType.TICKET_DETAILS, List.of(t), Map.of());

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.contains("Ticket [T-2]"));
        assertTrue(out.contains("Status: closed"));
        assertTrue(out.contains("Priority: medium"));
        assertTrue(out.contains("Owner: user2"));
    }

    @Test
    void generateResponse_routesToOperationResult_successTicket() {
        Ticket t = mockedTicket("T-3", "closed", "Door fixed", "user3", "low");
        var result = mockedResult(QueryProcessingService.QueryResultType.OPERATION_RESULT, t, Map.of("success", true));

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.toLowerCase().contains("operation successful"), "Should indicate success");
        assertTrue(out.contains("[T-3]"), "Should mention ticket id");
    }

    @Test
    void generateResponse_routesToOperationResult_failureReason() {
        var result = mockedResult(
                QueryProcessingService.QueryResultType.OPERATION_RESULT,
                null,
                new HashMap<>(Map.of("success", false, "reason", "Permission denied")));

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.startsWith("❌ Operation failed: Permission denied"));
    }

    @Test
    void generateResponse_routesToStatistics() {
        Map<String,Object> stats = Map.of("totalTickets", 10, "open", 4);
        var result = mockedResult(QueryProcessingService.QueryResultType.STATISTICS, stats, Map.of());

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.contains("System Statistics"));
        assertTrue(out.contains("- totalTickets: 10"));
        assertTrue(out.contains("- open: 4"));
    }

    @Test
    void generateResponse_routesToHelpCommands() {
        var result = mockedResult(
                QueryProcessingService.QueryResultType.HELP,
                null,
                Map.of("helpType", ResponseGenerationService.HelpType.COMMANDS));

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.contains("Available commands"), "Should show commands section");
        assertTrue(out.contains("show my tickets"));
    }
    
}
