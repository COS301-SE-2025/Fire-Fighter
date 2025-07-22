# 🤖 Postman Testing Guide - AI Chatbot Features

## 📋 About FireFighter Platform

FireFighter is an **Emergency Access Management Platform** that allows users to request temporary elevated access to critical systems (ERP, databases, production environments) during system emergencies without waiting for administrator approval. This enables rapid incident response and system fixes.

**Key Concepts:**
- **Tickets** = Access requests for elevated permissions in integrated systems
- **Emergency Types**: Critical System Failure, Security Incident, Data Recovery, Network Outage, User Lockout, Other Emergency
- **Users** can create tickets that immediately grant temporary elevated access
- **Administrators** review access logs and can revoke access if needed
- This is for **SYSTEM emergencies**, not physical emergencies

## 📋 Prerequisites

1. **Application Running**: Start with `mvn spring-boot:run`
2. **Base URL**: `https://localhost:8443` (HTTPS) or `http://localhost:8080` (HTTP)
3. **API Key**: Ensure `GOOGLE_GEMINI_API_KEY` is set in `.env` file
4. **Test User**: Have a valid Firebase user ID for testing

## 🔧 Postman Collection Setup

### Environment Variables
Create a Postman environment with these variables:
```
base_url: https://localhost:8443
test_user_id: your-firebase-user-id-here
admin_user_id: admin-firebase-user-id-here
```

## 🧪 Test Cases

### 1. Health Check
**Purpose**: Verify chatbot service is running

```http
GET {{base_url}}/api/chatbot/health
```

**Expected Response** (200 OK):
```json
{
  "status": "healthy",
  "service": "AI Chatbot",
  "timestamp": "2025-01-18T18:30:00",
  "version": "1.0.0"
}
```

---

### 2. Basic User Query - Show My Tickets
**Purpose**: Test basic user ticket query functionality (MUST show actual data, not navigation)

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show me my tickets",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your latest access tickets (3 most recent): - SYS-2025-001: Critical System Failure...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00",
  "formattedTimestamp": "2025-01-18 18:30:00"
}
```

---

### 3. Active Access Query
**Purpose**: Test filtering for active access permissions only

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "What elevated access do I currently have?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your active access tickets (2 total): - SYS-2025-001: SYSTEM_OUTAGE - Production database access...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 4. Emergency Type Query
**Purpose**: Test filtering by emergency type

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Do I have any security incident access?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your security incident tickets (1 total): - SEC-2025-001: Security breach response access...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 5. Latest Tickets Query
**Purpose**: Test showing user's latest tickets (not admin guidance)

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Can you show me the latest tickets in the system",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your latest access tickets (3 most recent): - SYS-2025-001: Critical System Failure...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 6. Access Activity Query
**Purpose**: Test showing user's recent access activity (not navigation)

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show recent access activity",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your recent access activity (3 latest tickets): - SYS-2025-001: Critical System Failure...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 7. Recent Tickets Query
**Purpose**: Test recent ticket filtering

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show my recent activity",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your most recent tickets: - FIRE-2025-001: FIRE (Active, Jan 18, 14:30)...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 6. Navigation Query
**Purpose**: Test application navigation guidance

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "How do I navigate to My Requests page?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "📋 My Requests: Your personal access management page showing: - All your access tickets...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 7. Application Structure Query
**Purpose**: Test understanding of app structure

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Help me understand the Dashboard",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "📊 Dashboard: Your main overview page showing: - Key metrics and system status...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 8. Request Creation Query
**Purpose**: Test emergency request creation guidance

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "How do I create a new emergency request?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "🚨 Creating Emergency Access Requests: Step 1: Access the Request Form...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 9. Request Details Query
**Purpose**: Test specific request information guidance

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "What information do I need for a request?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "📝 Step 2: Fill Out Request Details: Request Date, Emergency Type, Reason for Access...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 10. No Active Tickets Query
**Purpose**: Test showing past tickets when no active tickets exist

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show me my tickets",
  "userId": "{{test_user_id_no_active}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "You have no active tickets. Here are your past 3 tickets: - SYS-2025-001: Critical System Failure...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 11. Time Remaining Query
**Purpose**: Test showing time remaining on active tickets

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "How much time is remaining on my active tickets?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Time remaining on your active tickets: - SYS-2025-001: Critical System Failure - 2h 15m remaining...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 12. Ticket Expiration Query
**Purpose**: Test asking about ticket expiration

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "When do my tickets expire?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Time remaining on your active tickets: - SYS-2025-001: Critical System Failure - 45 minutes remaining...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 13. Closed Tickets Query
**Purpose**: Test closed ticket filtering

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "What tickets have I completed recently?",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Your recently closed tickets (1 total): - MEDICAL-2025-002: MEDICAL - Completed...",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 7. Get User Capabilities
**Purpose**: Test capability discovery for regular user

```http
GET {{base_url}}/api/chatbot/capabilities/{{test_user_id}}
```

**Expected Response** (200 OK):
```json
{
  "available": true,
  "adminAccess": false,
  "accessLevel": "Personal ticket access",
  "suggestedQueries": [
    "Show my tickets",
    "What tickets am I assigned to?",
    "Do I have any active emergencies?",
    "Help with ticket status",
    "How do I update a ticket?"
  ]
}
```

---

### 8. Get User Suggestions
**Purpose**: Test personalized query suggestions

```http
GET {{base_url}}/api/chatbot/suggestions/{{test_user_id}}
```

**Expected Response** (200 OK):
```json
{
  "available": true,
  "userRole": "User",
  "accessLevel": "Personal ticket access",
  "suggestedQueries": [
    "Show my tickets",
    "What tickets am I assigned to?",
    "Do I have any active emergencies?",
    "Help with ticket status",
    "How do I update a ticket?"
  ],
  "examples": [
    "What tickets am I assigned to?",
    "Do I have any urgent tasks?",
    "How do I update my ticket status?",
    "Show my recent activity",
    "Help me with emergency procedures"
  ]
}
```

---

### 9. Admin Query (Future Feature)
**Purpose**: Test admin-level query (currently shows user context for security)

```http
POST {{base_url}}/api/chatbot/admin/query
Content-Type: application/json

