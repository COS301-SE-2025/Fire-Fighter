import { Ticket } from './ticket.service';

// Initial mock tickets if none exist in storage
const initialMockTickets: Ticket[] = [
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
  const now = new Date();
  const requestDate = new Date(date);
  const diffInSeconds = Math.floor((now.getTime() - requestDate.getTime()) / 1000);

  if (diffInSeconds < 60) {
    return 'just now';
  }

  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) {
    return `${diffInMinutes} minute${diffInMinutes === 1 ? '' : 's'} ago`;
  }

  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) {
    return `${diffInHours} hour${diffInHours === 1 ? '' : 's'} ago`;
  }

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7) {
    return `${diffInDays} day${diffInDays === 1 ? '' : 's'} ago`;
  }

  return requestDate.toLocaleDateString();
};

export class MockTicketDatabase {
  private readonly STORAGE_KEY = 'mock_tickets';

  constructor() {
    // Initialize storage with mock data if empty
    if (!localStorage.getItem(this.STORAGE_KEY)) {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(initialMockTickets));
    }
  }

  private getTicketsFromStorage(): Ticket[] {
    const storedTickets = localStorage.getItem(this.STORAGE_KEY);
    if (!storedTickets) return [];
    return JSON.parse(storedTickets);
  }

  private saveTicketsToStorage(tickets: Ticket[]): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(tickets));
  }

  // Get all tickets
  getAllTickets(): Ticket[] {
    const tickets = this.getTicketsFromStorage();
    // Update timeAgo for all tickets
    return tickets.map(ticket => ({
      ...ticket,
      timeAgo: calculateTimeAgo(ticket.requestDate)
    }));
  }

  // Get ticket by ID
  getTicketById(id: string): Ticket | undefined {
    const tickets = this.getTicketsFromStorage();
    const ticket = tickets.find(ticket => ticket.id === id);
    if (!ticket) return undefined;
    
    return {
      ...ticket,
      timeAgo: calculateTimeAgo(ticket.requestDate)
    };
  }

  // Create new ticket
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'timeAgo'>): Ticket {
    const tickets = this.getTicketsFromStorage();
    const newTicket: Ticket = {
      id: generateId(),
      status: 'Pending',
      timeAgo: 'just now',
      ...ticketData
    };
    
    tickets.unshift(newTicket);
    this.saveTicketsToStorage(tickets);
    return { ...newTicket };
  }

  // Update ticket status
  updateTicketStatus(id: string, status: Ticket['status']): Ticket | undefined {
    const tickets = this.getTicketsFromStorage();
    const ticketIndex = tickets.findIndex(ticket => ticket.id === id);
    if (ticketIndex === -1) return undefined;

    const updatedTicket = {
      ...tickets[ticketIndex],
      status,
      timeAgo: calculateTimeAgo(tickets[ticketIndex].requestDate)
    };

    tickets[ticketIndex] = updatedTicket;
    this.saveTicketsToStorage(tickets);
    return { ...updatedTicket };
  }

  // Delete ticket (optional, if needed)
  deleteTicket(id: string): boolean {
    const tickets = this.getTicketsFromStorage();
    const ticketIndex = tickets.findIndex(ticket => ticket.id === id);
    if (ticketIndex === -1) return false;

    tickets.splice(ticketIndex, 1);
    this.saveTicketsToStorage(tickets);
    return true;
  }
}

// Create a singleton instance
export const mockTicketDb = new MockTicketDatabase(); 