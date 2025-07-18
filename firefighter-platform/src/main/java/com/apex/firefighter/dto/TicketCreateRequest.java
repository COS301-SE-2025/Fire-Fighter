package com.apex.firefighter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new emergency ticket")
public class TicketCreateRequest {

    @Schema(description = "Unique ticket identifier", example = "FIRE-2025-001")
    private String ticketId;

    @Schema(description = "Description of the emergency", example = "House fire on Main Street")
    private String description;

    @Schema(description = "User ID who created the ticket", example = "user123")
    private String userId;

    @Schema(description = "Type of emergency", example = "FIRE", allowableValues = {"FIRE", "MEDICAL", "RESCUE", "HAZMAT"})
    private String emergencyType;

    @Schema(description = "Emergency contact information", example = "+1-555-0123")
    private String emergencyContact;

    @Schema(description = "Duration of the emergency response", example = "2 hours")
    private String duration;

    // Default constructor
    public TicketCreateRequest() {}

    // Constructor with all fields
    public TicketCreateRequest(String ticketId, String description, String userId, 
                              String emergencyType, String emergencyContact, String duration) {
        this.ticketId = ticketId;
        this.description = description;
        this.userId = userId;
        this.emergencyType = emergencyType;
        this.emergencyContact = emergencyContact;
        this.duration = duration;
    }

    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "TicketCreateRequest{" +
                "ticketId='" + ticketId + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", emergencyType='" + emergencyType + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
