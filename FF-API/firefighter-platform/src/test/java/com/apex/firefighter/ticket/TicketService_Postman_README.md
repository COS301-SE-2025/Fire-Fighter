# TicketService Postman Collection

## Overview

This Postman collection provides comprehensive testing for the FireFighter TicketService API. It includes all CRUD operations and error handling scenarios.

## 📁 Files

- **`TicketService_Postman_Collection.json`** - The Postman collection file
- **`TicketService_Postman_README.md`** - This documentation file

## 🚀 Quick Start

### 1. Import the Collection

1. Open Postman
2. Click **"Import"** button
3. Select **"Upload Files"**
4. Choose `TicketService_Postman_Collection.json`
5. Click **"Import"**

### 2. Set Up Environment Variables

The collection uses a variable for the base URL:

- **Variable Name**: `baseUrl`
- **Default Value**: `http://localhost:8080`
- **Current Value**: Set this to your application's URL

**To set the variable:**
1. Click on the collection name
2. Go to **"Variables"** tab
3. Set the **"Current Value"** for `baseUrl`

### 3. Start Your Application

Before running the tests, ensure your Spring Boot application is running:

```bash
cd firefighter-platform
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 📋 Collection Structure

### 1. CREATE Operations
- **Create New Ticket** - Creates a valid ticket
- **Create Duplicate Ticket (Error Test)** - Tests duplicate ticket handling

### 2. READ Operations
- **Get All Tickets** - Retrieves all tickets
- **Get Ticket by Database ID** - Retrieves by internal ID
- **Get Ticket by Ticket ID** - Retrieves by ticket ID
- **Get Non-existent Ticket (Error Test)** - Tests 404 handling

### 3. UPDATE Operations
- **Update Existing Ticket** - Updates multiple fields of a ticket by its database ID.
- **Update Non-existent Ticket (Error Test)** - Tests update error handling

### 4. DELETE Operations
- **Delete Ticket by Database ID** - Deletes by internal ID
- **Delete Ticket by Ticket ID** - Deletes by ticket ID
- **Delete Non-existent Ticket (Error Test)** - Tests delete error handling

### 5. Test Scenarios
- **Complete Ticket Lifecycle** - End-to-end testing guide
- **Bulk Operations Test** - Multiple operations testing guide

## 🧪 Testing Scenarios

### Scenario 1: Complete Ticket Lifecycle

1. **Create New Ticket**
   - Method: `POST`
   - URL: `{{baseUrl}}/api/tickets`
   - Body: 
   ```json
   {
       "ticketId": "JIRA-123",
       "description": "Test ticket for API testing",
       "userId": "user1",
       "emergencyType": "critical-system-failure",
       "emergencyContact": "12345"
   }
   ```

2. **Get All Tickets** (verify creation)
   - Method: `GET`
   - URL: `{{baseUrl}}/api/tickets`

3. **Update Existing Ticket**
   - Method: `PUT`
   - URL: `{{baseUrl}}/api/tickets/1`
   - Body:
   ```json
   {
       "description": "Updated ticket description",
       "status": "Completed",
       "emergencyType": "security-incident",
       "emergencyContact": "09876"
   }
   ```

4. **Delete Ticket**
   - Method: `DELETE`
   - URL: `{{baseUrl}}/api/tickets/ticket-id/JIRA-123`

5. **Get All Tickets** (verify deletion)
   - Method: `GET`
   - URL: `{{baseUrl}}/api/tickets`

### Scenario 2: Error Handling Tests

1. **Create Duplicate Ticket**
   - Should return error for duplicate ticketId

2. **Get Non-existent Ticket**
   - Should return 404 status

3. **Update Non-existent Ticket**
   - Should return 404 status

4. **Delete Non-existent Ticket**
   - Should return 404 status

## 📊 Expected Responses

### Successful Responses

#### Create Ticket (201)
```json
{
    "id": 1,
    "ticketId": "JIRA-123",
    "description": "Test ticket for API testing",
    "status": "Active",
    "dateCreated": "...",
    "requestDate": "...",
    "userId": "user1",
    "emergencyType": "critical-system-failure",
    "emergencyContact": "12345"
}
```

#### Get All Tickets (200)
```json
[
    {
        "id": 1,
        "ticketId": "JIRA-123",
        "description": "Test ticket",
        "status": "Active",
        "dateCreated": "...",
        "requestDate": "...",
        "userId": "user1",
        "emergencyType": "critical-system-failure",
        "emergencyContact": "12345"
    }
]
```

### Error Responses

#### Duplicate Ticket (500)
```json
{
    "error": "Ticket with ID 'JIRA-123' already exists"
}
```

#### Not Found (404)
```json
{
    "error": "Ticket not found"
}
```

## 🛠️ Customization

### Adding New Tests

1. **Duplicate an existing request**
2. **Modify the URL, method, or body**
3. **Update the description**
4. **Add to appropriate folder**

### Environment Variables

You can add more variables for different environments:

- `devUrl`: `http://localhost:8080`
- `testUrl`: `http://test-server:8080`
- `prodUrl`: `https://api.firefighter.com`

### Test Scripts

The collection includes automatic test scripts that validate:
- Status codes (200, 201, 404)
- Response time (< 2000ms)
- Required headers

## 🐞 Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure Spring Boot application is running
   - Check if port 8080 is available
   - Verify firewall settings

2. **404 Errors**
   - Check if the API endpoints are correct
   - Verify the base URL variable
   - Ensure the application context path is correct

3. **500 Errors**
   - Check for duplicate ticketId or missing required fields

## 📈 Performance Testing

### Load Testing with Postman

1. **Create a Runner**
   - Click "Runner" in Postman
   - Select the collection
   - Set iterations (e.g., 100)
   - Set delay between requests

2. **Monitor Performance**
   - Check response times
   - Monitor memory usage
   - Verify database performance

### Recommended Test Sequence

1. **Single Operations**
   - Test each endpoint individually
   - Verify correct responses

2. **Sequential Operations**
   - Run complete lifecycle tests
   - Test error scenarios

3. **Concurrent Operations**
   - Use Postman Runner for load testing
   - Monitor for race conditions

## 🔒 Security Considerations

### Testing Security

1. **Input Validation**
   - Test with malformed JSON
   - Test with SQL injection attempts
   - Test with XSS payloads

2. **Authentication**
   - Add authentication headers if required
   - Test unauthorized access

3. **Rate Limiting**
   - Test with rapid requests
   - Verify rate limiting behavior

## 📝 Notes

- **Database State**: Tests may affect database state
- **Test Data**: Use unique ticket IDs for each test run
- **Cleanup**: Consider database cleanup between test runs
- **Environment**: Use separate environments for dev/test/prod

## 🤝 Contributing

When adding new tests:

1. Follow the existing naming convention
2. Add appropriate descriptions
3. Include both success and error scenarios
4. Update this documentation
5. Test thoroughly before committing

---

**Last Updated**: June 2024  
**Postman Version**: 2.1.0  
**Spring Boot Version**: 3.x  
**Java Version**: 17+ 