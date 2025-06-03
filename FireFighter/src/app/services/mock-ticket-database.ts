import { Ticket } from './ticket.service';

// Mock database with some initial tickets
const mockTickets: Ticket[] = [
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
    status: 'Approved',
    timeAgo: '1 day ago',
    reason: 'Database server maintenance',
    requestDate: '2024-03-19',
    userId: 'user123'
  },
  {
    id: 'TICK-003',
    status: 'Completed',
    timeAgo: '3 days ago',
    reason: 'Security patch deployment',
    requestDate: '2024-03-17',
    userId: 'user123'
  },
  {
    id: 'TICK-004',
    status: 'Rejected',
    timeAgo: '1 week ago',
    reason: 'Request for overtime',
    requestDate: '2024-03-13',
    userId: 'user123'
  }
];

// Helper function to generate a unique ID
const generateId = (): string => {
  const timestamp = Date.now().toString();
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
  return `TICK-${timestamp.slice(-4)}${random}`;
};

// Helper function to calculate time ago
const calculateTimeAgo = (date: string): string => {
  const requestDate = new Date(date);
  const now = new Date();
  const diffInHours = Math.floor((now.getTime() - requestDate.getTime()) / (1000 * 60 * 60));
  
  if (diffInHours < 24) {
    return `${diffInHours} hours ago`;
  } else if (diffInHours < 48) {
    return '1 day ago';
  } else {
    const diffInDays = Math.floor(diffInHours / 24);
    return `${diffInDays} days ago`;
  }
};

export class MockTicketDatabase {
  private tickets: Ticket[] = [...mockTickets];

  // Get all tickets
  getAllTickets(): Ticket[] {
    return [...this.tickets];
  }

  // Get ticket by ID
  getTicketById(id: string): Ticket | undefined {
    return this.tickets.find(ticket => ticket.id === id);
  }

  // Create new ticket
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'timeAgo'>): Ticket {
    const newTicket: Ticket = {
      id: generateId(),
      status: 'Pending',
      timeAgo: 'Just now',
      ...ticketData
    };
    
    this.tickets.unshift(newTicket);
    return { ...newTicket };
  }

  // Update ticket status
  updateTicketStatus(id: string, status: Ticket['status']): Ticket | undefined {
    const ticketIndex = this.tickets.findIndex(ticket => ticket.id === id);
    if (ticketIndex === -1) return undefined;

    const updatedTicket = {
      ...this.tickets[ticketIndex],
      status,
      timeAgo: calculateTimeAgo(this.tickets[ticketIndex].requestDate)
    };

    this.tickets[ticketIndex] = updatedTicket;
    return { ...updatedTicket };
  }
}

// Create a singleton instance
export const mockTicketDb = new MockTicketDatabase(); 