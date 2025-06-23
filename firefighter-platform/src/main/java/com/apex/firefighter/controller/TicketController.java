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

        Ticket ticket = ticketService.createTicket(ticketId, description, userId, emergencyType, emergencyContact);
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
        try {
            Ticket updatedTicket = ticketService.updateTicket(id, description, status, emergencyType, emergencyContact);
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
}