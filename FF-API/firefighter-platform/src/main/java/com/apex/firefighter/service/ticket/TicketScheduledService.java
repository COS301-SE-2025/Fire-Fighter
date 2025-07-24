package com.apex.firefighter.service.ticket;

import com.apex.firefighter.model.Ticket;
import com.apex.firefighter.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TicketScheduledService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketScheduledService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostConstruct
    public void runStartupCheck() {
        System.out.println("🚀 STARTUP CHECK: Running expired ticket check at application startup...");
        closeExpiredTickets();
    }

    @Scheduled(cron = "0 */2 * * * *")
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
                    
                    closedCount++;
                    System.out.println("Closed expired ticket: " + ticket.getTicketId());
                }
            }
            
            if (closedCount > 0) {
                System.out.println("Closed " + closedCount + " expired tickets");
            }
            
        } catch (Exception e) {
            System.err.println("Error closing expired tickets: " + e.getMessage());
        }
    }
} 