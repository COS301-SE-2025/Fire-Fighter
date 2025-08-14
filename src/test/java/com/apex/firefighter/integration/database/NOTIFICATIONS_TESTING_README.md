# Notification Service Testing Guide

## Overview

This guide provides comprehensive testing instructions for the FireFighter Notification Service, including unit tests, integration tests, and API testing with Postman.

## 🧪 Test Structure

### Test Files Location
```
FF-API/src/test/java/com/apex/firefighter/
├── service/
│   └── NotificationServiceTest.java          # Unit tests for business logic
├── controller/
│   └── NotificationControllerTest.java       # Integration tests for REST endpoints
├── repository/
│   └── NotificationRepositoryTest.java       # Database operation tests
└── notification/
    ├── create_notifications_table.sql        # Database setup script
    ├── NOTIFICATION_DATABASE_SETUP.md        # Database setup guide
    └── TESTING_README.md                     # This file
```

### Postman Collection
```
FF-API/src/test/postman/
└── FireFighter_Notification_API.postman_collection.json
```

### Test Scripts
```
FF-API/src/test/scripts/
└── run-notification-tests.sh                 # Automated test runner
```

## 🚀 Quick Start

### 1. Run All Tests (Automated)
```bash
cd FF-API
./src/test/scripts/run-notification-tests.sh
```

### 2. Run Individual Test Suites
```bash
# Unit tests only
mvn test -Dtest=NotificationServiceTest

# Controller tests only
mvn test -Dtest=NotificationControllerTest

# Repository tests only
mvn test -Dtest=NotificationRepositoryTest

# All notification tests
mvn test -Dtest="*Notification*"
```

## 📋 Test Coverage

### NotificationServiceTest.java
Tests the business logic layer:

- ✅ **createNotification()** - Creating notifications with and without ticket ID
- ✅ **getNotificationsForUser()** - Retrieving user-specific notifications
- ✅ **getUnreadNotificationsForUser()** - Filtering unread notifications
- ✅ **markNotificationAsRead()** - Marking individual notifications as read
- ✅ **markAllNotificationsAsRead()** - Bulk mark as read operation
- ✅ **deleteReadNotifications()** - Bulk delete read notifications
- ✅ **deleteNotification()** - Delete individual notifications
- ✅ **getNotificationStats()** - Notification statistics calculation
- ✅ **getNotificationForUser()** - Single notification retrieval
- ✅ **notificationExistsForUser()** - Existence checking
- ✅ **getNotificationsByType()** - Type-based filtering
- ✅ **cleanupOldReadNotifications()** - Cleanup operations

### NotificationControllerTest.java
Tests the REST API layer:

- ✅ **GET /api/notifications** - Get all notifications
- ✅ **GET /api/notifications/unread** - Get unread notifications
- ✅ **GET /api/notifications/stats** - Get notification statistics
- ✅ **GET /api/notifications/{id}** - Get specific notification
- ✅ **PUT /api/notifications/{id}/read** - Mark as read
- ✅ **PUT /api/notifications/read-all** - Mark all as read
- ✅ **DELETE /api/notifications/{id}** - Delete specific notification
- ✅ **DELETE /api/notifications/read** - Delete read notifications
- ✅ **Error handling** - 404, 500 error responses

### NotificationRepositoryTest.java
Tests the data access layer:

- ✅ **Custom query methods** - All repository custom queries
- ✅ **Database operations** - CRUD operations
- ✅ **User isolation** - Ensuring user-specific data access
- ✅ **Bulk operations** - Mark all as read, delete operations
- ✅ **Filtering** - By read status, type, ticket ID
- ✅ **Ordering** - Timestamp-based ordering
- ✅ **Cleanup operations** - Old notification cleanup

## 🔧 Test Configuration

### Test Properties
Create `src/test/resources/application-test.properties`:

```properties
# Test Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration for Tests
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Test Profile
spring.profiles.active=test
```

### Test Dependencies
Ensure these dependencies are in your `pom.xml`:

```xml
<dependencies>
    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 📡 API Testing with Postman

### 1. Import Collection
1. Open Postman
2. Click "Import"
3. Select `FF-API/src/test/postman/FireFighter_Notification_API.postman_collection.json`
4. Collection will be imported with all endpoints

### 2. Configure Environment Variables
Set these variables in Postman:

| Variable | Value | Description |
|----------|-------|-------------|
| `baseUrl` | `http://localhost:8080` | API base URL |
| `userId` | `test-user-123` | Test user ID |
| `notificationId` | `1` | Test notification ID |

### 3. Test Scenarios

