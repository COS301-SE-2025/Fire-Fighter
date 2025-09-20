package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.DolibarrUserGroupService;
import com.apex.firefighter.service.AnomalyDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    private final DolibarrUserGroupService dolibarrUserGroupService;
    private final UserRepository userRepository;
    private final AnomalyDetectionService anomalyDetectionService;

    @Autowired
    public TicketService(TicketRepository ticketRepository, NotificationService notificationService, 
                        DolibarrUserGroupService dolibarrUserGroupService, UserRepository userRepository,
                        AnomalyDetectionService anomalyDetectionService) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
        this.userRepository = userRepository;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    public Ticket createTicket(String description, String userId, String emergencyType, String emergencyContact, Integer duration) {
        String ticketId = generateTicketId();

        Ticket ticket = new Ticket(ticketId, description, "Active", userId, emergencyType, emergencyContact);

        // Set the duration (default to 60 minutes if null)
        ticket.setDuration(duration != null ? duration : 60);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Create notification with email support
        try {
            notificationService.createTicketCreationNotification(userId, ticketId, savedTicket);
            System.out.println("✅ TICKET SERVICE: Created notification for ticket creation: " + ticketId + " (Duration: " + ticket.getDuration() + " minutes)");
        } catch (Exception e) {
            System.err.println("⚠️ TICKET SERVICE: Failed to create notification for ticket: " + ticketId + " - " + e.getMessage());
        }

        //add user to firefighter group
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                // Use emergency type for group allocation if available, otherwise fall back to description
                String allocationText = (emergencyType != null && !emergencyType.isEmpty())
                    ? emergencyType + " " + description
                    : description;
                dolibarrUserGroupService.addUserToGroup(userOpt.get().getDolibarrId(), allocationText);
            } else {
                throw new RuntimeException("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            System.err.println("⚠️ TICKET SERVICE: Failed to add user to firefighter group for ticket: " + ticketId + " - " + e.getMessage());
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

            // Remove user from firefighter group if ticket is closed
            try {
                if ("Closed".equals(newStatus) || "Completed".equals(newStatus)) {
                    Optional<User> userOpt = userRepository.findById(ticket.getUserId());
                    if (userOpt.isPresent()) {
                        // Use emergency type for group allocation if available, otherwise fall back to description
                        String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                            ? ticket.getEmergencyType() + " " + ticket.getDescription()
                            : ticket.getDescription();
                        dolibarrUserGroupService.removeUserFromGroup(userOpt.get().getDolibarrId(), allocationText);
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

            // Remove user from firefighter group when ticket is automatically closed
            try {
                Optional<User> user = userRepository.findById(ticket.getUserId());
                if (user.isPresent()) {
                    // Use emergency type for group allocation if available, otherwise fall back to description
                    String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                        ? ticket.getEmergencyType() + " " + ticket.getDescription()
                        : ticket.getDescription();
                    dolibarrUserGroupService.removeUserFromGroup(user.get().getDolibarrId(), allocationText);
                    System.out.println("✅ AUTO-CLOSE (24h): Successfully removed user " + ticket.getUserId() + " from firefighter group for ticket: " + ticket.getTicketId());
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
}
