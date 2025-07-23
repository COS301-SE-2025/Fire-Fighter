import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, tap, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface Notification {
  id: number;
  userId: string;
  type: 'ticket_created' | 'request_completed' | 'request_approved' | 'action_taken' | 'new_request' | 'ticket_revoked';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  ticketId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications = new BehaviorSubject<Notification[]>([]);
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Load notifications from backend on service initialization
    this.loadNotifications();
  }

  /**
   * Load notifications from backend
   */
  private loadNotifications(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot load notifications');
      return;
    }

    this.http.get<Notification[]>(`${this.apiUrl}?userId=${currentUser.uid}`)
      .pipe(
        map(notifications => notifications.map(n => ({
          ...n,
          timestamp: new Date(n.timestamp)
        }))),
        catchError(error => {
          console.error('Error loading notifications:', error);
          return of([]);
        })
      )
      .subscribe(notifications => {
        this.notifications.next(notifications);
      });
  }

  /**
   * Get notifications observable
   */
  getNotifications(): Observable<Notification[]> {
    return this.notifications.asObservable();
  }

  /**
   * Refresh notifications from backend
   */
  refreshNotifications(): void {
    this.loadNotifications();
  }

  /**
   * Add notification (for backward compatibility - notifications are now created by backend)
   */
  addNotification(notification: Omit<Notification, 'id' | 'timestamp' | 'read' | 'userId'>): void {
    console.warn('addNotification called - notifications are now created by the backend');
    // Refresh notifications to get any new ones from backend
    this.refreshNotifications();
  }

  /**
   * Mark a specific notification as read
   */
  markAsRead(notificationId: number): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot mark notification as read');
      return;
    }

    this.http.put(`${this.apiUrl}/${notificationId}/read?userId=${currentUser.uid}`, {})
      .pipe(
        catchError(error => {
          console.error('Error marking notification as read:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        // Update local state
        const currentNotifications = this.notifications.value;
        const updatedNotifications = currentNotifications.map(notification =>
          notification.id === notificationId
            ? { ...notification, read: true }
            : notification
        );
        this.notifications.next(updatedNotifications);
      });
  }

  /**
   * Mark all notifications as read
   */
  markAllAsRead(): void {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot mark all notifications as read');
      return;
    }

    this.http.put(`${this.apiUrl}/read-all?userId=${currentUser.uid}`, {})
      .pipe(
        catchError(error => {
          console.error('Error marking all notifications as read:', error);
          return of(null);
        })
      )
      .subscribe(() => {
        // Update local state
        const currentNotifications = this.notifications.value;
        const updatedNotifications = currentNotifications.map(notification => ({
          ...notification,
          read: true
        }));
        this.notifications.next(updatedNotifications);
      });
  }

  /**
   * Delete all read notifications
   */
  deleteReadNotifications(): Observable<any> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot delete read notifications');
      return of(null);
    }

    return this.http.delete(`${this.apiUrl}/read?userId=${currentUser.uid}`)
      .pipe(
        tap(() => {
          // Update local state by removing read notifications
          const currentNotifications = this.notifications.value;
          const updatedNotifications = currentNotifications.filter(notification => !notification.read);
          this.notifications.next(updatedNotifications);
        }),
        catchError(error => {
          console.error('Error deleting read notifications:', error);
          return of(null);
        })
      );
  }

  /**
   * Delete a specific notification
   */
  deleteNotification(notificationId: number): Observable<any> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot delete notification');
      return of(null);
    }

    return this.http.delete(`${this.apiUrl}/${notificationId}?userId=${currentUser.uid}`)
      .pipe(
        tap(() => {
          // Update local state by removing the notification
          const currentNotifications = this.notifications.value;
          const updatedNotifications = currentNotifications.filter(notification => notification.id !== notificationId);
          this.notifications.next(updatedNotifications);
        }),
        catchError(error => {
          console.error('Error deleting notification:', error);
          return of(null);
        })
      );
  }

  /**
   * Get notification statistics
   */
  getNotificationStats(): Observable<{total: number, unread: number, read: number}> {
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser?.uid) {
      console.warn('No authenticated user found, cannot get notification stats');
      return of({total: 0, unread: 0, read: 0});
    }

    return this.http.get<{total: number, unread: number, read: number}>(`${this.apiUrl}/stats?userId=${currentUser.uid}`)
      .pipe(
        catchError(error => {
          console.error('Error getting notification stats:', error);
          return of({total: 0, unread: 0, read: 0});
        })
      );
  }
}