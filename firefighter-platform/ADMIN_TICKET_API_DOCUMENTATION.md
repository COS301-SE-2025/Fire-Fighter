# Admin Ticket Management API Documentation

## Overview

This document describes the admin-specific endpoints for ticket management in the Fire-Fighter platform. These endpoints provide administrative functionality for managing tickets, including viewing active tickets, accessing ticket history, and revoking tickets.

## Authentication & Authorization

- **Admin Access Required**: All admin endpoints require users with `isAdmin = true` flag
- **User Validation**: The system validates admin status before allowing ticket revocation
- **Security**: Admin user ID must be provided and validated for revocation operations

---

## 📋 Admin Endpoints

### 1. Get Active Tickets

**Endpoint**: `GET /api/tickets/admin/active`

**Description**: Retrieves all tickets with status "Active" sorted by creation date (newest first)

**Response**: 
```json
[
    {
        "id": 1,
        "ticketId": "JIRA-123",
        "description": "Critical system failure",
        "status": "Active",
        "dateCreated": "2024-01-15T10:30:00",
        "requestDate": "2024-01-15",
        "userId": "user123",
        "emergencyType": "critical-system-failure",
        "emergencyContact": "12345",
        "rejectReason": null
    }
]
```

**Status Codes**:
- `200 OK`: Success
- `500 Internal Server Error`: Server error

---

### 2. Get Ticket History

**Endpoint**: `GET /api/tickets/admin/history`

**Description**: Retrieves all tickets sorted by creation date (newest first) for complete history view

**Response**: Same format as active tickets, but includes all statuses

**Status Codes**:
- `200 OK`: Success
- `500 Internal Server Error`: Server error

---

### 3. Get Tickets by Status

**Endpoint**: `GET /api/tickets/admin/status/{status}`

**Description**: Retrieves tickets filtered by specific status

**Parameters**:
- `status` (path): Status to filter by (e.g., "Active", "Closed", "Pending")

**Example**: `GET /api/tickets/admin/status/Closed`

**Response**: Array of tickets with the specified status

**Status Codes**:
- `200 OK`: Success
- `500 Internal Server Error`: Server error

---

### 4. Revoke Ticket by Database ID

**Endpoint**: `PUT /api/tickets/admin/revoke/{id}`

**Description**: Revokes (closes) a ticket by its database ID. Only admin users can perform this action.

**Parameters**:
- `id` (path): Database ID of the ticket to revoke

**Request Body**:
```json
{
    "adminUserId": "admin_firebase_uid",
    "rejectReason": "Duplicate request - resolved via phone call"
}
```

**Success Response**:
```json
{
    "success": true,
    "message": "Ticket revoked successfully",
    "ticket": {
        "id": 1,
        "ticketId": "JIRA-123",
        "description": "Critical system failure",
        "status": "Closed",
        "dateCreated": "2024-01-15T10:30:00",
        "requestDate": "2024-01-15",
        "userId": "user123",
        "emergencyType": "critical-system-failure",
        "emergencyContact": "12345",
        "rejectReason": "Duplicate request - resolved via phone call"
    }
}
```

**Error Responses**:
```json
// Missing admin user ID
{
    "error": "Admin user ID is required"
}

// Missing reject reason
{
    "error": "Reject reason is required"
}

// User not found
{
    "error": "Admin user not found: admin_firebase_uid"
}

// Insufficient privileges
{
    "error": "User does not have admin privileges: user_firebase_uid"
}

// Ticket not found
{
    "error": "Ticket not found with ID: 123"
}

// Ticket already closed
{
    "error": "Ticket is already closed: 123"
}
```

**Status Codes**:
- `200 OK`: Success
- `400 Bad Request`: Validation error or business logic error
- `500 Internal Server Error`: Server error

---

### 5. Revoke Ticket by Ticket ID

**Endpoint**: `PUT /api/tickets/admin/revoke/ticket-id/{ticketId}`

