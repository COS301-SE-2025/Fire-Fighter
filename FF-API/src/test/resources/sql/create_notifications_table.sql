-- Create notifications table in firefighter schema
-- Run this script in your PostgreSQL database

-- Create the notifications table
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

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON firefighter.notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_timestamp ON firefighter.notifications(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON firefighter.notifications(read);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON firefighter.notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_ticket_id ON firefighter.notifications(ticket_id);

-- Create composite index for common queries (user notifications ordered by timestamp)
CREATE INDEX IF NOT EXISTS idx_notifications_user_timestamp ON firefighter.notifications(user_id, timestamp DESC);

-- Create composite index for unread notifications per user
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON firefighter.notifications(user_id, read) WHERE read = FALSE;

-- Add foreign key constraint to users table (if users table exists)
-- Note: This will only work if the users table already exists
-- ALTER TABLE firefighter.notifications 
-- ADD CONSTRAINT fk_notifications_user_id 
-- FOREIGN KEY (user_id) REFERENCES firefighter.users(user_id) 
-- ON DELETE CASCADE;

-- Insert sample notifications for testing
INSERT INTO firefighter.notifications (user_id, type, title, message, ticket_id, read) VALUES 
('user1', 'ticket_created', 'New Ticket Created', 'A new ticket SAMPLE-001 has been created', 'SAMPLE-001', false),
('user1', 'request_completed', 'Request Completed', 'Your request SAMPLE-001 has been completed automatically', 'SAMPLE-001', false),
('user2', 'ticket_created', 'New Ticket Created', 'A new ticket SAMPLE-002 has been created', 'SAMPLE-002', true),
('user2', 'ticket_revoked', 'Ticket Revoked', 'Your ticket SAMPLE-002 has been revoked by an administrator', 'SAMPLE-002', false),
('user3', 'ticket_created', 'New Ticket Created', 'A new ticket SAMPLE-003 has been created', 'SAMPLE-003', false),
('user3', 'request_approved', 'Request Approved', 'Your access request has been approved', NULL, true),
('user1', 'action_taken', 'Action Required', 'Please review your pending requests', NULL, false),
('user2', 'new_request', 'New Request Available', 'A new emergency request is available in your area', NULL, false)
ON CONFLICT DO NOTHING;

-- Verify the table was created
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'firefighter' 
AND table_name = 'notifications'
ORDER BY ordinal_position;

-- Show sample data
SELECT 
    id,
    user_id,
    type,
    title,
    LEFT(message, 50) || '...' as message_preview,
    timestamp,
    read,
    ticket_id
FROM firefighter.notifications 
ORDER BY timestamp DESC;

-- Show notification counts by user
SELECT 
    user_id,
    COUNT(*) as total_notifications,
    COUNT(CASE WHEN read = false THEN 1 END) as unread_notifications,
    COUNT(CASE WHEN read = true THEN 1 END) as read_notifications
FROM firefighter.notifications 
GROUP BY user_id
ORDER BY user_id;

-- Show notification counts by type
SELECT 
    type,
    COUNT(*) as count
FROM firefighter.notifications 
GROUP BY type
ORDER BY count DESC;
