package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // e.g., "Granted Access", "Revoked Access"

    private LocalDateTime timestamp;

    private String ticketId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, getters, setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
