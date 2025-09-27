package com.apex.firefighter.unit.services;

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
        testTicket.setDuration(60); 
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(56)); 
        testTicket.setFiveMinuteWarningSent(false);
        testTicket.setStatus("Active");
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldSendWarningWhenTicketHas5MinutesLeft() {
        
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

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
       
        testTicket.setFiveMinuteWarningSent(true);
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldNotSendWarningWhenTicketHasMoreThan5MinutesLeft() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(50)); // Created 50 minutes ago (10 minutes left)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldNotSendWarningWhenTicketIsExpired() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_ShouldHandleNotificationServiceException() {
        
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(notificationService.createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class)))
            .thenThrow(new RuntimeException("Notification service error"));

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(notificationService, times(1)).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
       
        verify(ticketRepository, never()).save(any(Ticket.class));
        assert(!testTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testScheduledTicketCheck_ShouldCallBothMethods() {
        
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        ticketScheduledService.scheduledTicketCheck();

        verify(ticketRepository, times(2)).findActiveTicketsWithDuration(); 
    }

    @Test
    void testCloseExpiredTickets_ShouldCloseExpiredTicket() {
       
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); 
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
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
    void testCloseExpiredTickets_ShouldNotCloseNonExpiredTicket() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(30)); // Created 30 minutes ago (not expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_ShouldHandleNotificationServiceException() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(notificationService.createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class)))
            .thenThrow(new RuntimeException("Notification service error"));

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
        
        assert(testTicket.getStatus().equals("Closed"));
    }

    @Test
    void testCloseExpiredTickets_ShouldHandleRepositoryException() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Created 65 minutes ago (expired)
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);
        when(ticketRepository.save(any(Ticket.class))).thenThrow(new RuntimeException("Database error"));

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket);
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithEmptyTicketList_ShouldNotProcessAnyTickets() {
        
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithMultipleTickets_ShouldProcessCorrectly() {
        
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

        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, times(2)).save(any(Ticket.class)); 
        verify(notificationService, times(2)).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));

        assert(expiredTicket1.getStatus().equals("Closed"));
        assert(expiredTicket2.getStatus().equals("Closed"));
        
        assert(activeTicket.getStatus().equals("Active"));
    }

    @Test
    void testSendFiveMinuteWarnings_WithMultipleTickets_ShouldProcessCorrectly() {
        
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

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, times(2)).save(any(Ticket.class)); 
        verify(notificationService, times(2)).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));

        assert(warningTicket1.getFiveMinuteWarningSent());
        assert(warningTicket2.getFiveMinuteWarningSent());
       
        assert(alreadyWarnedTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testSendFiveMinuteWarnings_WithRepositoryException_ShouldHandleGracefully() {
        
        when(ticketRepository.findActiveTicketsWithDuration()).thenThrow(new RuntimeException("Database connection failed"));

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createFiveMinuteWarningNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testCloseExpiredTickets_WithRepositoryException_ShouldHandleGracefully() {

        when(ticketRepository.findActiveTicketsWithDuration()).thenThrow(new RuntimeException("Database connection failed"));

        
        ticketScheduledService.closeExpiredTickets();

        verify(ticketRepository).findActiveTicketsWithDuration();
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(notificationService, never()).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }

    @Test
    void testSendFiveMinuteWarnings_WithExactWarningTime_ShouldSendWarning() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(55)); // Exactly 5 minutes left
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

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
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(60)); // Exactly expired
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.closeExpiredTickets();

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
        
        testTicket.setFiveMinuteWarningSent(null); // Null should be treated as false
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(56)); // 4 minutes left
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.sendFiveMinuteWarnings();

        verify(notificationService, times(1)).createFiveMinuteWarningNotification(
            eq("test-user"),
            eq("TEST-001"),
            eq(testTicket)
        );
        verify(ticketRepository, times(1)).save(testTicket);
        assert(testTicket.getFiveMinuteWarningSent());
    }

    @Test
    void testRunStartupCheck_ShouldCallBothMethods() {
       
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(Arrays.asList());

        ticketScheduledService.runStartupCheck();

        verify(ticketRepository, times(2)).findActiveTicketsWithDuration(); 
    }

    @Test
    void testRunStartupCheck_WithActiveTickets_ShouldProcessThem() {
        
        testTicket.setDateCreated(LocalDateTime.now().minusMinutes(65)); // Expired
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTicketsWithDuration()).thenReturn(activeTickets);

        ticketScheduledService.runStartupCheck();

        verify(ticketRepository, times(2)).findActiveTicketsWithDuration();
        verify(ticketRepository).save(testTicket); // Should close expired ticket
        verify(notificationService).createTicketCompletionNotification(anyString(), anyString(), any(Ticket.class));
    }
}
