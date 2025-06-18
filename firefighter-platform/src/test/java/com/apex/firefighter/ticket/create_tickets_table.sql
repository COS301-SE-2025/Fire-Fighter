-- Create tickets table in firefighter schema
-- Run this script in your PostgreSQL database

-- Create the tickets table
CREATE TABLE IF NOT EXISTS firefighter.tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    valid BOOLEAN DEFAULT false,
    created_by VARCHAR(255),
    last_verified_at TIMESTAMP,
    verification_count INTEGER DEFAULT 0
);

-- Create index on ticket_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_tickets_ticket_id ON firefighter.tickets(ticket_id);

-- Create index on valid status for filtering
CREATE INDEX IF NOT EXISTS idx_tickets_valid ON firefighter.tickets(valid);

-- Create index on created_by for user-based queries
CREATE INDEX IF NOT EXISTS idx_tickets_created_by ON firefighter.tickets(created_by);

-- Insert some sample data for testing
INSERT INTO firefighter.tickets (ticket_id, description, valid, created_by, verification_count) VALUES 
('SAMPLE-001', 'Sample ticket for testing', true, 'test-user', 0),
('SAMPLE-002', 'Another sample ticket', false, 'test-user', 0),
('SAMPLE-003', 'Valid ticket for verification', true, 'admin-user', 0)
ON CONFLICT (ticket_id) DO NOTHING;

-- Verify the table was created
SELECT 
    table_name, 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'firefighter' 
AND table_name = 'tickets'
ORDER BY ordinal_position;

-- Show sample data
SELECT * FROM firefighter.tickets; 