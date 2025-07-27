import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { NotificationService, Notification } from '../../services/notification.service';
import { addIcons } from 'ionicons';
import { logOutOutline } from 'ionicons/icons';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.page.html',
  styleUrls: ['./notifications.page.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    RouterModule,
    IonContent,
    IonRefresher,
    IonRefresherContent,
    NavbarComponent
  ]
})
export class NotificationsPage implements OnInit {
  user$ = this.authService.user$;
  notifications$: Observable<Notification[]>;
  mobileMenuOpen = false;
  profileMenuOpen = false;

  // Confirmation modal properties
  showDeleteConfirmModal = false;
  deleteConfirmationType: 'single' | 'multiple' = 'single';
  notificationToDelete: number | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService
  ) {
    // Register icons
    addIcons({ logOutOutline });
    this.notifications$ = this.notificationService.getNotifications();
  }

  ngOnInit() {
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    // Close profile menu when opening mobile menu
    if (this.mobileMenuOpen) {
      this.profileMenuOpen = false;
    }
  }

  toggleProfileMenu() {
    this.profileMenuOpen = !this.profileMenuOpen;
    // Close mobile menu when opening profile menu
    if (this.profileMenuOpen) {
      this.mobileMenuOpen = false;
    }
  }

  async logout() {
    await this.authService.logout();
  }

  markAsRead(notificationId: number) {
    this.notificationService.markAsRead(notificationId);
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead();
  }

  deleteReadNotifications() {
    this.deleteConfirmationType = 'multiple';
    this.notificationToDelete = null;
    this.showDeleteConfirmModal = true;
  }

  deleteNotification(notificationId: number, event: Event) {
    // Prevent event bubbling to avoid triggering markAsRead
    event.stopPropagation();

    this.deleteConfirmationType = 'single';
    this.notificationToDelete = notificationId;
    this.showDeleteConfirmModal = true;
  }

  getTimeAgo(timestamp: Date): string {
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - timestamp.getTime()) / 1000);

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

    return timestamp.toLocaleDateString();
  }

  trackByNotificationId(index: number, notification: Notification): number {
    return notification.id;
  }

  hasUnreadNotifications(notifications: Notification[]): boolean {
    return notifications.some(n => !n.read);
  }

  getUnreadCount(notifications: Notification[]): number {
    return notifications.filter(n => !n.read).length;
  }

  getEmergencyCount(notifications: Notification[]): number {
    return notifications.filter(n => n.type === 'request_completed' || n.type === 'new_request').length;
  }

  doRefresh(event: any) {
    // Refresh notifications data from backend
    this.notificationService.refreshNotifications();
    // Complete the refresh
    event.target.complete();
  }

  hasReadNotifications(notifications: Notification[]): boolean {
    return notifications.some(n => n.read);
  }

  // Modal control methods
  cancelDeletion() {
    this.showDeleteConfirmModal = false;
    this.notificationToDelete = null;
    this.deleteConfirmationType = 'single';
    this.loading = false;
  }

  confirmDeletion() {
    this.loading = true;

    if (this.deleteConfirmationType === 'single' && this.notificationToDelete) {
      // Delete single notification
      this.notificationService.deleteNotification(this.notificationToDelete).subscribe({
        next: (response) => {
          if (response) {
            console.log('Notification deleted successfully');
            this.notificationService.refreshNotifications();
          }
          this.cancelDeletion();
        },
        error: (error) => {
          console.error('Error deleting notification:', error);
          alert('Failed to delete notification. Please try again.');
          this.loading = false;
        }
      });
    } else if (this.deleteConfirmationType === 'multiple') {
      // Delete all read notifications
      this.notificationService.deleteReadNotifications().subscribe({
        next: (response) => {
          if (response) {
            console.log('Read notifications deleted successfully');
            this.notificationService.refreshNotifications();
          }
          this.cancelDeletion();
        },
        error: (error) => {
          console.error('Error deleting read notifications:', error);
          alert('Failed to delete read notifications. Please try again.');
          this.loading = false;
        }
      });
    }
  }
}
