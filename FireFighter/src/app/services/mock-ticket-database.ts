const express = require('express');
const cors = require('cors');
import { Request, Response } from 'express';

const app = express();
const port = 3001;

//Middleware
app.use(cors());
app.use(express.json());

// Mock database with initial tickets
let tickets = [
  {
    id: 'TICK-001',
    status: 'Pending',
    timeAgo: '2 hours ago',
    reason: 'Request for annual leave',
    requestDate: '2024-03-20',
    userId: 'user123'
  },
  {
    id: 'TICK-002',
    status: 'Active',
    timeAgo: '1 day ago',
    reason: 'Database server maintenance',
    requestDate: '2024-03-19',
    userId: 'user123'
  }
];

// Helper function to generate a unique ID
const generateId = () => {
  const timestamp = Date.now().toString();
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
  return `TICK-${timestamp.slice(-4)}${random}`;
};

// Get all tickets
app.get('/api/tickets', (req: Request, res: Response) => {
  res.json(tickets);
});

// Get ticket by ID
app.get('/api/tickets/:id', (req: Request, res: Response) => {
  const ticket = tickets.find(t => t['id'] === req.params.id);
  if (!ticket) {
    return res.status(404).json({ message: 'Ticket not found' });
  }
  res.json(ticket);
});



app.listen(port, () => {
  console.log(`Mock ticket database running on port ${port}`);
});

export {};