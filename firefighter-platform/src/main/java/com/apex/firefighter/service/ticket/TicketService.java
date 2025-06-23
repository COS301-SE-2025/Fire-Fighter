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
    public Ticket createTicket(String ticketId, String description, String userId, String emergencyType, String emergencyContact) {
        System.out.println("🔵 CREATE TICKET: Creating ticket - " + ticketId);
        
        // Check if ticket ID already exists
        Optional<Ticket> existingTicket = ticketRepository.findByTicketId(ticketId);
        if (existingTicket.isPresent()) {
            System.out.println("⚠️ TICKET EXISTS: Ticket with ID '" + ticketId + "' already exists");
            throw new RuntimeException("Ticket with ID '" + ticketId + "' already exists");
        }
        
        Ticket ticket = new Ticket(ticketId, description, userId, emergencyType, emergencyContact);
        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println("✅ TICKET CREATED: " + savedTicket);
        return savedTicket;
    }

    /**
     * Update ticket information
     */
    public Ticket updateTicket(Long id, String description, String status, String emergencyType, String emergencyContact) {
        System.out.println("🔵 UPDATE TICKET: Updating ticket ID - " + id);
        
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            
            if (description != null) {
                ticket.setDescription(description);
            }
            if (status != null) {
                ticket.setStatus(status);
            }
            if (emergencyType != null) {
                ticket.setEmergencyType(emergencyType);
            }
            if (emergencyContact != null) {
                ticket.setEmergencyContact(emergencyContact);
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

    /**
     * Verify a ticket (increment verification count and update last verified time)
     */
    public boolean verifyTicket(String ticketId) {
        System.out.println("🔵 VERIFY TICKET: Verifying ticket - " + ticketId);
        
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setVerificationCount(ticket.getVerificationCount() + 1);
            ticket.setLastVerifiedAt(java.time.LocalDateTime.now());
            ticketRepository.save(ticket);
            System.out.println("✅ TICKET VERIFIED: " + ticket);
            return ticket.isValid();
        } else {
            System.out.println("❌ VERIFY FAILED: Ticket not found with ID - " + ticketId);
            return false;
        }
    }

    /**
     * Update ticket validity
     */
    public Ticket updateTicketValidity(String ticketId, boolean valid) {
        System.out.println("🔵 UPDATE VALIDITY: Updating ticket validity - " + ticketId + " to " + valid);
        
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setValid(valid);
            Ticket updatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ VALIDITY UPDATED: " + updatedTicket);
            return updatedTicket;
        } else {
            System.out.println("❌ UPDATE FAILED: Ticket not found with ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    /**
     * Update ticket description
     */
    public Ticket updateTicketDescription(String ticketId, String description) {
        System.out.println("🔵 UPDATE DESCRIPTION: Updating ticket description - " + ticketId);
        
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setDescription(description);
            Ticket updatedTicket = ticketRepository.save(ticket);
            System.out.println("✅ DESCRIPTION UPDATED: " + updatedTicket);
            return updatedTicket;
        } else {
            System.out.println("❌ UPDATE FAILED: Ticket not found with ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }
} 