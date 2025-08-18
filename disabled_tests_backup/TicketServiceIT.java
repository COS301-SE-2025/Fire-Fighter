package com.apex.firefighter.integration.services;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TicketServiceIT {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    private User adminUser;
    private User normalUser;
    private Ticket ticket;

    @BeforeEach
    @Transactional
    void setup() {
        // Clean up tickets and users before each test
        ticketRepository.deleteAll();
        userRepository.deleteAll();

        // Create admin and normal user
        adminUser = new User("admin1", "Admin User", "admin1@example.com", "Fire Department");
        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        normalUser = new User("user1", "Normal User", "user1@example.com", "Medical Department");
        normalUser.setIsAdmin(false);
        userRepository.save(normalUser);

        // Create a ticket
        ticket = ticketService.createTicket(
            "TICKET-001",
            "Fire emergency at warehouse",
            normalUser.getUserId(),
            "Fire",
            "0123456789",
            60
        );
    }

    @Test
    void testCreateTicket() {
        Ticket newTicket = ticketService.createTicket(
            "TICKET-002",
            "Medical emergency at school",
            normalUser.getUserId(),
            "Medical",
            "0987654321",
            30
        );
        Assertions.assertNotNull(newTicket.getId());
        Assertions.assertEquals("TICKET-002", newTicket.getTicketId());
        Assertions.assertEquals("Medical emergency at school", newTicket.getDescription());
    }

    @Test
    void testUpdateTicket() {
        Ticket updated = ticketService.updateTicket(
            ticket.getId(),
            "Updated description",
            "Active",
            "Fire",
            "0123456789",
            90
        );
        Assertions.assertEquals("Updated description", updated.getDescription());
        Assertions.assertEquals(90, updated.getDuration());
    }

    @Test
    void testRevokeTicket() {
        Ticket revoked = ticketService.revokeTicket(
            ticket.getId(),
            adminUser.getUserId(),
            "Not a real emergency"
        );
        Assertions.assertEquals("Rejected", revoked.getStatus());
        Assertions.assertEquals("Not a real emergency", revoked.getRejectReason());
        Assertions.assertEquals(adminUser.getUserId(), revoked.getRevokedBy());
        Assertions.assertNotNull(revoked.getDateCompleted());
    }

    @Test
    void testDeleteTicket() {
        boolean deleted = ticketService.deleteTicket(ticket.getId());
        Assertions.assertTrue(deleted);
        Assertions.assertFalse(ticketRepository.existsById(ticket.getId()));
    }

    @Test
    void testGetActiveTickets() {
        // Set status to Active
        ticket.setStatus("Active");
        ticketRepository.save(ticket);

        List<Ticket> activeTickets = ticketService.getActiveTickets();
        Assertions.assertFalse(activeTickets.isEmpty());
        Assertions.assertEquals("Active", activeTickets.get(0).getStatus());
    }

    @Test
    void testGetTicketHistory() {
        List<Ticket> history = ticketService.getTicketHistory();
        Assertions.assertFalse(history.isEmpty());
        Assertions.assertEquals(ticket.getTicketId(), history.get(0).getTicketId());
    }

    @Test
    void testGetTicketsByStatus() {
        ticket.setStatus("Active");
        ticketRepository.save(ticket);

        List<Ticket> activeTickets = ticketService.getTicketsByStatus("Active");
        Assertions.assertFalse(activeTickets.isEmpty());
        Assertions.assertEquals("Active", activeTickets.get(0).getStatus());
    }

    @Test
    void testGetTicketsByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        List<Ticket> ticketsInRange = ticketService.getTicketsByDateRange(start, end);
        Assertions.assertFalse(ticketsInRange.isEmpty());
        Assertions.assertEquals(ticket.getTicketId(), ticketsInRange.get(0).getTicketId());
    }
}