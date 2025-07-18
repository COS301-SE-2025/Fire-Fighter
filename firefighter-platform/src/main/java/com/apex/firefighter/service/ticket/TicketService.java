package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final UserRepository userRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new ticket
     */
    public Ticket createTicket(String ticketId, String description, String userId, String emergencyType, String emergencyContact, Integer duration) {
        System.out.println("🔵 CREATE TICKET: Creating ticket - " + ticketId);
        
        // Check if ticket ID already exists
        Optional<Ticket> existingTicket = ticketRepository.findByTicketId(ticketId);
        if (existingTicket.isPresent()) {
            System.out.println("⚠️ TICKET EXISTS: Ticket with ID '" + ticketId + "' already exists");
            throw new RuntimeException("Ticket with ID '" + ticketId + "' already exists");
        }
        
        Ticket ticket = new Ticket(ticketId, description, userId, emergencyType, emergencyContact, duration);
        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println("✅ TICKET CREATED: " + savedTicket);
        return savedTicket;
    }

    /**
     * Update ticket information
     */
    public Ticket updateTicket(Long id, String description, String status, String emergencyType, String emergencyContact, Integer duration) {
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
            if (duration != null) {
                ticket.setDuration(duration);
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

    /**
     * ADMIN OPERATIONS
     */

    /**
     * Get all active tickets (Admin function)
     * Returns tickets with status = "Active" sorted by creation date descending
     */
    public List<Ticket> getActiveTickets() {
        System.out.println("🔵 ADMIN: Getting all active tickets");
        List<Ticket> activeTickets = ticketRepository.findActiveTickets();
        System.out.println("✅ ADMIN: Found " + activeTickets.size() + " active tickets");
        return activeTickets;
    }

    /**
     * Get ticket history (Admin function)
     * Returns all tickets sorted by creation date descending
     */
    public List<Ticket> getTicketHistory() {
        System.out.println("🔵 ADMIN: Getting ticket history");
        List<Ticket> ticketHistory = ticketRepository.findAllByOrderByDateCreatedDesc();
        System.out.println("✅ ADMIN: Found " + ticketHistory.size() + " tickets in history");
        return ticketHistory;
    }

    /**
     * Revoke/Reject a ticket (Admin function)
     * Only users with admin flag can revoke tickets
     */
    public Ticket revokeTicket(Long ticketId, String adminUserId, String rejectReason) {
        System.out.println("🔵 ADMIN REVOKE: Attempting to revoke ticket ID - " + ticketId + " by admin - " + adminUserId);
        
        // Verify admin privileges
        Optional<User> adminUser = userRepository.findById(adminUserId);
        if (adminUser.isEmpty()) {
            System.out.println("❌ REVOKE FAILED: Admin user not found - " + adminUserId);
            throw new RuntimeException("Admin user not found: " + adminUserId);
        }
        
        if (!adminUser.get().isAdmin()) {
            System.out.println("❌ REVOKE FAILED: User does not have admin privileges - " + adminUserId);
            throw new RuntimeException("User does not have admin privileges: " + adminUserId);
        }
        
        // Find and update the ticket
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            System.out.println("❌ REVOKE FAILED: Ticket not found with ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
        
        Ticket ticket = ticketOpt.get();
        
        // Check if ticket is already completed or rejected
        if ("Completed".equalsIgnoreCase(ticket.getStatus()) || "Rejected".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("⚠️ REVOKE WARNING: Ticket is already " + ticket.getStatus().toLowerCase() + " - " + ticketId);
            throw new RuntimeException("Ticket is already " + ticket.getStatus().toLowerCase() + ": " + ticketId);
        }
        
        // Update ticket status, rejection reason, and completion date
        ticket.setStatus("Rejected");
        ticket.setRejectReason(rejectReason);
        ticket.setDateCompleted(LocalDateTime.now());
        
        Ticket revokedTicket = ticketRepository.save(ticket);
        System.out.println("✅ TICKET REVOKED: " + revokedTicket.getTicketId() + " by admin " + adminUserId);
        return revokedTicket;
    }

    /**
     * Revoke ticket by ticket ID (Admin function)
     */
    public Ticket revokeTicketByTicketId(String ticketId, String adminUserId, String rejectReason) {
        System.out.println("🔵 ADMIN REVOKE: Attempting to revoke ticket - " + ticketId + " by admin - " + adminUserId);
        
        // Verify admin privileges
        Optional<User> adminUser = userRepository.findById(adminUserId);
        if (adminUser.isEmpty()) {
            System.out.println("❌ REVOKE FAILED: Admin user not found - " + adminUserId);
            throw new RuntimeException("Admin user not found: " + adminUserId);
        }
        
        if (!adminUser.get().isAdmin()) {
            System.out.println("❌ REVOKE FAILED: User does not have admin privileges - " + adminUserId);
            throw new RuntimeException("User does not have admin privileges: " + adminUserId);
        }
        
        // Find and update the ticket
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isEmpty()) {
            System.out.println("❌ REVOKE FAILED: Ticket not found with ticket ID - " + ticketId);
            throw new RuntimeException("Ticket not found with ticket ID: " + ticketId);
        }
        
        Ticket ticket = ticketOpt.get();
        
        // Check if ticket is already completed or rejected
        if ("Completed".equalsIgnoreCase(ticket.getStatus()) || "Rejected".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("⚠️ REVOKE WARNING: Ticket is already " + ticket.getStatus().toLowerCase() + " - " + ticketId);
            throw new RuntimeException("Ticket is already " + ticket.getStatus().toLowerCase() + ": " + ticketId);
        }
        
        // Update ticket status, rejection reason, and completion date
        ticket.setStatus("Rejected");
        ticket.setRejectReason(rejectReason);
        ticket.setDateCompleted(LocalDateTime.now());
        
        Ticket revokedTicket = ticketRepository.save(ticket);
        System.out.println("✅ TICKET REVOKED: " + revokedTicket.getTicketId() + " by admin " + adminUserId);
        return revokedTicket;
    }

    /**
     * Check if user is admin
     */
    public boolean isUserAdmin(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent() && user.get().isAdmin();
    }

    /**
     * Get tickets by status
     */
    public List<Ticket> getTicketsByStatus(String status) {
        System.out.println("🔵 QUERY: Getting tickets with status - " + status);
        List<Ticket> tickets = ticketRepository.findByStatus(status);
        System.out.println("✅ QUERY: Found " + tickets.size() + " tickets with status " + status);
        return tickets;
    }

    /**
     * Get tickets by user ID
     */
    public List<Ticket> getTicketsByUserId(String userId) {
        System.out.println("🔵 QUERY: Getting tickets for user - " + userId);
        List<Ticket> userTickets = ticketRepository.findByUserId(userId);
        System.out.println("✅ QUERY: Found " + userTickets.size() + " tickets for user " + userId);
        return userTickets;
    }

    /**
     * Get tickets within a date range (Admin function)
     * Returns tickets created between startDate and endDate (inclusive)
     */
    public List<Ticket> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("🔵 ADMIN: Getting tickets between " + startDate + " and " + endDate);
        List<Ticket> tickets = ticketRepository.findByDateCreatedBetween(startDate, endDate);
        System.out.println("✅ ADMIN: Found " + tickets.size() + " tickets in date range");
        return tickets;
    }
} 