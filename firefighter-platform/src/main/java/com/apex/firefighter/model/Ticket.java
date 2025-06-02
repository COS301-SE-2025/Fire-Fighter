package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private String ticketId;

    @Column(nullable = false)
    private String description;

    @Column(name = "emergency_type")
    private String emergencyType; // e.g., "FIRE", "MEDICAL", "RESCUE"

    @Column(name = "priority_level")
    private String priorityLevel; // e.g., "HIGH", "MEDIUM", "LOW"

    @Column(nullable = false)
    private boolean valid = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @Column(name = "created_by")
    private String createdBy; // Firebase UID of creator

    @Column(name = "location")
    private String location;

    // Default constructor
    public Ticket() {
        this.createdAt = ZonedDateTime.now();
        this.valid = true;
    }

    // Constructor
    public Ticket(String ticketId, String description, String emergencyType) {
        this();
        this.ticketId = ticketId;
        this.description = description;
        this.emergencyType = emergencyType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Helper methods
    public void invalidate() {
        this.valid = false;
    }

    public boolean isExpired() {
        return expiresAt != null && ZonedDateTime.now().isAfter(expiresAt);
    }

    public void extendValidity(int hours) {
        if (this.expiresAt != null) {
            this.expiresAt = this.expiresAt.plusHours(hours);
        } else {
            this.expiresAt = ZonedDateTime.now().plusHours(hours);
        }
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", description='" + description + '\'' +
                ", emergencyType='" + emergencyType + '\'' +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", valid=" + valid +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", createdBy='" + createdBy + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
