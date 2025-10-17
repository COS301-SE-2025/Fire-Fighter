package com.apex.firefighter.model.accessgroup;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * Entity representing a user's assignment to an access group
 */
@Entity
@Table(name = "user_access_groups", schema = "firefighter")
public class UserAccessGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // Firebase UID

    @Column(name = "group_id", nullable = false)
    private String groupId; // Reference to AccessGroup.groupId

    @Column(name = "assigned_at", nullable = false)
    private ZonedDateTime assignedAt;

    @Column(name = "assigned_by")
    private String assignedBy; // Firebase UID of admin who assigned

    // Constructors
    public UserAccessGroup() {
        this.assignedAt = ZonedDateTime.now();
    }

    public UserAccessGroup(String userId, String groupId, String assignedBy) {
        this();
        this.userId = userId;
        this.groupId = groupId;
        this.assignedBy = assignedBy;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "UserAccessGroup{" +
                "userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
