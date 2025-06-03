import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { mockTicketDb } from './mock-ticket-database';

export interface Ticket {
  id: string;
  status: 'Pending' | 'Active' | 'Completed' | 'Rejected';
  timeAgo: string;
  reason: string;
  requestDate: string;
  userId: string;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/tickets`;
  private useMockDatabase = true; // Toggle this to switch between mock and real backend

  constructor(private http: HttpClient) {}

  /**
   * Create a new ticket request
   * @param ticketData The ticket data to create
   * @returns Observable of the created ticket
   */
  createTicket(ticketData: Omit<Ticket, 'id' | 'status' | 'timeAgo'>): Observable<Ticket> {
    if (this.useMockDatabase) {
      const ticket = mockTicketDb.createTicket(ticketData);
      return of(ticket);
    }
    // Original implementation
    return this.http.post<Ticket>(this.apiUrl, ticketData);
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