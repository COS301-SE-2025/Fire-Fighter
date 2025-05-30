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

    public enum RequestStatus {
        PENDING, APPROVED, DENIED, REVOKED
    }

    // Constructors, getters, setters
        public AccessRequest() {
        }

        public AccessRequest(Long id, String ticketId, RequestStatus status, LocalDateTime requestTime, User user) {
            this.id = id;
            this.ticketId = ticketId;
            this.status = status;
            this.requestTime = requestTime;
            this.user = user;
        }

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

        public RequestStatus getStatus() {
            return status;
        }

        public void setStatus(RequestStatus status) {
            this.status = status;
        }

        public LocalDateTime getRequestTime() {
            return requestTime;
        }

        public void setRequestTime(LocalDateTime requestTime) {
            this.requestTime = requestTime;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
}
