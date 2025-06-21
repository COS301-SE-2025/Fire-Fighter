# Ticket Database Setup Guide

## Overview

This guide will help you set up the `tickets` table in your PostgreSQL database for the FireFighter platform.

## 🗄️ Database Schema

The `tickets` table is located in the `firefighter` schema and contains the following structure:

### Table Structure
```sql
CREATE TABLE firefighter.tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    valid BOOLEAN DEFAULT false,
    created_by VARCHAR(255),
    last_verified_at TIMESTAMP,
    verification_count INTEGER DEFAULT 0,
    status VARCHAR(255) NOT NULL DEFAULT 'Active',
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request_date DATE NOT NULL DEFAULT CURRENT_DATE,
    user_id VARCHAR(255) NOT NULL,
    emergency_type VARCHAR(255) NOT NULL,
    emergency_contact VARCHAR(255) NOT NULL
);
```

### Column Descriptions
- **`id`**: Auto-incrementing primary key
- **`ticket_id`**: Unique identifier for the ticket (e.g., "JIRA-123")
- **`description`**: Text description of the ticket
- **`valid`**: Boolean flag indicating if the ticket is valid
- **`created_by`**: User who created the ticket
- **`last_verified_at`**: Timestamp of last verification
- **`verification_count`**: Number of times the ticket has been verified
- **`status`**: The current status of the ticket (e.g., 'Active', 'Completed').
- **`date_created`**: The timestamp when the ticket was created.
- **`request_date`**: The date the request was made.
- **`user_id`**: The ID of the user associated with the ticket.
- **`emergency_type`**: The type of emergency (e.g., 'critical-system-failure').
- **`emergency_contact`**: The contact information for the emergency.

## 🚀 Quick Setup

### Option 1: Using the SQL Script

1. **Connect to your PostgreSQL database**
   ```bash
   psql -h 100.83.111.92 -U ff_admin -d firefighter
   ```

2. **Run the setup script**
   ```bash
   \i create_tickets_table.sql
   ```

### Option 2: Manual Setup

1. **Connect to PostgreSQL**
   ```bash
   psql -h 100.83.111.92 -U ff_admin -d firefighter
   ```

2. **Create the table**
   ```sql
   CREATE TABLE IF NOT EXISTS firefighter.tickets (
       id BIGSERIAL PRIMARY KEY,
       ticket_id VARCHAR(255) NOT NULL UNIQUE,
       description TEXT,
       valid BOOLEAN DEFAULT false,
       created_by VARCHAR(255),
       last_verified_at TIMESTAMP,
       verification_count INTEGER DEFAULT 0,
       status VARCHAR(255) NOT NULL DEFAULT 'Active',
       date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       request_date DATE NOT NULL DEFAULT CURRENT_DATE,
       user_id VARCHAR(255) NOT NULL,
       emergency_type VARCHAR(255) NOT NULL,
       emergency_contact VARCHAR(255) NOT NULL
   );
   ```

3. **Create indexes for performance**
   ```sql
   CREATE INDEX IF NOT EXISTS idx_tickets_ticket_id ON firefighter.tickets(ticket_id);
   CREATE INDEX IF NOT EXISTS idx_tickets_valid ON firefighter.tickets(valid);
   CREATE INDEX IF NOT EXISTS idx_tickets_created_by ON firefighter.tickets(created_by);
   ```

4. **Insert sample data**
   ```sql
   INSERT INTO firefighter.tickets (ticket_id, description, valid, created_by, verification_count, status, date_created, request_date, user_id, emergency_type, emergency_contact) VALUES 
   ('SAMPLE-001', 'Sample ticket for testing', true, 'test-user', 0, 'Active', CURRENT_TIMESTAMP, CURRENT_DATE, 'user1', 'critical-system-failure', '12345'),
   ('SAMPLE-002', 'Another sample ticket', false, 'test-user', 0, 'Completed', CURRENT_TIMESTAMP, CURRENT_DATE, 'user2', 'network-outage', '67890'),
   ('SAMPLE-003', 'Valid ticket for verification', true, 'admin-user', 0, 'Active', CURRENT_TIMESTAMP, CURRENT_DATE, 'user3', 'security-incident', '11223')
   ON CONFLICT (ticket_id) DO NOTHING;
   ```

