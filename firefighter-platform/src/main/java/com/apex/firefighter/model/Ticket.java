package com.apex.firefighter.model;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private TicketStatus status; //Ticket statuses represented in the TicketStatus enum
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who created the ticket
    private LocalDateTime createdAt; // Timestamp when the ticket was created

    

    // Default constructor. Initializes a new instance of the Ticket class with empty(null) values.
    public Ticket() {
        description = null;
        valid = false;
    }

    //paramterized constructor. Accepts values for every attribute that may need to be set via setters.
    public Ticket(String ticketId, String description, boolean valid) {
        this.description = description;
        this.valid = valid;
    }

    // setters

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        try {
            this.status = TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ticket status: " + status);
        }
    }

    // getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public boolean isValid() {
        return valid;
    }
}