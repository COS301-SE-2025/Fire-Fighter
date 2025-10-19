package com.apex.firefighter.dto.registration;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO for pending approval information
 * Used when returning pending approval data to admins
 */
public class PendingApprovalDto {

    private Long id;
    private String firebaseUid;
    private String username;
    private String email;
    private String department;
    private String contactNumber;
    private String registrationMethod;
    private List<String> requestedAccessGroups;
    private String businessJustification;
    private String priorityLevel;
    private String status; // PENDING, APPROVED, REJECTED
    private ZonedDateTime createdAt;
    private String reviewedBy;
    private ZonedDateTime reviewedAt;
    private String dolibarrId;
    
    // System Access Request fields (additional information provided by user)
    private String systemAccessPriority;
    private String systemAccessDepartment;
    private String systemAccessPhoneNumber;
    private String systemAccessJustification;

    // Constructors
    public PendingApprovalDto() {}

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

    public String getSystemAccessPriority() {
        return systemAccessPriority;
    }

    public void setSystemAccessPriority(String systemAccessPriority) {
        this.systemAccessPriority = systemAccessPriority;
    }

    public String getSystemAccessDepartment() {
        return systemAccessDepartment;
    }

    public void setSystemAccessDepartment(String systemAccessDepartment) {
        this.systemAccessDepartment = systemAccessDepartment;
    }

    public String getSystemAccessPhoneNumber() {
        return systemAccessPhoneNumber;
    }

    public void setSystemAccessPhoneNumber(String systemAccessPhoneNumber) {
        this.systemAccessPhoneNumber = systemAccessPhoneNumber;
    }

    public String getSystemAccessJustification() {
        return systemAccessJustification;
    }

    public void setSystemAccessJustification(String systemAccessJustification) {
        this.systemAccessJustification = systemAccessJustification;
    }
}
