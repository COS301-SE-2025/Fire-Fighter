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
            // Get ALL user's tickets (not limited) to check for any tickets at all
            List<Ticket> allUserTickets = ticketService.getTicketsByUserId(userId)
                    .stream()
                    .sorted((t1, t2) -> t2.getDateCreated().compareTo(t1.getDateCreated())) // Most recent first
                    .collect(Collectors.toList());

            // Debug logging
            System.out.println("🔍 DEBUG: User " + userId + " has " + allUserTickets.size() + " total tickets");

            // Get limited tickets for display (token efficiency)
            List<Ticket> userTickets = allUserTickets.stream().limit(10).collect(Collectors.toList());

            // Always try to provide helpful context, even if no tickets
            // Determine what specific context to provide based on query
            if (containsKeywords(query, "time", "remaining", "expire", "duration", "left", "how long")) {
                context.append(getUserTicketTimeContext(userTickets, query));
            } else if (containsKeywords(query, "latest", "recent", "new", "show me", "list", "activity", "show", "display")) {
                context.append(getUserRecentTicketsContext(userTickets, query));
            } else if (containsKeywords(query, "active", "open", "current")) {
                context.append(getUserActiveTicketsContext(userTickets));
            } else if (containsKeywords(query, "closed", "completed", "finished", "resolved")) {
                context.append(getUserClosedTicketsContext(userTickets));
            } else if (containsKeywords(query, "emergency", "security", "critical", "data", "network", "lockout")) {
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
     * Get user's active tickets context - shows past tickets if no active ones
     */
    private String getUserActiveTicketsContext(List<Ticket> userTickets) {
        List<Ticket> activeTickets = userTickets.stream()
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        StringBuilder context = new StringBuilder();

        if (activeTickets.isEmpty()) {
            // Check if user has any past tickets
            List<Ticket> pastTickets = userTickets.stream()
                    .filter(t -> !"Active".equalsIgnoreCase(t.getStatus()))
                    .limit(10)
                    .collect(Collectors.toList());

            if (pastTickets.isEmpty()) {
                context.append("You have no access tickets in the system.");
            } else {
                context.append("You have no active access tickets. Here are your past ").append(Math.min(10, pastTickets.size())).append(" tickets:\n");
                for (Ticket ticket : pastTickets) {
                    context.append("- ").append(ticket.getTicketId())
                           .append(": ").append(ticket.getEmergencyType())
                           .append(" - ").append(truncateDescription(ticket.getDescription(), 50))
                           .append(" (").append(ticket.getStatus())
                           .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
                }
            }
        } else {
            context.append("Your active access tickets (").append(activeTickets.size()).append(" total):\n");
            context.append("These tickets are currently granting you elevated access to systems:\n");

            for (Ticket ticket : activeTickets) {
                context.append("- ").append(ticket.getTicketId())
                       .append(": ").append(ticket.getEmergencyType())
                       .append(" - ").append(truncateDescription(ticket.getDescription(), 60))
                       .append(" (Access granted: ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
            }
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
            return "You have no recently closed access tickets. No recent elevated access has been revoked.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Your recently closed access tickets (").append(closedTickets.size()).append(" total):\n");
        context.append("These tickets had elevated access that has been revoked or expired:\n");

        for (Ticket ticket : closedTickets) {
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" - ").append(ticket.getStatus())
                   .append(" (Access ended: ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Get user's recent tickets context - intelligently shows active or past tickets
     */
    private String getUserRecentTicketsContext(List<Ticket> userTickets, String query) {
        // userTickets is already sorted by most recent first
        List<Ticket> activeTickets = userTickets.stream()
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .limit(10)
                .collect(Collectors.toList());

        List<Ticket> allRecentTickets = userTickets.stream().limit(10).collect(Collectors.toList());

        StringBuilder context = new StringBuilder();

        if (allRecentTickets.isEmpty()) {
            context.append("You have no access tickets in the system.");
            return context.toString();
        }

        // If user has active tickets, show them first
        if (!activeTickets.isEmpty()) {
            if (containsKeywords(query, "activity")) {
                context.append("Your recent access activity (").append(allRecentTickets.size()).append(" latest tickets):\n");
            } else {
                context.append("Your latest access tickets (").append(allRecentTickets.size()).append(" most recent):\n");
            }

            for (Ticket ticket : allRecentTickets) {
                context.append("- ").append(ticket.getTicketId())
                       .append(": ").append(ticket.getEmergencyType())
                       .append(" - ").append(truncateDescription(ticket.getDescription(), 50))
                       .append(" (").append(ticket.getStatus())
                       .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
            }
        }
        // If no active tickets, show past tickets
        else {
            if (containsKeywords(query, "activity")) {
                context.append("You have no active tickets. Here is your past access activity (").append(allRecentTickets.size()).append(" latest):\n");
            } else {
                context.append("You have no active tickets. Here are your past ").append(allRecentTickets.size()).append(" tickets:\n");
            }

            for (Ticket ticket : allRecentTickets) {
                context.append("- ").append(ticket.getTicketId())
                       .append(": ").append(ticket.getEmergencyType())
                       .append(" - ").append(truncateDescription(ticket.getDescription(), 50))
                       .append(" (").append(ticket.getStatus())
                       .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
            }
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
     * Get all user tickets context (overview) - intelligently shows active or closed tickets
     */
    private String getUserAllTicketsContext(List<Ticket> userTickets) {
        List<Ticket> activeTickets = userTickets.stream()
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        List<Ticket> closedTickets = userTickets.stream()
                .filter(t -> !"Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        StringBuilder context = new StringBuilder();

        // If user has active tickets, show them
        if (!activeTickets.isEmpty()) {
            context.append("Your active access tickets (").append(activeTickets.size()).append(" total):\n");
            for (Ticket ticket : activeTickets.stream().limit(10).collect(Collectors.toList())) {
                context.append("- ").append(ticket.getTicketId())
                       .append(": ").append(ticket.getEmergencyType())
                       .append(" - ").append(truncateDescription(ticket.getDescription(), 50))
                       .append(" (").append(ticket.getStatus())
                       .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
            }

            // Also mention closed tickets if they exist
            if (!closedTickets.isEmpty()) {
                context.append("\nYou also have ").append(closedTickets.size()).append(" closed tickets.");
            }
        }
        // If no active tickets, show closed tickets
        else if (!closedTickets.isEmpty()) {
            context.append("You have no active tickets. Here are your past ").append(Math.min(10, closedTickets.size())).append(" tickets:\n");
            for (Ticket ticket : closedTickets.stream().limit(10).collect(Collectors.toList())) {
                context.append("- ").append(ticket.getTicketId())
                       .append(": ").append(ticket.getEmergencyType())
                       .append(" - ").append(truncateDescription(ticket.getDescription(), 50))
                       .append(" (").append(ticket.getStatus())
                       .append(", ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
            }
        }
        // If no tickets at all
        else {
            context.append("You have no access tickets in the system.");
        }

        return context.toString();
    }

    /**
     * Get user's ticket time/duration context
     */
    private String getUserTicketTimeContext(List<Ticket> userTickets, String query) {
        List<Ticket> activeTickets = userTickets.stream()
                .filter(t -> "Active".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        StringBuilder context = new StringBuilder();

        if (activeTickets.isEmpty()) {
            context.append("You have no active tickets with time remaining.");

            // Show recent expired tickets if available
            List<Ticket> recentClosed = userTickets.stream()
                    .filter(t -> !"Active".equalsIgnoreCase(t.getStatus()))
                    .limit(3)
                    .collect(Collectors.toList());

            if (!recentClosed.isEmpty()) {
                context.append(" Your recent expired tickets:\n");
                for (Ticket ticket : recentClosed) {
                    context.append("- ").append(ticket.getTicketId())
                           .append(": ").append(ticket.getEmergencyType())
                           .append(" (").append(ticket.getStatus())
                           .append(", ended ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
                }
            }

            return context.toString();
        }

        context.append("Time remaining on your active tickets:\n");

        for (Ticket ticket : activeTickets) {
            String timeRemaining = calculateTimeRemaining(ticket);
            context.append("- ").append(ticket.getTicketId())
                   .append(": ").append(ticket.getEmergencyType())
                   .append(" - ").append(timeRemaining)
                   .append(" (Duration: ").append(ticket.getDuration() != null ? ticket.getDuration() + " minutes" : "Not specified")
                   .append(", Started: ").append(formatDateTime(ticket.getDateCreated())).append(")\n");
        }

        return context.toString();
    }

    /**
     * Calculate time remaining on a ticket
     */
    private String calculateTimeRemaining(Ticket ticket) {
        if (ticket.getDuration() == null) {
            return "Duration not specified";
        }

        LocalDateTime startTime = ticket.getDateCreated();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(ticket.getDuration());

        if (now.isAfter(endTime)) {
            long minutesOverdue = java.time.Duration.between(endTime, now).toMinutes();
            return "EXPIRED " + minutesOverdue + " minutes ago";
        } else {
            long minutesRemaining = java.time.Duration.between(now, endTime).toMinutes();
            long hoursRemaining = minutesRemaining / 60;
            long remainingMinutes = minutesRemaining % 60;

            if (hoursRemaining > 0) {
                return hoursRemaining + "h " + remainingMinutes + "m remaining";
            } else {
                return remainingMinutes + " minutes remaining";
            }
        }
    }

    /**
     * Get emergency request creation context (only when specifically asked)
     */
    public String getRequestCreationContext(String query) {
        if (!containsKeywords(query, "create", "new", "request", "emergency", "how", "submit", "form")) {
            return ""; // Return empty if not request-creation related
        }

        StringBuilder context = new StringBuilder();
        context.append("🚨 Creating Emergency Access Requests:\n\n");

        context.append("📋 Step 1: Access the Request Form\n");
        context.append("• Navigate to 'My Requests' page\n");
        context.append("• Click 'Create New Request' button\n");
        context.append("• The request modal will appear\n\n");

        context.append("📝 Step 2: Fill Out Request Details\n");
        context.append("• Request Date: Defaults to current date (can modify for future requests)\n");
        context.append("• Emergency Type (Select one):\n");
        context.append("  - Critical System Failure: Production systems down\n");
        context.append("  - Security Incident: Security breach or investigation\n");
        context.append("  - Network Outage: Network connectivity issues\n");
        context.append("  - User Lockout: Urgent user access issues\n");
        context.append("  - Data Recovery: Database or data integrity issues\n");
        context.append("  - Other Emergency: Other urgent system issues\n\n");

        context.append("• Reason for Access (Required):\n");
        context.append("  - Provide detailed justification\n");
        context.append("  - Include specific systems affected\n");
        context.append("  - Reference any related incident tickets\n");
        context.append("  - Example: 'Critical production system failure - Plant offline. Manufacturing systems down affecting Line 3 and Line 7.'\n\n");

        context.append("• Emergency Contact: Your direct phone number (must be reachable)\n");
        context.append("• Access Duration:\n");
        context.append("  - Minimum: 15 minutes\n");
        context.append("  - Maximum: 2 hours (120 minutes)\n");
        context.append("  - Default: 1 hour (60 minutes)\n");
        context.append("  - Use +/- buttons to adjust in 15-minute increments\n\n");

        context.append("✅ Step 3: Submit Request\n");
        context.append("• Review all information for accuracy\n");
        context.append("• Click 'Submit Emergency Request'\n");
        context.append("• You'll receive confirmation of submission\n");
        context.append("• Request status will appear in your requests list\n\n");

        context.append("🔍 Request Validation:\n");
        context.append("The system automatically validates:\n");
        context.append("• User authorization level\n");
        context.append("• Request reason completeness\n");
        context.append("• Duration within limits\n");
        context.append("• Emergency contact format\n");

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
        if (lowerQuery.contains("critical") || lowerQuery.contains("system failure") || lowerQuery.contains("failure")) return "Critical System Failure";
        if (lowerQuery.contains("security") || lowerQuery.contains("breach") || lowerQuery.contains("incident")) return "Security Incident";
        if (lowerQuery.contains("data") || lowerQuery.contains("recovery") || lowerQuery.contains("backup")) return "Data Recovery";
        if (lowerQuery.contains("network") || lowerQuery.contains("outage") || lowerQuery.contains("connectivity")) return "Network Outage";
        if (lowerQuery.contains("user") || lowerQuery.contains("lockout") || lowerQuery.contains("locked")) return "User Lockout";
        if (lowerQuery.contains("other") || lowerQuery.contains("emergency")) return "Other Emergency";
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
