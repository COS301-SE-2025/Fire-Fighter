package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "access_sessions")
public class AccessSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false, updatable = false)
    private ZonedDateTime startTime;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "session_token")
    private String sessionToken;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private AccessRequest accessRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Default constructor
    public AccessSession() {
        this.startTime = ZonedDateTime.now();
        this.active = true;
    }

    // Constructor
    public AccessSession(AccessRequest accessRequest, User user) {
        this();
        this.accessRequest = accessRequest;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public AccessRequest getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(AccessRequest accessRequest) {
        this.accessRequest = accessRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods
    public void endSession() {
        this.active = false;
        this.endTime = ZonedDateTime.now();
    }

    public void extendSession(int hours) {
        if (this.endTime != null) {
            this.endTime = this.endTime.plusHours(hours);
        } else {
            this.endTime = ZonedDateTime.now().plusHours(hours);
        }
    }

    @Override
    public String toString() {
        return "AccessSession{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", active=" + active +
                ", sessionToken='" + sessionToken + '\'' +
                ", accessRequest=" + (accessRequest != null ? accessRequest.getId() : "null") +
                ", user=" + (user != null ? user.getUserId() : "null") +
                '}';
    }
}
