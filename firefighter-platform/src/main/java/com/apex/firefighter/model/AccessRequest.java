package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_requests")
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, DENIED, REVOKED

    private LocalDateTime requestTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors, getters, setters

    public enum RequestStatus {
        PENDING, APPROVED, DENIED, REVOKED
    }
}
