package com.apex.firefighter.dto.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for system access request information
 * Used when users submit additional access request details
 */
public class SystemAccessRequestDto {

    @NotBlank(message = "Firebase UID is required")
    private String firebaseUid;

    @NotBlank(message = "Priority level is required")
    private String requestPriority; // HIGH, MEDIUM, LOW

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String requestDepartment;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Justification is required")
    @Size(min = 50, max = 1000, message = "Justification must be between 50 and 1000 characters")
    private String justification;

    // Constructors
    public SystemAccessRequestDto() {}

    public SystemAccessRequestDto(String firebaseUid, String requestPriority, 
                                  String requestDepartment, String phoneNumber, String justification) {
        this.firebaseUid = firebaseUid;
        this.requestPriority = requestPriority;
        this.requestDepartment = requestDepartment;
        this.phoneNumber = phoneNumber;
        this.justification = justification;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "SystemAccessRequestDto{" +
                "firebaseUid='" + firebaseUid + '\'' +
                ", requestPriority='" + requestPriority + '\'' +
                ", requestDepartment='" + requestDepartment + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", justificationLength=" + (justification != null ? justification.length() : 0) +
                '}';
    }
}
