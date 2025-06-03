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

    @Column(name = "session_id")
    private Long sessionId; // Link to AccessSession

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, getters, setters

    // Default constructor
    public AccessLog() {
        user = null;
        action = null;
        ticketId = null;
        sessionId = null;
        //timestamp null or LocalDateTime.now()?
        timestamp = null;
    }

    // Parameterized constructor
    public AccessLog(User user, String action, String ticketId, LocalDateTime timestamp) {
        this.user = user;
        this.action = action;
        this.ticketId = ticketId;
        this.timestamp = timestamp;
        this.sessionId = null;
    }

    // Constructor with session ID
    public AccessLog(User user, String action, String ticketId, LocalDateTime timestamp, Long sessionId) {
        this.user = user;
        this.action = action;
        this.ticketId = ticketId;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
    }

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

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }
}