package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_roles", schema = "firefighter")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "role_id")
    private Role role;

    @Column(name = "assigned_at", nullable = true, columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime assignedAt;

    @Column(name = "assigned_by", nullable = true)
    private String assignedBy;

    // Default constructor
    public UserRole() {
        this.assignedAt = ZonedDateTime.now();
    }

    // Constructor
    public UserRole(User user, Role role, String assignedBy) {
        this();
        this.user = user;
        this.role = role;
        this.assignedBy = assignedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        return "UserRole{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUserId() : "null") +
                ", role=" + (role != null ? role.getName() : "null") +
                ", assignedAt=" + assignedAt +
                ", assignedBy='" + assignedBy + '\'' +
                '}';
    }
} 