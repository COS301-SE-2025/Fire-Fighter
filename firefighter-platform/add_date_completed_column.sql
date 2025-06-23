-- Add date_completed column to tickets table
-- This script adds a timestamp column to track when tickets reach their final state (completed or rejected)

ALTER TABLE firefighter.tickets 
ADD COLUMN date_completed TIMESTAMP;

-- Add comment to the column for documentation
COMMENT ON COLUMN firefighter.tickets.date_completed IS 'Timestamp when the ticket reached its final state (completed normally or rejected by admin)';

-- Verify the change
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_schema = 'firefighter' 
  AND table_name = 'tickets' 
  AND column_name = 'date_completed'; 