**Description**: Revokes (closes) a ticket by its ticket ID. Only admin users can perform this action.

**Parameters**:
- `ticketId` (path): Ticket ID (e.g., "JIRA-123")

**Request Body**: Same as revoke by database ID

**Response**: Same format as revoke by database ID

**Status Codes**: Same as revoke by database ID

---

### 6. Check Admin Status

**Endpoint**: `GET /api/tickets/admin/check/{userId}`

**Description**: Checks if a user has admin privileges

**Parameters**:
- `userId` (path): Firebase UID of the user to check

**Response**:
```json
{
    "isAdmin": true
}
```

**Status Codes**:
- `200 OK`: Always returns 200, check `isAdmin` value

---

## 🗄️ Database Changes

### Required SQL Migration

Run this SQL script to add the `reject_reason` column:

```sql
-- Add reject_reason column to tickets table
ALTER TABLE firefighter.tickets 
ADD COLUMN reject_reason TEXT;

-- Add comment to the column for documentation
COMMENT ON COLUMN firefighter.tickets.reject_reason IS 'Reason provided when a ticket is rejected or revoked by an admin';
```

---

## 🧪 Testing Examples

### Test Scenario 1: Admin Workflow

1. **Check Admin Status**
```bash
curl -X GET "http://localhost:8080/api/tickets/admin/check/admin_firebase_uid"
```

2. **Get Active Tickets**
```bash
curl -X GET "http://localhost:8080/api/tickets/admin/active"
```

3. **Revoke a Ticket**
```bash
curl -X PUT "http://localhost:8080/api/tickets/admin/revoke/1" \
-H "Content-Type: application/json" \
-d '{
    "adminUserId": "admin_firebase_uid",
    "rejectReason": "Duplicate request - resolved via phone call"
}'
```

4. **View Ticket History**
```bash
curl -X GET "http://localhost:8080/api/tickets/admin/history"
```

### Test Scenario 2: Error Handling

1. **Non-Admin User Trying to Revoke**
```bash
curl -X PUT "http://localhost:8080/api/tickets/admin/revoke/1" \
-H "Content-Type: application/json" \
-d '{
    "adminUserId": "regular_user_firebase_uid",
    "rejectReason": "Test reason"
}'
```

2. **Missing Reject Reason**
```bash
curl -X PUT "http://localhost:8080/api/tickets/admin/revoke/1" \
-H "Content-Type: application/json" \
-d '{
    "adminUserId": "admin_firebase_uid"
}'
```

---

## 🔒 Security Considerations

1. **Admin Verification**: Every revocation operation verifies admin status
2. **Input Validation**: All inputs are validated for required fields
3. **Audit Trail**: All operations are logged with admin user ID
4. **Business Logic**: Prevents revoking already closed tickets
5. **Error Handling**: Comprehensive error messages without exposing sensitive data

---

## 💡 Frontend Integration Tips

### For Admin Dashboard

```javascript
// Check if current user is admin
const checkAdminStatus = async (userId) => {
    const response = await fetch(`/api/tickets/admin/check/${userId}`);
    const data = await response.json();
    return data.isAdmin;
};

// Get active tickets for admin view
const getActiveTickets = async () => {
    const response = await fetch('/api/tickets/admin/active');
    return await response.json();
};

// Revoke a ticket
const revokeTicket = async (ticketId, adminUserId, rejectReason) => {
    const response = await fetch(`/api/tickets/admin/revoke/${ticketId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            adminUserId: adminUserId,
            rejectReason: rejectReason
        })
    });
    
    const data = await response.json();
    
    if (!response.ok) {
        throw new Error(data.error || 'Failed to revoke ticket');
    }
    
    return data;
};
```

---

## 📊 Status Workflow

```
Active → Closed (via admin revocation)
Active → Completed (via normal resolution)
Closed → [Final state]
```

The admin revocation sets status to "Closed" and adds a reject reason, creating a clear audit trail for administrative actions. 