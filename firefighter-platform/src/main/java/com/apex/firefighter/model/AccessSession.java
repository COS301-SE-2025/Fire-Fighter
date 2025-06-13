package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "access_sessions")
public class AccessSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_token", unique = true, nullable = false)
    private String sessionToken;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private AccessRequest accessRequest;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, getters, setters
    // Default constructor
    public AccessSession() {
        this.user = null;
        this.accessRequest = null;
        this.startTime = null;
        this.endTime = null;
        this.active = false;
        this.sessionToken = generateSessionToken();
    }

    // Parameterized constructor
    public AccessSession(User user, AccessRequest accessRequest, LocalDateTime startTime, LocalDateTime endTime, boolean active) {
        this.user = user;
        this.accessRequest = accessRequest;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.sessionToken = generateSessionToken();
    }

    // Helper method to generate unique session token
    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccessRequest(AccessRequest accessRequest) {
        this.accessRequest = accessRequest;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public boolean isActive() {
        return active;
    }

    public Long getId() {
        return id;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    //get duration of the session
    public long getDurationInMinutes() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }

    public AccessRequest getAccessRequest() {
        return accessRequest;
    }

    public User getUser() {
        return user;
    }

}