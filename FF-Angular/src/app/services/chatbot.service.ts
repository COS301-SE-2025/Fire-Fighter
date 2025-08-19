import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { catchError, map, retry, delay } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface ChatbotQuery {
  query: string;
}

export interface ChatbotResponse {
  message: string;
  success: boolean;
  userRole?: string;
  timestamp: string;
  formattedTimestamp?: string;
}

export interface ChatbotCapabilities {
  capabilities: string[];
  userRole: string;
}

export interface ChatbotSuggestions {
  suggestions: string[];
  userRole: string;
}

export interface ChatbotHealth {
  status: string;
  version?: string;
  timestamp: string;
  service?: string;
  services?: {
    geminiAI: string;
    ticketQuery: string;
    database: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private apiUrl = `${environment.apiUrl}/chatbot`;

  constructor(private http: HttpClient) {}

  /**
   * Send a basic user query to the chatbot
   * Endpoint: POST /api/chatbot/query
   */
  sendQuery(query: string): Observable<ChatbotResponse> {
    console.log('🤖 CHATBOT SERVICE: Sending query:', query);
    console.log('🤖 CHATBOT SERVICE: API URL:', `${this.apiUrl}/query`);
    
    const payload: ChatbotQuery = { query };
    console.log('🤖 CHATBOT SERVICE: Payload:', payload);
    
    return this.http.post<ChatbotResponse>(`${this.apiUrl}/query`, payload).pipe(
      map(response => {
        console.log('🤖 CHATBOT SERVICE: Success response:', response);
        return response;
      }),
      catchError(error => {
        console.error('🤖 CHATBOT SERVICE: Error response:', error);
        return this.handleError(error);
      })
    );
  }

  /**
   * Send an admin query to the chatbot
   * Endpoint: POST /api/chatbot/admin/query
   */
  sendAdminQuery(query: string): Observable<ChatbotResponse> {
    console.log('🤖 CHATBOT SERVICE: Sending admin query:', query);
    console.log('🤖 CHATBOT SERVICE: API URL:', `${this.apiUrl}/admin/query`);
    
    const payload: ChatbotQuery = { query };
    console.log('🤖 CHATBOT SERVICE: Admin payload:', payload);
    
    return this.http.post<ChatbotResponse>(`${this.apiUrl}/admin/query`, payload).pipe(
      map(response => {
        console.log('🤖 CHATBOT SERVICE: Admin success response:', response);
        return response;
      }),
      catchError(error => {
        console.error('🤖 CHATBOT SERVICE: Admin error response:', error);
        return this.handleError(error);
      })
    );
  }

  /**
   * Get user capabilities based on their role
   * Endpoint: GET /api/chatbot/capabilities
   */
  getCapabilities(): Observable<ChatbotCapabilities> {
    return this.http.get<ChatbotCapabilities>(`${this.apiUrl}/capabilities`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get suggested queries for the user
   * Endpoint: GET /api/chatbot/suggestions
   */
  getSuggestions(): Observable<ChatbotSuggestions> {
    return this.http.get<ChatbotSuggestions>(`${this.apiUrl}/suggestions`).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Check chatbot service health with retry logic
   */
  getHealth(): Observable<ChatbotHealth> {
    return this.http.get<ChatbotHealth>(`${this.apiUrl}/health`).pipe(
      retry({
        count: 3,
        delay: (error, retryCount) => {
          console.log(`🔄 Retry attempt ${retryCount} for health check`);
          return timer(1000 * retryCount); // Exponential backoff
        }
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Determine if a query should use admin endpoint based on content
   */
  isAdminQuery(query: string): boolean {
    const adminKeywords = [
      'all tickets', 'active tickets', 'system status', 'summary', 'statistics',
      'how many', 'total', 'export', 'admin', 'overview', 'dashboard',
      'recent activity', 'system', 'all users', 'performance'
    ];
    
    const lowerQuery = query.toLowerCase();
    return adminKeywords.some(keyword => lowerQuery.includes(keyword));
  }

  /**
   * Handle HTTP errors with comprehensive debugging
   */
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    console.error('🤖 CHATBOT SERVICE ERROR HANDLER:');
    console.error('  Status:', error.status);
    console.error('  Status Text:', error.statusText);
    console.error('  Error:', error.error);
    console.error('  Message:', error.message);
    console.error('  URL:', error.url);
    
    let errorMessage = 'I am experiencing technical difficulties. Please try again later.';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      console.error('🤖 Client-side error:', error.error.message);
      errorMessage = 'Connection error. Please check your internet connection.';
    } else {
      // Server-side error
      console.error('🤖 Server-side error:');
      console.error('  Status Code:', error.status);
      console.error('  Response Body:', error.error);
      
      switch (error.status) {
        case 401:
          errorMessage = 'You need to be logged in to use the AI assistant.';
          break;
        case 403:
          errorMessage = 'You do not have permission to perform this action.';
          break;
        case 404:
          errorMessage = 'The AI service is currently unavailable.';
          break;
        case 429:
          errorMessage = 'Too many requests. Please wait a moment before trying again.';
          break;
        case 500:
          errorMessage = 'The AI service is experiencing issues. Please try again later.';
          break;
        default:
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else {
            errorMessage = `Service Error (${error.status}): ${error.statusText}`;
          }
      }
    }

    // Return a user-friendly error response
    const errorResponse: ChatbotResponse = {
      message: errorMessage,
      success: false,
      timestamp: new Date().toISOString(),
      formattedTimestamp: new Date().toLocaleString()
    };

    console.error('🤖 Final error response:', errorResponse);
    return throwError(() => errorResponse);
  };

  /**
   * Format timestamp for display
   */
  formatTimestamp(timestamp: string): string {
    try {
      return new Date(timestamp).toLocaleString();
    } catch {
      return new Date().toLocaleString();
    }
  }
}
