package com.apex.firefighter.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        Ticket ticket = ticketService.createTicket("JIRA-123", "Test ticket", "user1", "critical-system-failure", "12345");
        assertThat(ticket).isNotNull();
        assertThat(ticket.getTicketId()).isEqualTo("JIRA-123");
        assertThat(ticket.getDescription()).isEqualTo("Test ticket");
        assertThat(ticket.getUserId()).isEqualTo("user1");
        assertThat(ticket.getEmergencyType()).isEqualTo("critical-system-failure");
        assertThat(ticket.getEmergencyContact()).isEqualTo("12345");
        assertThat(ticket.getStatus()).isEqualTo("Active");

        // Get ticket by ID
        Optional<Ticket> foundById = ticketService.getTicketById(ticket.getId());
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getTicketId()).isEqualTo("JIRA-123");

        // Get ticket by ticket ID
        Optional<Ticket> foundByTicketId = ticketService.getTicketByTicketId("JIRA-123");
        assertThat(foundByTicketId).isPresent();
        assertThat(foundByTicketId.get().getId()).isEqualTo(ticket.getId());
    }

    @Test
    void testUpdateTicket() {
        // Create a ticket
        Ticket ticket = ticketService.createTicket("JIRA-456", "Original description", "user2", "network-outage", "67890");

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
        Ticket ticket = ticketService.createTicket("JIRA-789", "To be deleted", "user3", "user-lockout", "11223");

        // Delete by ID
        boolean deletedById = ticketService.deleteTicket(ticket.getId());
        assertThat(deletedById).isTrue();
        assertThat(ticketService.getTicketById(ticket.getId())).isEmpty();

        // Create another ticket
        Ticket ticket2 = ticketService.createTicket("JIRA-101", "To be deleted by ticket ID", "user4", "data-recovery", "44556");

        // Delete by ticket ID
        boolean deletedByTicketId = ticketService.deleteTicketByTicketId("JIRA-101");
        assertThat(deletedByTicketId).isTrue();
        assertThat(ticketService.getTicketByTicketId("JIRA-101")).isEmpty();
    }

    @Test
    void testGetAllTickets() {
        // Create multiple tickets
        ticketService.createTicket("JIRA-1", "First ticket", "user1", "type1", "111");
        ticketService.createTicket("JIRA-2", "Second ticket", "user2", "type2", "222");
        ticketService.createTicket("JIRA-3", "Third ticket", "user3", "type3", "333");

        // Get all tickets
        List<Ticket> allTickets = ticketService.getAllTickets();
        assertThat(allTickets).hasSize(3);
        assertThat(allTickets).extracting(Ticket::getTicketId)
                .containsExactlyInAnyOrder("JIRA-1", "JIRA-2", "JIRA-3");
    }

    @Test
    void testUpdateNonExistentTicket() {
        // Try to update non-existent ticket
        assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicket(999L, "New description", "new status", "new type", "new contact");
        });
    }
} 