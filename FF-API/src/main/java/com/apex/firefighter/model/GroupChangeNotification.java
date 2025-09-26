package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_change_notifications")
public class GroupChangeNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "security_level", nullable = false)
    private String securityLevel;
    
    @Column(name = "group_name")
    private String groupName;
    
    @Column(name = "change_type", nullable = false)
    private String changeType;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
    
    // Default constructor
    public GroupChangeNotification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Constructor
    public GroupChangeNotification(String userId, String username, String securityLevel, String groupName, String changeType) {
        this();
        this.userId = userId;
        this.username = username;
        this.securityLevel = securityLevel;
        this.groupName = groupName;
        this.changeType = changeType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
}