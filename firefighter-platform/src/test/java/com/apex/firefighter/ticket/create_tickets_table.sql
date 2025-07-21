-- Create tickets table in firefighter schema
-- Run this script in your PostgreSQL database

-- Create the tickets table
CREATE TABLE IF NOT EXISTS firefighter.tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'Active',
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request_date DATE NOT NULL DEFAULT CURRENT_DATE,
    user_id VARCHAR(255) NOT NULL,
    emergency_type VARCHAR(255) NOT NULL,
    emergency_contact VARCHAR(255) NOT NULL,
    duration INTEGER
);

-- Create index on ticket_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_tickets_ticket_id ON firefighter.tickets(ticket_id);

-- Insert some sample data for testing
INSERT INTO firefighter.tickets (ticket_id, description, status, date_created, request_date, user_id, emergency_type, emergency_contact) VALUES 
('SAMPLE-001', 'Sample ticket for testing', 'Active', CURRENT_TIMESTAMP, CURRENT_DATE, 'user1', 'critical-system-failure', '12345'),
('SAMPLE-002', 'Another sample ticket', 'Completed', CURRENT_TIMESTAMP, CURRENT_DATE, 'user2', 'network-outage', '67890'),
('SAMPLE-003', 'Valid ticket for verification', 'Active', CURRENT_TIMESTAMP, CURRENT_DATE, 'user3', 'security-incident', '11223')
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