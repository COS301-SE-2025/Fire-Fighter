package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "api_keys", schema = "firefighter")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key", nullable = false, unique = true, length = 64)
    private String apiKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public ApiKey() {
        this.createdAt = ZonedDateTime.now();
        this.isActive = true;
    }

    public ApiKey(String apiKey, User user) {
        this();
        this.apiKey = apiKey;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    
    
    

    
}
