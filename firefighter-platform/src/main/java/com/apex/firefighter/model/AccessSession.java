package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_sessions")
public class AccessSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public boolean isActive() {
        return active;
    }


}
