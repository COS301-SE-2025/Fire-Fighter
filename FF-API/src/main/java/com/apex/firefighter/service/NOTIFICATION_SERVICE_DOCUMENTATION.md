# FireFighter Notification Service Documentation

## Overview

The FireFighter Notification Service provides a comprehensive notification system that allows users to receive, manage, and interact with notifications related to ticket operations and system events. The service is built using Spring Boot and provides RESTful API endpoints for notification management.

## Features

- **User-specific notifications** with Firebase UID integration
- **Real-time notification creation** for ticket lifecycle events
- **Mark as read functionality** for individual and bulk operations
- **Delete functionality** for notification cleanup
- **Database persistence** with PostgreSQL
- **RESTful API** with comprehensive Swagger documentation
- **Comprehensive testing** with unit and integration tests

## Architecture

### Components

1. **Notification Entity** (`Notification.java`) - JPA entity representing notifications
2. **Notification Repository** (`NotificationRepository.java`) - Data access layer with custom queries
3. **Notification Service** (`NotificationService.java`) - Business logic layer
4. **Notification Controller** (`NotificationController.java`) - REST API endpoints

### Database Schema

```sql
CREATE TABLE firefighter.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    ticket_id VARCHAR(255)
);
```

## Notification Types

The system supports the following notification types:

- `ticket_created` - When a new ticket is created
- `request_completed` - When a ticket/request is completed
- `ticket_revoked` - When a ticket is revoked/rejected by an admin
- `request_approved` - When an access request is approved
- `action_taken` - When an action is taken on a ticket
- `new_request` - When a new request is available

## API Endpoints

### Base URL
```
http://localhost:8080/api/notifications
```

### 1. Get All Notifications

**Endpoint:** `GET /api/notifications`

**Description:** Retrieve all notifications for a specific user, ordered by timestamp descending.

**Parameters:**
- `userId` (required) - Firebase UID of the user

**Response:**
```json
[
  {
    "id": 1,
    "userId": "user123",
    "type": "ticket_created",
    "title": "New Ticket Created",
    "message": "A new ticket TICKET-001 has been created",
    "timestamp": "2024-01-01T10:00:00",
    "read": false,
    "ticketId": "TICKET-001"
  }
]
```

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/notifications?userId=user123"
```

### 2. Get Unread Notifications

**Endpoint:** `GET /api/notifications/unread`

**Description:** Retrieve only unread notifications for a specific user.

**Parameters:**
- `userId` (required) - Firebase UID of the user

**Response:** Same as above, but only unread notifications

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/notifications/unread?userId=user123"
```

### 3. Get Notification Statistics

**Endpoint:** `GET /api/notifications/stats`

**Description:** Get notification counts (total, unread, read) for a user.

**Parameters:**
- `userId` (required) - Firebase UID of the user

**Response:**
```json
{
  "total": 10,
  "unread": 3,
  "read": 7
}
```

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/notifications/stats?userId=user123"
```

### 4. Get Specific Notification

**Endpoint:** `GET /api/notifications/{notificationId}`

**Description:** Retrieve a specific notification by ID for a user.

**Parameters:**
- `notificationId` (path) - ID of the notification
- `userId` (query, required) - Firebase UID of the user

**Response:** Single notification object or 404 if not found

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/notifications/1?userId=user123"
```

### 5. Mark Notification as Read

**Endpoint:** `PUT /api/notifications/{notificationId}/read`

**Description:** Mark a specific notification as read.

**Parameters:**
- `notificationId` (path) - ID of the notification
- `userId` (query, required) - Firebase UID of the user

**Response:**
```json
{
  "success": true,
  "message": "Notification marked as read"
}
```

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/api/notifications/1/read?userId=user123"
```

### 6. Mark All Notifications as Read

**Endpoint:** `PUT /api/notifications/read-all`

**Description:** Mark all unread notifications as read for a user.

**Parameters:**
- `userId` (query, required) - Firebase UID of the user

**Response:**
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "updatedCount": 3
}
```

**Example Request:**
```bash
curl -X PUT "http://localhost:8080/api/notifications/read-all?userId=user123"
```

