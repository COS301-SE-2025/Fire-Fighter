# Automatic Ticket Closure Guide

This guide explains how to configure and use the automatic ticket closure functionality in the Fire Fighter platform.

## Overview

The automatic ticket closure feature monitors active tickets and automatically closes them when their duration expires. This ensures that tickets don't remain open indefinitely and helps maintain system hygiene.

## How It Works

1. **Scheduled Task**: A background task runs every 5 minutes to check for expired tickets
2. **Duration Check**: The system calculates if `dateCreated + duration` is past the current time
3. **Automatic Closure**: Expired tickets are automatically updated with:
   - Status changed to "Closed"
   - dateCompleted set to the current timestamp

## Prerequisites

### Database Setup

1. **Run the migration script** to ensure your database has the required columns:
   ```bash
   psql -h your_host -d your_database -U your_user -f database_migration_for_auto_close.sql
   ```

2. **Required columns** in the `tickets` table:
   - `duration` (INTEGER) - Duration in minutes
   - `date_completed` (TIMESTAMP) - When the ticket was completed
   - `status` (VARCHAR) - Current ticket status
   - `date_created` (TIMESTAMP) - When the ticket was created

### Application Configuration

The automatic closure feature is enabled by default once you:

1. **Add @EnableScheduling** to your main application class (already done)
2. **Deploy the TicketScheduledService** (already implemented)

## Configuration Options

### Scheduling Frequency

The default schedule runs every 5 minutes. You can modify this in `TicketScheduledService.java`:

```java
// Current: Every 5 minutes
@Scheduled(cron = "0 */5 * * * *")

// Alternative options:
@Scheduled(cron = "0 * * * * *")     // Every minute
@Scheduled(cron = "0 0 * * * *")     // Every hour
@Scheduled(cron = "0 0 0 * * *")     // Every day at midnight
@Scheduled(cron = "0 0 */6 * * *")   // Every 6 hours
```

### Fixed Rate Alternative

Instead of cron expressions, you can use fixed rate (in milliseconds):

```java
@Scheduled(fixedRate = 300000) // 5 minutes = 300,000 ms
```

## Creating Tickets with Duration

When creating tickets, set the duration field (in minutes):

```java
// Example: Create a ticket that expires in 2 hours (120 minutes)
Ticket ticket = new Ticket("TICKET-001", "Emergency response", 
                          "user123", "fire", "911", 120);
```

### API Example

```bash
POST /api/tickets
Content-Type: application/json

{
    "ticketId": "EMERGENCY-001",
    "description": "Building fire emergency",
    "userId": "firefighter123",
    "emergencyType": "fire",
    "emergencyContact": "911",
    "duration": 180
}
```

## Monitoring

### Log Output

The scheduled task produces console output:

```
Checking for expired tickets at 2024-01-15T14:30:00
Closed expired ticket: TICKET-001
Closed 1 expired tickets
```

### Database Queries

Check tickets that will expire soon:
```sql
SELECT ticket_id, date_created, duration,
       date_created + INTERVAL '1 minute' * duration AS expires_at
FROM firefighter.tickets 
WHERE status = 'Active' 
  AND duration IS NOT NULL
  AND date_created + INTERVAL '1 minute' * duration < NOW() + INTERVAL '1 hour';
```

Check recently auto-closed tickets:
```sql
SELECT ticket_id, date_created, date_completed, duration
FROM firefighter.tickets 
WHERE status = 'Closed' 
  AND date_completed IS NOT NULL
  AND date_completed > NOW() - INTERVAL '1 day';
```

## Troubleshooting

### Common Issues

1. **Tickets not closing automatically**
   - Check that `@EnableScheduling` is present in the main application class
   - Verify the `TicketScheduledService` is being instantiated as a Spring bean
   - Check application logs for error messages

2. **Database errors**
   - Run the migration script: `database_migration_for_auto_close.sql`
   - Verify database connectivity and permissions

3. **Scheduling not working**
   - Check Spring Boot version compatibility
   - Ensure the application context is properly configured

### Debug Mode

To see more detailed logging, add this to your `application.properties`:

```properties
logging.level.com.apex.firefighter.service.ticket=DEBUG
```

## Testing

### Manual Testing

1. Create a ticket with a short duration (e.g., 1 minute)
2. Wait for the next scheduled run (up to 5 minutes)
3. Check that the ticket status changed to "Closed"

### Test Data

```sql
-- Insert a test ticket that expires in 1 minute
INSERT INTO firefighter.tickets 
(ticket_id, description, status, date_created, user_id, emergency_type, emergency_contact, duration)
VALUES 
('TEST-EXPIRE-001', 'Test ticket for auto-closure', 'Active', 
 NOW() - INTERVAL '2 minutes', 'testuser', 'test', '911', 1);
```

## Best Practices

1. **Set appropriate durations** based on emergency type:
   - Critical emergencies: 30-60 minutes
   - Standard incidents: 2-4 hours
   - Non-urgent tasks: 8-24 hours

2. **Monitor system performance** - very frequent scheduling may impact performance

3. **Consider time zones** when setting cron expressions

4. **Backup before migrations** - always backup your database before running migration scripts

5. **Test in staging** - verify the feature works correctly before production deployment

## Status Values

The system recognizes these ticket statuses:
- `Active` - Ticket is open and being processed
- `Closed` - Ticket automatically closed due to expiration
- `Completed` - Ticket manually completed by user
- `Rejected` - Ticket rejected by admin

Only tickets with status `Active` and a non-null `duration` will be automatically closed. 