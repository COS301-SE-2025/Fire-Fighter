package com.apex.firefighter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTickets() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> tickets = new ArrayList<>();
        
        // Sample ticket data
        Map<String, Object> ticket1 = new HashMap<>();
        ticket1.put("id", 1);
        ticket1.put("title", "Sample Ticket");
        ticket1.put("status", "OPEN");
        ticket1.put("priority", "HIGH");
        tickets.add(ticket1);
        
        response.put("tickets", tickets);
        response.put("total", tickets.size());
        return ResponseEntity.ok(response);
    }
}
