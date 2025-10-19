package com.apex.firefighter.model.registration;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Entity representing a system access request for new user registration
 * This is the primary table for managing all new user sign-up requests
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

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "request_priority", nullable = false, length = 50)
    private String requestPriority; // HIGH, MEDIUM, LOW

    @Column(name = "request_department", nullable = false, length = 100)
    private String requestDepartment;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(name = "registration_method", nullable = false, length = 50)
    private String registrationMethod; // google_sso, email, azure_ad

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "system_access_request_groups", schema = "firefighter",
                    joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "access_group")
    private List<String> requestedAccessGroups;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "reviewed_by", length = 128)
    private String reviewedBy; // Firebase UID of admin who reviewed

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Column(name = "dolibarr_id", length = 50)
    private String dolibarrId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // Constructors
    public SystemAccessRequest() {
        this.createdAt = ZonedDateTime.now();
        this.status = "PENDING";
    }

    public SystemAccessRequest(String firebaseUid, String username, String email,
                               String registrationMethod, String requestPriority, 
                               String requestDepartment, String phoneNumber, String justification) {
        this();
        this.firebaseUid = firebaseUid;
        this.username = username;
        this.email = email;
        this.registrationMethod = registrationMethod;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationMethod() {
        return registrationMethod;
    }

    public void setRegistrationMethod(String registrationMethod) {
        this.registrationMethod = registrationMethod;
    }

    public List<String> getRequestedAccessGroups() {
        return requestedAccessGroups;
    }

    public void setRequestedAccessGroups(List<String> requestedAccessGroups) {
        this.requestedAccessGroups = requestedAccessGroups;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public ZonedDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(ZonedDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getDolibarrId() {
        return dolibarrId;
    }

    public void setDolibarrId(String dolibarrId) {
        this.dolibarrId = dolibarrId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "SystemAccessRequest{" +
                "requestId=" + requestId +
                ", firebaseUid='" + firebaseUid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", requestPriority='" + requestPriority + '\'' +
                ", requestDepartment='" + requestDepartment + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", registrationMethod='" + registrationMethod + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", reviewedAt=" + reviewedAt +
                ", dolibarrId='" + dolibarrId + '\'' +
                '}';
    }
}