#### Scenario 1: Basic CRUD Operations
1. **Get All Notifications** - Should return empty array initially
2. **Get Notification Statistics** - Should show 0 for all counts
3. **Create test data** (via database or ticket creation)
4. **Get All Notifications** - Should return created notifications
5. **Mark as Read** - Test individual mark as read
6. **Delete Notification** - Test individual deletion

#### Scenario 2: Bulk Operations
1. **Create multiple notifications** (via ticket operations)
2. **Mark All as Read** - Test bulk mark as read
3. **Get Unread Notifications** - Should return empty array
4. **Delete Read Notifications** - Test bulk deletion

#### Scenario 3: Error Handling
1. **Get Non-existent Notification** - Should return 404
2. **Mark Non-existent as Read** - Should return 404
3. **Delete Non-existent Notification** - Should return 404
4. **Invalid User ID** - Should return appropriate errors

### 4. Automated Testing
The Postman collection includes automated tests:

```javascript
// Example test from collection
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each notification has required fields", function () {
    const notifications = pm.response.json();
    if (notifications.length > 0) {
        const notification = notifications[0];
        pm.expect(notification).to.have.property('id');
        pm.expect(notification).to.have.property('userId');
        pm.expect(notification).to.have.property('type');
        pm.expect(notification).to.have.property('title');
        pm.expect(notification).to.have.property('message');
        pm.expect(notification).to.have.property('timestamp');
        pm.expect(notification).to.have.property('read');
    }
});
```

## 🗄️ Database Testing

### 1. Setup Test Database
```bash
# Option 1: Use H2 in-memory database (for unit tests)
# Already configured in test properties

# Option 2: Use PostgreSQL test database
docker run -d --name postgres-test \
  -e POSTGRES_DB=firefighter_test \
  -e POSTGRES_USER=ff_admin \
  -e POSTGRES_PASSWORD=test_password \
  -p 5433:5432 postgres:13
```

### 2. Create Test Schema
```bash
# Run the setup script on test database
psql -h localhost -p 5433 -U ff_admin -d firefighter_test \
  -f src/test/java/com/apex/firefighter/notification/create_notifications_table.sql
```

### 3. Verify Database Setup
```sql
-- Check table exists
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'firefighter' AND table_name = 'notifications';

-- Check indexes
SELECT indexname, indexdef FROM pg_indexes 
WHERE tablename = 'notifications';

-- Insert test data
INSERT INTO firefighter.notifications 
(user_id, type, title, message, ticket_id, read) VALUES 
('test-user-123', 'ticket_created', 'Test Notification', 'Test message', 'TEST-001', false);
```

## 📊 Test Reports

### 1. Generate Coverage Report
```bash
mvn jacoco:report
```
Report available at: `target/site/jacoco/index.html`

### 2. Surefire Test Reports
```bash
mvn surefire-report:report
```
Report available at: `target/site/surefire-report.html`

### 3. Integration Test Reports
```bash
mvn failsafe:report
```

## 🐛 Troubleshooting

### Common Test Issues

1. **Database Connection Errors**
   ```bash
   # Check if test database is running
   docker ps | grep postgres-test
   
   # Check connection
   psql -h localhost -p 5433 -U ff_admin -d firefighter_test -c "SELECT 1;"
   ```

2. **Test Data Conflicts**
   ```bash
   # Clean test database
   psql -h localhost -p 5433 -U ff_admin -d firefighter_test -c "TRUNCATE firefighter.notifications;"
   ```

3. **Port Conflicts**
   ```bash
   # Check if port 8080 is in use
   lsof -i :8080
   
   # Kill process if needed
   kill -9 <PID>
   ```

### Debug Test Failures

1. **Enable Debug Logging**
   ```properties
   # Add to application-test.properties
   logging.level.com.apex.firefighter=DEBUG
   logging.level.org.springframework.web=DEBUG
   ```

2. **Run Single Test with Debug**
   ```bash
   mvn test -Dtest=NotificationServiceTest#createNotification_ShouldCreateAndReturnNotification -X
   ```

3. **Check Test Output**
   ```bash
   # View detailed test output
   cat target/surefire-reports/TEST-*.xml
   ```

## ✅ Test Checklist

Before deploying, ensure all tests pass:

- [ ] All unit tests pass (`NotificationServiceTest`)
- [ ] All integration tests pass (`NotificationControllerTest`)
- [ ] All repository tests pass (`NotificationRepositoryTest`)
- [ ] Postman collection tests pass
- [ ] Database schema is correctly created
- [ ] Test coverage is above 80%
- [ ] No test data conflicts
- [ ] All error scenarios are tested
- [ ] Performance tests pass (if applicable)

## 📚 Additional Resources

- [Spring Boot Testing Documentation](https://spring.io/guides/gs/testing-web/)
- [Postman Testing Guide](https://learning.postman.com/docs/writing-scripts/test-scripts/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

**Happy Testing! 🚀**
