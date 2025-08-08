# Hibernate Entity Mapping Guide for FireFighter Schema

This guide explains how to map database tables in the `firefighter` schema to Hibernate entities in your Spring Boot application.

## Overview

Hibernate entities are Java classes that represent database tables. Each entity class corresponds to a table, and each field in the class corresponds to a column in the table.

## File Structure

All entity classes should be placed in: `src/main/java/com/apex/firefighter/model/`

```
src/main/java/com/apex/firefighter/model/
├── User.java
├── Role.java
├── UserRole.java
├── AccessRequest.java
├── AccessSession.java
├── AccessLog.java
└── Ticket.java
```

## Step-by-Step Mapping Process

### Step 1: Create the Entity Class

Create a new Java class in the `model` package for each database table.

### Step 2: Add Basic Annotations

Every entity needs these basic annotations:

```java
@Entity                           // Marks this as a JPA entity
@Table(name = "table_name")       // Maps to specific database table
public class EntityName {
    // class content
}
```

### Step 3: Define the Primary Key

Every entity must have a primary key:

```java
@Id                                           // Marks as primary key
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
@Column(name = "column_name")                 // Maps to specific column
private Long id;
```

### Step 4: Map Other Columns

Map each table column to a class field:

```java
@Column(name = "database_column_name", nullable = false, unique = true)
private String fieldName;
```

### Step 5: Define Relationships

Use JPA annotations to define relationships between entities:

- `@OneToOne` - One-to-one relationship
- `@OneToMany` - One-to-many relationship  
- `@ManyToOne` - Many-to-one relationship
- `@ManyToMany` - Many-to-many relationship

## Complete Examples

### Example 1: Simple Entity (Role Table)

**Database Table:**
```sql
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```

**Hibernate Entity:**
```java
package com.apex.firefighter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name; // e.g., ADMIN, USER, FIREFIGHTER
    
    // Constructors
    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

### Example 2: Entity with Foreign Keys (AccessRequest Table)

**Database Table:**
```sql
CREATE TABLE access_requests (
    request_id SERIAL PRIMARY KEY,
    ticket_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    request_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_time TIMESTAMP WITH TIME ZONE,
    approved_by VARCHAR(255),
    user_id VARCHAR(255) NOT NULL REFERENCES users(user_id)
);
```

**Hibernate Entity:**
```java
package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "access_requests")
public class AccessRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    
    @Column(name = "ticket_id", nullable = false)
    private String ticketId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "request_time", nullable = false, updatable = false)
    private ZonedDateTime requestTime;
    
    @Column(name = "approved_time")
    private ZonedDateTime approvedTime;
    
    @Column(name = "approved_by")
    private String approvedBy; // Firebase UID of approver
    
    // Foreign Key Relationship
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Constructors
    public AccessRequest() {
        this.requestTime = ZonedDateTime.now();
        this.status = RequestStatus.PENDING;
    }
    
    // Getters and Setters (generated)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    // Enum for status values
    public enum RequestStatus {
        PENDING, APPROVED, DENIED, REVOKED
    }
}
```

### Example 3: Many-to-Many Relationship (User-Role Junction Table)

**Database Tables:**
```sql
CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY,  -- Firebase UID
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department VARCHAR(100),
    is_authorized BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE
);

CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL REFERENCES users(user_id),
    role_id BIGINT NOT NULL REFERENCES roles(role_id),
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(255)
);
```

**User Entity:**
```java
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
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "is_authorized", columnDefinition = "boolean default false")
    private Boolean isAuthorized = false;
    
    @Column(name = "created_at", updatable = false, 
            columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime createdAt;
    
    @Column(name = "last_login")
    private ZonedDateTime lastLogin;
    
    // One-to-Many relationship with UserRole junction table
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();
    
    // Constructors
    public User() {
        this.createdAt = ZonedDateTime.now();
        this.isAuthorized = false;
    }
    
    public User(String userId, String username, String email, String department) {
        this();
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.department = department;
    }
    
    // Helper methods for managing roles
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
    
    // Getters and Setters (omitted for brevity)
}
```

**UserRole Junction Entity:**
```java
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
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "assigned_at", 
            columnDefinition = "timestamp with time zone default CURRENT_TIMESTAMP")
    private ZonedDateTime assignedAt;
    
    @Column(name = "assigned_by")
    private String assignedBy;
    
    // Constructors
    public UserRole() {
        this.assignedAt = ZonedDateTime.now();
    }
    
    public UserRole(User user, Role role, String assignedBy) {
        this();
        this.user = user;
        this.role = role;
        this.assignedBy = assignedBy;
    }
    
    // Getters and Setters (omitted for brevity)
}
```

## Common Annotations Reference

### Class-Level Annotations
- `@Entity` - Marks class as JPA entity
- `@Table(name = "table_name", schema = "schema_name")` - Maps to database table

### Field-Level Annotations
- `@Id` - Marks primary key field
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-increment primary key
- `@Column(name = "col_name", nullable = false, unique = true)` - Maps to database column
- `@Enumerated(EnumType.STRING)` - Maps enum to string in database
- `@Temporal(TemporalType.TIMESTAMP)` - For date/time fields

### Relationship Annotations
- `@ManyToOne` - Many records of this entity relate to one record of another
- `@OneToMany(mappedBy = "fieldName")` - One record relates to many of another
- `@JoinColumn(name = "foreign_key_column")` - Specifies foreign key column
- `@OneToOne` - One-to-one relationship
- `@ManyToMany` - Many-to-many relationship

### Fetch and Cascade Options
- `fetch = FetchType.LAZY` - Load related data only when accessed
- `fetch = FetchType.EAGER` - Load related data immediately
- `cascade = CascadeType.ALL` - Cascade all operations to related entities

## Best Practices

### 1. Naming Conventions
- Entity class names should be PascalCase (e.g., `AccessRequest`)
- Field names should be camelCase (e.g., `createdAt`)
- Use `@Column(name = "snake_case")` to map to database columns

### 2. Constructor Patterns
```java
// Always provide default constructor
public EntityName() {}

// Provide constructors for required fields
public EntityName(String requiredField1, String requiredField2) {
    this.requiredField1 = requiredField1;
    this.requiredField2 = requiredField2;
}
```

### 3. Relationship Management
```java
// Always provide helper methods for bidirectional relationships
public void addChild(ChildEntity child) {
    this.children.add(child);
    child.setParent(this);
}

public void removeChild(ChildEntity child) {
    this.children.remove(child);
    child.setParent(null);
}
```

### 4. toString() Method
```java
@Override
public String toString() {
    return "EntityName{" +
            "id=" + id +
            ", field1='" + field1 + '\'' +
            ", field2=" + field2 +
            '}';
}
```

## Creating New Entity Mapping

To map a new database table:

1. **Create Entity Class**: `src/main/java/com/apex/firefighter/model/EntityName.java`
2. **Add Basic Structure**:
   ```java
   @Entity
   @Table(name = "table_name", schema = "firefighter")
   public class EntityName {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       // Add other fields with @Column annotations
       // Add relationships with @ManyToOne, @OneToMany, etc.
       // Add constructors, getters, setters
   }
   ```
3. **Create Repository Interface**: `src/main/java/com/apex/firefighter/repository/EntityNameRepository.java`
4. **Add Service Methods**: Update or create service class in `src/main/java/com/apex/firefighter/service/`
5. **Test the Mapping**: Create tests in `src/test/java/com/apex/firefighter/repository/`

## Example: Complete New Entity

If you have a new table `incidents`:

```sql
CREATE TABLE incidents (
    incident_id SERIAL PRIMARY KEY,
    incident_number VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    location VARCHAR(255),
    severity VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(255) REFERENCES users(user_id)
);
```

Create `src/main/java/com/apex/firefighter/model/Incident.java`:

```java
package com.apex.firefighter.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "incidents", schema = "firefighter")
public class Incident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private Long id;
    
    @Column(name = "incident_number", nullable = false, unique = true)
    private String incidentNumber;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String location;
    
    @Enumerated(EnumType.STRING)
    private Severity severity;
    
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reportedBy;
    
    // Constructors, getters, setters, enum definition...
    
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
```

This guide should help you map any database table in your firefighter schema to Hibernate entities following the established patterns in your codebase. 