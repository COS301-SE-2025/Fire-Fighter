package com.apex.firefighter.dto.registration;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * DTO for approval/rejection decisions
 */
public class ApprovalDecisionDto {

    @NotBlank(message = "Firebase UID is required")
    private String firebaseUid;

    private List<String> assignedAccessGroups;

    private String rejectionReason;

    private String department;

    private String dolibarrId;

    // Constructors
    public ApprovalDecisionDto() {}

    public ApprovalDecisionDto(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    // Getters and Setters
    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public List<String> getAssignedAccessGroups() {
        return assignedAccessGroups;
    }

    public void setAssignedAccessGroups(List<String> assignedAccessGroups) {
        this.assignedAccessGroups = assignedAccessGroups;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDolibarrId() {
        return dolibarrId;
    }

    public void setDolibarrId(String dolibarrId) {
        this.dolibarrId = dolibarrId;
    }
}
