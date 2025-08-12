package com.apex.firefighter.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void testCreateAndGetTicket() {
        // Create a new ticket
        Ticket ticket = ticketService.createTicket("Test ticket", "user1", "critical-system-failure", "12345");
        assertThat(ticket).isNotNull();
        assertThat(ticket.getTicketId()).isNotNull(); // Generated ID
        assertThat(ticket.getDescription()).isEqualTo("Test ticket");
        assertThat(ticket.getUserId()).isEqualTo("user1");
        assertThat(ticket.getEmergencyType()).isEqualTo("critical-system-failure");
        assertThat(ticket.getEmergencyContact()).isEqualTo("12345");
        assertThat(ticket.getStatus()).isEqualTo("Active");

        // Get ticket by ID
        Optional<Ticket> foundById = ticketService.getTicketById(ticket.getId());
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getTicketId()).isEqualTo("JIRA-123");
        assertThat(foundById.get().getDuration()).isEqualTo(45);

        // Get ticket by ticket ID
        Optional<Ticket> foundByTicketId = ticketService.getTicketByTicketId("JIRA-123");
        assertThat(foundByTicketId).isPresent();
        assertThat(foundByTicketId.get().getId()).isEqualTo(ticket.getId());
        assertThat(foundByTicketId.get().getDuration()).isEqualTo(45);
    }

    @Test
    void testUpdateTicket() {
        // Create a ticket
        Ticket ticket = ticketService.createTicket("Original description", "user2", "network-outage", "67890");

        // Update ticket
        Ticket updatedTicket = ticketService.updateTicket(ticket.getId(), "Updated description", "Completed", "security-incident", "09876");
        assertThat(updatedTicket.getDescription()).isEqualTo("Updated description");
        assertThat(updatedTicket.getStatus()).isEqualTo("Completed");
        assertThat(updatedTicket.getEmergencyType()).isEqualTo("security-incident");
        assertThat(updatedTicket.getEmergencyContact()).isEqualTo("09876");

        // Verify updates persisted
        Optional<Ticket> found = ticketService.getTicketByTicketId("JIRA-456");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Updated description");
        assertThat(found.get().getStatus()).isEqualTo("Completed");
    }

    @Test
    void testDeleteTicket() {
        // Create a ticket
        Ticket ticket = ticketService.createTicket("To be deleted", "user3", "user-lockout", "11223");

        // Delete by ticket ID
        ticketService.deleteTicket(ticket.getTicketId());
        assertThat(ticketService.getTicketById(ticket.getId())).isEmpty();

        // Create another ticket
        Ticket ticket2 = ticketService.createTicket("To be deleted by ticket ID", "user4", "data-recovery", "44556");

        // Delete by ticket ID
        ticketService.deleteTicket(ticket2.getTicketId());
        assertThat(ticketService.getTicketByTicketId(ticket2.getTicketId())).isEmpty();
    }

    @Test
    void testGetAllTickets() {
        // Create multiple tickets
        Ticket ticket1 = ticketService.createTicket("First ticket", "user1", "type1", "111");
        Ticket ticket2 = ticketService.createTicket("Second ticket", "user2", "type2", "222");
        Ticket ticket3 = ticketService.createTicket("Third ticket", "user3", "type3", "333");

        // Get all tickets
        List<Ticket> allTickets = ticketService.getAllTickets();
        assertThat(allTickets).hasSize(3);
        assertThat(allTickets).extracting(Ticket::getTicketId)
                .containsExactlyInAnyOrder(ticket1.getTicketId(), ticket2.getTicketId(), ticket3.getTicketId());
    }

    @Test
    void testUpdateNonExistentTicket() {
        // Try to update non-existent ticket
        assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(999L, "New description", "new status", "new type", "new contact");
        });
    }
} 