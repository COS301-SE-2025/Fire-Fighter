import express from 'express';
import cors from 'cors';
import { Ticket } from './ticket.service';

const app = express();
const port = 3001;

// Middleware
app.use(cors());
app.use(express.json());

// Temporary in-memory storage
let tickets: Ticket[] = [];

// Get all tickets
app.get('/api/tickets', (req, res) => {
  res.json(tickets);
});

// Create new ticket
app.post('/api/tickets', (req, res) => {
  const newTicket: Ticket = {
    id: `TICK-${Date.now()}`,
    status: 'Pending',
    timeAgo: 'Just now',
    ...req.body
  };
  tickets.unshift(newTicket);
  res.status(201).json(newTicket);
});

// Start the server
app.listen(port, () => {
  console.log(`Mock ticket server running at http://localhost:${port}`);
}); 