{
  "query": "Show system overview",
  "userId": "{{admin_user_id}}"
}
```

**Expected Response** (200 OK):
```json
{
  "message": "Admin access available. Currently showing user-specific context for security...",
  "success": true,
  "userRole": "Administrator",
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 10. Admin Capabilities
**Purpose**: Test capability discovery for admin user

```http
GET {{base_url}}/api/chatbot/capabilities/{{admin_user_id}}
```

**Expected Response** (200 OK):
```json
{
  "available": true,
  "adminAccess": true,
  "accessLevel": "Full system access",
  "suggestedQueries": [
    "Show me active tickets",
    "What's the current ticket summary?",
    "How many fire emergencies are active?",
    "Show recent ticket activity",
    "What tickets need attention?",
    "Export ticket statistics"
  ]
}
```

---

## ❌ Error Test Cases

### 11. Empty Query
**Purpose**: Test validation for empty queries

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (400 Bad Request):
```json
{
  "message": "Query cannot be empty",
  "success": false,
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 12. Missing User ID
**Purpose**: Test validation for missing user ID

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show my tickets"
}
```

**Expected Response** (400 Bad Request):
```json
{
  "message": "User ID is required",
  "success": false,
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 13. Invalid User ID
**Purpose**: Test authentication with non-existent user

```http
POST {{base_url}}/api/chatbot/query
Content-Type: application/json

{
  "query": "Show my tickets",
  "userId": "invalid-user-id-123"
}
```

**Expected Response** (200 OK with error message):
```json
{
  "message": "User not found. Please ensure you are properly authenticated.",
  "success": false,
  "timestamp": "2025-01-18T18:30:00"
}
```

---

### 14. Non-Admin Accessing Admin Endpoint
**Purpose**: Test security for admin endpoints

```http
POST {{base_url}}/api/chatbot/admin/query
Content-Type: application/json

{
  "query": "Show system statistics",
  "userId": "{{test_user_id}}"
}
```

**Expected Response** (403 Forbidden):
```json
{
  "message": "Administrator privileges required for this query.",
  "success": false,
  "timestamp": "2025-01-18T18:30:00"
}
```

---

## 🔍 Testing Checklist

### ✅ Functional Tests
- [ ] Health check responds correctly
- [ ] Basic user queries work
- [ ] Ticket filtering works (active, closed, emergency type)
- [ ] User capabilities are returned correctly
- [ ] Suggestions are personalized

### ✅ Security Tests
- [ ] Empty queries are rejected
- [ ] Missing user IDs are rejected
- [ ] Invalid user IDs are handled gracefully
- [ ] Non-admin users cannot access admin endpoints
- [ ] Users only see their own ticket data

### ✅ Integration Tests
- [ ] AI service integration works (requires valid API key)
- [ ] Database queries return user-specific data
- [ ] Response formatting is consistent

---

## 🚨 Troubleshooting

### Common Issues
1. **"AI service not configured"**: Check `GOOGLE_GEMINI_API_KEY` in `.env`
2. **SSL Certificate errors**: Use HTTP endpoint or accept certificate
3. **User not found**: Ensure user exists in database with correct Firebase UID
4. **Empty responses**: Check console logs for detailed error messages

### Debug Endpoints
- **Health**: `/api/chatbot/health`
- **Capabilities**: `/api/chatbot/capabilities/{userId}`
- **Application Health**: `/actuator/health` (if actuator enabled)

---

**🤖 AI Chatbot Testing Complete!**  
*FireFighter Platform - Emergency Response Management System*
