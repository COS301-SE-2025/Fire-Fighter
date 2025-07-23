# Notification Database Setup Guide

## Overview

This guide will help you set up the `notifications` table in your PostgreSQL database for the FireFighter platform notification system.

## 🗄️ Database Schema

The `notifications` table is located in the `firefighter` schema and contains the following structure:

### Table Structure
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

### Column Descriptions
- **`id`**: Auto-incrementing primary key
- **`user_id`**: Firebase UID of the user (foreign key to users table)
- **`type`**: Type of notification (e.g., 'ticket_created', 'request_completed', 'ticket_revoked')
- **`title`**: Short title/subject of the notification
- **`message`**: Full notification message content
- **`timestamp`**: When the notification was created (auto-set)
- **`read`**: Whether the user has read the notification (default: false)
- **`ticket_id`**: Optional reference to related ticket

### Notification Types
- `ticket_created`: When a new ticket is created
- `request_completed`: When a ticket/request is completed
- `request_approved`: When an access request is approved
- `action_taken`: When an action is taken on a ticket
- `new_request`: When a new request is available
- `ticket_revoked`: When a ticket is revoked/rejected

## 🚀 Quick Setup

### Option 1: Using the SQL Script

1. **Connect to your PostgreSQL database**
   ```bash
   psql -h 100.83.111.92 -U ff_admin -d firefighter
   ```

2. **Run the setup script**
   ```bash
   \i create_notifications_table.sql
   ```

### Option 2: Manual Setup

1. **Connect to PostgreSQL**
   ```bash
   psql -h 100.83.111.92 -U ff_admin -d firefighter
   ```

2. **Create the table**
   ```sql
   CREATE TABLE IF NOT EXISTS firefighter.notifications (
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

3. **Create indexes for performance**
   ```sql
   CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON firefighter.notifications(user_id);
   CREATE INDEX IF NOT EXISTS idx_notifications_timestamp ON firefighter.notifications(timestamp DESC);
   CREATE INDEX IF NOT EXISTS idx_notifications_read ON firefighter.notifications(read);
   CREATE INDEX IF NOT EXISTS idx_notifications_user_timestamp ON firefighter.notifications(user_id, timestamp DESC);
   CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON firefighter.notifications(user_id, read) WHERE read = FALSE;
   ```

## 🛠️ Verification

### Check if table exists
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'firefighter' 
AND table_name = 'notifications';
```

### View table structure
```sql
SELECT 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'firefighter' 
AND table_name = 'notifications'
ORDER BY ordinal_position;
```

### View sample data
```sql
SELECT * FROM firefighter.notifications ORDER BY timestamp DESC;
```

## 🧪 Testing the Setup

### 1. Test API Endpoint (after backend implementation)
```bash
curl -X GET "http://localhost:8080/api/notifications" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Expected Response
```json
[
  {
    "id": 1,
    "userId": "user1",
    "type": "ticket_created",
    "title": "New Ticket Created",
    "message": "A new ticket SAMPLE-001 has been created",
    "timestamp": "2024-01-01T10:00:00",
    "read": false,
    "ticketId": "SAMPLE-001"
  }
]
```

## 🔍 Common Queries

### Get unread notifications for a user
```sql
SELECT * FROM firefighter.notifications 
WHERE user_id = 'user1' AND read = false 
ORDER BY timestamp DESC;
```

### Mark notification as read
```sql
UPDATE firefighter.notifications 
SET read = true 
WHERE id = 1 AND user_id = 'user1';
```

### Delete read notifications for a user
```sql
DELETE FROM firefighter.notifications 
WHERE user_id = 'user1' AND read = true;
```

### Get notification counts by user
```sql
SELECT 
    user_id,
    COUNT(*) as total,
    COUNT(CASE WHEN read = false THEN 1 END) as unread
FROM firefighter.notifications 
GROUP BY user_id;
```

## 📈 Performance Optimization

### Indexes
The setup script creates the following indexes for optimal performance:

1. **`idx_notifications_user_id`**: For user-specific queries
2. **`idx_notifications_timestamp`**: For chronological ordering
3. **`idx_notifications_read`**: For filtering by read status
4. **`idx_notifications_user_timestamp`**: For user notifications ordered by time
5. **`idx_notifications_user_unread`**: For unread notifications per user

## 📝 Notes

- The table uses the `firefighter` schema to match other entities
- Foreign key constraint to users table can be added after users table exists
- Sample data is included for testing purposes
- Indexes are optimized for common notification queries
- The `timestamp` field uses PostgreSQL's TIMESTAMP type for consistency

## 🆘 Support

If you encounter issues:

1. Check the application logs for detailed error messages
2. Verify database connectivity
3. Ensure proper permissions
4. Test with the provided SQL commands
5. Check that the firefighter schema exists

---

**Last Updated**: January 2024  
**Database**: PostgreSQL  
**Schema**: firefighter  
**Table**: notifications
