package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "firefighter")
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId; // Firebase UID

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "department", nullable = true)
    private String department;

    @Column(name = "is_authorized", nullable = true, columnDefinition = "boolean default false")
    private Boolean isAuthorized = false;

    @Column(name = "created_at", nullable = true, updatable = false, columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime createdAt;

    @Column(name = "last_login", nullable = true)
    private ZonedDateTime lastLogin;

    @Column(name = "role", nullable = true)
    private String role;

    @Column(name = "is_admin", nullable = true, columnDefinition = "boolean default false")
    private Boolean isAdmin = false;

    // One-to-Many relationship with UserRole (instead of Many-to-Many)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    // Default constructor
    public User() {
        this.createdAt = ZonedDateTime.now();
        this.isAuthorized = false;
        this.isAdmin = false;
    }

    // Constructor with Firebase UID
    public User(String userId, String username, String email, String department) {
        this();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.department = department;
    }

    // Getters and Setters
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

    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(ZonedDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    // Helper methods for roles
    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.setUser(this);
    }

    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole);
        userRole.setUser(null);
    }

    public boolean hasRole(String roleName) {
        return this.userRoles.stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
    }

    public boolean isAuthorized() {
        return this.isAuthorized;
    }

    public boolean isAdmin() {
        return this.isAdmin != null && this.isAdmin;
    }

    // Update last login timestamp
    public void updateLastLogin() {
        this.lastLogin = ZonedDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", isAuthorized=" + isAuthorized +
                ", isAdmin=" + isAdmin +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", roles=" + userRoles.size() + " roles" +
                '}';
    }
}