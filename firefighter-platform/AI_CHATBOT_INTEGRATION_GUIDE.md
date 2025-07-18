# AI Chatbot Integration Guide - FireFighter Platform

## Overview

The FireFighter Platform now includes an AI-powered chatbot using Google Gemini AI that allows users to query tickets, get emergency information, and receive intelligent assistance with emergency response operations.

## Features

### User Capabilities
- **Personal Ticket Queries**: "Show my tickets", "What am I assigned to?"
- **Status Updates**: "Do I have any urgent tasks?"
- **Help & Guidance**: "How do I update a ticket status?"
- **Emergency Procedures**: "Help me with emergency procedures"

### Admin Capabilities
- **System Overview**: "Show me active tickets", "What's the current summary?"
- **Emergency Analysis**: "How many fire emergencies are active?"
- **Statistics**: "Show recent ticket activity", "What's the average response time?"
- **Data Export**: "Export current active tickets"
- **System Monitoring**: Real-time system status and alerts

## API Endpoints

### 1. Basic Query
```http
POST /api/chatbot/query
Content-Type: application/json

{
  "query": "Show my tickets",
  "userId": "firebase-user-id"
}
```

### 2. Admin Query
```http
POST /api/chatbot/admin/query
Content-Type: application/json

{
  "query": "How many active fire emergencies?",
  "userId": "admin-firebase-user-id"
}
```

### 3. Get Capabilities
```http
GET /api/chatbot/capabilities/{userId}
```

### 4. Get Suggestions
```http
GET /api/chatbot/suggestions/{userId}
```

### 5. Health Check
```http
GET /api/chatbot/health
```

## Architecture

### Modular Design
```
ChatbotController
    ↓
ChatbotService (Orchestration)
    ↓
├── GeminiAIService (AI Processing)
└── TicketQueryService (Data Context)
```

### Service Responsibilities

#### 1. GeminiAIService
- Direct integration with Google Gemini API
- Handles AI request/response processing
- Manages API authentication and error handling
- Configurable safety settings and generation parameters

#### 2. TicketQueryService
- Provides contextual ticket data to AI
- Filters data based on user permissions
- Generates relevant context for different query types
- Handles user-specific vs admin-level data access

#### 3. ChatbotService
- Main orchestration service
- User authentication and authorization
- Response formatting and metadata
- Error handling and fallback responses

## Configuration

### Environment Variables
Add to your `.env` file:
```
GOOGLE_GEMINI_API_KEY=your-gemini-api-key-here
```

### Application Properties
The system automatically configures WebFlux for HTTP client functionality.

## Query Types & Context

### Personal Queries
- **Keywords**: "my", "mine", "assigned"
- **Context**: User's assigned tickets only
- **Example**: "Show my active tickets"

### Active Tickets
- **Keywords**: "active", "open", "current"
- **Context**: All active tickets (admin only)
- **Example**: "What active emergencies do we have?"

### Recent Activity
- **Keywords**: "recent", "today", "latest"
- **Context**: Recent ticket history
- **Example**: "Show me today's activity"

### Statistics
- **Keywords**: "status", "count", "summary"
- **Context**: System statistics and counts
- **Example**: "Give me a ticket summary"

### Emergency Types
- **Keywords**: "fire", "medical", "rescue", "hazmat"
- **Context**: Type-specific ticket information
- **Example**: "How many fire emergencies are active?"

## Response Format

### Successful Response
```json
{
  "message": "You currently have 3 active tickets: FIRE-2025-001 (House fire), MEDICAL-2025-002 (Ambulance request), RESCUE-2025-003 (Cat rescue).",
  "success": true,
  "userRole": "User",
  "timestamp": "2025-01-18T10:30:00",
  "formattedTimestamp": "2025-01-18 10:30:00"
}
```

### Error Response
```json
{
  "message": "I'm experiencing technical difficulties. Please try again later.",
  "success": false,
  "timestamp": "2025-01-18T10:30:00"
}
```

## Security & Permissions

### User Authentication
- All endpoints require valid Firebase user ID
- User must exist in the system database
- Authentication handled through existing user service

### Role-Based Access
- **Regular Users**: Access to personal tickets only
- **Administrators**: Full system access and statistics
- **Automatic Detection**: System automatically detects user role

### Data Privacy
- Users can only see their assigned tickets
- Admins have full system visibility
- No sensitive data exposed in error messages

## Error Handling

### Graceful Degradation
- AI service unavailable → Fallback responses
- Invalid queries → Helpful error messages
- Authentication failures → Clear permission messages
- Network issues → Retry suggestions

### Logging
- All errors logged with context
- No sensitive data in logs
- Performance metrics tracked

## Integration Examples

### Frontend Integration
```javascript
// Basic query
const response = await fetch('/api/chatbot/query', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: "Show my tickets",
    userId: currentUser.uid
  })
});

const result = await response.json();
console.log(result.message);
```

### Admin Dashboard
```javascript
// Admin query
const adminResponse = await fetch('/api/chatbot/admin/query', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: "System status summary",
    userId: adminUser.uid
  })
});
```

## Testing

### Manual Testing
1. Start the application: `mvn spring-boot:run`
2. Access Swagger UI: `https://localhost:8443/swagger-ui.html`
3. Navigate to "AI Chatbot" section
4. Test with sample queries

### Sample Queries
- **User**: "What tickets am I working on?"
- **Admin**: "How many active fire emergencies?"
- **General**: "Help me understand ticket statuses"

## Troubleshooting

### Common Issues
1. **"AI service not configured"**: Check GOOGLE_GEMINI_API_KEY in .env
2. **"User not found"**: Ensure user is authenticated and exists in database
3. **"Admin privileges required"**: User needs admin role for system-wide queries
4. **Empty responses**: Check API key validity and network connectivity

### Debug Information
- Check console logs for detailed error messages
- Use `/api/chatbot/health` endpoint to verify service status
- Verify user permissions with `/api/chatbot/capabilities/{userId}`

## Future Enhancements

### Planned Features
- **Voice Integration**: Speech-to-text and text-to-speech
- **Multi-language Support**: Localized responses
- **Learning Capabilities**: Improved responses based on usage patterns
- **Integration Expansion**: Weather data, traffic information, resource availability
- **Advanced Analytics**: Predictive insights and recommendations

---

**FireFighter Platform Development Team**  
*Emergency Response Management System*
