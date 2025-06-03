import { Ticket } from './ticket.service';
import { Subject } from 'rxjs';

const STORAGE_KEY = 'mock_tickets';
const AUTO_COMPLETE_DELAY = 15000; 

// Initial mock tickets that will be loaded into localStorage on first run
const initialMockTickets: Ticket[] = [
  {
    id: 'TICK-001',
    status: 'Pending',
    dateCreated: new Date('2024-03-20T10:00:00'),
    reason: 'Request for annual leave',
    requestDate: '2024-03-20',
    userId: 'user123'
  },
  {
    id: 'TICK-002',
    status: 'Active',
    dateCreated: new Date('2024-03-19T15:30:00'),
    reason: 'Database server maintenance',
    requestDate: '2024-03-19',
    userId: 'user123'
  },
  {
    id: 'TICK-003',
    status: 'Completed',
    dateCreated: new Date('2024-03-17T09:15:00'),
    reason: 'Security patch deployment',
    requestDate: '2024-03-17',
    userId: 'user123'
  },
  {
    id: 'TICK-004',
    status: 'Rejected',
    dateCreated: new Date('2024-03-13T14:45:00'),
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

// Helper function to calculate time ago from a Date
export const calculateTimeAgo = (date: Date): string => {
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
  
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

  return date.toLocaleDateString();
};

// Helper function to load tickets from storage
const loadFromStorage = (): Ticket[] => {
  const stored = localStorage.getItem(STORAGE_KEY);
  if (!stored) {
    // If no stored data, save initial tickets
    localStorage.setItem(STORAGE_KEY, JSON.stringify(initialMockTickets));
    return initialMockTickets;
  }
  
  // Parse stored tickets and convert dateCreated strings back to Date objects
  const parsedTickets = JSON.parse(stored);
  return parsedTickets.map((ticket: any) => ({
    ...ticket,
    dateCreated: new Date(ticket.dateCreated)
  }));
};

// Helper function to save tickets to storage
const saveToStorage = (tickets: Ticket[]): void => {
  // Convert tickets to a format that can be safely stored in localStorage
  const ticketsToStore = tickets.map(ticket => ({
    ...ticket,
    dateCreated: ticket.dateCreated.toISOString() // Store as ISO string
  }));
  localStorage.setItem(STORAGE_KEY, JSON.stringify(ticketsToStore));
};

export class MockTicketDatabase {
  private tickets: Ticket[];
  private statusUpdateSubject = new Subject<Ticket>();
  public onStatusUpdate$ = this.statusUpdateSubject.asObservable();

  constructor() {
    this.tickets = loadFromStorage();
  }

  // Get all tickets
  getAllTickets(): Ticket[] {
    return [...this.tickets];
  }

  // Get ticket by ID
  getTicketById(id: string): Ticket | undefined {
    return this.tickets.find(ticket => ticket.id === id);
  }

  // Create new ticket
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'dateCreated'>): Ticket {
    const newTicket: Ticket = {
      id: generateId(),
      status: 'Pending',
      dateCreated: new Date(),
      ...ticketData
    };
    
    this.tickets.unshift(newTicket);
    saveToStorage(this.tickets);

    // Schedule auto-completion after 60 seconds
    setTimeout(() => {
      const ticketIndex = this.tickets.findIndex(t => t.id === newTicket.id);
      if (ticketIndex !== -1 && this.tickets[ticketIndex].status === 'Pending') {
        this.updateTicketStatus(newTicket.id, 'Completed');
      }
    }, AUTO_COMPLETE_DELAY);

    return { ...newTicket };
  }

  // Update ticket status
  updateTicketStatus(id: string, status: Ticket['status']): Ticket | undefined {
    const ticketIndex = this.tickets.findIndex(ticket => ticket.id === id);
    if (ticketIndex === -1) return undefined;

    const updatedTicket = {
      ...this.tickets[ticketIndex],
      status
    };

    this.tickets[ticketIndex] = updatedTicket;
    saveToStorage(this.tickets);
    
    // Notify subscribers of the status update
    this.statusUpdateSubject.next({ ...updatedTicket });
    
    return { ...updatedTicket };
  }

  // Reset to initial tickets (useful for testing)
  resetToInitial(): void {
    this.tickets = [...initialMockTickets];
    saveToStorage(this.tickets);
  }

  // Clear all tickets (useful for testing)
  clearAll(): void {
    this.tickets = [];
    localStorage.removeItem(STORAGE_KEY);
  }
}

// Create a singleton instance
export const mockTicketDb = new MockTicketDatabase(); 