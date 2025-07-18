package com.apex.firefighter.service.ai;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketQueryService {

    @Autowired
    private TicketService ticketService;

    /**
     * Get user-specific ticket context for AI (focuses on user's own tickets only)
     */
    public String getUserTicketContext(String query, String userId) {
        StringBuilder context = new StringBuilder();

        try {
            // Get user's tickets (limit to 10 most recent for token efficiency)
            List<Ticket> userTickets = ticketService.getTicketsByUserId(userId)
                    .stream()
                    .sorted((t1, t2) -> t2.getDateCreated().compareTo(t1.getDateCreated())) // Most recent first
                    .limit(10)
                    .collect(Collectors.toList());

            if (userTickets.isEmpty()) {
                context.append("You currently have no tickets assigned to you.");
                return context.toString();
            }

            // Determine what specific context to provide based on query
            if (containsKeywords(query, "active", "open", "current")) {
                context.append(getUserActiveTicketsContext(userTickets));
            } else if (containsKeywords(query, "closed", "completed", "finished", "resolved")) {
                context.append(getUserClosedTicketsContext(userTickets));
            } else if (containsKeywords(query, "recent", "latest", "new")) {
                context.append(getUserRecentTicketsContext(userTickets));
            } else if (containsKeywords(query, "emergency", "fire", "medical", "rescue", "hazmat")) {
                context.append(getUserEmergencyTypeContext(userTickets, query));
            } else {
                // Default: provide overview of all user tickets
                context.append(getUserAllTicketsContext(userTickets));
            }

        } catch (Exception e) {
            System.err.println("Error building user ticket context: " + e.getMessage());
            context.append("Unable to retrieve your ticket information at this time.");
        }

        return context.toString();
    }

    /**
     * Get user's active tickets context
     */
    private String getUserActiveTicketsContext(List<Ticket> userTickets) {
        List<Ticket> activeTickets = userTickets.stream()
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (activeTickets.isEmpty()) {
            return "You have no active tickets at the moment.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Your active tickets (").append(activeTickets.size()).append(" total):\n");

        for (Ticket ticket : activeTickets) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" - ").append(truncateDescription(ticket.getDescription(), 60))
                   .append(" (Created: ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Get user's closed tickets context
     */
    private String getUserClosedTicketsContext(List<Ticket> userTickets) {
        List<Ticket> closedTickets = userTickets.stream()
                .filter(t -> "Completed".equalsIgnoreCase(t.getStatus()) ||
                           "Rejected".equalsIgnoreCase(t.getStatus()) ||
                           "Closed".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (closedTickets.isEmpty()) {
            return "You have no recently closed tickets.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Your recently closed tickets (").append(closedTickets.size()).append(" total):\n");

        for (Ticket ticket : closedTickets) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" - ").append(ticket.getStatus())
                   .append(" (").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Get user's recent tickets context
     */
    private String getUserRecentTicketsContext(List<Ticket> userTickets) {
        // userTickets is already sorted by most recent first
        List<Ticket> recentTickets = userTickets.stream().limit(5).collect(Collectors.toList());

        StringBuilder context = new StringBuilder();
        context.append("Your most recent tickets:\n");

        for (Ticket ticket : recentTickets) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" (").append(ticket.getStatus())
                   .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Get user's emergency type specific context
     */
    private String getUserEmergencyTypeContext(List<Ticket> userTickets, String query) {
        String emergencyType = extractEmergencyType(query);
        if (emergencyType == null) {
            return getUserAllTicketsContext(userTickets);
        }

        List<Ticket> typeTickets = userTickets.stream()
                .filter(t -> emergencyType.equalsIgnoreCase(t.getEmergencyType()))
                .collect(Collectors.toList());

        if (typeTickets.isEmpty()) {
            return "You have no " + emergencyType.toLowerCase() + " tickets.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Your ").append(emergencyType.toLowerCase()).append(" tickets (")
               .append(typeTickets.size()).append(" total):\n");

        for (Ticket ticket : typeTickets) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(truncateDescription(ticket.getDescription(), 60))
                   .append(" (").append(ticket.getStatus())
                   .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Get all user tickets context (overview)
     */
    private String getUserAllTicketsContext(List<Ticket> userTickets) {
        long activeCount = userTickets.stream().filter(t -> "Active".equalsIgnoreCase(t.getStatus())).count();
        long closedCount = userTickets.size() - activeCount;

        StringBuilder context = new StringBuilder();
        context.append("Your ticket overview:\n");
        context.append("- Total tickets: ").append(userTickets.size()).append("\n");
        context.append("- Active tickets: ").append(activeCount).append("\n");
        context.append("- Closed tickets: ").append(closedCount).append("\n\n");

        context.append("Recent tickets:\n");
        for (Ticket ticket : userTickets.stream().limit(5).collect(Collectors.toList())) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" (").append(ticket.getStatus())
                   .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    // Helper methods
    private boolean containsKeywords(String query, String... keywords) {
        String lowerQuery = query.toLowerCase();
        for (String keyword : keywords) {
            if (lowerQuery.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String extractEmergencyType(String query) {
        String lowerQuery = query.toLowerCase();
        if (lowerQuery.contains("fire")) return "FIRE";
        if (lowerQuery.contains("medical")) return "MEDICAL";
        if (lowerQuery.contains("rescue")) return "RESCUE";
        if (lowerQuery.contains("hazmat")) return "HAZMAT";
        return null;
    }

    private String truncateDescription(String description, int maxLength) {
        if (description == null) return "No description";
        if (description.length() <= maxLength) return description;
        return description.substring(0, maxLength) + "...";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
    }
}
