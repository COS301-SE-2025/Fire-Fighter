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

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_valid", nullable = false)
    private boolean valid;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy; // Firebase UID of creator

    @Column(name = "last_verified_at")
    private ZonedDateTime lastVerifiedAt;

    @Column(name = "verification_count")
    private Integer verificationCount = 0;

    // Default constructor
    public Ticket() {
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
        this.valid = false;
        this.verificationCount = 0;
    }

    // Parameterized constructor
    public Ticket(String ticketId, String description, boolean valid) {
        this();
        this.ticketId = ticketId;
        this.description = description;
        this.valid = valid;
    }

    // Getters and Setters
    public Long getId() {
        return id;
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
        this.updatedAt = ZonedDateTime.now();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
        this.updatedAt = ZonedDateTime.now();
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(ZonedDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public Integer getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(Integer verificationCount) {
        this.verificationCount = verificationCount;
    }

    // Helper methods
    public void incrementVerificationCount() {
        this.verificationCount++;
        this.lastVerifiedAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", description='" + description + '\'' +
                ", valid=" + valid +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", lastVerifiedAt=" + lastVerifiedAt +
                ", verificationCount=" + verificationCount +
                '}';
    }
}