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
@CrossOrigin(origins = "*") // For testing purposes only - configure properly in production
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
        Boolean valid = (Boolean) payload.get("valid");
        String createdBy = (String) payload.get("createdBy");

        Ticket ticket = ticketService.createTicket(ticketId, description, valid != null ? valid : false);
        if (createdBy != null) {
            ticket.setCreatedBy(createdBy);
        }
        
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

    // Update ticket validity
    @PatchMapping("/{ticketId}/validity")
    public ResponseEntity<Ticket> updateTicketValidity(
            @PathVariable String ticketId,
            @RequestBody Map<String, Boolean> payload) {
        try {
            Boolean valid = payload.get("valid");
            if (valid == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(ticketService.updateTicketValidity(ticketId, valid));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update ticket description
    @PatchMapping("/{ticketId}/description")
    public ResponseEntity<Ticket> updateTicketDescription(
            @PathVariable String ticketId,
            @RequestBody Map<String, String> payload) {
        try {
            String description = payload.get("description");
            if (description == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(ticketService.updateTicketDescription(ticketId, description));
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

    // Verify ticket
    @GetMapping("/{ticketId}/verify")
    public ResponseEntity<Map<String, Object>> verifyTicket(@PathVariable String ticketId) {
        boolean isValid = ticketService.verifyTicket(ticketId);
        return ticketService.getTicketByTicketId(ticketId)
                .map(ticket -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", isValid);
                    response.put("ticketId", ticket.getTicketId());
                    response.put("lastVerifiedAt", ticket.getLastVerifiedAt());
                    response.put("verificationCount", ticket.getVerificationCount());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}