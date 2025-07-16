import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface AdminTicket {
  id: number;
  ticketId: string;
  description: string;
  status: 'Active' | 'Rejected' | 'Completed' | 'Closed';
  dateCreated: string;
  requestDate: string;
  userId: string;
  emergencyType: string;
  emergencyContact: string;
  rejectReason?: string;
  dateCompleted?: string;
  revokedBy?: string;
}

export interface RevokeTicketRequest {
  adminUserId: string;
  rejectReason: string;
}

export interface RevokeTicketResponse {
  success: boolean;
  message: string;
  ticket: AdminTicket;
}

export interface AdminStatusResponse {
  isAdmin: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/tickets/admin`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Get all active tickets
   * Endpoint: GET /api/tickets/admin/active
   */
  getActiveTickets(): Observable<AdminTicket[]> {
    return this.http.get<AdminTicket[]>(`${this.apiUrl}/active`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get complete ticket history
   * Endpoint: GET /api/tickets/admin/history
   */
  getTicketHistory(): Observable<AdminTicket[]> {
    return this.http.get<AdminTicket[]>(`${this.apiUrl}/history`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get tickets by status
   * Endpoint: GET /api/tickets/admin/status/{status}
   */
  getTicketsByStatus(status: string): Observable<AdminTicket[]> {
    return this.http.get<AdminTicket[]>(`${this.apiUrl}/status/${status}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Revoke a ticket by database ID
   * Endpoint: PUT /api/tickets/admin/revoke/{id}
   */
  revokeTicketById(ticketDatabaseId: number, rejectReason: string): Observable<RevokeTicketResponse> {
    const currentUser = this.authService.getCurrentUserProfile();
    if (!currentUser) {
      return throwError(() => new Error('User not authenticated'));
    }

    const requestBody: RevokeTicketRequest = {
      adminUserId: currentUser.userId,
      rejectReason: rejectReason
    };

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.put<RevokeTicketResponse>(
      `${this.apiUrl}/revoke/${ticketDatabaseId}`,
      requestBody,
      { headers }
    ).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Revoke a ticket by ticket ID
   * Endpoint: PUT /api/tickets/admin/revoke/ticket-id/{ticketId}
   */
  revokeTicketByTicketId(ticketId: string, rejectReason: string): Observable<RevokeTicketResponse> {
    const currentUser = this.authService.getCurrentUserProfile();
    if (!currentUser) {
      return throwError(() => new Error('User not authenticated'));
    }

    const requestBody: RevokeTicketRequest = {
      adminUserId: currentUser.userId,
      rejectReason: rejectReason
    };

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.put<RevokeTicketResponse>(
      `${this.apiUrl}/revoke/ticket-id/${ticketId}`,
      requestBody,
      { headers }
    ).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Check if a user has admin privileges
   * Endpoint: GET /api/tickets/admin/check/{userId}
   */
  checkAdminStatus(userId: string): Observable<AdminStatusResponse> {
    return this.http.get<AdminStatusResponse>(`${this.apiUrl}/check/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Map AdminTicket to the format expected by the current admin page
   */
  mapAdminTicketToEmergencyRequest(ticket: AdminTicket): any {
    const mapped = {
      id: ticket.ticketId,
      databaseId: ticket.id, // Store the database ID for revocation
      requester: ticket.userId,
      reason: ticket.description,
      status: this.mapTicketStatus(ticket.status),
      accessStart: ticket.dateCreated,
      accessEnd: '', // Not provided by backend
      system: ticket.emergencyType,
      justification: ticket.description,
      logs: [`${ticket.dateCreated}: Ticket created`],
      email: ticket.userId, // Assuming userId is email
      phone: ticket.emergencyContact,
      rejectReason: ticket.rejectReason,
      dateCompleted: ticket.dateCompleted,
      // Add completion info based on status and dateCompleted
      completionLabel: this.getCompletionDateLabel(ticket),
      formattedCompletionDate: ticket.dateCompleted ? new Date(ticket.dateCompleted).toLocaleString() : null
    };
    
    console.log('Mapping ticket:', { 
      originalId: ticket.id, 
      ticketId: ticket.ticketId, 
      mappedDatabaseId: mapped.databaseId,
      status: ticket.status,
      dateCompleted: ticket.dateCompleted,
      completionLabel: mapped.completionLabel,
      mapped
    });
    
    return mapped;
  }

  /**
   * Map backend ticket status to frontend status
   */
  mapTicketStatus(backendStatus: string): string {
    switch (backendStatus) {
      case 'Active':
        return 'Open';
      case 'Rejected':
        return 'Revoked';
      case 'Completed':
        return 'Resolved';
      default:
        return backendStatus;
    }
  }

  /**
   * Get the appropriate label for the completion date based on ticket status
   */
  getCompletionDateLabel(ticket: AdminTicket): string | null {
    if (!ticket.dateCompleted) {
      return null; // Active ticket - no completion date
    }
    
    return ticket.status === 'Rejected' ? 'Date Rejected' : 'Date Completed';
  }

  /**
   * Format ticket completion information for display
   */
  formatTicketCompletion(ticket: AdminTicket): { label: string | null; formattedDate: string | null } {
    const label = this.getCompletionDateLabel(ticket);
    
    if (!label || !ticket.dateCompleted) {
      return { label: null, formattedDate: null };
    }
    
    const formattedDate = new Date(ticket.dateCompleted).toLocaleString();
    return { label, formattedDate };
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: any): Observable<never> {
    console.error('AdminService API Error:', error);
    
    let errorMessage = 'An error occurred';
    
    if (error.error) {
      if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.error.error) {
        errorMessage = error.error.error;
      } else if (error.error.message) {
        errorMessage = error.error.message;
      }
    } else if (error.message) {
      errorMessage = error.message;
    }

    return throwError(() => new Error(errorMessage));
  }
} 