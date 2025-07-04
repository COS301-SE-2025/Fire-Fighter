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
@RequestMapping("/api/endpoints/tickets")
public class SecuredTicketController {

    private final TicketService ticketService;

    @Autowired
    public SecuredTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

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

    
} 