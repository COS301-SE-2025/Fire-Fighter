package com.apex.firefighter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketId;

    private String description;

    private boolean valid;

    // Default constructor. Initializes a new instance of the Ticket class with empty(null) values.
    public Ticket() {
        ticketId = null;
        description = null;
        valid = false;
    }

    //paramterized constructor. Accepts values for every attribute that may need to be set via setters.
    public Ticket(String ticketId, String description, boolean valid) {
        this.ticketId = ticketId;
        this.description = description;
        this.valid = valid;
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
}
