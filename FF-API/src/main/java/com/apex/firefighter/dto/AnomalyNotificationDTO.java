package com.apex.firefighter.dto;

import java.time.LocalDateTime;

public class AnomalyNotificationDTO {
    private Long id;
    private String anomalyType;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String username;

    // Default constructor
    public AnomalyNotificationDTO() {}

    // Constructor
    public AnomalyNotificationDTO(Long id, String anomalyType, String message, LocalDateTime createdAt, boolean isRead, String username) {
        this.id = id;
        this.anomalyType = anomalyType;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.username = username;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}