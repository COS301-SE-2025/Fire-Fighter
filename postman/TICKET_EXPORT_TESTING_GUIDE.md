# Ticket Export with Date Range - Postman Testing Guide

## Overview
This guide provides comprehensive testing instructions for the enhanced ticket export endpoint that supports optional date range filtering.

## Setup Instructions

### 1. Import the Postman Collection
1. Open Postman
2. Click "Import" button
3. Select the file: `Ticket_Export_Date_Range_Tests.json`
4. The collection will be imported with all test cases

### 2. Configure Environment Variables
Before running tests, update these variables in the collection:

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `base_url` | Your server URL | `http://localhost:8080` |
| `admin_user_id` | Valid admin user Firebase UID | `admin123` |
| `admin_email` | Valid admin user email | `admin@firefighter.com` |
| `regular_user_id` | Non-admin user ID for error testing | `user456` |

### 3. Prerequisites
- Ensure your Spring Boot application is running
- Have at least one admin user in the database
- Have some test tickets with different creation dates
- Configure email settings (Gmail SMTP) for actual email testing

## Test Cases Overview

### ✅ Success Test Cases

#### 1. Export All Tickets (No Date Filter)
- **Purpose**: Test backward compatibility
- **Expected**: All tickets exported, no date filtering applied
- **Response**: 200 OK with success message

#### 2. Export Tickets with Start Date Only
- **Purpose**: Test filtering from a specific date onwards
- **Expected**: Only tickets created after startDate
- **Response**: 200 OK with "filtered by date range" message

#### 3. Export Tickets with End Date Only
- **Purpose**: Test filtering up to a specific date
- **Expected**: Only tickets created before endDate
- **Response**: 200 OK with "filtered by date range" message

#### 4. Export Tickets with Both Start and End Date
- **Purpose**: Test filtering within a specific date range
- **Expected**: Only tickets within the date range
- **Response**: 200 OK with "filtered by date range" message

#### 5. Export Using Email Instead of UserId
- **Purpose**: Test alternative user identification method
- **Expected**: Same functionality using email lookup
- **Response**: 200 OK with success message

### ❌ Error Test Cases

#### 6. Invalid Date Format
- **Purpose**: Test date validation
- **Input**: Invalid date strings
- **Expected**: 400 Bad Request with format error message

#### 7. Start Date After End Date
- **Purpose**: Test date range validation
- **Input**: startDate > endDate
- **Expected**: 400 Bad Request with range error message

#### 8. Non-Admin User
- **Purpose**: Test authorization
- **Input**: Regular user credentials
- **Expected**: 403 Forbidden with authorization error

#### 9. Missing User Identification
- **Purpose**: Test required parameters
- **Input**: No userId or email provided
- **Expected**: 400 Bad Request with missing parameter error

#### 10. Empty Date Strings
- **Purpose**: Test edge case handling
- **Input**: Empty string dates
- **Expected**: 200 OK, treated as no filtering

## Date Format Requirements

### Accepted Format
- **ISO 8601**: `yyyy-MM-ddTHH:mm:ss`
- **Examples**:
  - `2024-01-01T00:00:00`
  - `2024-12-31T23:59:59`
  - `2024-06-15T12:30:45`

### Invalid Formats (Will Cause Errors)
- `2024-01-01` (missing time)
- `01/01/2024` (wrong format)
- `2024-1-1T0:0:0` (missing leading zeros)
- `invalid-date` (non-date string)

## Sample Test Data

### Create Test Tickets with Different Dates
Before running tests, create tickets with various creation dates:

```sql
-- Recent tickets (should appear in most filters)
INSERT INTO firefighter.tickets (ticket_id, description, status, date_created, user_id, emergency_type, emergency_contact)
VALUES 
('TEST-2024-001', 'Recent ticket 1', 'Active', '2024-07-01 10:00:00', 'testuser', 'Fire', '911'),
('TEST-2024-002', 'Recent ticket 2', 'Completed', '2024-07-15 14:30:00', 'testuser', 'Medical', '911');

-- Older tickets (for date range testing)
INSERT INTO firefighter.tickets (ticket_id, description, status, date_created, user_id, emergency_type, emergency_contact)
VALUES 
('TEST-2023-001', 'Old ticket 1', 'Closed', '2023-12-01 09:00:00', 'testuser', 'Fire', '911'),
('TEST-2023-002', 'Old ticket 2', 'Rejected', '2023-06-15 16:45:00', 'testuser', 'Rescue', '911');
```

## Running the Tests

### Option 1: Run Individual Tests
1. Select a specific test case
2. Click "Send"
3. Verify the response matches expected results
4. Check the "Test Results" tab for automated assertions

### Option 2: Run All Tests
1. Right-click on the collection name
2. Select "Run collection"
3. Choose test execution order
4. Review the test results summary

### Option 3: Automated Testing
1. Use Postman's Collection Runner
2. Set up data files for different test scenarios
3. Schedule automated runs

## Verification Steps

### 1. Check Response Status Codes
- Success cases: `200 OK`
- Validation errors: `400 Bad Request`
- Authorization errors: `403 Forbidden`

### 2. Verify Response Messages
- Success: "Tickets exported and emailed successfully"
- Date filtering: Should include "filtered by date range"
- Errors: Should contain specific error descriptions

### 3. Check Email Delivery
- Verify CSV attachment is received
- Confirm CSV contains expected tickets
- Validate CSV format and data accuracy

### 4. Validate Date Filtering
- Compare ticket counts between filtered and unfiltered exports
- Manually verify tickets in CSV match date criteria
- Test edge cases (exact boundary dates)

## Troubleshooting

### Common Issues

#### 1. 403 Forbidden Error
- **Cause**: User is not admin or doesn't exist
- **Solution**: Verify admin user credentials and database records

#### 2. Email Not Received
- **Cause**: SMTP configuration issues
- **Solution**: Check Gmail app password and environment variables

#### 3. Date Parsing Errors
- **Cause**: Incorrect date format
- **Solution**: Use exact ISO format: `yyyy-MM-ddTHH:mm:ss`

#### 4. Empty CSV
- **Cause**: No tickets match date criteria
- **Solution**: Adjust date range or create test data

### Debug Information
The endpoint provides detailed console logging:
- Payload parsing
- Date validation
- User authentication
- Ticket filtering
- Email sending

Check server logs for detailed debugging information.

## Performance Considerations

### Large Dataset Testing
- Test with 1000+ tickets
- Verify response times stay reasonable
- Monitor memory usage during CSV generation
- Test email attachment size limits

### Concurrent Request Testing
- Multiple simultaneous export requests
- Rate limiting behavior
- Resource cleanup

## Security Testing

### Authentication Tests
- Invalid user IDs
- Non-existent emails
- SQL injection attempts in date parameters
- XSS attempts in date strings

### Authorization Tests
- Regular users attempting admin operations
- Expired or invalid sessions
- Cross-user data access attempts
