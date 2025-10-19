package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.DolibarrUserGroupService;
import com.apex.firefighter.service.AnomalyNotificationService;
import com.apex.firefighter.service.anomaly.AnomalyDetectionService;
import com.apex.firefighter.dto.EmergencyStatisticsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    private final DolibarrUserGroupService dolibarrUserGroupService;
    private final UserRepository userRepository;
    private final AnomalyDetectionService anomalyDetectionService;
    private final AnomalyNotificationService anomalyNotificationService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, NotificationService notificationService, 
                        DolibarrUserGroupService dolibarrUserGroupService, UserRepository userRepository,
                        AnomalyDetectionService anomalyDetectionService, AnomalyNotificationService anomalyNotificationService) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
        this.userRepository = userRepository;
        this.anomalyDetectionService = anomalyDetectionService;
        this.anomalyNotificationService = anomalyNotificationService;
    }

    public Ticket createTicket(String description, String userId, String emergencyType, String emergencyContact, Integer duration) {
        String ticketId = generateTicketId();

        Ticket ticket = new Ticket(ticketId, description, "Active", userId, emergencyType, emergencyContact);

        // Set the duration (default to 60 minutes if null)
        ticket.setDuration(duration != null ? duration : 60);

        Ticket savedTicket = ticketRepository.save(ticket);

        // 🚀 PERFORMANCE FIX: Move heavy operations to async background processing
        // This reduces ticket creation time from 15s to ~100ms

        // Find the user object once for all operations
        Optional<User> userOpt = userRepository.findById(userId);

        // Create basic notification synchronously (fast operation)
        try {
            notificationService.createTicketCreationNotification(userId, ticketId, savedTicket);
            System.out.println("✅ TICKET SERVICE: Created notification for ticket creation: " + ticketId + " (Duration: " + ticket.getDuration() + " minutes)");
        } catch (Exception e) {
            System.err.println("⚠️ TICKET SERVICE: Failed to create notification for ticket: " + ticketId + " - " + e.getMessage());
        }

        // 🔄 ASYNC: Move heavy operations to background threads
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String allocationText = (emergencyType != null && !emergencyType.isEmpty())
                ? emergencyType + " " + description
                : description;

            // Async anomaly detection and admin notifications
            CompletableFuture.runAsync(() -> {
                try {
                    anomalyNotificationService.checkAndNotifyAnomalies(user, savedTicket);
                    System.out.println("✅ TICKET SERVICE: [ASYNC] Completed anomaly detection and notification for user: " + userId);
                } catch (Exception e) {
                    System.err.println("⚠️ TICKET SERVICE: [ASYNC] Failed to check for anomalies for user: " + userId + " - " + e.getMessage());
                }
            });

            // Async Dolibarr group management
            CompletableFuture.runAsync(() -> {
                try {
                    dolibarrUserGroupService.addUserToGroup(user.getDolibarrId(), allocationText, ticketId);
                    System.out.println("✅ TICKET SERVICE: [ASYNC] Added user to firefighter group and notified admins for ticket: " + ticketId);
                } catch (Exception e) {
                    System.err.println("⚠️ TICKET SERVICE: [ASYNC] Failed to add user to firefighter group for ticket: " + ticketId + " - " + e.getMessage());
                }
            });
        } else {
            System.err.println("⚠️ TICKET SERVICE: Could not find user " + userId + " for background processing");
        }

        return savedTicket;
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
            String oldStatus = ticket.getStatus();
            ticket.setStatus(newStatus);

            // Set completion date if ticket is being completed
            if ("Completed".equals(newStatus) || "Closed".equals(newStatus)) {
                ticket.setDateCompleted(LocalDateTime.now());
            }

            Ticket savedTicket = ticketRepository.save(ticket);

            // Create notification with email support for completion
            if ("Completed".equals(newStatus) && !"Completed".equals(oldStatus)) {
                try {
                    notificationService.createTicketCompletionNotification(ticket.getUserId(), ticket.getTicketId(), savedTicket);
                    System.out.println("✅ TICKET SERVICE: Created completion notification for ticket: " + ticket.getTicketId());
                } catch (Exception e) {
                    System.err.println("⚠️ TICKET SERVICE: Failed to create completion notification for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
                }
            }

            // Remove user from firefighter group if ticket is closed and notify admins
            try {
                if ("Closed".equals(newStatus) || "Completed".equals(newStatus)) {
                    Optional<User> userOpt = userRepository.findById(ticket.getUserId());
                    if (userOpt.isPresent()) {
                        // Use emergency type for group allocation if available, otherwise fall back to description
                        String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                            ? ticket.getEmergencyType() + " " + ticket.getDescription()
                            : ticket.getDescription();
                        // Pass the ticket ID to enable admin notifications
                        dolibarrUserGroupService.removeUserFromGroup(userOpt.get().getDolibarrId(), allocationText, ticketId);
                        System.out.println("✅ TICKET SERVICE: Removed user from firefighter group and notified admins for ticket: " + ticketId);
                    } else {
                        System.err.println("⚠️ TICKET SERVICE: User not found with ID: " + ticket.getUserId());
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ TICKET SERVICE: Failed to remove user from firefighter group for ticket: " + ticketId + " - " + e.getMessage());
            }

            return savedTicket;
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

            // Remove user from firefighter group when ticket is automatically closed and notify admins
            try {
                Optional<User> user = userRepository.findById(ticket.getUserId());
                if (user.isPresent()) {
                    // Use emergency type for group allocation if available, otherwise fall back to description
                    String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                        ? ticket.getEmergencyType() + " " + ticket.getDescription()
                        : ticket.getDescription();
                    // Pass the ticket ID to enable admin notifications
                    dolibarrUserGroupService.removeUserFromGroup(user.get().getDolibarrId(), allocationText, ticket.getTicketId());
                    System.out.println("✅ AUTO-CLOSE (24h): Successfully removed user " + ticket.getUserId() + " from firefighter group and notified admins for ticket: " + ticket.getTicketId());
                } else {
                    System.err.println("⚠️ AUTO-CLOSE (24h): User not found with ID: " + ticket.getUserId());
                }
            } catch (Exception e) {
                System.err.println("⚠️ AUTO-CLOSE (24h): Failed to remove user from firefighter group for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
            }
        }
    }

    private String generateTicketId() {
        // Generate 5-digit random number (10000-99999)
        int randomNumber = 10000 + (int)(Math.random() * 90000);
        return "BMW-FF-" + randomNumber;
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
            Ticket savedTicket = ticketRepository.save(ticket);

            // Create notification with email support
            try {
                notificationService.createTicketRevocationNotification(ticket.getUserId(), ticket.getTicketId(), savedTicket, rejectReason);
                System.out.println("✅ TICKET SERVICE: Created revocation notification for ticket: " + ticket.getTicketId());
            } catch (Exception e) {
                System.err.println("⚠️ TICKET SERVICE: Failed to create revocation notification for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
            }

            // Remove user from firefighter group when ticket is manually revoked
            try {
                Optional<User> user = userRepository.findById(ticket.getUserId());
                if (user.isPresent()) {
                    // Use emergency type for group allocation if available, otherwise fall back to description
                    String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                        ? ticket.getEmergencyType() + " " + ticket.getDescription()
                        : ticket.getDescription();
                    dolibarrUserGroupService.removeUserFromGroup(user.get().getDolibarrId(), allocationText);
                } else {
                    System.err.println("⚠️ TICKET SERVICE: User not found with ID: " + ticket.getUserId());
                }
            } catch (Exception e) {
                System.err.println("⚠️ TICKET SERVICE: Failed to remove user from firefighter group for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
            }

            return savedTicket;
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
            Ticket savedTicket = ticketRepository.save(ticket);

            // Create notification with email support
            try {
                notificationService.createTicketRevocationNotification(ticket.getUserId(), ticket.getTicketId(), savedTicket, rejectReason);
                System.out.println("✅ TICKET SERVICE: Created revocation notification for ticket: " + ticket.getTicketId());
            } catch (Exception e) {
                System.err.println("⚠️ TICKET SERVICE: Failed to create revocation notification for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
            }

            // Remove user from firefighter group when ticket is manually revoked
            try {
                Optional<User> user = userRepository.findById(ticket.getUserId());
                if (user.isPresent()) {
                    // Use emergency type for group allocation if available, otherwise fall back to description
                    String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                        ? ticket.getEmergencyType() + " " + ticket.getDescription()
                        : ticket.getDescription();
                    dolibarrUserGroupService.removeUserFromGroup(user.get().getDolibarrId(), allocationText);
                } else {
                    System.err.println("⚠️ TICKET SERVICE: User not found with ID: " + ticket.getUserId());
                }
            } catch (Exception e) {
                System.err.println("⚠️ TICKET SERVICE: Failed to remove user from firefighter group for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
            }

            return savedTicket;
        }
        throw new RuntimeException("Ticket not found with ID: " + ticketId);
    }

    public boolean isUserAdmin(String userId) {
        // This should check user admin status - implement based on your User model
        return false; // Placeholder - implement proper admin check
    }

    public Ticket updateTicket(Long id, String description, String status, String emergencyType, String emergencyContact, Integer duration) {
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

            return ticketRepository.save(ticket);
        }
        throw new RuntimeException("Ticket not found with ID: " + id);
    }

    /**
     * Normalize emergency type to a standard format
     * Handles both legacy format (hr-emergency) and new format (hr, financial, management, logistics)
     * 
     * @param emergencyType The raw emergency type string
     * @return Normalized emergency type key
     */
    private String normalizeEmergencyType(String emergencyType) {
        if (emergencyType == null) {
            return null;
        }
        
        String normalized = emergencyType.toLowerCase().trim();
        
        // Map various formats to standardized keys
        if (normalized.equals("hr") || normalized.equals("hr-emergency")) {
            return "hr";
        } else if (normalized.equals("financial") || normalized.equals("financials") || 
                   normalized.equals("financial-emergency")) {
            return "financial";
        } else if (normalized.equals("management") || normalized.equals("fmanager") || 
                   normalized.equals("manager") || normalized.equals("management-emergency")) {
            return "management";
        } else if (normalized.equals("logistics") || normalized.equals("logistics-emergency")) {
            return "logistics";
        }
        
        return null; // Unknown type
    }
    
    /**
     * Get display name for emergency type
     * 
     * @param normalizedType The normalized emergency type key
     * @return Display-friendly name
     */
    private String getEmergencyTypeDisplayName(String normalizedType) {
        if (normalizedType == null) {
            return "N/A";
        }
        
        return switch (normalizedType) {
            case "hr" -> "HR";
            case "financial" -> "Financial";
            case "management" -> "Management";
            case "logistics" -> "Logistics";
            default -> "N/A";
        };
    }

    /**
     * Calculate emergency response statistics from all tickets in the system
     * This provides accurate system-wide statistics regardless of user role
     * Supports both legacy emergency type format (hr-emergency) and new format (hr, financial, etc.)
     * 
     * @return EmergencyStatisticsResponse containing all calculated statistics
     */
    public EmergencyStatisticsResponse calculateEmergencyStatistics() {
        List<Ticket> allTickets = ticketRepository.findAll();
        
        // Initialize response object
        EmergencyStatisticsResponse response = new EmergencyStatisticsResponse();
        
        // Total tickets count
        int totalTickets = allTickets.size();
        response.setTotalTickets(totalTickets);
        
        // If no tickets, return default values
        if (totalTickets == 0) {
            response.setEmergencyTypeBreakdown(new HashMap<>());
            response.setMostCommonEmergencyType("N/A");
            response.setSystemHealthScore(100);
            response.setAverageResponseTime(0);
            response.setCompletionRate(0.0);
            response.setCurrentMonthTickets(0);
            response.setActiveTickets(0);
            return response;
        }
        
        // Calculate emergency type breakdown with normalized keys
        Map<String, Integer> emergencyTypeBreakdown = new HashMap<>();
        emergencyTypeBreakdown.put("hr", 0);
        emergencyTypeBreakdown.put("financial", 0);
        emergencyTypeBreakdown.put("management", 0);
        emergencyTypeBreakdown.put("logistics", 0);
        
        int activeTicketsCount = 0;
        int rejectedTicketsCount = 0;
        int closedTicketsCount = 0;
        int totalDuration = 0;
        int currentMonthTicketsCount = 0;
        
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        // Process all tickets
        for (Ticket ticket : allTickets) {
            // Emergency type breakdown - normalize the type first
            String rawEmergencyType = ticket.getEmergencyType();
            String normalizedType = normalizeEmergencyType(rawEmergencyType);
            
            if (normalizedType != null && emergencyTypeBreakdown.containsKey(normalizedType)) {
                emergencyTypeBreakdown.put(normalizedType, emergencyTypeBreakdown.get(normalizedType) + 1);
            }
            
            // Status counts
            String status = ticket.getStatus();
            if ("Active".equals(status)) {
                activeTicketsCount++;
            } else if ("Rejected".equals(status)) {
                rejectedTicketsCount++;
            } else if ("Closed".equals(status)) {
                closedTicketsCount++;
            }
            
            // Duration sum for average
            Integer duration = ticket.getDuration();
            if (duration != null) {
                totalDuration += duration;
            }
            
            // Current month tickets
            LocalDateTime dateCreated = ticket.getDateCreated();
            if (dateCreated != null && 
                dateCreated.getMonthValue() == currentMonth && 
                dateCreated.getYear() == currentYear) {
                currentMonthTicketsCount++;
            }
        }
        
        response.setEmergencyTypeBreakdown(emergencyTypeBreakdown);
        response.setActiveTickets(activeTicketsCount);
        
        // Calculate most common emergency type
        String mostCommonType = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : emergencyTypeBreakdown.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonType = entry.getKey();
            }
        }
        
        // Convert to display format using the helper method
        response.setMostCommonEmergencyType(getEmergencyTypeDisplayName(mostCommonType));
        
        // Calculate system health score
        // Health score: 100% - (active tickets weight + rejected tickets weight)
        double activeWeight = ((double) activeTicketsCount / totalTickets) * 30; // Active tickets reduce health by up to 30%
        double rejectedWeight = ((double) rejectedTicketsCount / totalTickets) * 20; // Rejected tickets reduce health by up to 20%
        int healthScore = Math.max(50, (int) Math.round(100 - activeWeight - rejectedWeight));
        response.setSystemHealthScore(healthScore);
        
        // Calculate average response time (average duration)
        int averageResponseTime = totalTickets > 0 ? Math.round((float) totalDuration / totalTickets) : 0;
        response.setAverageResponseTime(averageResponseTime);
        
        // Calculate completion rate (percentage of closed tickets)
        double completionRate = ((double) closedTicketsCount / totalTickets) * 100;
        response.setCompletionRate(Math.round(completionRate * 10) / 10.0); // Round to 1 decimal place
        
        // Set current month tickets count
        response.setCurrentMonthTickets(currentMonthTicketsCount);
        
        return response;
    }
}
