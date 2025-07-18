package com.apex.firefighter.controller;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import com.apex.firefighter.service.GmailEmailService;
import com.apex.firefighter.service.UserService;
import com.apex.firefighter.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Autowired
    private GmailEmailService gmailEmailService;
    @Autowired
    private UserService userService;

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

    // Export tickets as CSV and email to the given address (Admin only)
    // Supports optional date range filtering with startDate and endDate parameters
    @PostMapping("/admin/export")
    public ResponseEntity<?> exportTicketsAndEmail(@RequestBody Map<String, Object> payload) {
        System.out.println("=== EXPORT ENDPOINT DEBUG ===");
        System.out.println("Payload received: " + payload);

        String userId = (String) payload.get("userId");
        String email = (String) payload.get("email");
        String startDateStr = (String) payload.get("startDate");
        String endDateStr = (String) payload.get("endDate");

        System.out.println("UserId: " + userId);
        System.out.println("Email: " + email);
        System.out.println("Start Date: " + startDateStr);
        System.out.println("End Date: " + endDateStr);

        if (userId == null && email == null) {
            return ResponseEntity.badRequest().body("Either userId or email is required.");
        }

        // Parse optional date range parameters
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        try {
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                startDate = LocalDateTime.parse(startDateStr);
                System.out.println("Parsed start date: " + startDate);
            }
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                endDate = LocalDateTime.parse(endDateStr);
                System.out.println("Parsed end date: " + endDate);
            }

            // Validate date range if both dates are provided
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().body("Start date cannot be after end date.");
            }

        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss");
        }

        User user;
        if (userId != null) {
            System.out.println("Looking up user by userId: " + userId);
            user = userService.getUserWithRoles(userId).orElse(null);
        } else {
            System.out.println("Looking up user by email: " + email);
            user = userService.getUserByEmail(email).orElse(null);
        }

        System.out.println("User found: " + (user != null ? "YES" : "NO"));
        if (user != null) {
            System.out.println("User email: " + user.getEmail());
            System.out.println("User is admin: " + user.isAdmin());
        }

        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized: Only admins can export tickets.");
        }

        String targetEmail = user.getEmail();
        System.out.println("Target email for sending: " + targetEmail);

        // Get tickets based on date range filtering
        List<Ticket> tickets;
        if (startDate != null && endDate != null) {
            System.out.println("Filtering tickets by date range: " + startDate + " to " + endDate);
            tickets = ticketService.getTicketsByDateRange(startDate, endDate);
        } else if (startDate != null) {
            System.out.println("Filtering tickets from start date: " + startDate);
            // If only start date is provided, get tickets from start date to now
            tickets = ticketService.getTicketsByDateRange(startDate, LocalDateTime.now());
        } else if (endDate != null) {
            System.out.println("Filtering tickets up to end date: " + endDate);
            // If only end date is provided, get tickets from beginning of time to end date
            tickets = ticketService.getTicketsByDateRange(LocalDateTime.of(2000, 1, 1, 0, 0), endDate);
        } else {
            System.out.println("No date filtering - getting all tickets");
            tickets = ticketService.getAllTickets();
        }

        System.out.println("Number of tickets retrieved: " + tickets.size());

        String csv = gmailEmailService.exportTicketsToCsv(tickets);
        System.out.println("CSV generated, length: " + csv.length());

        try {
            gmailEmailService.sendTicketsCsv(targetEmail, csv, user);
            String dateRangeInfo = (startDate != null || endDate != null) ?
                " (filtered by date range)" : "";
            return ResponseEntity.ok("Tickets exported and emailed successfully to " + targetEmail + dateRangeInfo);
        } catch (Exception e) {
            System.err.println("Controller caught exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send email: " + e.getMessage());
        }
    }
}
