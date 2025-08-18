package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * UserPreferences entity representing user notification preferences in the system.
 * Stores preferences for email notifications for various ticket events.
 */
@Entity
@Table(name = "user_preferences", schema = "firefighter")
public class UserPreferences {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId; // Firebase UID - foreign key to users table

    @Column(name = "email_notifications_enabled", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailNotificationsEnabled = false;

    @Column(name = "email_ticket_creation", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailTicketCreation = false;

    @Column(name = "email_ticket_completion", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailTicketCompletion = false;

    @Column(name = "email_ticket_revocation", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailTicketRevocation = false;

    @Column(name = "email_five_minute_warning", nullable = false, columnDefinition = "boolean default false")
    private Boolean emailFiveMinuteWarning = false;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime updatedAt;

    // Default constructor
    public UserPreferences() {
        this.emailNotificationsEnabled = false;
        this.emailTicketCreation = false;
        this.emailTicketCompletion = false;
        this.emailTicketRevocation = false;
        this.emailFiveMinuteWarning = false;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    // Constructor with user ID
    public UserPreferences(String userId) {
        this();
        this.userId = userId;
    }

    // Constructor with all preferences
    public UserPreferences(String userId, Boolean emailNotificationsEnabled, 
                          Boolean emailTicketCreation, Boolean emailTicketCompletion,
                          Boolean emailTicketRevocation, Boolean emailFiveMinuteWarning) {
        this();
        this.userId = userId;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.emailTicketCreation = emailTicketCreation;
        this.emailTicketCompletion = emailTicketCompletion;
        this.emailTicketRevocation = emailTicketRevocation;
        this.emailFiveMinuteWarning = emailFiveMinuteWarning;
    }

    // Update timestamp before persisting
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(Boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public Boolean getEmailTicketCreation() {
        return emailTicketCreation;
    }

    public void setEmailTicketCreation(Boolean emailTicketCreation) {
        this.emailTicketCreation = emailTicketCreation;
    }

    public Boolean getEmailTicketCompletion() {
        return emailTicketCompletion;
    }

    public void setEmailTicketCompletion(Boolean emailTicketCompletion) {
        this.emailTicketCompletion = emailTicketCompletion;
    }

    public Boolean getEmailTicketRevocation() {
        return emailTicketRevocation;
    }

    public void setEmailTicketRevocation(Boolean emailTicketRevocation) {
        this.emailTicketRevocation = emailTicketRevocation;
    }

    public Boolean getEmailFiveMinuteWarning() {
        return emailFiveMinuteWarning;
    }

    public void setEmailFiveMinuteWarning(Boolean emailFiveMinuteWarning) {
        this.emailFiveMinuteWarning = emailFiveMinuteWarning;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled != null && emailNotificationsEnabled;
    }

    public boolean isEmailTicketCreationEnabled() {
        return isEmailNotificationsEnabled() && emailTicketCreation != null && emailTicketCreation;
    }

    public boolean isEmailTicketCompletionEnabled() {
        return isEmailNotificationsEnabled() && emailTicketCompletion != null && emailTicketCompletion;
    }

    public boolean isEmailTicketRevocationEnabled() {
        return isEmailNotificationsEnabled() && emailTicketRevocation != null && emailTicketRevocation;
    }

    public boolean isEmailFiveMinuteWarningEnabled() {
        return isEmailNotificationsEnabled() && emailFiveMinuteWarning != null && emailFiveMinuteWarning;
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "userId='" + userId + '\'' +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", emailTicketCreation=" + emailTicketCreation +
                ", emailTicketCompletion=" + emailTicketCompletion +
                ", emailTicketRevocation=" + emailTicketRevocation +
                ", emailFiveMinuteWarning=" + emailFiveMinuteWarning +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
