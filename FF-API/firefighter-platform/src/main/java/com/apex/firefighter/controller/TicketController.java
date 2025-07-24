package com.apex.firefighter.controller;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Create a new ticket
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Map<String, Object> payload) {
        String ticketId = (String) payload.get("ticketId");
        String description = (String) payload.get("description");
        String userId = (String) payload.get("userId");
        String emergencyType = (String) payload.get("emergencyType");
        String emergencyContact = (String) payload.get("emergencyContact");
        Integer duration = payload.get("duration") != null ? ((Number) payload.get("duration")).intValue() : null;

        Ticket ticket = ticketService.createTicket(ticketId, description, userId, emergencyType, emergencyContact, duration);
        return ResponseEntity.ok(ticket);
    }

    // Get all tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // Get ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get ticket by ticket ID
    @GetMapping("/ticket-id/{ticketId}")
    public ResponseEntity<Ticket> getTicketByTicketId(@PathVariable String ticketId) {
        return ticketService.getTicketByTicketId(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update a ticket
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String description = (String) payload.get("description");
        String status = (String) payload.get("status");
        String emergencyType = (String) payload.get("emergencyType");
        String emergencyContact = (String) payload.get("emergencyContact");
        Integer duration = payload.get("duration") != null ? ((Number) payload.get("duration")).intValue() : null;
        try {
            Ticket updatedTicket = ticketService.updateTicket(id, description, status, emergencyType, emergencyContact, duration);
            return ResponseEntity.ok(updatedTicket);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete ticket by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        boolean deleted = ticketService.deleteTicket(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Delete ticket by ticket ID
    @DeleteMapping("/ticket-id/{ticketId}")
    public ResponseEntity<Void> deleteTicketByTicketId(@PathVariable String ticketId) {
        boolean deleted = ticketService.deleteTicketByTicketId(ticketId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // ==================== ADMIN ENDPOINTS ====================

    // Get all active tickets (Admin only)
    @GetMapping("/admin/active")
    public ResponseEntity<List<Ticket>> getActiveTickets() {
        try {
            List<Ticket> activeTickets = ticketService.getActiveTickets();
            return ResponseEntity.ok(activeTickets);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get ticket history sorted by creation date (Admin only)
    @GetMapping("/admin/history")
    public ResponseEntity<List<Ticket>> getTicketHistory() {
        try {
            List<Ticket> ticketHistory = ticketService.getTicketHistory();
            return ResponseEntity.ok(ticketHistory);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Get tickets by status (Admin only)
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable String status) {
        try {
            List<Ticket> tickets = ticketService.getTicketsByStatus(status);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Revoke ticket by database ID (Admin only)
    @PutMapping("/admin/revoke/{id}")
    public ResponseEntity<Map<String, Object>> revokeTicket(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        String adminUserId = payload.get("adminUserId");
        String rejectReason = payload.get("rejectReason");
        
        Map<String, Object> response = new HashMap<>();
        
        if (adminUserId == null || adminUserId.trim().isEmpty()) {
            response.put("error", "Admin user ID is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            response.put("error", "Reject reason is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Ticket revokedTicket = ticketService.revokeTicket(id, adminUserId, rejectReason);
            response.put("success", true);
            response.put("message", "Ticket revoked successfully");
            response.put("ticket", revokedTicket);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Revoke ticket by ticket ID (Admin only)
    @PutMapping("/admin/revoke/ticket-id/{ticketId}")
    public ResponseEntity<Map<String, Object>> revokeTicketByTicketId(
            @PathVariable String ticketId, 
            @RequestBody Map<String, String> payload) {
        
        String adminUserId = payload.get("adminUserId");
        String rejectReason = payload.get("rejectReason");
        
        Map<String, Object> response = new HashMap<>();
        
        if (adminUserId == null || adminUserId.trim().isEmpty()) {
            response.put("error", "Admin user ID is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            response.put("error", "Reject reason is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Ticket revokedTicket = ticketService.revokeTicketByTicketId(ticketId, adminUserId, rejectReason);
            response.put("success", true);
            response.put("message", "Ticket revoked successfully");
            response.put("ticket", revokedTicket);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Check if user is admin (Helper endpoint)
    @GetMapping("/admin/check/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkAdminStatus(@PathVariable String userId) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isAdmin = ticketService.isUserAdmin(userId);
        response.put("isAdmin", isAdmin);
        return ResponseEntity.ok(response);
    }
}