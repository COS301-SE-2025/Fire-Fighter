package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TicketService handles ticket management.
 * This service is responsible for:
 * - Creating and managing tickets
 * - Validating ticket status
 * - Querying tickets by various criteria
 * - Managing ticket lifecycle
 */
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
        System.out.println("🔵 CREATE TICKET: Creating ticket - " + ticketId);
        
        // Check if ticket ID already exists
        Optional<Ticket> existingTicket = ticketRepository.findByTicketId(ticketId);
        if (existingTicket.isPresent()) {
            System.out.println("⚠️ TICKET EXISTS: Ticket with ID '" + ticketId + "' already exists");
            throw new RuntimeException("Ticket with ID '" + ticketId + "' already exists");
        }
        
        Ticket ticket = new Ticket(ticketId, description, valid);
        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println("✅ TICKET CREATED: " + savedTicket);
        return savedTicket;
    }

    /**
     * Update ticket information
     */
    public Ticket updateTicket(Long id, String description, Boolean valid) {
        System.out.println("🔵 UPDATE TICKET: Updating ticket ID - " + id);
        
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            
            if (description != null) {
                ticket.setDescription(description);
            }
            if (valid != null) {
                ticket.setValid(valid);
            }
            
            Ticket updatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ TICKET UPDATED: " + updatedTicket);
            return updatedTicket;
        } else {
            System.out.println("❌ UPDATE FAILED: Ticket not found with ID - " + id);
            throw new RuntimeException("Ticket not found with ID: " + id);
        }
    }

    /**
     * Validate a ticket (mark as valid)
     */
    public Ticket validateTicket(String ticketId) {
        System.out.println("🔵 VALIDATE TICKET: Validating ticket - " + ticketId);
        
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setValid(true);
            Ticket validatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ TICKET VALIDATED: " + validatedTicket);
            return validatedTicket;
        } else {
            System.out.println("❌ VALIDATE FAILED: Ticket not found with ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    /**
     * Invalidate a ticket (mark as invalid)
     */
    public Ticket invalidateTicket(String ticketId) {
        System.out.println("🔵 INVALIDATE TICKET: Invalidating ticket - " + ticketId);
        
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setValid(false);
            Ticket invalidatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ TICKET INVALIDATED: " + invalidatedTicket);
            return invalidatedTicket;
        } else {
            System.out.println("❌ INVALIDATE FAILED: Ticket not found with ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    /**
     * Check if a ticket is valid
     */
    public boolean isTicketValid(String ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            return ticketOpt.get().isValid();
        }
        return false;
    }

    /**
     * QUERY OPERATIONS
     */

    /**
     * Get all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Get ticket by ID
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Get ticket by ticket ID
     */
    public Optional<Ticket> getTicketByTicketId(String ticketId) {
        return ticketRepository.findByTicketId(ticketId);
    }

    /**
     * Get all valid tickets
     */
    public List<Ticket> getValidTickets() {
        return ticketRepository.findAll().stream()
                .filter(Ticket::isValid)
                .collect(Collectors.toList());
    }

    /**
     * Get all invalid tickets
     */
    public List<Ticket> getInvalidTickets() {
        return ticketRepository.findAll().stream()
                .filter(ticket -> !ticket.isValid())
                .collect(Collectors.toList());
    }

    /**
     * Search tickets by description (contains)
     */
    public List<Ticket> searchTicketsByDescription(String description) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getDescription() != null && 
                       ticket.getDescription().toLowerCase().contains(description.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Check if ticket exists by ticket ID
     */
    public boolean ticketExists(String ticketId) {
        return ticketRepository.findByTicketId(ticketId).isPresent();
    }

    /**
     * Get ticket count
     */
    public long getTicketCount() {
        return ticketRepository.count();
    }

    /**
     * Get valid ticket count
     */
    public long getValidTicketCount() {
        return getValidTickets().size();
    }

    /**
     * Delete ticket
     */
    public boolean deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            System.out.println("✅ TICKET DELETED: ID - " + id);
            return true;
        }
        System.out.println("❌ DELETE FAILED: Ticket not found with ID - " + id);
        return false;
    }

    /**
     * Delete ticket by ticket ID
     */
    public boolean deleteTicketByTicketId(String ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            ticketRepository.delete(ticketOpt.get());
            System.out.println("✅ TICKET DELETED: Ticket ID - " + ticketId);
            return true;
        }
        System.out.println("❌ DELETE FAILED: Ticket not found with ticket ID - " + ticketId);
        return false;
    }
} 