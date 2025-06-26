import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: 'ticket_created' | 'request_completed' | 'request_approved' | 'action_taken' | 'new_request';
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
  private readonly STORAGE_KEY = 'notifications';

  constructor() {
    // Load notifications from storage on service initialization
    const storedNotifications = localStorage.getItem(this.STORAGE_KEY);
    if (storedNotifications) {
      const parsedNotifications = JSON.parse(storedNotifications).map((n: any) => ({
        ...n,
        timestamp: new Date(n.timestamp)
      }));
      this.notifications.next(parsedNotifications);
    }
  }

  getNotifications(): Observable<Notification[]> {
    return this.notifications.asObservable();
  }

  addNotification(notification: Omit<Notification, 'id' | 'timestamp' | 'read'>): void {
    const newNotification: Notification = {
      ...notification,
      id: this.generateId(),
      timestamp: new Date(),
      read: false
    };

    const currentNotifications = this.notifications.value;
    this.notifications.next([newNotification, ...currentNotifications]);
    this.saveToStorage();
  }

  markAsRead(notificationId: string): void {
    const currentNotifications = this.notifications.value;
    const updatedNotifications = currentNotifications.map(notification =>
      notification.id === notificationId
        ? { ...notification, read: true }
        : notification
    );
    this.notifications.next(updatedNotifications);
    this.saveToStorage();
  }

  markAllAsRead(): void {
    const currentNotifications = this.notifications.value;
    const updatedNotifications = currentNotifications.map(notification => ({
      ...notification,
      read: true
    }));
    this.notifications.next(updatedNotifications);
    this.saveToStorage();
  }

  private generateId(): string {
    return Math.random().toString(36).substring(2, 15);
  }

  private saveToStorage(): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.notifications.value));
  }
}