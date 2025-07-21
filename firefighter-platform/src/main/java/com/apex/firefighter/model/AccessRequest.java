package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "access_requests", schema = "firefighter")
public class AccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "request_time", nullable = false, updatable = false)
    private ZonedDateTime requestTime;

    @Column(name = "approved_time")
    private ZonedDateTime approvedTime;

    @Column(name = "approved_by")
    private String approvedBy; // Firebase UID of approver

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Default constructor
    public AccessRequest() {
        this.requestTime = ZonedDateTime.now();
        this.status = RequestStatus.PENDING;
    }

    // Constructor
    public AccessRequest(String ticketId, User user) {
        this();
        this.ticketId = ticketId;
        this.user = user;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public ZonedDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(ZonedDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public ZonedDateTime getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(ZonedDateTime approvedTime) {
        this.approvedTime = approvedTime;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods
    public void approve(String approvedByUserId) {
        this.status = RequestStatus.APPROVED;
        this.approvedTime = ZonedDateTime.now();
        this.approvedBy = approvedByUserId;
    }

    public void deny(String deniedByUserId) {
        this.status = RequestStatus.DENIED;
        this.approvedTime = ZonedDateTime.now();
        this.approvedBy = deniedByUserId;
    }

    public void revoke(String revokedByUserId) {
        this.status = RequestStatus.REVOKED;
        this.approvedTime = ZonedDateTime.now();
        this.approvedBy = revokedByUserId;
    }

    @Override
    public String toString() {
        return "AccessRequest{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", status=" + status +
                ", requestTime=" + requestTime +
                ", approvedTime=" + approvedTime +
                ", approvedBy='" + approvedBy + '\'' +
                ", user=" + (user != null ? user.getUserId() : "null") +
                '}';
    }

    public enum RequestStatus {
        PENDING, APPROVED, DENIED, REVOKED
    }
}