package com.apex.firefighter.model.registration;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * Entity representing a system access request
 * Stores additional information provided by users during registration
 */
@Entity
@Table(name = "system_access_requests", schema = "firefighter")
public class SystemAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    private String firebaseUid;

    @Column(name = "request_priority", nullable = false, length = 50)
    private String requestPriority; // HIGH, MEDIUM, LOW

    @Column(name = "request_department", nullable = false, length = 100)
    private String requestDepartment;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    // Constructors
    public SystemAccessRequest() {
        this.createdAt = ZonedDateTime.now();
    }

    public SystemAccessRequest(String firebaseUid, String requestPriority, 
                               String requestDepartment, String phoneNumber, String justification) {
        this();
        this.firebaseUid = firebaseUid;
        this.requestPriority = requestPriority;
        this.requestDepartment = requestDepartment;
        this.phoneNumber = phoneNumber;
        this.justification = justification;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(String requestPriority) {
        this.requestPriority = requestPriority;
    }

    public String getRequestDepartment() {
        return requestDepartment;
    }

    public void setRequestDepartment(String requestDepartment) {
        this.requestDepartment = requestDepartment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SystemAccessRequest{" +
                "requestId=" + requestId +
                ", firebaseUid='" + firebaseUid + '\'' +
                ", requestPriority='" + requestPriority + '\'' +
                ", requestDepartment='" + requestDepartment + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", justification='" + justification + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
