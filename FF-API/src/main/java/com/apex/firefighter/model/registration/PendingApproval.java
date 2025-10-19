package com.apex.firefighter.model.registration;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Entity representing a pending user approval request
 */
@Entity
@Table(name = "pending_user_approvals", schema = "firefighter")
public class PendingApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "department")
    private String department;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "registration_method", nullable = false)
    private String registrationMethod; // google_sso, email, azure_ad

    @ElementCollection
    @CollectionTable(name = "pending_approval_access_groups", schema = "firefighter",
                    joinColumns = @JoinColumn(name = "approval_id"))
    @Column(name = "access_group")
    private List<String> requestedAccessGroups;

    @Column(name = "business_justification", length = 1000)
    private String businessJustification;

    @Column(name = "priority_level")
    private String priorityLevel; // HIGH, MEDIUM, LOW

    @Column(name = "status", nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Column(name = "dolibarr_id")
    private String dolibarrId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // Constructors
    public PendingApproval() {
        this.createdAt = ZonedDateTime.now();
        this.status = "PENDING";
    }

    public PendingApproval(String firebaseUid, String username, String email, 
                          String registrationMethod) {
        this();
        this.firebaseUid = firebaseUid;
        this.username = username;
        this.email = email;
        this.registrationMethod = registrationMethod;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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

    public String getBusinessJustification() {
        return businessJustification;
    }

    public void setBusinessJustification(String businessJustification) {
        this.businessJustification = businessJustification;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
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
        return "PendingApproval{" +
                "id=" + id +
                ", firebaseUid='" + firebaseUid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
