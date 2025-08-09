package com.apex.firefighter.service;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.ticket.TicketScheduledService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketScheduledServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TicketScheduledService ticketScheduledService;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setTicketId("TEST-001");
        testTicket.setUserId("test-user");
        testTicket.setDescription("Test ticket");
        testTicket.setEmergencyType("test");
        testTicket.setEmergencyContact("test@example.com");
        testTicket.setDuration(60); // 60 minutes
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(56)); // Created 56 minutes ago (4 minutes left)
        testTicket.setFiveMinuteWarningSent(false);
        testTicket.setStatus("Active");
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldSendWarningWhenTicketHas5MinutesLeft() {
        // Arrange
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, times(1)).createFiveMinuteWarningNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        verify(ticketRepository, times(1)).save(testTicket);
        assert(testTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldNotSendWarningWhenAlreadySent() {
        // Arrange
        testTicket.setFiveMinuteWarningSent(true);
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldNotSendWarningWhenTicketHasMoreThan5MinutesLeft() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(50)); // Created 50 minutes ago (10 minutes left)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldNotSendWarningWhenTicketIsExpired() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldHandleNotificationServiceException() {
        // Arrange
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(notificationService.createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class)))
            .thenThrow(new RuntimeException("Notification service error"));

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, times(1)).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        // Should not save the ticket if notification fails
        verify(ticketRepository, never()).save(any(Ticket.class));
        assert(!testTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testScheduledTicketCheck_ShouldCallBothMethods() {
        // Arrange
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        // Act
        ticketScheduledService.scheduledTicketCheck();

        // Assert
        verify(ticketRepository, times(2)).findActiveTicketsWithDuration(); // Called by both methods
    }

    // ==================== CLOSE EXPIRED TICKETS TESTS ====================

    @Test
    void testCloseExpiredTickets_ShouldCloseExpiredTicket() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketCompletionNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        // Verify ticket status was set to "Closed"
        assert(testTicket.getStatus().equals("Closed"));
        assert(testTicket.getDateCompleted() != null);
    }

    @Test
    void testCloseExpiredTickets_ShouldNotCloseNonExpiredTicket() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(30)); // Created 30 minutes ago (not expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_ShouldHandleNotificationServiceException() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(notificationService.createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class)))
            .thenThrow(new RuntimeException("Notification service error"));

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
        // Ticket should still be closed even if notification fails
        assert(testTicket.getStatus().equals("Closed"));
    }

    @Test
    void testCloseExpiredTickets_ShouldHandleRepositoryException() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(ticketRepository.save(any(Ticket.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket);
        // Should not call notification service if save fails
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithEmptyTicketList_ShouldNotProcessAnyTickets() {
        // Arrange
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithMultipleTickets_ShouldProcessCorrectly() {
        // Arrange
        Ticket expiredTicket1 = new Ticket();
        expiredTicket1.setTicketId("EXPIRED-001");
        expiredTicket1.setUserId("user1");
        expiredTicket1.setDuration(60);
        expiredTicket1.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Expired
        expiredTicket1.setStatus("Active");

        Ticket expiredTicket2 = new Ticket();
        expiredTicket2.setTicketId("EXPIRED-002");
        expiredTicket2.setUserId("user2");
        expiredTicket2.setDuration(30);
        expiredTicket2.setDateCreated(LocalDateTime.now().minusMinutes(35)); // Expired
        expiredTicket2.setStatus("Active");

        Ticket activeTicket = new Ticket();
        activeTicket.setTicketId("ACTIVE-001");
        activeTicket.setUserId("user3");
        activeTicket.setDuration(60);
        activeTicket.setDateCreated(LocalDateTime.now().minusMinutes(30)); // Not expired
        activeTicket.setStatus("Active");

        List<Ticket> tickets = Arrays.asList(expiredTicket1, expiredTicket2, activeTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(tickets);

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, times(2)).save(any(Ticket.class)); // Only expired tickets
        verify(notificationService, times(2)).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));

        // Verify expired tickets were closed
        assert(expiredTicket1.getStatus().equals("Closed"));
        assert(expiredTicket2.getStatus().equals("Closed"));
        // Verify active ticket was not touched
        assert(activeTicket.getStatus().equals("Active"));
    }

    // ==================== ADDITIONAL FIVE MINUTE WARNING TESTS ====================

    @Test
    void testSendFiveMinuteWarnings_WithMultipleTickets_ShouldProcessCorrectly() {
        // Arrange
        Ticket warningTicket1 = new Ticket();
        warningTicket1.setTicketId("WARNING-001");
        warningTicket1.setUserId("user1");
        warningTicket1.setDuration(60);
        warningTicket1.setDateCreated(LocalDateTime.now().minusMinutes(56)); // 4 minutes left
        warningTicket1.setFiveMinuteWarningSent(false);
        warningTicket1.setStatus("Active");

        Ticket warningTicket2 = new Ticket();
        warningTicket2.setTicketId("WARNING-002");
        warningTicket2.setUserId("user2");
        warningTicket2.setDuration(30);
        warningTicket2.setDateCreated(LocalDateTime.now().minusMinutes(26)); // 4 minutes left
        warningTicket2.setFiveMinuteWarningSent(false);
        warningTicket2.setStatus("Active");

        Ticket alreadyWarnedTicket = new Ticket();
        alreadyWarnedTicket.setTicketId("WARNED-001");
        alreadyWarnedTicket.setUserId("user3");
        alreadyWarnedTicket.setDuration(60);
        alreadyWarnedTicket.setDateCreated(LocalDateTime.now().minusMinutes(56)); // 4 minutes left
        alreadyWarnedTicket.setFiveMinuteWarningSent(true); // Already warned
        alreadyWarnedTicket.setStatus("Active");

        List<Ticket> tickets = Arrays.asList(warningTicket1, warningTicket2, alreadyWarnedTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(tickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, times(2)).save(any(Ticket.class)); // Only tickets that need warning
        verify(notificationService, times(2)).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));

        // Verify warning flags were set
        assert(warningTicket1.getFiveMinuteWarningSent());
        assert(warningTicket2.getFiveMinuteWarningSent());
        // Already warned ticket should remain true
        assert(alreadyWarnedTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testSendFiveMinuteWarnings_WithRepositoryException_ShouldHandleGracefully() {
        // Arrange
        when(ticketRepository.findActiveTicketsWithDuration()).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithRepositoryException_ShouldHandleGracefully() {
        // Arrange
        when(ticketRepository.findActiveTicketsWithDuration()).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_WithExactWarningTime_ShouldSendWarning() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(55)); // Exactly 5 minutes left
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, times(1)).createFiveMinuteWarningNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        verify(ticketRepository, times(1)).save(testTicket);
        assert(testTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testCloseExpiredTickets_WithExactExpirationTime_ShouldCloseTicket() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(60)); // Exactly expired
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.closeExpiredTickets();

        // Assert
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketCompletionNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        assert(testTicket.getStatus().equals("Closed"));
        assert(testTicket.getDateCompleted() != null);
    }

    @Test
    void testSendFiveMinuteWarnings_WithNullFiveMinuteWarningSent_ShouldTreatAsFalse() {
        // Arrange
        testTicket.setFiveMinuteWarningSent(null); // Null should be treated as false
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(56)); // 4 minutes left
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.sendFiveMinuteWarnings();

        // Assert
        verify(notificationService, times(1)).createFiveMinuteWarningNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        verify(ticketRepository, times(1)).save(testTicket);
        assert(testTicket.getFiveMinuteWarningSent());
    }

    // ==================== STARTUP CHECK TESTS ====================

    @Test
    void testRunStartupCheck_ShouldCallBothMethods() {
        // Arrange
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        // Act
        ticketScheduledService.runStartupCheck();

        // Assert
        verify(ticketRepository, times(2)).findActiveTicketsWithDuration(); // Called by both methods
    }

    @Test
    void testRunStartupCheck_WithActiveTickets_ShouldProcessThem() {
        // Arrange
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Expired
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        // Act
        ticketScheduledService.runStartupCheck();

        // Assert
        verify(ticketRepository, times(2)).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket); // Should close expired ticket
        verify(notificationService).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }
}
