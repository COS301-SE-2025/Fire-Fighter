import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { mockTicketDb } from './mock-ticket-database';
import { NotificationService } from './notification.service';

export interface Ticket {
  id: string;
  status: 'Pending' | 'Active' | 'Completed' | 'Rejected';
  dateCreated: Date;
  reason: string;
  requestDate: string;
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;
  private useMockDatabase = true; // Set to false when using real API

  constructor(
    private http: HttpClient,
    private notificationService: NotificationService
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
   * Create a new ticket request
   * @param ticketData The ticket data to create
   * @returns Observable of the created ticket
   */
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'dateCreated'>): Observable<Ticket> {
    const createTicket$ = this.useMockDatabase
      ? of(mockTicketDb.createTicket(ticketData))
      : this.http.post<Ticket>(this.apiUrl, ticketData);

    return createTicket$.pipe(
      tap(ticket => {
        // Create a notification for the new ticket
        this.notificationService.addNotification({
          type: 'ticket_created',
          title: 'New Ticket Created',
          message: `A new ticket ${ticket.id} has been created`,
          ticketId: ticket.id
        });
      })
    );
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
    // Original implementation
    return this.http.get<Ticket[]>(this.apiUrl);
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
    // Original implementation
    return this.http.get<Ticket>(`${this.apiUrl}/${ticketId}`);
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
    // Original implementation
    return this.http.patch<Ticket>(`${this.apiUrl}/${ticketId}`, { status });
  }
}