### 7. Delete Specific Notification

**Endpoint:** `DELETE /api/notifications/{notificationId}`

**Description:** Delete a specific notification.

**Parameters:**
- `notificationId` (path) - ID of the notification
- `userId` (query, required) - Firebase UID of the user

**Response:**
```json
{
  "success": true,
  "message": "Notification deleted successfully"
}
```

**Example Request:**
```bash
curl -X DELETE "http://localhost:8080/api/notifications/1?userId=user123"
```

### 8. Delete All Read Notifications

**Endpoint:** `DELETE /api/notifications/read`

**Description:** Delete all read notifications for a user.

**Parameters:**
- `userId` (query, required) - Firebase UID of the user

**Response:**
```json
{
  "success": true,
  "message": "Read notifications deleted successfully",
  "deletedCount": 5
}
```

**Example Request:**
```bash
curl -X DELETE "http://localhost:8080/api/notifications/read?userId=user123"
```

## Error Responses

All endpoints return appropriate HTTP status codes:

- `200 OK` - Successful operation
- `404 Not Found` - Notification not found or doesn't belong to user
- `500 Internal Server Error` - Server error

Error response format:
```json
{
  "success": false,
  "message": "Error description"
}
```

## Integration with Ticket System

The notification service is automatically integrated with the ticket system:

### Automatic Notification Creation

1. **Ticket Creation** - When `TicketService.createTicket()` is called
2. **Ticket Completion** - When `TicketScheduledService.closeExpiredTickets()` runs
3. **Ticket Revocation** - When `TicketService.revokeTicket()` is called

### Example Integration Code

```java
// In TicketService
public Ticket createTicket(...) {
    Ticket savedTicket = ticketRepository.save(ticket);
    
    // Create notification
    notificationService.createNotification(
        userId,
        "ticket_created",
        "New Ticket Created",
        "A new ticket " + ticketId + " has been created",
        ticketId
    );
    
    return savedTicket;
}
```

## Testing

### Running Tests

```bash
# Run all notification tests
mvn test -Dtest="*Notification*"

# Run specific test classes
mvn test -Dtest="NotificationServiceTest"
mvn test -Dtest="NotificationControllerTest"
mvn test -Dtest="NotificationRepositoryTest"
```

### Postman Collection

Import the Postman collection from:
```
FF-API/src/test/postman/FireFighter_Notification_API.postman_collection.json
```

The collection includes:
- All API endpoints with sample requests
- Automated tests for response validation
- Environment variables for easy configuration
- Pre-configured test data

### Test Coverage

- **Unit Tests** - Service layer business logic
- **Integration Tests** - Controller endpoints and HTTP responses
- **Repository Tests** - Database operations and custom queries
- **Postman Tests** - End-to-end API testing

## Configuration

### Database Configuration

Ensure your `application.properties` includes:

```properties
# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Environment Variables

Required environment variables:
- `DB_HOST` - Database host
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## Performance Considerations

### Database Indexes

The notification table includes optimized indexes:

```sql
-- User-specific queries
CREATE INDEX idx_notifications_user_id ON firefighter.notifications(user_id);

-- Timestamp ordering
CREATE INDEX idx_notifications_timestamp ON firefighter.notifications(timestamp DESC);

-- Read status filtering
CREATE INDEX idx_notifications_read ON firefighter.notifications(read);

