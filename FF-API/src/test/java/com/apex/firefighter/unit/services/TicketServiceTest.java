package com.apex.firefighter.unit.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setTicketId("TEST-001");
        testTicket.setDescription("Test emergency");
        testTicket.setUserId("test-user");
        testTicket.setEmergencyType("fire");
        testTicket.setEmergencyContact("123-456-7890");
        testTicket.setDuration(60);
        testTicket.setStatus("Active");
        testTicket.setDateCreated(LocalDateTime.now());

        testUser = new User();
        testUser.setUserId("test-user");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setIsAdmin(false);

        adminUser = new User();
        adminUser.setUserId("admin-user");
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setIsAdmin(true);
    }

    @Test
    void createTicket_WithValidData_ShouldCreateTicketSuccessfully() {
        
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.empty());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(notificationService.createTicketCreationNotification(anyString(), anyString(), any(Ticket.class))).thenReturn(null);

        Ticket result = ticketService.createTicket("TEST-001", "Test emergency", "test-user", "fire", "123-456-7890", 60);

        assertThat(result).isNotNull();
        assertThat(result.getTicketId()).isEqualTo("TEST-001");
        assertThat(result.getDescription()).isEqualTo("Test emergency");
        assertThat(result.getUserId()).isEqualTo("test-user");
        assertThat(result.getEmergencyType()).isEqualTo("fire");
        assertThat(result.getEmergencyContact()).isEqualTo("123-456-7890");
        assertThat(result.getDuration()).isEqualTo(60);

        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository).save(any(Ticket.class));
        verify(notificationService).createTicketCreationNotification("test-user", "TEST-001", testTicket);
    }

    @Test
    void createTicket_WithDuplicateTicketId_ShouldThrowException() {
        
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.createTicket("TEST-001", "Test emergency", "test-user", "fire", "123-456-7890", 60);
        });

        assertThat(exception.getMessage()).contains("Ticket with ID 'TEST-001' already exists");
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void createTicket_WithNotificationServiceException_ShouldStillCreateTicket() {
        
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.empty());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        doThrow(new RuntimeException("Notification service error")).when(notificationService)
                .createTicketCreationNotification(anyString(), anyString(), any(Ticket.class));

        Ticket result = ticketService.createTicket("TEST-001", "Test emergency", "test-user", "fire", "123-456-7890", 60);

        assertThat(result).isNotNull();
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository).save(any(Ticket.class));
        verify(notificationService).createTicketCreationNotification("test-user", "TEST-001", testTicket);
    }

    @Test
    void updateTicket_WithValidData_ShouldUpdateTicketSuccessfully() {
        
        Ticket updatedTicket = new Ticket();
        updatedTicket.setDescription("Updated description");
        updatedTicket.setStatus("Completed");
        updatedTicket.setEmergencyType("medical");
        updatedTicket.setEmergencyContact("987-654-3210");
        updatedTicket.setDuration(90);

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(updatedTicket);

        Ticket result = ticketService.updateTicket(1L, "Updated description", "Completed", "medical", "987-654-3210", 90);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getStatus()).isEqualTo("Completed");
        assertThat(result.getEmergencyType()).isEqualTo("medical");
        assertThat(result.getEmergencyContact()).isEqualTo("987-654-3210");
        assertThat(result.getDuration()).isEqualTo(90);

        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(testTicket);
    }

    @Test
    void updateTicket_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.updateTicket(1L, "New description", null, null, null, null);

        assertThat(result).isNotNull();
        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(testTicket);
    }

    @Test
    void updateTicket_WithNonExistentId_ShouldThrowException() {
        
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(999L, "New description", "Active", "fire", "123-456-7890", 60);
        });

        assertThat(exception.getMessage()).contains("Ticket not found with ID: 999");
        verify(ticketRepository).findById(999L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void getAllTickets_ShouldReturnAllTickets() {
        
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findAll();
    }

    @Test
    void getTicketById_WithExistingId_ShouldReturnTicket() {
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        Optional<Ticket> result = ticketService.getTicketById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTicket);
        verify(ticketRepository).findById(1L);
    }

    @Test
    void getTicketById_WithNonExistentId_ShouldReturnEmpty() {
        
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.getTicketById(999L);

        assertThat(result).isEmpty();
        verify(ticketRepository).findById(999L);
    }

    @Test
    void getTicketByTicketId_WithExistingTicketId_ShouldReturnTicket() {
        
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));

        Optional<Ticket> result = ticketService.getTicketByTicketId("TEST-001");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTicket);
        verify(ticketRepository).findByTicketId("TEST-001");
    }

    @Test
    void getTicketByTicketId_WithNonExistentTicketId_ShouldReturnEmpty() {
        
        when(ticketRepository.findByTicketId("NON-EXISTENT")).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.getTicketByTicketId("NON-EXISTENT");

        assertThat(result).isEmpty();
        verify(ticketRepository).findByTicketId("NON-EXISTENT");
    }

    @Test
    void searchTicketsByDescription_WithMatchingDescription_ShouldReturnMatchingTickets() {
        
        Ticket ticket1 = new Ticket();
        ticket1.setDescription("Fire emergency in building A");
        Ticket ticket2 = new Ticket();
        ticket2.setDescription("Medical emergency");
        Ticket ticket3 = new Ticket();
        ticket3.setDescription("Fire alarm malfunction");

        List<Ticket> allTickets = Arrays.asList(ticket1, ticket2, ticket3);
        when(ticketRepository.findAll()).thenReturn(allTickets);

        List<Ticket> result = ticketService.searchTicketsByDescription("fire");

        assertThat(result).hasSize(2);
        assertThat(result).contains(ticket1, ticket3);
        verify(ticketRepository).findAll();
    }

    @Test
    void searchTicketsByDescription_WithNoMatches_ShouldReturnEmptyList() {
        
        List<Ticket> allTickets = Arrays.asList(testTicket);
        when(ticketRepository.findAll()).thenReturn(allTickets);

        List<Ticket> result = ticketService.searchTicketsByDescription("nonexistent");

        assertThat(result).isEmpty();
        verify(ticketRepository).findAll();
    }

    @Test
    void searchTicketsByDescription_WithNullDescription_ShouldHandleGracefully() {
        
        Ticket ticketWithNullDesc = new Ticket();
        ticketWithNullDesc.setDescription(null);
        List<Ticket> allTickets = Arrays.asList(ticketWithNullDesc);
        when(ticketRepository.findAll()).thenReturn(allTickets);

        List<Ticket> result = ticketService.searchTicketsByDescription("test");

        assertThat(result).isEmpty();
        verify(ticketRepository).findAll();
    }

    @Test
    void ticketExists_WithExistingTicketId_ShouldReturnTrue() {
       
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));

        boolean result = ticketService.ticketExists("TEST-001");

        assertThat(result).isTrue();
        verify(ticketRepository).findByTicketId("TEST-001");
    }

    @Test
    void ticketExists_WithNonExistentTicketId_ShouldReturnFalse() {
       
        when(ticketRepository.findByTicketId("NON-EXISTENT")).thenReturn(Optional.empty());

        boolean result = ticketService.ticketExists("NON-EXISTENT");

        assertThat(result).isFalse();
        verify(ticketRepository).findByTicketId("NON-EXISTENT");
    }

    @Test
    void getTicketCount_ShouldReturnCorrectCount() {
       
        when(ticketRepository.count()).thenReturn(5L);

        long result = ticketService.getTicketCount();

        assertThat(result).isEqualTo(5L);
        verify(ticketRepository).count();
    }

    @Test
    void deleteTicket_WithExistingId_ShouldReturnTrue() {
        
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        boolean result = ticketService.deleteTicket(1L);

        assertThat(result).isTrue();
        verify(ticketRepository).existsById(1L);
        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void deleteTicket_WithNonExistentId_ShouldReturnFalse() {
        
        when(ticketRepository.existsById(999L)).thenReturn(false);

        boolean result = ticketService.deleteTicket(999L);

        assertThat(result).isFalse();
        verify(ticketRepository).existsById(999L);
        verify(ticketRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTicketByTicketId_WithExistingTicketId_ShouldReturnTrue() {
       
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));
        doNothing().when(ticketRepository).delete(testTicket);

        boolean result = ticketService.deleteTicketByTicketId("TEST-001");

        assertThat(result).isTrue();
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository).delete(testTicket);
    }

    @Test
    void deleteTicketByTicketId_WithNonExistentTicketId_ShouldReturnFalse() {
        
        when(ticketRepository.findByTicketId("NON-EXISTENT")).thenReturn(Optional.empty());

        boolean result = ticketService.deleteTicketByTicketId("NON-EXISTENT");

        assertThat(result).isFalse();
        verify(ticketRepository).findByTicketId("NON-EXISTENT");
        verify(ticketRepository, never()).delete(any(Ticket.class));
    }

    @Test
    void updateTicketDescription_WithExistingTicketId_ShouldUpdateSuccessfully() {
        
        Ticket updatedTicket = new Ticket();
        updatedTicket.setDescription("Updated description");

        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(testTicket)).thenReturn(updatedTicket);

        Ticket result = ticketService.updateTicketDescription("TEST-001", "Updated description");

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository).save(testTicket);
    }

    @Test
    void updateTicketDescription_WithNonExistentTicketId_ShouldThrowException() {
       
        when(ticketRepository.findByTicketId("NON-EXISTENT")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicketDescription("NON-EXISTENT", "New description");
        });

        assertThat(exception.getMessage()).contains("Ticket not found with ID: NON-EXISTENT");
        verify(ticketRepository).findByTicketId("NON-EXISTENT");
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void getActiveTickets_ShouldReturnActiveTickets() {
        
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findActiveTickets()).thenReturn(activeTickets);

        List<Ticket> result = ticketService.getActiveTickets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findActiveTickets();
    }

    @Test
    void getTicketHistory_ShouldReturnAllTicketsOrderedByDate() {
       
        List<Ticket> ticketHistory = Arrays.asList(testTicket);
        when(ticketRepository.findAllByOrderByDateCreatedDesc()).thenReturn(ticketHistory);

        List<Ticket> result = ticketService.getTicketHistory();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findAllByOrderByDateCreatedDesc();
    }

    @Test
    void revokeTicket_WithValidAdminAndActiveTicket_ShouldRevokeSuccessfully() {
        
        String rejectReason = "Policy violation";
        Ticket revokedTicket = new Ticket();
        revokedTicket.setStatus("Rejected");
        revokedTicket.setRejectReason(rejectReason);
        revokedTicket.setRevokedBy("admin-user");

        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(revokedTicket);
        when(notificationService.createTicketRevocationNotification(anyString(), anyString(), any(Ticket.class), anyString())).thenReturn(null);

        Ticket result = ticketService.revokeTicket(1L, "admin-user", rejectReason);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("Rejected");
        assertThat(result.getRejectReason()).isEqualTo(rejectReason);
        assertThat(result.getRevokedBy()).isEqualTo("admin-user");

        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketRevocationNotification(eq("test-user"), eq("TEST-001"), eq(testTicket), eq(rejectReason));
    }

    @Test
    void revokeTicket_WithNonExistentAdmin_ShouldThrowException() {
       
        when(userRepository.findById("non-existent-admin")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicket(1L, "non-existent-admin", "Reason");
        });

        assertThat(exception.getMessage()).contains("Admin user not found: non-existent-admin");
        verify(userRepository).findById("non-existent-admin");
        verify(ticketRepository, never()).findById(anyLong());
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicket_WithNonAdminUser_ShouldThrowException() {
        
        when(userRepository.findById("test-user")).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicket(1L, "test-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("User does not have admin privileges: test-user");
        verify(userRepository).findById("test-user");
        verify(ticketRepository, never()).findById(anyLong());
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicket_WithNonExistentTicket_ShouldThrowException() {
        
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicket(999L, "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket not found with ID: 999");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findById(999L);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicket_WithAlreadyCompletedTicket_ShouldThrowException() {
       
        testTicket.setStatus("Completed");
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicket(1L, "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket is already completed: 1");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicket_WithAlreadyRejectedTicket_ShouldThrowException() {
        
        testTicket.setStatus("Rejected");
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicket(1L, "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket is already rejected: 1");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicket_WithNotificationServiceException_ShouldStillRevokeTicket() {
       
        String rejectReason = "Policy violation";
        Ticket revokedTicket = new Ticket();
        revokedTicket.setStatus("Rejected");

        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(revokedTicket);
        doThrow(new RuntimeException("Notification service error")).when(notificationService)
                .createTicketRevocationNotification(anyString(), anyString(), any(Ticket.class), anyString());

        Ticket result = ticketService.revokeTicket(1L, "admin-user", rejectReason);

        assertThat(result).isNotNull();
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketRevocationNotification(anyString(), anyString(), any(Ticket.class), anyString());
    }

    @Test
    void revokeTicketByTicketId_WithValidAdminAndActiveTicket_ShouldRevokeSuccessfully() {
       
        String rejectReason = "Security violation";
        Ticket revokedTicket = new Ticket();
        revokedTicket.setStatus("Rejected");
        revokedTicket.setRejectReason(rejectReason);
        revokedTicket.setRevokedBy("admin-user");

        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(revokedTicket);
        when(notificationService.createTicketRevocationNotification(anyString(), anyString(), any(Ticket.class), anyString())).thenReturn(null);

        Ticket result = ticketService.revokeTicketByTicketId("TEST-001", "admin-user", rejectReason);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("Rejected");
        assertThat(result.getRejectReason()).isEqualTo(rejectReason);
        assertThat(result.getRevokedBy()).isEqualTo("admin-user");

        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository).save(testTicket);
        verify(notificationService).createTicketRevocationNotification(eq("test-user"), eq("TEST-001"), eq(testTicket), eq(rejectReason));
    }

    @Test
    void revokeTicketByTicketId_WithNonExistentAdmin_ShouldThrowException() {
        
        when(userRepository.findById("non-existent-admin")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicketByTicketId("TEST-001", "non-existent-admin", "Reason");
        });

        assertThat(exception.getMessage()).contains("Admin user not found: non-existent-admin");
        verify(userRepository).findById("non-existent-admin");
        verify(ticketRepository, never()).findByTicketId(anyString());
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicketByTicketId_WithNonAdminUser_ShouldThrowException() {
        
        when(userRepository.findById("test-user")).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicketByTicketId("TEST-001", "test-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("User does not have admin privileges: test-user");
        verify(userRepository).findById("test-user");
        verify(ticketRepository, never()).findByTicketId(anyString());
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicketByTicketId_WithNonExistentTicket_ShouldThrowException() {
       
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findByTicketId("NON-EXISTENT")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicketByTicketId("NON-EXISTENT", "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket not found with ticket ID: NON-EXISTENT");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findByTicketId("NON-EXISTENT");
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicketByTicketId_WithAlreadyCompletedTicket_ShouldThrowException() {
        
        testTicket.setStatus("Completed");
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicketByTicketId("TEST-001", "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket is already completed: TEST-001");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void revokeTicketByTicketId_WithAlreadyRejectedTicket_ShouldThrowException() {
        
        testTicket.setStatus("Rejected");
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(ticketRepository.findByTicketId("TEST-001")).thenReturn(Optional.of(testTicket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketService.revokeTicketByTicketId("TEST-001", "admin-user", "Reason");
        });

        assertThat(exception.getMessage()).contains("Ticket is already rejected: TEST-001");
        verify(userRepository).findById("admin-user");
        verify(ticketRepository).findByTicketId("TEST-001");
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void isUserAdmin_WithAdminUser_ShouldReturnTrue() {
        
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));

        boolean result = ticketService.isUserAdmin("admin-user");

        assertThat(result).isTrue();
        verify(userRepository).findById("admin-user");
    }

    @Test
    void isUserAdmin_WithNonAdminUser_ShouldReturnFalse() {
        
        when(userRepository.findById("test-user")).thenReturn(Optional.of(testUser));

        boolean result = ticketService.isUserAdmin("test-user");

        assertThat(result).isFalse();
        verify(userRepository).findById("test-user");
    }

    @Test
    void isUserAdmin_WithNonExistentUser_ShouldReturnFalse() {
        
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        boolean result = ticketService.isUserAdmin("non-existent");

        assertThat(result).isFalse();
        verify(userRepository).findById("non-existent");
    }

    @Test
    void getTicketsByStatus_ShouldReturnTicketsWithSpecifiedStatus() {
       
        List<Ticket> activeTickets = Arrays.asList(testTicket);
        when(ticketRepository.findByStatus("Active")).thenReturn(activeTickets);

        List<Ticket> result = ticketService.getTicketsByStatus("Active");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findByStatus("Active");
    }

    @Test
    void getTicketsByUserId_ShouldReturnTicketsForSpecifiedUser() {
      
        List<Ticket> userTickets = Arrays.asList(testTicket);
        when(ticketRepository.findByUserId("test-user")).thenReturn(userTickets);

        List<Ticket> result = ticketService.getTicketsByUserId("test-user");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findByUserId("test-user");
    }

    @Test
    void getTicketsByDateRange_ShouldReturnTicketsInDateRange() {
  
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Ticket> ticketsInRange = Arrays.asList(testTicket);
        when(ticketRepository.findByDateCreatedBetween(startDate, endDate)).thenReturn(ticketsInRange);

        List<Ticket> result = ticketService.getTicketsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTicket);
        verify(ticketRepository).findByDateCreatedBetween(startDate, endDate);
    }

    @Test
    void getTicketsByDateRange_WithEmptyResult_ShouldReturnEmptyList() {

        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().minusDays(5);
        when(ticketRepository.findByDateCreatedBetween(startDate, endDate)).thenReturn(Arrays.asList());

        List<Ticket> result = ticketService.getTicketsByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
        verify(ticketRepository).findByDateCreatedBetween(startDate, endDate);
    }

    @Test
    void getTicketsByStatus_WithEmptyResult_ShouldReturnEmptyList() {

        when(ticketRepository.findByStatus("Completed")).thenReturn(Arrays.asList());

        List<Ticket> result = ticketService.getTicketsByStatus("Completed");

        assertThat(result).isEmpty();
        verify(ticketRepository).findByStatus("Completed");
    }

    @Test
    void getTicketsByUserId_WithEmptyResult_ShouldReturnEmptyList() {
        when(ticketRepository.findByUserId("non-existent-user")).thenReturn(Arrays.asList());

        List<Ticket> result = ticketService.getTicketsByUserId("non-existent-user");

        assertThat(result).isEmpty();
        verify(ticketRepository).findByUserId("non-existent-user");
    }
}