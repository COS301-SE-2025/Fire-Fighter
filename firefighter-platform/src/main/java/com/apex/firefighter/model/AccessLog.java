package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "GRANTED_ACCESS", "REVOKED_ACCESS", "LOGIN", "LOGOUT"

    @Column(nullable = false, updatable = false)
    private ZonedDateTime timestamp;

    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "details")
    private String details; // Additional details about the action

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Default constructor
    public AccessLog() {
        this.timestamp = ZonedDateTime.now();
    }

    // Constructor
    public AccessLog(String action, User user, String ticketId) {
        this();
        this.action = action;
        this.user = user;
        this.ticketId = ticketId;
    }

    // Constructor with details
    public AccessLog(String action, User user, String ticketId, String details) {
        this(action, user, ticketId);
        this.details = details;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AccessLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", ticketId='" + ticketId + '\'' +
                ", sessionId=" + sessionId +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", details='" + details + '\'' +
                ", user=" + (user != null ? user.getUserId() : "null") +
                '}';
    }
}
