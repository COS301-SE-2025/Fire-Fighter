# FireFighter Platform - Login API Documentation

This document provides detailed information about the login and authentication endpoints in the FireFighter Platform.

## **Overview**

The FireFighter platform uses **Firebase Authentication** for user identity verification combined with a custom backend authorization system. The login process involves two steps:

1. **Firebase Authentication** (handled by frontend)
2. **Backend User Verification** (handled by this API)

---

## **Primary Login Endpoint**

### **POST /api/users/verify**

This is the main endpoint called when a Firebase-authenticated user accesses the system for the first time or subsequent logins.

#### **Purpose:**
- Verifies or creates users from Firebase authentication
- Updates last login timestamp
- Returns complete user profile including roles and permissions

#### **Request Format:**

**Content-Type:** `application/x-www-form-urlencoded`

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `firebaseUid` | String | ✅ Yes | Firebase User ID (UID) |
| `username` | String | ✅ Yes | User's display name |
| `email` | String | ✅ Yes | User's email address |
| `department` | String | ❌ No | User's department/division |

#### **Example Request:**

```bash
curl -X POST "http://localhost:8080/api/users/verify" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "firebaseUid=abc123xyz&username=john_doe&email=john@firefighter.com&department=Emergency%20Response"
```

#### **Response Format:**

**Success Response (200 OK):**
```json
{
  "userId": "abc123xyz",
  "username": "john_doe",
  "email": "john@firefighter.com",
  "department": "Emergency Response",
  "isAuthorized": true,
  "isAdmin": false,
  "role": "FIREFIGHTER",
  "createdAt": "2024-01-15T10:30:00Z",
  "lastLogin": "2024-01-15T14:25:00Z",
  "userRoles": [
    {
      "id": 1,
      "role": {
        "id": 2,
        "name": "FIREFIGHTER"
      },
      "assignedAt": "2024-01-15T12:00:00Z",
      "assignedBy": "admin@firefighter.com"
    }
  ]
}
```

**Error Response (500 Internal Server Error):**
```json
{
  "status": 500,
  "error": "Internal Server Error"
}
```

#### **Response Fields Explained:**

| Field | Type | Description |
|-------|------|-------------|
| `userId` | String | Firebase UID (primary key) |
| `username` | String | User's display name |
| `email` | String | User's email address |
| `department` | String | User's department/division |
| `isAuthorized` | Boolean | Whether user can access protected resources |
| `isAdmin` | Boolean | Whether user has admin privileges |
| `role` | String | Simple role designation (Admin/Normal/etc.) |
| `createdAt` | DateTime | When user account was first created |
| `lastLogin` | DateTime | Timestamp of most recent login |
| `userRoles` | Array | Complex role assignments with metadata |

---

## **Supporting Authentication Endpoints**

### **GET /api/users/{firebaseUid}**

Get complete user information by Firebase UID.

**Example:**
```bash
curl http://localhost:8080/api/users/abc123xyz
```

**Response:** Same as `/verify` endpoint

---

### **GET /api/users/{firebaseUid}/authorized**

Check if a user is authorized to access protected resources.

**Example:**
```bash
curl http://localhost:8080/api/users/abc123xyz/authorized
```

**Response:**
```json
true
```

---

### **GET /api/users/{firebaseUid}/roles/{roleName}**

Check if a user has a specific role.

**Example:**
```bash
curl http://localhost:8080/api/users/abc123xyz/roles/FIREFIGHTER
```

**Response:**
```json
true
```

---

## **Frontend Integration**

### **JavaScript/React Example:**