## 🔧 Verification

### Check if table exists
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'firefighter' 
AND table_name = 'tickets';
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
AND table_name = 'tickets'
ORDER BY ordinal_position;
```

### View sample data
```sql
SELECT * FROM firefighter.tickets;
```

## 🧪 Testing the Setup

### 1. Test API Endpoint
```bash
curl -X POST "http://localhost:8080/api/tickets" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": "TEST-001",
    "description": "Test ticket from API",
    "valid": true,
    "createdBy": "test-user",
    "userId": "user-test",
    "emergencyType": "test-emergency",
    "emergencyContact": "555-1234"
  }'
```

### 2. Expected Response
```json
{
    "id": 1,
    "ticketId": "TEST-001",
    "description": "Test ticket from API",
    "valid": true,
    "createdBy": "test-user",
    "lastVerifiedAt": null,
    "verificationCount": 0,
    "status": "Active",
    "dateCreated": "...",
    "requestDate": "...",
    "userId": "user-test",
    "emergencyType": "test-emergency",
    "emergencyContact": "555-1234"
}
```

## 🔍 Troubleshooting

### Common Issues

1. **"relation 'tickets' does not exist"**
   - Solution: Run the SQL script to create the table
   - Check if you're connected to the correct database and schema

2. **"schema 'firefighter' does not exist"**
   - Solution: Create the schema first:
   ```sql
   CREATE SCHEMA IF NOT EXISTS firefighter;
   ```

3. **Permission denied**
   - Solution: Ensure your database user has CREATE privileges:
   ```sql
   GRANT CREATE ON SCHEMA firefighter TO ff_admin;
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA firefighter TO ff_admin;
   ```

4. **Connection issues**
   - Check your database connection settings in `application.properties`
   - Verify the database server is running and accessible

### Debug Commands

```sql
-- Check current schema
SELECT current_schema();

-- List all schemas
SELECT schema_name FROM information_schema.schemata;

-- List all tables in firefighter schema
SELECT table_name FROM information_schema.tables WHERE table_schema = 'firefighter';

-- Check user permissions
SELECT grantee, privilege_type 
FROM information_schema.role_table_grants 
WHERE table_schema = 'firefighter' AND table_name = 'tickets';
```

## 📊 Performance Optimization

### Indexes
The setup script creates the following indexes for optimal performance:

1. **`idx_tickets_ticket_id`**: For fast ticket lookups by ID
2. **`idx_tickets_valid`**: For filtering valid/invalid tickets
3. **`idx_tickets_created_by`**: For user-based queries

### Additional Indexes (Optional)
```sql
-- For date-based queries
CREATE INDEX IF NOT EXISTS idx_tickets_last_verified_at ON firefighter.tickets(last_verified_at);

-- For composite queries
CREATE INDEX IF NOT EXISTS idx_tickets_valid_created_by ON firefighter.tickets(valid, created_by);
```

## 🔄 Hibernate Auto-Creation

If you prefer to let Hibernate create the table automatically:

1. **Set DDL mode to create**
   ```properties
   spring.jpa.hibernate.ddl-auto=create
   ```

2. **Restart the application**
   - Hibernate will create the table automatically
   - **Warning**: This will drop existing data

3. **Switch back to update mode**
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

## 📝 Notes

- The table uses the `firefighter` schema to match other entities
- All columns except `id` and `ticket_id` are nullable for flexibility
- The `ticket_id` column has a unique constraint to prevent duplicates
- Sample data is included for testing purposes
- Indexes are created for common query patterns

## 🆘 Support

If you encounter issues:

1. Check the application logs for detailed error messages
2. Verify database connectivity
3. Ensure proper permissions
4. Test with the provided SQL commands
5. Check the troubleshooting section above

---

**Last Updated**: June 2024  
**Database**: PostgreSQL  
**Schema**: firefighter  
**Table**: tickets 