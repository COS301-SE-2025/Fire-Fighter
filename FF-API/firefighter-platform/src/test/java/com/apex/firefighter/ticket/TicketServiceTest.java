package com.apex.firefighter.ticket;

import com.apex.firefighter.config.TestConfig;
import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.ticket.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestConfig.class)
@Transactional
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void testCreateAndGetTicket() {
        // Create a new ticket - fix method signature
        Ticket ticket = ticketService.createTicket("Test ticket", "user1", "critical-system-failure", "12345");
        assertThat(ticket).isNotNull();
        assertThat(ticket.getTicketId()).isNotNull(); // Generated automatically
        assertThat(ticket.getDescription()).isEqualTo("Test ticket");
        assertThat(ticket.getUserId()).isEqualTo("user1");
        assertThat(ticket.getEmergencyType()).isEqualTo("critical-system-failure");
        assertThat(ticket.getEmergencyContact()).isEqualTo("12345");
        assertThat(ticket.getStatus()).isEqualTo("Active");
    }

    @Test
    void testUpdateTicket() {
        // Create a ticket first
        Ticket ticket = ticketService.createTicket("Original description", "user1", "critical-system-failure", "12345");
        
        // Update the ticket - fix method signature
        Ticket updatedTicket = ticketService.updateTicket(ticket.getId(), "Updated description", "Completed", "network-outage", "67890");
        
        assertThat(updatedTicket).isNotNull();
        assertThat(updatedTicket.getDescription()).isEqualTo("Updated description");
        assertThat(updatedTicket.getStatus()).isEqualTo("Completed");
        assertThat(updatedTicket.getEmergencyType()).isEqualTo("network-outage");
        assertThat(updatedTicket.getEmergencyContact()).isEqualTo("67890");
    }

    @Test
    void testDeleteTicket() {
        // Create a ticket first
        Ticket ticket = ticketService.createTicket("Test ticket for deletion", "user1", "critical-system-failure", "12345");
        
        // Delete the ticket - fix method name
        ticketService.deleteTicket(ticket.getTicketId());
        
        // Verify deletion
        Optional<Ticket> deletedTicket = ticketService.getTicketByTicketId(ticket.getTicketId());
        assertThat(deletedTicket).isEmpty();
    }

    @Test
    void testGetAllTickets() {
        // Create multiple tickets
        ticketService.createTicket("Ticket 1", "user1", "critical-system-failure", "12345");
        ticketService.createTicket("Ticket 2", "user2", "network-outage", "67890");
        ticketService.createTicket("Ticket 3", "user3", "security-incident", "11223");
        
        // Get all tickets
        List<Ticket> tickets = ticketService.getAllTickets();
        assertThat(tickets).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void testUpdateTicketStatus() {
        // Create a ticket
        Ticket ticket = ticketService.createTicket("Test ticket", "user1", "critical-system-failure", "12345");
        
        // Update status - fix method signature
        Ticket updatedTicket = ticketService.updateTicket(ticket.getId(), ticket.getDescription(), "Completed", ticket.getEmergencyType(), ticket.getEmergencyContact());
        
        assertThat(updatedTicket.getStatus()).isEqualTo("Completed");
    }
} 
