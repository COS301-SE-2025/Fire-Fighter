import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, tap, map, take, interval, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface Notification {
  id: number;
  userId: string;
  type: 'ticket_created' | 'request_completed' | 'request_approved' | 'action_taken' | 'new_request' | 'ticket_revoked' | 'time_warning';
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
  private pollingInterval = 30000; // Poll every 30 seconds
  private isPolling = false;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Load notifications from backend on service initialization
    this.loadNotifications();
    // Start automatic polling for new notifications
    this.startPolling();
    // Listen for page visibility changes to refresh when user returns
    this.setupVisibilityListener();
  }

  /**
   * Start automatic polling for notifications
   */
  private startPolling(): void {
    if (this.isPolling) {
      return;
    }

    this.isPolling = true;

    // Poll every 30 seconds
    interval(this.pollingInterval).pipe(
      switchMap(() => this.authService.user$.pipe(take(1))),
      switchMap(currentUser => {
        if (!currentUser?.uid) {
          return of([]);
        }

        return this.http.get<Notification[]>(`${this.apiUrl}?userId=${currentUser.uid}`)
          .pipe(
            map(notifications => notifications.map(n => ({
              ...n,
              timestamp: new Date(n.timestamp)
            }))),
            catchError(error => {
              console.error('Error polling notifications:', error);
              return of([]);
            })
          );
      })
    ).subscribe(notifications => {
      const currentNotifications = this.notifications.value;

      // Check if there are new notifications
      if (notifications.length > currentNotifications.length) {
        console.log('🔔 New notifications detected:', notifications.length - currentNotifications.length);
      }

      this.notifications.next(notifications);
    });
  }

  /**
   * Stop automatic polling
   */
  public stopPolling(): void {
    this.isPolling = false;
  }

  /**
   * Load notifications from backend
   */
  private loadNotifications(): void {
    this.authService.user$.pipe(
      take(1)
    ).subscribe(currentUser => {
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
   * Force an immediate refresh of notifications (useful after actions that should trigger notifications)
   */
  forceRefresh(): void {
    console.log('🔄 Force refreshing notifications...');
    this.loadNotifications();
  }

  /**
   * Setup listener for page visibility changes
   */
  private setupVisibilityListener(): void {
    if (typeof document !== 'undefined') {
      document.addEventListener('visibilitychange', () => {
        if (!document.hidden) {
          // Page became visible, refresh notifications
          console.log('📱 Page became visible, refreshing notifications...');
          this.forceRefresh();
        }
      });
    }
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
    this.authService.user$.pipe(
      take(1)
    ).subscribe(currentUser => {
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
    });
  }

  /**
   * Mark all notifications as read
   */
  markAllAsRead(): void {
    this.authService.user$.pipe(
      take(1)
    ).subscribe(currentUser => {
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
    });
  }

  /**
   * Delete all read notifications
   */
  deleteReadNotifications(): Observable<any> {
    return new Observable(observer => {
      this.authService.user$.pipe(
        take(1)
      ).subscribe(currentUser => {
        if (!currentUser?.uid) {
          console.warn('No authenticated user found, cannot delete read notifications');
          observer.next(null);
          observer.complete();
          return;
        }

        this.http.delete(`${this.apiUrl}/read?userId=${currentUser.uid}`)
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
          )
          .subscribe(result => {
            observer.next(result);
            observer.complete();
          });
      });
    });
  }

  /**
   * Delete a specific notification
   */
  deleteNotification(notificationId: number): Observable<any> {
    return new Observable(observer => {
      this.authService.user$.pipe(
        take(1)
      ).subscribe(currentUser => {
        if (!currentUser?.uid) {
          console.warn('No authenticated user found, cannot delete notification');
          observer.next(null);
          observer.complete();
          return;
        }

        this.http.delete(`${this.apiUrl}/${notificationId}?userId=${currentUser.uid}`)
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
          )
          .subscribe(result => {
            observer.next(result);
            observer.complete();
          });
      });
    });
  }

  /**
   * Get notification statistics
   */
  getNotificationStats(): Observable<{total: number, unread: number, read: number}> {
    return new Observable(observer => {
      this.authService.user$.pipe(
        take(1)
      ).subscribe(currentUser => {
        if (!currentUser?.uid) {
          console.warn('No authenticated user found, cannot get notification stats');
          observer.next({total: 0, unread: 0, read: 0});
          observer.complete();
          return;
        }

        this.http.get<{total: number, unread: number, read: number}>(`${this.apiUrl}/stats?userId=${currentUser.uid}`)
          .pipe(
            catchError(error => {
              console.error('Error getting notification stats:', error);
              return of({total: 0, unread: 0, read: 0});
            })
          )
          .subscribe(result => {
            observer.next(result);
            observer.complete();
          });
      });
    });
  }
}