```javascript
// Complete login flow
async function handleFirebaseLogin(firebaseUser) {
  try {
    // 1. Extract Firebase user data
    const firebaseUid = firebaseUser.uid;
    const username = firebaseUser.displayName || firebaseUser.email.split('@')[0];
    const email = firebaseUser.email;
    
    // 2. Verify user with backend
    const response = await fetch('/api/users/verify', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams({
        firebaseUid: firebaseUid,
        username: username,
        email: email,
        department: 'Emergency Response' // Optional
      })
    });
    
    if (!response.ok) {
      throw new Error('Login verification failed');
    }
    
    const userData = await response.json();
    
    // 3. Handle user data
    console.log('User logged in:', userData);
    
    // Check authorization
    if (userData.isAuthorized) {
      // User can access protected features
      if (userData.isAdmin) {
        // Show admin features
        console.log('Admin user logged in');
      } else {
        // Show normal user features
        console.log('Normal user logged in');
      }
    } else {
      // Show "waiting for approval" message
      console.log('User awaiting authorization');
    }
    
    return userData;
    
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}

// Usage with Firebase Auth
firebase.auth().onAuthStateChanged(async (firebaseUser) => {
  if (firebaseUser) {
    try {
      const userData = await handleFirebaseLogin(firebaseUser);
      // Store user data in your app state
      setCurrentUser(userData);
    } catch (error) {
      // Handle login error
      console.error('Authentication failed:', error);
    }
  } else {
    // User logged out
    setCurrentUser(null);
  }
});
```

### **TypeScript Interface:**

```typescript
interface UserResponse {
  userId: string;
  username: string;
  email: string;
  department?: string;
  isAuthorized: boolean;
  isAdmin: boolean;
  role?: string;
  createdAt: string;
  lastLogin?: string;
  userRoles: UserRole[];
}

interface UserRole {
  id: number;
  role: {
    id: number;
    name: string;
  };
  assignedAt: string;
  assignedBy: string;
}
```

---

## **Quick Start Guide**

### **1. First-Time User Login:**

```bash
# User logs in with Firebase, then your app calls:
POST /api/users/verify
# → Creates new user account
# → Returns user data with isAuthorized: false
```

### **2. Admin Authorization (Required):**

```bash
# Admin authorizes the new user:
PUT /api/users/abc123xyz/authorize?authorizedBy=admin@firefighter.com
# → Sets isAuthorized: true
```

### **3. Subsequent Logins:**

```bash
# Same call updates last login:
POST /api/users/verify
# → Updates lastLogin timestamp
# → Returns current user data
```

---

## **Security Notes**

1. **Firebase Handles Authentication** - This API assumes Firebase has already verified the user's identity
2. **Backend Handles Authorization** - Your database controls who can access what
3. **No Passwords Stored** - All password management is handled by Firebase
4. **Automatic User Creation** - Users are created automatically on first login
5. **Admin Approval Required** - New users need admin authorization before accessing protected features

---

## **Testing**

### **Manual Testing:**

```bash
# Test user creation/verification
curl -X POST "http://localhost:8080/api/users/verify" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "firebaseUid=test123&username=testuser&email=test@example.com"

# Test authorization check
curl http://localhost:8080/api/users/test123/authorized

# Test user info retrieval
curl http://localhost:8080/api/users/test123
```

### **Expected Flow:**

1. ✅ **POST /verify** creates user with `isAuthorized: false`
2. ✅ **GET /authorized** returns `false`
3. ✅ **Admin authorizes user** (separate endpoint)
4. ✅ **GET /authorized** returns `true`
5. ✅ **Subsequent logins** update `lastLogin` timestamp

---

## **Troubleshooting**

### **Common Issues:**

1. **500 Error on /verify**
   - Check database connection
   - Verify Firebase UID format
   - Check application logs

2. **User Not Found**
   - Ensure Firebase UID is correct
   - User must call /verify first

3. **Authorization Always False**
   - New users need admin approval
   - Check isAuthorized column in database

4. **Role Check Fails**
   - Role must exist in roles table
   - User must be assigned to role first

### **Debugging Tips:**

- Check application logs for detailed error messages
- Verify database connection and schema
- Test with simple curl commands first
- Ensure Firebase UID is being passed correctly

---

## **Additional Resources**

- [Database Connection Guide](DATABASE_CONNECTION_GUIDE.md)
- [Hibernate Mapping Guide](HIBERNATE_MAPPING_GUIDE.md)
- [Firebase Integration Guide](FIREBASE_HIBERNATE_GUIDE.md)

---

*This documentation covers the login/authentication API endpoints. For information about other API endpoints (tickets, roles, etc.), see their respective documentation files.* 