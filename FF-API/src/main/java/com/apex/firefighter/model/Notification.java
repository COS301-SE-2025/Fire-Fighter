package com.apex.firefighter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification entity representing user notifications in the system.
 * Stores notifications for ticket creation, completion, revocation, and other events.
 */
@Entity
@Table(name = "notifications", schema = "firefighter")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private String userId; // Firebase UID - foreign key to users table

    @Column(nullable = false)
    @JsonProperty("type")
    private String type; // ticket_created, request_completed, request_approved, action_taken, new_request, ticket_revoked

    @Column(nullable = false)
    @JsonProperty("title")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @JsonProperty("message")
    private String message;

    @Column(nullable = false, updatable = false)
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @JsonProperty("read")
    private Boolean read = false;

    @Column(name = "ticket_id")
    @JsonProperty("ticketId")
    private String ticketId; // Optional reference to related ticket

    // Constructors
    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public Notification(String userId, String type, String title, String message) {
        this();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public Notification(String userId, String type, String title, String message, String ticketId) {
        this(userId, type, title, message);
        this.ticketId = ticketId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    // Utility methods
    public boolean isRead() {
        return read != null && read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", read=" + read +
                ", ticketId='" + ticketId + '\'' +
                '}';
    }
}
