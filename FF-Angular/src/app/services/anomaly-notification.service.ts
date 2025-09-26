import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

export interface AnomalyNotificationDTO {
  id: number;
  anomalyType: string;
  message: string;
  createdAt: string;
  isRead: boolean;
  username: string;
}

export interface GroupChangeNotificationDTO {
  id: number;
  securityLevel: string;
  groupName: string;
  changeType: string;
  createdAt: string;
  isRead: boolean;
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnomalyNotificationService {

  constructor(private apiService: ApiService) {}

  // Get unread anomaly notifications for the current user
  getUnreadNotifications(): Observable<AnomalyNotificationDTO[]> {
    return this.apiService.get<AnomalyNotificationDTO[]>('/anomaly-notifications/unread');
  }

  // Mark anomaly notification as read
  markAsRead(notificationId: number): Observable<any> {
    return this.apiService.put(`/anomaly-notifications/${notificationId}/mark-read`);
  }

  // Get user group change notifications
  getGroupChangeNotifications(): Observable<GroupChangeNotificationDTO[]> {
    return this.apiService.get<GroupChangeNotificationDTO[]>('/user-group-notifications/unread');
  }

  // Mark group change notification as read
  markGroupChangeAsRead(notificationId: number): Observable<any> {
    return this.apiService.put(`/user-group-notifications/${notificationId}/mark-read`);
  }

  // Get count of unread anomaly notifications
  getUnreadCount(): Observable<{count: number}> {
    return this.apiService.get<{count: number}>('/anomaly-notifications/count');
  }

  // Get count of unread group change notifications
  getGroupChangeUnreadCount(): Observable<{count: number}> {
    return this.apiService.get<{count: number}>('/user-group-notifications/count');
  }
}