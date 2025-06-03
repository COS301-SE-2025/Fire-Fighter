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

    // Constructors, getters, setters

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
}
