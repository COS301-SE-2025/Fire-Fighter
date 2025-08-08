package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String description, String userId, String emergencyType, String emergencyContact) {
        String ticketId = generateTicketId();
        
        Ticket ticket = new Ticket(ticketId, description, "Active", userId, emergencyType, emergencyContact);
        
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> getTicketByTicketId(String ticketId) {
        return ticketRepository.findByTicketId(ticketId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByUserId(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    public Ticket updateTicketStatus(String ticketId, String newStatus) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(newStatus);
            return ticketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with ID: " + ticketId);
    }

    public void deleteTicket(String ticketId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            ticketRepository.delete(ticketOpt.get());
        } else {
            throw new RuntimeException("Ticket not found with ID: " + ticketId);
        }
    }

    public List<Ticket> getActiveTicketsOlderThan(LocalDateTime cutoffDate) {
        return ticketRepository.findActiveTicketsOlderThan(cutoffDate);
    }

    public void closeExpiredTickets() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        List<Ticket> expiredTickets = getActiveTicketsOlderThan(cutoffDate);
        
        for (Ticket ticket : expiredTickets) {
            ticket.setStatus("Closed");
            ticketRepository.save(ticket);
        }
    }

    private String generateTicketId() {
        return "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<Ticket> getActiveTickets() {
        return ticketRepository.findByStatus("Active");
    }

    public List<Ticket> getTicketHistory() {
        return ticketRepository.findAll();
    }

    public Ticket revokeTicket(Long id, String adminUserId, String rejectReason) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus("Rejected");
            ticket.setRejectReason(rejectReason);
            ticket.setDateCompleted(LocalDateTime.now());
            return ticketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with ID: " + id);
    }

    public Ticket revokeTicketByTicketId(String ticketId, String adminUserId, String rejectReason) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketId(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus("Rejected");
            ticket.setRejectReason(rejectReason);
            ticket.setDateCompleted(LocalDateTime.now());
            return ticketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with ID: " + ticketId);
    }

    public boolean isUserAdmin(String userId) {
        // This should check user admin status - implement based on your User model
        return false; // Placeholder - implement proper admin check
    }

    public Ticket updateTicket(Long id, String description, String status, String emergencyType, String emergencyContact) {
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
            
            return ticketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with ID: " + id);
    }
}
