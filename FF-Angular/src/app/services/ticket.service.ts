import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { mockTicketDb } from './mock-ticket-database';
import { NotificationService } from './notification.service';
import { AuthService } from './auth.service';

export interface Ticket {
  id: string;
  status: 'Active' | 'Completed' | 'Rejected' | 'Closed';
  dateCreated: Date;
  reason: string;
  requestDate: string;
  userId: string;
  emergencyType?: string;
  emergencyContact?: string;
  duration: number; // Duration in minutes
  fiveMinuteWarningSent?: boolean; // Tracks if 5-minute warning notification was sent
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;
  private useMockDatabase = false; // Set to false when using real API

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {
    // Subscribe to ticket status updates from mock database
    if (this.useMockDatabase) {
      mockTicketDb.onStatusUpdate$.subscribe(ticket => {
        if (ticket.status === 'Completed') {
          this.notificationService.addNotification({
            type: 'request_completed',
            title: 'Request Completed',
            message: `Your request ${ticket.id} has been completed automatically`,
            ticketId: ticket.id
          });
        }
      });
    }
  }

  /**
   * Map backend ticket object to frontend Ticket interface
   */
  private mapBackendTicketToFrontend(ticket: any): Ticket {
    return {
      id: ticket.ticketId, // Use ticketId from backend as id in frontend
      status: ticket.status as 'Active' | 'Completed' | 'Rejected' | 'Closed',
      dateCreated: ticket.dateCreated ? new Date(ticket.dateCreated) : new Date(),
      reason: ticket.description, // Map description to reason
      requestDate: typeof ticket.requestDate === 'string' ? ticket.requestDate : (ticket.requestDate ? ticket.requestDate.toString().split('T')[0] : new Date().toISOString().split('T')[0]),
      userId: ticket.userId,
      emergencyType: ticket.emergencyType,
      emergencyContact: ticket.emergencyContact,
      duration: ticket.duration || 60 // Default to 60 if not present
    };
  }

  /**
   * Create a new ticket request
   * @param ticketData The ticket data to create
   * @returns Observable of the created ticket
   */
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'dateCreated'>): Observable<Ticket> {
    if (this.useMockDatabase) {
      const createTicket$ = of(mockTicketDb.createTicket(ticketData));
      return createTicket$.pipe(
        tap(ticket => {
          this.notificationService.addNotification({
            type: 'ticket_created',
            title: 'New Ticket Created',
            message: `A new ticket ${ticket.id} has been created`,
            ticketId: ticket.id
          });
        })
      );
    } else {
      // Get current user info from AuthService
      let currentUserId = '';
      let currentUserEmail = '';
      let currentUserName = '';
      const user = (this.authService as any).auth.currentUser;
      if (user) {
        currentUserId = user.uid || '';
        currentUserEmail = user.email || '';
        currentUserName = user.displayName || user.email?.split('@')[0] || '';
      }
      // Only send the fields that exist in the mock-ticket-database
      const backendTicketData: any = {
        ticketId: generateTicketId(),
        description: ticketData.reason,
        requestDate: ticketData.requestDate || new Date().toISOString().split('T')[0],
        userId: currentUserId || currentUserEmail || currentUserName || 'unknown',
        emergencyType: ticketData.emergencyType || 'critical-system-failure',
        emergencyContact: ticketData.emergencyContact || '',
        duration: ticketData.duration || 60
      };
      return this.http.post<any>(this.apiUrl, backendTicketData).pipe(
        map(ticket => this.mapBackendTicketToFrontend(ticket)),
        tap(ticket => {
          // Force refresh notifications to get the backend-created notification
          setTimeout(() => {
            this.notificationService.forceRefresh();
          }, 1000); // Small delay to ensure backend notification is created
        })
      );
    }
  }

  /**
   * Get all tickets for the current user
   * @returns Observable of array of tickets
   */
  getTickets(): Observable<Ticket[]> {
    if (this.useMockDatabase) {
      const tickets = mockTicketDb.getAllTickets();
      return of(tickets);
    }
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(tickets => tickets.map(ticket => this.mapBackendTicketToFrontend(ticket)))
    );
  }

  /**
   * Get a specific ticket by ID
   * @param ticketId The ID of the ticket to retrieve
   * @returns Observable of the ticket
   */
  getTicket(ticketId: string): Observable<Ticket> {
    if (this.useMockDatabase) {
      const ticket = mockTicketDb.getTicketById(ticketId);
      if (!ticket) {
        throw new Error('Ticket not found');
      }
      return of(ticket);
    }
    return this.http.get<any>(`${this.apiUrl}/${ticketId}`).pipe(
      map(ticket => this.mapBackendTicketToFrontend(ticket))
    );
  }

  /**
   * Update a ticket's status
   * @param ticketId The ID of the ticket to update
   * @param status The new status
   * @returns Observable of the updated ticket
   */
  updateTicketStatus(ticketId: string, status: Ticket['status']): Observable<Ticket> {
    if (this.useMockDatabase) {
      const ticket = mockTicketDb.updateTicketStatus(ticketId, status);
      if (!ticket) {
        throw new Error('Ticket not found');
      }
      return of(ticket);
    }
    return this.http.patch<any>(`${this.apiUrl}/${ticketId}`, { status }).pipe(
      map(ticket => this.mapBackendTicketToFrontend(ticket)),
      tap(() => {
        // Force refresh notifications after status update
        setTimeout(() => {
          this.notificationService.forceRefresh();
        }, 1000);
      })
    );
  }
}

function generateTicketId(): string {
  // Example: BMW-FF-<timestamp>
  return `BMW-FF-${Date.now()}`;
}