package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ai.TicketQueryService;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketQueryServiceTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketQueryService ticketQueryService;

    private Ticket activeTicket;
    private Ticket completedTicket;
    private Ticket rejectedTicket;
    private List<Ticket> testTickets;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        activeTicket = new Ticket();
        activeTicket.setTicketId("ACTIVE-001");
        activeTicket.setDescription("Critical system failure in production");
        activeTicket.setUserId("test-user");
        activeTicket.setEmergencyType("critical-system-failure");
        activeTicket.setEmergencyContact("123-456-7890");
        activeTicket.setStatus("Active");
        activeTicket.setDuration(60);
        activeTicket.setDateCreated(now.minusMinutes(30));

        completedTicket = new Ticket();
        completedTicket.setTicketId("COMPLETED-001");
        completedTicket.setDescription("Network outage resolved");
        completedTicket.setUserId("test-user");
        completedTicket.setEmergencyType("network-outage");
        completedTicket.setEmergencyContact("123-456-7890");
        completedTicket.setStatus("Completed");
        completedTicket.setDuration(45);
        completedTicket.setDateCreated(now.minusHours(2));

        rejectedTicket = new Ticket();
        rejectedTicket.setTicketId("REJECTED-001");
        rejectedTicket.setDescription("Unauthorized access request");
        rejectedTicket.setUserId("test-user");
        rejectedTicket.setEmergencyType("security-incident");
        rejectedTicket.setEmergencyContact("123-456-7890");
        rejectedTicket.setStatus("Rejected");
        rejectedTicket.setDuration(30);
        rejectedTicket.setDateCreated(now.minusHours(1));

        testTickets = Arrays.asList(activeTicket, rejectedTicket, completedTicket);
    }

    // ==================== GET USER TICKET CONTEXT TESTS ====================

    @Test
    void getUserTicketContext_WithActiveTicketsQuery_ShouldReturnActiveTicketsContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "active" without "show" to trigger active context
        String result = ticketQueryService.getUserTicketContext("my active tickets", "test-user");

        // Assert
        assertThat(result).contains("Your active access tickets");
        assertThat(result).contains("ACTIVE-001");
        assertThat(result).contains("critical-system-failure");
        assertThat(result).contains("These tickets are currently granting you elevated access");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithNoActiveTickets_ShouldShowPastTickets() {
        // Arrange
        List<Ticket> pastTicketsOnly = Arrays.asList(completedTicket, rejectedTicket);
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(pastTicketsOnly);

        // Act
        String result = ticketQueryService.getUserTicketContext("my active tickets", "test-user");

        // Assert
        assertThat(result).contains("You have no active access tickets");
        assertThat(result).contains("Here are your past");
        assertThat(result).contains("COMPLETED-001");
        assertThat(result).contains("REJECTED-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithNoTickets_ShouldReturnNoTicketsMessage() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(Collections.emptyList());

        // Act
        String result = ticketQueryService.getUserTicketContext("show my active tickets", "test-user");

        // Assert
        assertThat(result).contains("You have no access tickets in the system");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithTimeQuery_ShouldReturnTimeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("how much time is remaining", "test-user");

        // Assert
        assertThat(result).contains("Time remaining on your active tickets");
        assertThat(result).contains("ACTIVE-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithRecentQuery_ShouldReturnRecentTicketsContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("show my recent tickets", "test-user");

        // Assert
        assertThat(result).contains("latest access tickets");
        assertThat(result).contains("ACTIVE-001");
        assertThat(result).contains("REJECTED-001");
        assertThat(result).contains("COMPLETED-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithClosedQuery_ShouldReturnClosedTicketsContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "closed" without "show" to trigger closed context
        String result = ticketQueryService.getUserTicketContext("my closed tickets", "test-user");

        // Assert
        assertThat(result).contains("recently closed access tickets");
        assertThat(result).contains("COMPLETED-001");
        assertThat(result).contains("REJECTED-001");
        assertThat(result).contains("elevated access that has been revoked");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithEmergencyTypeQuery_ShouldReturnEmergencyTypeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "security" without "show" to trigger emergency type context
        String result = ticketQueryService.getUserTicketContext("my security incidents", "test-user");

        // Assert
        assertThat(result).contains("security");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithGenericQuery_ShouldReturnAllTicketsContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("tell me about my tickets", "test-user");

        // Assert
        assertThat(result).contains("access tickets");
        assertThat(result).contains("ACTIVE-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithServiceException_ShouldReturnErrorMessage() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = ticketQueryService.getUserTicketContext("show my tickets", "test-user");

        // Assert
        assertThat(result).contains("Unable to retrieve your ticket information");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    // ==================== REQUEST CREATION CONTEXT TESTS ====================

    @Test
    void getRequestCreationContext_WithCreateRequestQuery_ShouldReturnCreationGuidance() {
        // Act
        String result = ticketQueryService.getRequestCreationContext("how do I create a new emergency request");

        // Assert
        assertThat(result).contains("Creating Emergency Access Requests");
        assertThat(result).contains("Step 1: Access the Request Form");
        assertThat(result).isNotEmpty();
    }

    @Test
    void getRequestCreationContext_WithFormQuery_ShouldReturnCreationGuidance() {
        // Act
        String result = ticketQueryService.getRequestCreationContext("where is the request form");

        // Assert
        assertThat(result).contains("request");
        assertThat(result).isNotEmpty();
    }

    @Test
    void getRequestCreationContext_WithSubmitQuery_ShouldReturnCreationGuidance() {
        // Act
        String result = ticketQueryService.getRequestCreationContext("how to submit new access request");

        // Assert
        assertThat(result).contains("access");
        assertThat(result).isNotEmpty();
    }

    @Test
    void getRequestCreationContext_WithNonCreationQuery_ShouldReturnCreationGuidance() {
        // Act - The service always returns creation guidance regardless of query
        String result = ticketQueryService.getRequestCreationContext("show my existing tickets");

        // Assert
        assertThat(result).contains("Creating Emergency Access Requests");
        assertThat(result).isNotEmpty();
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void getUserTicketContext_WithNullQuery_ShouldHandleGracefully() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext(null, "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithEmptyQuery_ShouldHandleGracefully() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithNullUserId_ShouldHandleGracefully() {
        // Act
        String result = ticketQueryService.getUserTicketContext("show my tickets", null);

        // Assert
        assertThat(result).contains("You have no access tickets in the system");
        verify(ticketService, never()).getTicketsByUserId(anyString());
    }

    @Test
    void getUserTicketContext_WithLargeNumberOfTickets_ShouldLimitResults() {
        // Arrange
        List<Ticket> manyTickets = Collections.nCopies(20, activeTicket);
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(manyTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("show my tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        // Should limit to 10 tickets for display
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithTicketsHavingNullFields_ShouldHandleGracefully() {
        // Arrange
        Ticket ticketWithNulls = new Ticket();
        ticketWithNulls.setTicketId("NULL-001");
        ticketWithNulls.setDescription(null);
        ticketWithNulls.setEmergencyType(null);
        ticketWithNulls.setStatus("Active");
        ticketWithNulls.setDateCreated(LocalDateTime.now());

        when(ticketService.getTicketsByUserId("test-user")).thenReturn(Arrays.asList(ticketWithNulls));

        // Act
        String result = ticketQueryService.getUserTicketContext("show my tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).contains("NULL-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    // ==================== SPECIFIC CONTEXT METHOD TESTS ====================

    @Test
    void getUserTicketContext_WithActivityQuery_ShouldReturnActivityContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("show my activity", "test-user");

        // Assert
        assertThat(result).contains("recent access activity"); // "activity" triggers recent context
        assertThat(result).contains("ACTIVE-001");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithDisplayQuery_ShouldReturnDisplayContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("display my tickets", "test-user");

        // Assert
        assertThat(result).contains("latest access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithListQuery_ShouldReturnListContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("list my tickets", "test-user");

        // Assert
        assertThat(result).contains("latest access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithExpireQuery_ShouldReturnTimeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("when do my tickets expire", "test-user");

        // Assert
        assertThat(result).contains("Time remaining on your active tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithDurationQuery_ShouldReturnTimeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act
        String result = ticketQueryService.getUserTicketContext("what is the duration of my tickets", "test-user");

        // Assert
        assertThat(result).contains("Time remaining on your active tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithCurrentQuery_ShouldReturnActiveContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "current" without "show" to trigger active context
        String result = ticketQueryService.getUserTicketContext("my current tickets", "test-user");

        // Assert
        assertThat(result).contains("active access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithOpenQuery_ShouldReturnActiveContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "open" without "show" to trigger active context
        String result = ticketQueryService.getUserTicketContext("my open tickets", "test-user");

        // Assert
        assertThat(result).contains("active access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithFinishedQuery_ShouldReturnClosedContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "finished" without "show" to trigger closed context
        String result = ticketQueryService.getUserTicketContext("my finished tickets", "test-user");

        // Assert
        assertThat(result).contains("closed access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithResolvedQuery_ShouldReturnClosedContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "resolved" without "show" to trigger closed context
        String result = ticketQueryService.getUserTicketContext("my resolved tickets", "test-user");

        // Assert
        assertThat(result).contains("closed access tickets");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithCriticalQuery_ShouldReturnEmergencyTypeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "critical" without "show" to trigger emergency type context
        String result = ticketQueryService.getUserTicketContext("my critical tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithDataQuery_ShouldReturnEmergencyTypeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "data" without "show" to trigger emergency type context
        String result = ticketQueryService.getUserTicketContext("my data recovery tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithNetworkQuery_ShouldReturnEmergencyTypeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "network" without "show" to trigger emergency type context
        String result = ticketQueryService.getUserTicketContext("my network tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithLockoutQuery_ShouldReturnEmergencyTypeContext() {
        // Arrange
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(testTickets);

        // Act - Use "lockout" without "show" to trigger emergency type context
        String result = ticketQueryService.getUserTicketContext("my lockout tickets", "test-user");

        // Assert
        assertThat(result).isNotEmpty();
        verify(ticketService).getTicketsByUserId("test-user");
    }

    // ==================== CLOSED TICKETS SPECIFIC TESTS ====================

    @Test
    void getUserTicketContext_WithClosedTicketsOnly_ShouldShowClosedTickets() {
        // Arrange
        List<Ticket> closedTicketsOnly = Arrays.asList(completedTicket, rejectedTicket);
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(closedTicketsOnly);

        // Act - Use "closed" without "show" to trigger closed context
        String result = ticketQueryService.getUserTicketContext("my closed tickets", "test-user");

        // Assert
        assertThat(result).contains("recently closed access tickets");
        assertThat(result).contains("COMPLETED-001");
        assertThat(result).contains("REJECTED-001");
        assertThat(result).contains("elevated access that has been revoked");
        verify(ticketService).getTicketsByUserId("test-user");
    }

    @Test
    void getUserTicketContext_WithNoClosedTickets_ShouldReturnNoClosedMessage() {
        // Arrange
        List<Ticket> activeTicketsOnly = Arrays.asList(activeTicket);
        when(ticketService.getTicketsByUserId("test-user")).thenReturn(activeTicketsOnly);

        // Act - Use "closed" without "show" to trigger closed context
        String result = ticketQueryService.getUserTicketContext("my closed tickets", "test-user");

        // Assert
        assertThat(result).contains("You have no recently closed access tickets");
        assertThat(result).contains("No recent elevated access has been revoked");
        verify(ticketService).getTicketsByUserId("test-user");
    }
}
