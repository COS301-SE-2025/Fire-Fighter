import { Ticket } from './ticket.service';
import { Subject } from 'rxjs';

const STORAGE_KEY = 'mock_tickets';
const AUTO_COMPLETE_DELAY = 15000; 

// BMW IT Hub emergency scenarios and systems
const bmwSystems = [
  'BMW-PROD-WEB', 'BMW-SAP-ERP', 'BMW-MES-PROD', 'BMW-PLM-SYS', 'BMW-CRM-SALES',
  'BMW-SCM-LOG', 'BMW-HR-CORE', 'BMW-FIN-SAP', 'BMW-QMS-AUDIT', 'BMW-IOT-PLANT',
  'BMW-EDI-B2B', 'BMW-DWH-BI', 'BMW-CAD-PLM', 'BMW-SRM-PROC', 'BMW-TMS-FLEET'
];

const emergencyReasons = [
  'Critical production line system failure - Munich plant offline',
  'SAP ERP database corruption preventing order processing',
  'Manufacturing Execution System (MES) communication breakdown',
  'PLM system unavailable blocking R&D engineering workflows',
  'Customer portal authentication service outage affecting sales',
  'Supply chain management system failure impacting logistics',
  'HR core system down preventing payroll processing',
  'Financial reporting system crashed before quarterly close',
  'Quality management system breach requiring immediate audit access',
  'IoT sensor network failure in production facility',
  'B2B EDI gateway down blocking supplier communications',
  'Data warehouse corruption affecting business intelligence reports',
  'CAD system license server failure blocking design workflows',
  'Supplier relationship management portal security breach',
  'Transportation management system outage affecting delivery schedules',
  'BMW ConnectedDrive service degradation impacting customer vehicles',
  'Production planning system database locked during shift change',
  'Inventory management system discrepancy in parts allocation',
  'Enterprise security system alert requiring immediate investigation',
  'BMW Group intranet authentication failure affecting global operations'
];

// Helper function to generate BMW-style ticket ID
const generateBmwTicketId = (): string => {
  const timestamp = Date.now().toString();
  const ticketNumber = timestamp.slice(-6);
  return `BMW-FF-${ticketNumber}`;
};

// Helper function to get random BMW system
const getRandomBmwSystem = (): string => {
  return bmwSystems[Math.floor(Math.random() * bmwSystems.length)];
};

// Helper function to get random emergency reason
const getRandomEmergencyReason = (): string => {
  return emergencyReasons[Math.floor(Math.random() * emergencyReasons.length)];
};

// Initial mock tickets that will be loaded into localStorage on first run
const initialMockTickets: Ticket[] = [
  {
    id: 'BMW-FF-847293',
    status: 'Active',
    dateCreated: new Date(Date.now() - 1000 * 60 * 45), // 45 minutes ago
    reason: 'Critical production line system failure - Munich plant offline. Manufacturing systems BMW-MES-PROD down affecting Line 3 and Line 7. Immediate access required to restart production controllers and restore automated assembly processes.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'max.mueller@bmw.de',
    emergencyType: 'critical-system-failure',
    emergencyContact: '+49 89 382 12345',
    duration: 60
  },
  {
    id: 'BMW-FF-739456',
    status: 'Active',
    dateCreated: new Date(Date.now() - 1000 * 60 * 32), // 32 minutes ago
    reason: 'SAP ERP database corruption preventing order processing. BMW-SAP-ERP experiencing data integrity issues in customer orders module. Production planning cannot access current order status. Escalated by Munich IT Operations.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'sarah.weber@bmw.de',
    emergencyType: 'critical-system-failure',
    emergencyContact: '+49 89 382 67890',
    duration: 60
  },
  {
    id: 'BMW-FF-682157',
    status: 'Active',
    dateCreated: new Date(Date.now() - 1000 * 60 * 18), // 18 minutes ago
    reason: 'BMW ConnectedDrive service degradation impacting customer vehicles. Authentication servers for BMW-IOT-PLANT showing high latency. Customer vehicles unable to access remote services including digital key and charging station locator.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'thomas.schmidt@bmw.de',
    emergencyType: 'network-outage',
    emergencyContact: '+49 89 382 54321',
    duration: 60
  },
  {
    id: 'BMW-FF-591834',
    status: 'Completed',
    dateCreated: new Date(Date.now() - 1000 * 60 * 60 * 4), // 4 hours ago
    reason: 'Quality management system breach requiring immediate audit access. BMW-QMS-AUDIT detected unauthorized access attempts. Security team needs emergency access to review audit logs and implement countermeasures.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'anna.fischer@bmw.de',
    emergencyType: 'security-incident',
    emergencyContact: '+49 89 382 98765',
    duration: 60
  },
  {
    id: 'BMW-FF-463729',
    status: 'Completed',
    dateCreated: new Date(Date.now() - 1000 * 60 * 60 * 8), // 8 hours ago
    reason: 'Manufacturing Execution System (MES) communication breakdown between BMW-MES-PROD and robotic assembly units. Production Line 5 at Dingolfing plant completely halted. Quality control checkpoints not receiving sensor data.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'michael.hoffman@bmw.de',
    emergencyType: 'critical-system-failure',
    emergencyContact: '+49 89 382 13579',
    duration: 60
  },
  {
    id: 'BMW-FF-357291',
    status: 'Rejected',
    dateCreated: new Date(Date.now() - 1000 * 60 * 60 * 12), // 12 hours ago
    reason: 'Routine password reset for BMW-HR-CORE system. User account locked after multiple failed attempts.',
    requestDate: new Date().toISOString().split('T')[0],
    userId: 'petra.lang@bmw.de',
    emergencyType: 'user-lockout',
    emergencyContact: '+49 89 382 24680',
    duration: 60
  }
];

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
    dateCreated: new Date(ticket.dateCreated),
    duration: ticket.duration || 60
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
      id: generateBmwTicketId(),
      status: 'Active',
      dateCreated: new Date(),
      ...ticketData,
      duration: ticketData.duration || 60
    };
    
    this.tickets.unshift(newTicket);
    saveToStorage(this.tickets);

    // Schedule auto-completion after 60 seconds
    setTimeout(() => {
      const ticketIndex = this.tickets.findIndex(t => t.id === newTicket.id);
      if (ticketIndex !== -1 && this.tickets[ticketIndex].status === 'Active') {
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

  // Generate a new random BMW emergency ticket (for demo purposes)
  generateRandomBmwTicket(): Ticket {
    const emergencyTypes = ['critical-system-failure', 'security-incident', 'network-outage', 'data-recovery'];
    const randomEmergencyType = emergencyTypes[Math.floor(Math.random() * emergencyTypes.length)];
    
    const randomTicket: Ticket = {
      id: generateBmwTicketId(),
      status: 'Active',
      dateCreated: new Date(),
      reason: getRandomEmergencyReason(),
      requestDate: new Date().toISOString().split('T')[0],
      userId: 'system.generated@bmw.de',
      emergencyType: randomEmergencyType,
      emergencyContact: '+49 89 382 00000',
      duration: 60
    };

    this.tickets.unshift(randomTicket);
    saveToStorage(this.tickets);

    return { ...randomTicket };
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
