package com.apex.firefighter.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for user registration requests
 * Contains all information needed when a new user requests access to the system
 */
public class RegistrationRequestDto {

    @NotBlank(message = "Firebase UID is required")
    private String firebaseUid;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String department;

    private String contactNumber;

    @NotBlank(message = "Registration method is required")
    private String registrationMethod; // google_sso, email, azure_ad

    private List<String> requestedAccessGroups;

    @Size(max = 1000, message = "Business justification must not exceed 1000 characters")
    private String businessJustification;

    private String priorityLevel; // HIGH, MEDIUM, LOW

    private String dolibarrId;

    // Constructors
    public RegistrationRequestDto() {}

    public RegistrationRequestDto(String firebaseUid, String username, String email, 
                                  String department, String registrationMethod) {
        this.firebaseUid = firebaseUid;
        this.username = username;
        this.email = email;
        this.department = department;
        this.registrationMethod = registrationMethod;
    }

    // Getters and Setters
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

    public String getDolibarrId() {
        return dolibarrId;
    }

    public void setDolibarrId(String dolibarrId) {
        this.dolibarrId = dolibarrId;
    }

    @Override
    public String toString() {
        return "RegistrationRequestDto{" +
                "firebaseUid='" + firebaseUid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", registrationMethod='" + registrationMethod + '\'' +
                ", priorityLevel='" + priorityLevel + '\'' +
                '}';
    }
}
