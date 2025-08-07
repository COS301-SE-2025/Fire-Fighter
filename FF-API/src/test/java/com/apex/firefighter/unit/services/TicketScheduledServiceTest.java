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
}
