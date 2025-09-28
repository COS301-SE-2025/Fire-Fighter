package com.apex.firefighter.service.nlp;

import com.apex.firefighter.service.nlp.EntityExtractionService;
import com.apex.firefighter.service.nlp.IntentRecognitionService;
import com.apex.firefighter.service.nlp.QueryProcessingService;
import com.apex.firefighter.service.nlp.ResponseGenerationService;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;

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
    private Ticket mockedTicket(String id, String status, String desc, String user, String emergencyType) {
        Ticket t = mock(Ticket.class);
        when(t.getTicketId()).thenReturn(id);
        when(t.getStatus()).thenReturn(status);
        when(t.getDescription()).thenReturn(desc);
        when(t.getUserId()).thenReturn(user);
        when(t.getEmergencyType()).thenReturn(emergencyType);
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
        Ticket t = mockedTicket("T-1", "open", "Printer jam", "user1", null);
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
        Ticket t = mockedTicket("T-2", "closed", "Reset router", "user2", "hr");
        var result = mockedResult(QueryProcessingService.QueryResultType.TICKET_DETAILS, List.of(t), Map.of());

        String out = service.generateResponse(result, context, prefs);
        assertTrue(out.contains("Ticket [T-2]"));
        assertTrue(out.contains("Status: closed"));
        assertTrue(out.contains("Description: Reset router"));
        assertTrue(out.contains("Owner: user2"));
    }

    @Test
    void generateResponse_routesToOperationResult_successTicket() {
        Ticket t = mockedTicket("T-3", "closed", "Door fixed", "user3", null);
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

    // ----- Direct generator tests -----

    @Test
    void generateTicketListResponse_empty() {
        var result = mockedResult(QueryProcessingService.QueryResultType.TICKET_LIST, List.of(), Map.of());
        String out = service.generateTicketListResponse(result, context, prefs);
        assertEquals("No tickets found matching your query.", out);
    }

    @Test
    void generateOperationResponse_handlesList() {
        Ticket a = mockedTicket("A", "open", "Foo", "u", null);
        Ticket b = mockedTicket("B", "open", "Bar", "u", null);
        var result = mockedResult(QueryProcessingService.QueryResultType.OPERATION_RESULT, List.of(a, b), Map.of("success", true));

        String out = service.generateOperationResponse(result, context, prefs);
        assertTrue(out.contains("Operation successful on 2 ticket(s)."));
    }

    @Test
    void generateErrorResponse_mapsDataNotFound() {
        var result = mockedResult(
                QueryProcessingService.QueryResultType.ERROR,
                null,
                Map.of("errorType", ResponseGenerationService.ErrorType.DATA_NOT_FOUND));

        String out = service.generateErrorResponse(result, context, prefs);
        assertTrue(out.contains("I couldn't find any matching data"));
    }

    // ----- customizeResponse -----

    @Test
    void customizeResponse_verboseAddsContextAndTruncates() {
        String base = "Line1\nLine2\nLine3\nLine4\nLine5\nLine6\nLine7\nLine8\nLine9";
        prefs.setVerboseMode(true);
        prefs.setMaxResponseLength(80);  // small cap for test

        String out = service.customizeResponse(base, context, prefs);
        assertTrue(out.length() <= 80, "Should be truncated to max length");
        // Because of truncation, we can’t assert exact suffix, but ensure not empty
        assertFalse(out.isEmpty());
    }

    @Test
    void customizeResponse_conciseShortensOutput() {
        String base = String.join("\n", Collections.nCopies(20, "row"));
        prefs.setStyle(ResponseGenerationService.ResponseStyle.CONCISE);

        String out = service.customizeResponse(base, context, prefs);
        assertTrue(out.split("\\R").length <= 8, "Concise should cap lines ~8");
    }

    // ==================== MISSING NON-ADMIN FUNCTION RESPONSE TESTS ====================

    @Test
    void generateResponse_ShowTickets_ShouldFormatUserTicketsCorrectly() {
        Ticket ticket1 = mockedTicket("1", "Active", "User ticket 1", "user1", "high");
        Ticket ticket2 = mockedTicket("2", "Completed", "User ticket 2", "user1", "medium");
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);

        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, tickets, null);

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Here are your tickets"));
        assertTrue(response.contains("User ticket 1"));
        assertTrue(response.contains("User ticket 2"));
        assertTrue(response.contains("Active"));
        assertTrue(response.contains("Completed"));
    }

    @Test
    void generateResponse_ShowRejectedTickets_ShouldFormatRejectedTicketsCorrectly() {
        Ticket ticket1 = mockedTicket("1", "Rejected", "Rejected ticket 1", "user1", "high");
        Ticket ticket2 = mockedTicket("2", "Rejected", "Rejected ticket 2", "user1", "low");
        List<Ticket> rejectedTickets = Arrays.asList(ticket1, ticket2);

        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, rejectedTickets, null);

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Here are your tickets") || response.contains("Found"));
        assertTrue(response.contains("Rejected ticket 1"));
        assertTrue(response.contains("Rejected ticket 2"));
        assertTrue(response.contains("Rejected"));
    }

    @Test
    void generateResponse_SearchTickets_ShouldFormatSearchResultsCorrectly() {
        Ticket ticket1 = mockedTicket("1", "Active", "HR emergency", "user1", "high");
        Ticket ticket2 = mockedTicket("2", "Active", "Financial emergency", "user1", "medium");
        ticket1.setEmergencyType("hr-emergency");
        ticket2.setEmergencyType("financial-emergency");
        List<Ticket> searchResults = Arrays.asList(ticket1, ticket2);

        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, searchResults, null);

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Here are your tickets") || response.contains("Found"));
        assertTrue(response.contains("HR emergency"));
        assertTrue(response.contains("Financial emergency"));
    }

    @Test
    void generateResponse_GetTicketDetails_ShouldFormatTicketDetailsCorrectly() {
        Ticket ticket = mockedTicket("123", "Active", "Detailed ticket description", "user1", "high");
        ticket.setEmergencyType("hr-emergency");

        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.TICKET_DETAILS, ticket, null);

        String response = service.generateResponse(result);

        System.out.println("DEBUG GetTicketDetails response: " + response);

        assertNotNull(response);
        assertTrue(response.contains("Ticket [123]"));
        assertTrue(response.contains("123"));
        assertTrue(response.contains("Detailed ticket description"));
        assertTrue(response.contains("Active"));
        assertTrue(response.contains("high")); // Emergency Type shows as "high" not "hr-emergency"
    }

    @Test
    void generateResponse_ShowRecentActivity_ShouldFormatRecentActivityCorrectly() {
        Ticket ticket1 = mockedTicket("1", "Active", "Recent ticket 1", "user1", "high");
        Ticket ticket2 = mockedTicket("2", "Completed", "Recent ticket 2", "user1", "medium");
        List<Ticket> recentTickets = Arrays.asList(ticket1, ticket2);

        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.TICKET_LIST, recentTickets, null);
        when(result.getMessage()).thenReturn("📊 **Recent Activity** 📊\n\n• **[1]** Active - Recent ticket 1 (high)\n• **[2]** Completed - Recent ticket 2 (medium)");

        String response = service.generateResponse(result);

        System.out.println("DEBUG ShowRecentActivity response: " + response);

        assertNotNull(response);
        assertTrue(response.contains("Here are your tickets"));
        assertTrue(response.contains("Recent ticket 1"));
        assertTrue(response.contains("Recent ticket 2"));
    }

    @Test
    void generateResponse_ShowEmergencyTypes_ShouldFormatEmergencyTypesCorrectly() {
        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.HELP, "Emergency types help content", null);
        when(result.getMessage()).thenReturn("🚨 **Available Emergency Types** 🚨\n\n• **hr-emergency** → HR Group Access\n• **financial-emergency** → Financial Group Access");

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Available Emergency Types"));
        assertTrue(response.contains("hr-emergency"));
        assertTrue(response.contains("financial-emergency"));
        assertTrue(response.contains("HR Group Access"));
        assertTrue(response.contains("Financial Group Access"));
    }

    @Test
    void generateResponse_RequestEmergencyAccessHelp_ShouldFormatHelpCorrectly() {
        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.HELP, "Emergency access help content", null);
        when(result.getMessage()).thenReturn("🔐 **Emergency Access Request Guide** 🔐\n\n**How to Request Emergency Access:**\n\n1. **Create an Emergency Ticket**");

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Emergency Access Request Guide"));
        assertTrue(response.contains("How to Request Emergency Access"));
        assertTrue(response.contains("Create an Emergency Ticket"));
    }

    @Test
    void generateResponse_ShowMyAccessLevel_ShouldFormatAccessLevelCorrectly() {
        QueryProcessingService.QueryResult result = mockedResult(
            QueryProcessingService.QueryResultType.HELP, "Access level information", null);
        when(result.getMessage()).thenReturn("👤 **Current Access Level** 👤\n\n**Your current access level:** Standard User\n\n**Active Emergency Tickets:** None");

        String response = service.generateResponse(result);

        assertNotNull(response);
        assertTrue(response.contains("Current Access Level"));
        assertTrue(response.contains("Standard User"));
        assertTrue(response.contains("Active Emergency Tickets"));
    }

}
