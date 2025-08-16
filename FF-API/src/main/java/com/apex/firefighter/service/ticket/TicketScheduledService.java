package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import com.apex.firefighter.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;

@Service
// @EnableScheduling // Comment this out temporarily
public class TicketScheduledService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;

    @Autowired
    public TicketScheduledService(TicketRepository ticketRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
    }

    // @PostConstruct // Disable startup check
    public void runStartupCheck() {
        System.out.println("🚀 STARTUP CHECK: Running expired ticket check at application startup...");
        sendFiveMinuteWarnings();
        closeExpiredTickets();
    }

    // @Scheduled(cron = "0 */2 * * * *") // Disable scheduled execution
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

                LocalDateTime expirationTime = ticket.getDateCreated().plusMinutes(ticket.getDuration());
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
            throw e; // Re-throw to trigger transaction rollback
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
                LocalDateTime expirationTime = ticket.getDateCreated().plusMinutes(ticket.getDuration());
                
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

                    closedCount++;
                    System.out.println("Closed expired ticket: " + ticket.getTicketId());
                }
            }
            
            if (closedCount > 0) {
                System.out.println("Closed " + closedCount + " expired tickets");
            }
            
        } catch (Exception e) {
            System.err.println("Error closing expired tickets: " + e.getMessage());
            throw e; // Re-throw to trigger transaction rollback
        }
    }
} 
