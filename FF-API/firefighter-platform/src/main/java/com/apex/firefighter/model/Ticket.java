package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", schema = "firefighter")
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_id", unique = true, nullable = false)
    private String ticketId;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
    
    @Column(name = "request_date")
    private LocalDateTime requestDate;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "emergency_type")
    private String emergencyType;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "date_completed")
    private LocalDateTime dateCompleted;
    
    @Column(name = "reject_reason")
    private String rejectReason;

    // Constructors
    public Ticket() {}

    public Ticket(String ticketId, String description, String status, String userId, String emergencyType, String emergencyContact) {
        this.ticketId = ticketId;
        this.description = description;
        this.status = status;
        this.userId = userId;
        this.emergencyType = emergencyType;
        this.emergencyContact = emergencyContact;
        this.dateCreated = LocalDateTime.now();
        this.requestDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmergencyType() { return emergencyType; }
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getDateCompleted() { return dateCompleted; }
    public void setDateCompleted(LocalDateTime dateCompleted) { this.dateCompleted = dateCompleted; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
