package com.apex.firefighter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "tickets", schema = "firefighter")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("ticketId")
    private String ticketId;

    @JsonProperty("description")
    private String description;

    @Column(nullable = false)
    @JsonProperty("status")
    private String status;

    @Column(nullable = false, updatable = false)
    @JsonProperty("dateCreated")
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    @JsonProperty("requestDate")
    private LocalDate requestDate;

    @Column(nullable = false)
    @JsonProperty("userId")
    private String userId;

    @Column(nullable = false)
    @JsonProperty("emergencyType")
    private String emergencyType;

    @Column(nullable = false)
    @JsonProperty("emergencyContact")
    private String emergencyContact;

    @Column(name = "revoked_by")
    @JsonProperty("revokedBy")
    private String revokedBy;

    @Column(name = "reject_reason")
    @JsonProperty("rejectReason")
    private String rejectReason;

    @Column(name = "date_completed")
    @JsonProperty("dateCompleted")
    private LocalDateTime dateCompleted;

    @Column(name = "duration")
    @JsonProperty("duration")
    private Integer duration; // duration in minutes

    // Default constructor
    public Ticket() {
        this.dateCreated = LocalDateTime.now();
        this.requestDate = LocalDate.now();
        this.status = "Active";
        this.duration = null;
    }

    // Parameterized constructor
    public Ticket(String ticketId, String description, String userId, String emergencyType, String emergencyContact, Integer duration) {
        this.ticketId = ticketId;
        this.description = description;
        this.userId = userId;
        this.emergencyType = emergencyType;
        this.emergencyContact = emergencyContact;
        this.dateCreated = LocalDateTime.now();
        this.requestDate = LocalDate.now();
        this.status = "Active";
        this.duration = duration;
    }

    // Legacy constructor for backward compatibility
    public Ticket(String ticketId, String description, String userId, String emergencyType, String emergencyContact) {
        this(ticketId, description, userId, emergencyType, emergencyContact, null);
    }

    // setters
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public void setDateCompleted(LocalDateTime dateCompleted) { this.dateCompleted = dateCompleted; }
    public void setDuration(Integer duration) { this.duration = duration; }

    // getters
    public Long getId() { return id; }
    public String getTicketId() { return ticketId; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public LocalDate getRequestDate() { return requestDate; }
    public String getUserId() { return userId; }
    public String getEmergencyType() { return emergencyType; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getRevokedBy() { return revokedBy; }
    public String getRejectReason() { return rejectReason; }
    public LocalDateTime getDateCompleted() { return dateCompleted; }
    public Integer getDuration() { return duration; }
}