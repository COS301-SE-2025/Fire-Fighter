package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.model.User;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.repository.UserRepository;
import com.apex.firefighter.service.NotificationService;
import com.apex.firefighter.service.DolibarrUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketScheduledService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    private final DolibarrUserGroupService dolibarrUserGroupService;
    private final UserRepository userRepository;

    @Autowired
    public TicketScheduledService(TicketRepository ticketRepository, NotificationService notificationService, DolibarrUserGroupService dolibarrUserGroupService, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
        this.dolibarrUserGroupService = dolibarrUserGroupService;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void runStartupCheck() {
        System.out.println("🚀 STARTUP CHECK: Running expired ticket check at application startup...");
        sendFiveMinuteWarnings();
        closeExpiredTickets();
    }

    @Scheduled(cron = "0 */2 * * * *") // Run every 2 minutes
    @Transactional
    public void scheduledTicketCheck() {
        try {
            sendFiveMinuteWarnings();
            closeExpiredTickets();
        } catch (Exception e) {
            System.err.println("❌ Scheduled ticket check failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendFiveMinuteWarnings() {
        System.out.println("Checking for tickets needing 5-minute warnings at " + LocalDateTime.now());

        try {
            List<Ticket> activeTicketsWithDuration = ticketRepository.findActiveTicketsWithDuration();

            int warningsSent = 0;
            LocalDateTime currentTime = LocalDateTime.now();

            for (Ticket ticket : activeTicketsWithDuration) {
                // Skip if warning already sent
                if (ticket.getFiveMinuteWarningSent() != null && ticket.getFiveMinuteWarningSent()) {
                    continue;
                }

                // Use default duration of 60 minutes if duration is null
                int durationMinutes = ticket.getDuration() != null ? ticket.getDuration() : 60;
                LocalDateTime expirationTime = ticket.getDateCreated().plusMinutes(durationMinutes);
                LocalDateTime warningTime = expirationTime.minusMinutes(5);

                // Check if current time is at or past the warning time but before expiration
                if (currentTime.isAfter(warningTime) && currentTime.isBefore(expirationTime)) {
                    // Send 5-minute warning notification (with email support)
                    try {
                        notificationService.createFiveMinuteWarningNotification(
                            ticket.getUserId(),
                            ticket.getTicketId(),
                            ticket
                        );

                        // Mark warning as sent
                        ticket.setFiveMinuteWarningSent(true);
                        ticketRepository.save(ticket);

                        warningsSent++;
                        System.out.println("🔔 5-MINUTE WARNING SENT: Notification sent to user " + ticket.getUserId() + " for ticket " + ticket.getTicketId());
                    } catch (Exception e) {
                        System.err.println("⚠️ WARNING NOTIFICATION FAILED: Could not send 5-minute warning for ticket " + ticket.getTicketId() + ": " + e.getMessage());
                    }
                }
            }

            if (warningsSent > 0) {
                System.out.println("Sent " + warningsSent + " five-minute warning notifications");
            }

        } catch (Exception e) {
            System.err.println("Error sending five-minute warnings: " + e.getMessage());
            // Log error but don't re-throw to allow graceful handling
        }
    }

    @Transactional
    public void closeExpiredTickets() {
        System.out.println("Checking for expired tickets at " + LocalDateTime.now());
        
        try {
            List<Ticket> activeTicketsWithDuration = ticketRepository.findActiveTicketsWithDuration();
            
            int closedCount = 0;
            LocalDateTime currentTime = LocalDateTime.now();
            
            for (Ticket ticket : activeTicketsWithDuration) {
                // Use default duration of 60 minutes if duration is null
                int durationMinutes = ticket.getDuration() != null ? ticket.getDuration() : 60;
                LocalDateTime expirationTime = ticket.getDateCreated().plusMinutes(durationMinutes);
                
                if (currentTime.isAfter(expirationTime)) {
                    ticket.setStatus("Closed");
                    ticket.setDateCompleted(currentTime);
                    ticketRepository.save(ticket);

                    // Create notification for ticket completion (with email support)
                    try {
                        notificationService.createTicketCompletionNotification(
                            ticket.getUserId(),
                            ticket.getTicketId(),
                            ticket
                        );
                        System.out.println("🔔 NOTIFICATION CREATED: Ticket completion notification sent to user " + ticket.getUserId());
                    } catch (Exception e) {
                        System.err.println("⚠️ NOTIFICATION FAILED: Could not create ticket completion notification: " + e.getMessage());
                    }

                    // Remove user from firefighter group when ticket is automatically closed
                    try {
                        Optional<User> user = userRepository.findById(ticket.getUserId());
                        if (user.isPresent()) {
                            // Use emergency type for group allocation if available, otherwise fall back to description
                            String allocationText = (ticket.getEmergencyType() != null && !ticket.getEmergencyType().isEmpty())
                                ? ticket.getEmergencyType() + " " + ticket.getDescription()
                                : ticket.getDescription();
                            dolibarrUserGroupService.removeUserFromGroup(user.get().getDolibarrId(), allocationText);
                            System.out.println("✅ AUTO-CLOSE: Successfully removed user " + ticket.getUserId() + " from firefighter group for ticket: " + ticket.getTicketId());
                        } else {
                            System.err.println("⚠️ AUTO-CLOSE: User not found with ID: " + ticket.getUserId());
                        }
                    } catch (Exception e) {
                        System.err.println("⚠️ AUTO-CLOSE: Failed to remove user from firefighter group for ticket: " + ticket.getTicketId() + " - " + e.getMessage());
                    }

                    closedCount++;
                    System.out.println("Closed expired ticket: " + ticket.getTicketId());
                }
            }
            
            if (closedCount > 0) {
                System.out.println("Closed " + closedCount + " expired tickets");
            }
            
        } catch (Exception e) {
            System.err.println("Error closing expired tickets: " + e.getMessage());
            // Log error but don't re-throw to allow graceful handling
        }
    }
} 
