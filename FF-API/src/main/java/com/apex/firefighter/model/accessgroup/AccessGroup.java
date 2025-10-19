package com.apex.firefighter.model.accessgroup;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

/**
 * Entity representing an access group
 */
@Entity
@Table(name = "access_groups", schema = "firefighter")
public class AccessGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_id", nullable = false, unique = true)
    private String groupId; // e.g., 'financial', 'hr', 'logistics'

    @Column(name = "name", nullable = false)
    private String name; // e.g., 'Financial Management'

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    // Constructors
    public AccessGroup() {
        this.createdAt = ZonedDateTime.now();
    }

    public AccessGroup(String groupId, String name, String description) {
        this();
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AccessGroup{" +
                "id=" + id +
                ", groupId='" + groupId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
