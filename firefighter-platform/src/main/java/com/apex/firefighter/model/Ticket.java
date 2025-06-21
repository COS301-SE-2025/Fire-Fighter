package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "tickets", schema = "firefighter")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketId;

    private String description;

    private boolean valid;

    private String createdBy;

    private LocalDateTime lastVerifiedAt;

    private int verificationCount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDate requestDate;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String emergencyType;

    @Column(nullable = false)
    private String emergencyContact;

    // Default constructor. Initializes a new instance of the Ticket class with empty(null) values.
    public Ticket() {
        this.dateCreated = LocalDateTime.now();
        this.requestDate = LocalDate.now();
        this.status = "Active";
        this.valid = false;
        this.verificationCount = 0;
    }

    //paramterized constructor. Accepts values for every attribute that may need to be set via setters.
    public Ticket(String ticketId, String description, boolean valid, String userId, String emergencyType, String emergencyContact, String createdBy) {
        this.ticketId = ticketId;
        this.description = description;
        this.valid = valid;
        this.userId = userId;
        this.emergencyType = emergencyType;
        this.emergencyContact = emergencyContact;
        this.createdBy = createdBy;

        this.dateCreated = LocalDateTime.now();
        this.requestDate = LocalDate.now();
        this.status = "Active";
        this.verificationCount = 0;
    }

    // setters

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public void setVerificationCount(int verificationCount) {
        this.verificationCount = verificationCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public boolean isValid() {
        return valid;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public int getVerificationCount() {
        return verificationCount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }
}