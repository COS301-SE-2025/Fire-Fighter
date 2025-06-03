# 🔥 Firebase + Hibernate Integration Guide

This guide explains how **Firebase Authentication** works with **Hibernate ORM** in your FireFighter platform.

## 🏗️ **Architecture Overview**

```
🔐 Firebase Auth (Frontend)
    ↓ (Firebase UID)
📱 Your Frontend Application
    ↓ (API calls with Firebase UID)
🌐 Spring Boot Backend (Your API)
    ↓ (Hibernate ORM)
🗄️ PostgreSQL Database (firefighter schema)
```

## 🔍 **How It Works**

### **1. Authentication Flow**
1. **User logs in** through Firebase (frontend)
2. **Firebase returns UID** (e.g., "abc123xyz")
3. **Frontend sends UID** to your Spring Boot API
4. **Your API verifies/creates user** in PostgreSQL
5. **Database stores user info** with Firebase UID as primary key

### **2. Authorization Flow**
1. **User makes API request** with Firebase UID
2. **Your API checks authorization** in PostgreSQL
3. **Returns user data** if authorized
4. **Blocks access** if not authorized

---

## 📊 **Database Structure (Hibernate → PostgreSQL)**

### **Your Entities Map to These Tables:**

#### **🧑‍💼 users table**
```sql
-- Created from User.java entity
user_id (text, PRIMARY KEY)      ← Firebase UID
username (text, NOT NULL)        ← User display name  
email (text, NOT NULL, UNIQUE)   ← User email
department (text)                ← Fire department
is_authorized (boolean)          ← Access permission
created_at (timestamptz)         ← Account creation
last_login (timestamptz)         ← Last access time
```

#### **👥 roles table**
```sql
-- Created from Role.java entity  
id (bigserial, PRIMARY KEY)      ← Auto-increment ID
name (text, NOT NULL, UNIQUE)    ← Role name (ADMIN, FIREFIGHTER, etc.)
```

#### **🔗 user_roles table**
```sql
-- Created from UserRole.java entity
id (bigserial, PRIMARY KEY)      ← Auto-increment ID
user_id (text, FOREIGN KEY)      ← References users.user_id
role_id (integer, FOREIGN KEY)   ← References roles.id
assigned_at (timestamptz)        ← When role was assigned
assigned_by (text)               ← Who assigned the role
```

---

## 🚀 **API Endpoints for Your Frontend**

### **🔐 User Verification (First Login)**
```bash
# When a Firebase user first accesses your app
POST /api/users/verify
Content-Type: application/x-www-form-urlencoded

firebaseUid=abc123xyz&username=john_doe&email=john@station.com&department=Station%201
```

**Response:**
```json
{
  "userId": "abc123xyz",
  "username": "john_doe", 
  "email": "john@station.com",
  "department": "Station 1",
  "isAuthorized": false,
  "createdAt": "2025-06-01T10:30:00Z",
  "lastLogin": "2025-06-01T10:30:00Z",
  "userRoles": []
}
```

### **✅ Authorization Check**
```bash
# Check if user is authorized for protected resources
GET /api/users/abc123xyz/authorized
```

**Response:**
```json
true  // or false
```

### **👤 Get User Info**
```bash
# Get complete user information including roles
GET /api/users/abc123xyz
```

**Response:**
```json
{
  "userId": "abc123xyz",
  "username": "john_doe",
  "email": "john@station.com", 
  "department": "Station 1",
  "isAuthorized": true,
  "createdAt": "2025-06-01T10:30:00Z",
  "lastLogin": "2025-06-01T15:45:00Z",
  "userRoles": [
    {
      "id": 1,
      "role": {
        "id": 2,
        "name": "FIREFIGHTER"
      },
      "assignedAt": "2025-06-01T12:00:00Z",
      "assignedBy": "admin@station.com"
    }
  ]
}
```

### **🛡️ Role Check**
```bash
# Check if user has specific role
GET /api/users/abc123xyz/roles/FIREFIGHTER
```

**Response:**
```json
true  // or false
```

---

## 👨‍💼 **Admin Operations**

### **✅ Authorize User**
```bash
# Admin authorizes a new user
PUT /api/users/abc123xyz/authorize?authorizedBy=admin@station.com
```

### **❌ Revoke Authorization**
```bash
# Admin revokes user access
PUT /api/users/abc123xyz/revoke?revokedBy=admin@station.com
```

### **🎭 Assign Role**
```bash
# Admin assigns role to user
POST /api/users/abc123xyz/roles?roleName=FIREFIGHTER&assignedBy=admin@station.com
```

---

## 💻 **Frontend Integration Examples**

