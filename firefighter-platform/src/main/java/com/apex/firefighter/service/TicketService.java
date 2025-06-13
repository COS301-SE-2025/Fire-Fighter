package com.apex.firefighter.service;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Create a new ticket
     */
    public Ticket createTicket(String ticketId, String description, boolean valid) {
        System.out.println("🔵 CREATE: Creating new ticket - " + ticketId);
        
        Ticket ticket = new Ticket(ticketId, description, valid);
        Ticket savedTicket = ticketRepository.save(ticket);
        
        System.out.println("✅ Created: " + savedTicket);
        return savedTicket;
    }

    /**
     * Get all tickets
     */
    public List<Ticket> getAllTickets() {
        System.out.println("🔵 READ: Fetching all tickets");
        
        List<Ticket> tickets = ticketRepository.findAll();
        System.out.println("✅ Found " + tickets.size() + " tickets");
        
        return tickets;
    }

    /**
     * Get ticket by ID
     */
    public Optional<Ticket> getTicketById(Long id) {
        System.out.println("🔵 READ: Fetching ticket by ID - " + id);
        
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            System.out.println("✅ Found: " + ticket.get());
        } else {
            System.out.println("❌ Not found: Ticket with ID " + id);
        }
        
        return ticket;
    }

    /**
     * Get ticket by ticket ID (e.g., "JIRA-123")
     */
    public Optional<Ticket> getTicketByTicketId(String ticketId) {
        System.out.println("🔵 READ: Fetching ticket by ticket ID - " + ticketId);
        
        Optional<Ticket> ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket.isPresent()) {
            System.out.println("✅ Found: " + ticket.get());
        } else {
            System.out.println("❌ Not found: Ticket with ID " + ticketId);
        }
        
        return ticket;
    }

    /**
     * Update ticket validity
     */
    public Ticket updateTicketValidity(String ticketId, boolean valid) {
        System.out.println("🔵 UPDATE: Updating ticket validity - " + ticketId);
        
        Optional<Ticket> existingTicket = ticketRepository.findByTicketId(ticketId);
        
        if (existingTicket.isPresent()) {
            Ticket ticket = existingTicket.get();
            ticket.setValid(valid);
            
            Ticket updatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ Updated: " + updatedTicket);
            return updatedTicket;
        } else {
            System.out.println("❌ Update failed: Ticket with ID " + ticketId + " not found");
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    /**
     * Update ticket description
     */
    public Ticket updateTicketDescription(String ticketId, String description) {
        System.out.println("🔵 UPDATE: Updating ticket description - " + ticketId);
        
        Optional<Ticket> existingTicket = ticketRepository.findByTicketId(ticketId);
        
        if (existingTicket.isPresent()) {
            Ticket ticket = existingTicket.get();
            ticket.setDescription(description);
            
            Ticket updatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ Updated: " + updatedTicket);
            return updatedTicket;
        } else {
            System.out.println("❌ Update failed: Ticket with ID " + ticketId + " not found");
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    /**
     * Delete ticket by ID
     */
    public boolean deleteTicket(Long id) {
        System.out.println("🔵 DELETE: Deleting ticket with ID - " + id);
        
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            System.out.println("✅ Deleted: Ticket with ID " + id);
            return true;
        } else {
            System.out.println("❌ Delete failed: Ticket with ID " + id + " not found");
            return false;
        }
    }

    /**
     * Delete ticket by ticket ID
     */
    public boolean deleteTicketByTicketId(String ticketId) {
        System.out.println("🔵 DELETE: Deleting ticket with ticket ID - " + ticketId);
        
        Optional<Ticket> ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket.isPresent()) {
            ticketRepository.delete(ticket.get());
            System.out.println("✅ Deleted: Ticket with ID " + ticketId);
            return true;
        } else {
            System.out.println("❌ Delete failed: Ticket with ID " + ticketId + " not found");
            return false;
        }
    }

    /**
     * Verify ticket validity
     * This method can be used to verify a ticket's validity with the mock ticket service
     */
    public boolean verifyTicket(String ticketId) {
        System.out.println("🔵 VERIFY: Verifying ticket - " + ticketId);
        
        Optional<Ticket> ticket = ticketRepository.findByTicketId(ticketId);
        if (ticket.isPresent()) {
            boolean isValid = ticket.get().isValid();
            System.out.println("✅ Verification result: " + isValid);
            return isValid;
        }
        
        System.out.println("❌ Verification failed: Ticket not found - " + ticketId);
        return false;
    }
} 