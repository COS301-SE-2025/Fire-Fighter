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
        Ticket ticket = ticketService.createTicket("JIRA-123", "Test ticket", true);
        assertThat(ticket).isNotNull();
        assertThat(ticket.getTicketId()).isEqualTo("JIRA-123");
        assertThat(ticket.getDescription()).isEqualTo("Test ticket");
        assertThat(ticket.isValid()).isTrue();

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
        Ticket ticket = ticketService.createTicket("JIRA-456", "Original description", true);

        // Update description
        Ticket updatedDescription = ticketService.updateTicketDescription("JIRA-456", "Updated description");
        assertThat(updatedDescription.getDescription()).isEqualTo("Updated description");

        // Update validity
        Ticket updatedValidity = ticketService.updateTicketValidity("JIRA-456", false);
        assertThat(updatedValidity.isValid()).isFalse();

        // Verify updates persisted
        Optional<Ticket> found = ticketService.getTicketByTicketId("JIRA-456");
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Updated description");
        assertThat(found.get().isValid()).isFalse();
    }

    @Test
    void testDeleteTicket() {
        // Create a ticket
        Ticket ticket = ticketService.createTicket("JIRA-789", "To be deleted", true);

        // Delete by ID
        boolean deletedById = ticketService.deleteTicket(ticket.getId());
        assertThat(deletedById).isTrue();
        assertThat(ticketService.getTicketById(ticket.getId())).isEmpty();

        // Create another ticket
        Ticket ticket2 = ticketService.createTicket("JIRA-101", "To be deleted by ticket ID", true);

        // Delete by ticket ID
        boolean deletedByTicketId = ticketService.deleteTicketByTicketId("JIRA-101");
        assertThat(deletedByTicketId).isTrue();
        assertThat(ticketService.getTicketByTicketId("JIRA-101")).isEmpty();
    }

    @Test
    void testVerifyTicket() {
        // Create a valid ticket
        Ticket validTicket = ticketService.createTicket("JIRA-202", "Valid ticket", true);
        assertThat(ticketService.verifyTicket("JIRA-202")).isTrue();

        // Create an invalid ticket
        Ticket invalidTicket = ticketService.createTicket("JIRA-303", "Invalid ticket", false);
        assertThat(ticketService.verifyTicket("JIRA-303")).isFalse();

        // Verify non-existent ticket
        assertThat(ticketService.verifyTicket("NON-EXISTENT")).isFalse();
    }

    @Test
    void testGetAllTickets() {
        // Create multiple tickets
        ticketService.createTicket("JIRA-1", "First ticket", true);
        ticketService.createTicket("JIRA-2", "Second ticket", true);
        ticketService.createTicket("JIRA-3", "Third ticket", false);

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
            ticketService.updateTicketDescription("NON-EXISTENT", "New description");
        });

        assertThrows(RuntimeException.class, () -> {
            ticketService.updateTicketValidity("NON-EXISTENT", true);
        });
    }
} 