### **React/JavaScript Example:**
```javascript
// 1. After Firebase login
firebase.auth().onAuthStateChanged(async (firebaseUser) => {
  if (firebaseUser) {
    const firebaseUid = firebaseUser.uid;
    const username = firebaseUser.displayName;
    const email = firebaseUser.email;
    
    // 2. Verify user in your backend
    const response = await fetch('/api/users/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `firebaseUid=${firebaseUid}&username=${username}&email=${email}&department=Station 1`
    });
    
    const user = await response.json();
    
    // 3. Check if user is authorized
    const authCheck = await fetch(`/api/users/${firebaseUid}/authorized`);
    const isAuthorized = await authCheck.json();
    
    if (isAuthorized) {
      // User can access protected features
      console.log('User authorized:', user);
    } else {
      // Show "waiting for approval" message
      console.log('User awaiting authorization');
    }
  }
});

// Check user role before showing admin features
async function canUserManageRoles(firebaseUid) {
  const response = await fetch(`/api/users/${firebaseUid}/roles/ADMIN`);
  return await response.json();
}
```

---

## 🔧 **How Hibernate ORM Works Here**

### **1. Entity Annotations Explained:**
```java
@Entity                                    // ← Tells Hibernate: "This is a database table"
@Table(name = "users", schema = "firefighter")  // ← Maps to firefighter.users table
public class User {
    
    @Id                                    // ← Primary key
    @Column(name = "user_id")              // ← Maps to user_id column
    private String userId;                 // ← Firebase UID (String, not auto-increment)
    
    @Column(nullable = false)              // ← NOT NULL constraint
    private String username;
    
    @OneToMany(mappedBy = "user")          // ← One user has many roles
    private Set<UserRole> userRoles;       // ← Relationship to user_roles table
}
```

### **2. Automatic SQL Generation:**
When you do this in Java:
```java
User user = new User("abc123xyz", "john_doe", "john@station.com", "Station 1");
userRepository.save(user);
```

Hibernate automatically generates this SQL:
```sql
INSERT INTO firefighter.users (user_id, username, email, department, is_authorized, created_at) 
VALUES ('abc123xyz', 'john_doe', 'john@station.com', 'Station 1', false, CURRENT_TIMESTAMP);
```

### **3. Query Examples:**
```java
// Find user by Firebase UID
Optional<User> user = userRepository.findByUserId("abc123xyz");

// Find all authorized users
List<User> authorized = userRepository.findByIsAuthorizedTrue();

// Find users with specific role
List<User> firefighters = userRepository.findByRoleName("FIREFIGHTER");
```

---

## 🛠️ **Testing Your Setup**

### **1. Start Application:**
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### **2. Test User Verification:**
```bash
# Create a test user
curl -X POST "http://localhost:8080/api/users/verify" \
  -d "firebaseUid=test123&username=test_user&email=test@example.com&department=Test Station"
```

### **3. Test Authorization:**
```bash
# Check authorization (should be false initially)
curl http://localhost:8080/api/users/test123/authorized

# Authorize the user (admin operation)
curl -X PUT "http://localhost:8080/api/users/test123/authorize?authorizedBy=admin"

# Check again (should be true now)
curl http://localhost:8080/api/users/test123/authorized
```

---

## 🎯 **Key Benefits of This Architecture**

✅ **Secure**: Firebase handles authentication, you handle authorization  
✅ **Scalable**: Firebase UID as primary key works with any number of users  
✅ **Flexible**: Easy to add roles and permissions  
✅ **Traceable**: Full audit trail of who authorized/assigned what  
✅ **Simple**: Frontend just passes Firebase UID to your API  

---

## 🆘 **Troubleshooting**

### **Common Issues:**

1. **"User not found"** → Make sure Firebase UID is passed correctly
2. **"Schema not found"** → Check your database connection points to firefighter schema
3. **"Authorization failed"** → User needs to be authorized by admin first
4. **"Role assignment failed"** → Make sure role exists in roles table

### **Debug Commands:**
```sql
-- Check if user exists
SELECT * FROM firefighter.users WHERE user_id = 'abc123xyz';

-- Check user roles
SELECT u.username, r.name as role_name 
FROM firefighter.users u
JOIN firefighter.user_roles ur ON u.user_id = ur.user_id  
JOIN firefighter.roles r ON ur.role_id = r.id
WHERE u.user_id = 'abc123xyz';

-- List all authorized users
SELECT user_id, username, email, is_authorized 
FROM firefighter.users 
WHERE is_authorized = true;
```

---

**🔥 Your Firebase + Hibernate integration is ready to go! 👨‍🚒** 