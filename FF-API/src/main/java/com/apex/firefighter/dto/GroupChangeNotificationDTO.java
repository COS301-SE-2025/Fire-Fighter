package com.apex.firefighter.dto;

import java.time.LocalDateTime;

public class GroupChangeNotificationDTO {
    private Long id;
    private String securityLevel;
    private String groupName;
    private String changeType;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String username;

    // Default constructor
    public GroupChangeNotificationDTO() {}

    // Constructor
    public GroupChangeNotificationDTO(Long id, String securityLevel, String groupName, String changeType, LocalDateTime createdAt, boolean isRead, String username) {
        this.id = id;
        this.securityLevel = securityLevel;
        this.groupName = groupName;
        this.changeType = changeType;
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

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
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