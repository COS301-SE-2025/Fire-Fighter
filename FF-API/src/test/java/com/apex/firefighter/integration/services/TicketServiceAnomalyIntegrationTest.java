package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.AnomalyNotificationService;
import com.apex.firefighter.service.DolibarrUserGroupService;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TicketServiceAnomalyIntegrationTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private NotificationService notificationService;
    @Mock private DolibarrUserGroupService dolibarrUserGroupService;
    @Mock private UserRepository userRepository;
    @Mock private AnomalyNotificationService anomalyNotificationService;

    @InjectMocks private TicketService ticketService;

    private User testUser;
    private Ticket savedTicket;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("user123");
        testUser.setUsername("testuser");

        savedTicket = new Ticket();
        savedTicket.setTicketId("TICKET-001");
        savedTicket.setDateCreated(LocalDateTime.now());
    }

    @Test
    void createTicket_WithValidUser_ShouldCheckForAnomaliesAndNotify() throws Exception {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(notificationService.createTicketCreationNotification(anyString(), anyString(), any(Ticket.class))).thenReturn(null);
        doNothing().when(anomalyNotificationService).checkAndNotifyAnomalies(any(User.class), any(Ticket.class));
        doNothing().when(dolibarrUserGroupService).addUserToGroup(anyString(), anyString(), anyString());

        // Act
        Ticket result = ticketService.createTicket("Test", "user123", "Fire", "911", 60);

        // Assert
        assertThat(result).isNotNull();
        
        // Wait a short time for async operations to complete
        Thread.sleep(500);
        
        verify(anomalyNotificationService).checkAndNotifyAnomalies(testUser, savedTicket);
    }

    @Test
    void createTicket_WithUserNotFound_ShouldStillCreateTicketButNotCheckAnomalies() throws Exception {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        when(notificationService.createTicketCreationNotification(anyString(), anyString(), any(Ticket.class))).thenReturn(null);
        doNothing().when(dolibarrUserGroupService).addUserToGroup(anyString(), anyString(), anyString());

        // Act
        Ticket result = ticketService.createTicket("Test", "user123", "Fire", "911", 60);

        // Assert
        assertThat(result).isNotNull();
        verify(anomalyNotificationService, never()).checkAndNotifyAnomalies(any(User.class), any(Ticket.class));
    }

    @Test
    void createTicket_WithAnomalyNotificationException_ShouldStillCreateTicket() throws Exception {
        // Arrange
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(notificationService.createTicketCreationNotification(anyString(), anyString(), any(Ticket.class))).thenReturn(null);
        doThrow(new RuntimeException("Anomaly error")).when(anomalyNotificationService).checkAndNotifyAnomalies(any(User.class), any(Ticket.class));
        doNothing().when(dolibarrUserGroupService).addUserToGroup(anyString(), anyString(), anyString());

        // Act
        Ticket result = ticketService.createTicket("Test", "user123", "Fire", "911", 60);

        // Assert
        assertThat(result).isNotNull();
        
        // Wait a short time for async operations to complete
        Thread.sleep(500);
        
        verify(anomalyNotificationService).checkAndNotifyAnomalies(testUser, savedTicket);
    }
}