-- Composite indexes for common queries
CREATE INDEX idx_notifications_user_timestamp ON firefighter.notifications(user_id, timestamp DESC);
CREATE INDEX idx_notifications_user_unread ON firefighter.notifications(user_id, read) WHERE read = FALSE;
```

### Cleanup Strategy

Use the cleanup method to remove old read notifications:

```java
// Delete read notifications older than 30 days
notificationService.cleanupOldReadNotifications(30);
```

## Security

- All endpoints require a valid `userId` parameter
- Notifications are user-scoped - users can only access their own notifications
- Database queries include user ID filtering to prevent unauthorized access
- Input validation is performed on all parameters

## Monitoring and Logging

The service includes comprehensive logging:

```java
System.out.println("🔔 NOTIFICATION CREATED: " + savedNotification);
System.out.println("✅ NOTIFICATION MARKED AS READ: " + notificationId);
System.out.println("🗑️ DELETED " + deletedRows + " READ NOTIFICATIONS");
```

Monitor these logs for:
- Notification creation patterns
- User engagement with notifications
- Error rates and performance issues

## Future Enhancements

Potential improvements:
- Real-time notifications with WebSocket support
- Email/SMS notification delivery
- Notification templates and customization
- Bulk operations for admin users
- Notification scheduling and delayed delivery
- Push notifications for mobile apps

## Usage Examples

### Frontend Integration (Angular)

```typescript
// notification.service.ts
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  getNotifications(): Observable<Notification[]> {
    const userId = this.authService.getCurrentUser()?.uid;
    return this.http.get<Notification[]>(`${this.apiUrl}?userId=${userId}`);
  }

  markAsRead(notificationId: number): void {
    const userId = this.authService.getCurrentUser()?.uid;
    this.http.put(`${this.apiUrl}/${notificationId}/read?userId=${userId}`, {})
      .subscribe(() => this.refreshNotifications());
  }

  deleteReadNotifications(): Observable<any> {
    const userId = this.authService.getCurrentUser()?.uid;
    return this.http.delete(`${this.apiUrl}/read?userId=${userId}`);
  }
}
```

### Backend Service Usage

```java
// Creating notifications programmatically
@Autowired
private NotificationService notificationService;

// Create a notification
Notification notification = notificationService.createNotification(
    "user123",
    "ticket_created",
    "New Ticket Created",
    "Your emergency ticket has been created successfully",
    "TICKET-001"
);

// Get user statistics
NotificationService.NotificationStats stats =
    notificationService.getNotificationStats("user123");
System.out.println("User has " + stats.getUnread() + " unread notifications");
```

## Testing Guide

### 1. Setup Test Environment

```bash
# Start PostgreSQL database
docker run -d --name postgres-test \
  -e POSTGRES_DB=firefighter \
  -e POSTGRES_USER=ff_admin \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 postgres:13

# Run the SQL setup script
psql -h localhost -U ff_admin -d firefighter -f create_notifications_table.sql
```

### 2. Postman Testing

1. **Import Collection**: Import `FireFighter_Notification_API.postman_collection.json`
2. **Set Variables**:
   - `baseUrl`: `http://localhost:8080`
   - `userId`: Your test user ID
   - `notificationId`: ID of a test notification
3. **Run Collection**: Execute all requests to test the complete API

### 3. Unit Testing

```bash
# Run notification service tests
mvn test -Dtest=NotificationServiceTest

# Run controller tests
mvn test -Dtest=NotificationControllerTest

# Run repository tests
mvn test -Dtest=NotificationRepositoryTest
```

### 4. Integration Testing

```bash
# Run all notification-related tests
mvn test -Dtest="*Notification*"

# Generate test coverage report
mvn jacoco:report
```

## Troubleshooting

### Common Issues

1. **404 Not Found Errors**
   - Verify the notification belongs to the specified user
   - Check that the notification ID exists in the database
   - Ensure the user ID is correct

2. **Database Connection Issues**
   - Verify database credentials in environment variables
   - Check that the `firefighter` schema exists
   - Ensure the notifications table has been created

3. **Performance Issues**
   - Monitor database query performance
   - Check that indexes are properly created
   - Consider implementing pagination for large notification lists

### Debug Commands

```sql
-- Check notification counts by user
SELECT user_id, COUNT(*) as total,
       COUNT(CASE WHEN read = false THEN 1 END) as unread
FROM firefighter.notifications
GROUP BY user_id;

-- Find notifications for specific user
SELECT * FROM firefighter.notifications
WHERE user_id = 'your-user-id'
ORDER BY timestamp DESC;

-- Check database indexes
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'notifications';
```

## Support

For issues or questions:
1. Check the test cases for usage examples
2. Review the Swagger documentation at `/swagger-ui.html`
3. Examine the Postman collection for API testing
4. Check application logs for detailed error information
5. Review the troubleshooting section above
6. Consult the database setup documentation in `NOTIFICATION_DATABASE_SETUP.md`
