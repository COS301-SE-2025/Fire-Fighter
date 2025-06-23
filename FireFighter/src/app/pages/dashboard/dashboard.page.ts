import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { TicketService, Ticket } from '../../services/ticket.service';
import { NotificationService } from '../../services/notification.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { calculateTimeAgo } from '../../services/mock-ticket-database';
import { catchError, finalize } from 'rxjs/operators';
import { of, Subscription } from 'rxjs';

export interface Activity {
  type: 'granted' | 'revoked' | 'denied' | 'submitted';
  title: string;
  description: string;
  user: string;
  timeAgo: string;
  status: 'active' | 'completed' | 'pending' | 'denied';
  timestamp: Date;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    IonContent,
    NavbarComponent
  ]
})
export class DashboardPage implements OnInit, OnDestroy {
  user$ = this.authService.user$;
  mobileMenuOpen = false;
  profileMenuOpen = false;
  tickets: Ticket[] = [];
  isLoading = false;
  error: string | null = null;
  unreadNotificationsCount = 0;
  adminAccessDeniedMessage: string | null = null;
  private notificationSubscription?: Subscription;

  // Add a public usernames map for template access
  public usernames: { [userId: string]: string } = {};

  // Add calculateTimeAgo function
  calculateTimeAgo = calculateTimeAgo;

  // Helper method to extract display name from email
  private extractUserName(email: string): string {
    if (!email || !email.includes('@')) return 'BMW User';
    
    const localPart = email.split('@')[0];
    const nameParts = localPart.split('.');
    
    if (nameParts.length >= 2) {
      const firstName = nameParts[0].charAt(0).toUpperCase() + nameParts[0].slice(1);
      const lastName = nameParts[1].charAt(0).toUpperCase() + nameParts[1].slice(1);
      return `${firstName} ${lastName}`;
    }
    
    return localPart.charAt(0).toUpperCase() + localPart.slice(1);
  }

  // Helper method to truncate long descriptions
  private truncateDescription(text: string, maxLength: number): string {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
    private notificationService: NotificationService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    this.loadTickets();
    this.subscribeToNotifications();
    this.checkForAdminAccessError();
  }

  checkForAdminAccessError() {
    this.route.queryParams.subscribe(params => {
      if (params['error'] === 'admin_access_denied') {
        this.adminAccessDeniedMessage = 'Access denied: You do not have administrator privileges to access the admin panel.';
        // Clear the error message after 5 seconds
        setTimeout(() => {
          this.adminAccessDeniedMessage = null;
        }, 5000);
      }
    });
  }

  ngOnDestroy() {
    if (this.notificationSubscription) {
      this.notificationSubscription.unsubscribe();
    }
  }

  subscribeToNotifications() {
    this.notificationSubscription = this.notificationService.getNotifications()
      .subscribe(notifications => {
        this.unreadNotificationsCount = notifications.filter(n => !n.read).length;
      });
  }

  loadTickets() {
    this.isLoading = true;
    this.error = null;
    this.ticketService.getTickets()
      .pipe(
        catchError(err => {
          this.error = 'Failed to load tickets. Please try again later.';
          return of([]);
        }),
        finalize(() => this.isLoading = false)
      )
      .subscribe(tickets => {
        // Sort tickets: non-expired Active with least time remaining first, then Completed/Rejected, then Expired
        const now = new Date();
        const getRemainingMs = (ticket: Ticket) => {
          const createdTime = new Date(ticket.dateCreated);
          const durationMs = (ticket.duration || 60) * 60 * 1000;
          const endTime = new Date(createdTime.getTime() + durationMs);
          return endTime.getTime() - now.getTime();
        };
        this.tickets = [...tickets].sort((a, b) => {
          // Expired = Active but remaining time <= 0
          const aRem = getRemainingMs(a);
          const bRem = getRemainingMs(b);
          const aExpired = a.status === 'Active' && aRem <= 0;
          const bExpired = b.status === 'Active' && bRem <= 0;
          // 1. Non-expired Active first, sorted by least time remaining
          if (a.status === 'Active' && !aExpired && (b.status !== 'Active' || bExpired)) return -1;
          if (b.status === 'Active' && !bExpired && (a.status !== 'Active' || aExpired)) return 1;
          if (a.status === 'Active' && !aExpired && b.status === 'Active' && !bExpired) {
            return aRem - bRem; // Least time remaining first
          }
          // 2. Completed/Rejected next (keep their order)
          if ((a.status === 'Completed' || a.status === 'Rejected') && (b.status !== 'Completed' && b.status !== 'Rejected')) return 1;
          if ((b.status === 'Completed' || b.status === 'Rejected') && (a.status !== 'Completed' && a.status !== 'Rejected')) return -1;
          // 3. Expired Active last
          if (aExpired && !bExpired) return 1;
          if (bExpired && !aExpired) return -1;
          // Otherwise, keep original order
          return 0;
        });

        // Fetch usernames for unique userIds
        const uniqueUserIds = Array.from(new Set(this.tickets.map(t => t.userId)));
        uniqueUserIds.forEach(userId => {
          if (!this.usernames[userId]) {
            this.authService.getUserProfileById(userId).subscribe(profile => {
              this.usernames[userId] = profile.username || profile.email || userId;
            }, () => {
              this.usernames[userId] = userId; // fallback if error
            });
          }
        });
      });
  }

  get activeTicketsCount() {
    return this.tickets.filter(t => t.status === 'Active').length;
  }

  get totalTicketsCount() {
    return this.tickets.length;
  }

  get recentTickets() {
    return this.tickets.slice(0, 5); // Get the 5 most recent tickets
  }

  // Get active tickets for the emergency requests display (first 3 active)
  get activeTicketsForDisplay() {
    return this.tickets.filter(t => t.status === 'Active').slice(0, 3);
  }

  // Monthly Statistics Properties
  get currentMonthName(): string {
    const months = ['January', 'February', 'March', 'April', 'May', 'June', 
                   'July', 'August', 'September', 'October', 'November', 'December'];
    return months[new Date().getMonth()];
  }

  get monthlyGrowthPercentage(): number {
    // Simulate growth percentage based on current ticket count
    return Math.max(5, Math.min(25, Math.floor(this.totalTicketsCount * 2.3)));
  }

  get approvalRate(): number {
    const completedTickets = this.tickets.filter(t => t.status === 'Completed').length;
    const totalProcessed = this.tickets.filter(t => t.status !== 'Active').length;
    
    if (totalProcessed === 0) return 95.5; // Default value if no processed tickets
    
    const rate = (completedTickets / totalProcessed) * 100;
    return Math.round(rate * 10) / 10; // Round to 1 decimal place
  }

  get approvalRateGrowth(): number {
    return 2.1; // Simulated growth percentage
  }

  get criticalIssuesCount(): number {
    // Count rejected tickets as critical issues
    const rejectedCount = this.tickets.filter(t => t.status === 'Rejected').length;
    return Math.max(1, rejectedCount * 3); // Multiply for more realistic numbers
  }

  get criticalIssuesChange(): number {
    return 3; // Simulated change number
  }

  get successRate(): number {
    const completedTickets = this.tickets.filter(t => t.status === 'Completed').length;
    const totalTickets = this.tickets.length;
    
    if (totalTickets === 0) return 98.7; // Default value if no tickets
    
    const rate = (completedTickets / totalTickets) * 100;
    const adjustedRate = Math.max(85, rate); // Ensure minimum 85% success rate
    return Math.round(adjustedRate * 10) / 10; // Round to 1 decimal place
  }

  get successRateGrowth(): number {
    return 0.3; // Simulated growth percentage
  }

  // Generate recent activities from tickets
  get recentActivities(): Activity[] {
    const activities: Activity[] = [];
    // Sort tickets by date (most recent first)
    const sortedTickets = [...this.tickets].sort((a, b) => 
      new Date(b.dateCreated).getTime() - new Date(a.dateCreated).getTime()
    );
    sortedTickets.slice(0, 4).forEach(ticket => {
      const ticketId = ticket.id;
      if (ticket.status === 'Active') {
        activities.push({
          type: 'granted',
          title: `Emergency access granted for ${ticketId}`,
          description: this.truncateDescription(ticket.reason, 80),
          user: this.extractUserName(ticket.userId),
          timeAgo: this.calculateTimeAgo(ticket.dateCreated),
          status: 'active',
          timestamp: ticket.dateCreated
        });
      } else if (ticket.status === 'Completed') {
        activities.push({
          type: 'revoked',
          title: `Emergency access completed for ${ticketId}`,
          description: this.truncateDescription(ticket.reason, 80),
          user: this.extractUserName(ticket.userId),
          timeAgo: this.calculateTimeAgo(ticket.dateCreated),
          status: 'completed',
          timestamp: ticket.dateCreated
        });
      } else if (ticket.status === 'Rejected') {
        activities.push({
          type: 'denied',
          title: `Emergency access request ${ticketId} denied`,
          description: 'Request did not meet emergency access criteria',
          user: 'BMW Security Team',
          timeAgo: this.calculateTimeAgo(ticket.dateCreated),
          status: 'denied',
          timestamp: ticket.dateCreated
        });
      }
    });
    // Add some sample activities if we have tickets
    if (this.tickets.length > 0) {
      const latestTicket = sortedTickets[0];
      const ticketId = latestTicket.id.split('-').pop() || latestTicket.id;
      activities.unshift({
        type: 'submitted',
        title: 'New emergency access request submitted',
        description: 'Network infrastructure failure',
        user: 'Anna Schmidt',
        timeAgo: '23 minutes ago',
        status: 'pending',
        timestamp: new Date(Date.now() - 23 * 60 * 1000) // 23 minutes ago
      });
    }
    // Sort activities by timestamp (most recent first, i.e., lowest time passed at the top)
    return activities.sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime()).slice(0, 4);
  }

  // Calculate remaining time based on creation time (using ticket's duration)
  getRemainingTime(ticket: Ticket): string {
    const now = new Date();
    const createdTime = new Date(ticket.dateCreated);
    const durationMs = (ticket.duration || 60) * 60 * 1000;
    const endTime = new Date(createdTime.getTime() + durationMs);
    
    const remainingMs = endTime.getTime() - now.getTime();
    
    if (remainingMs <= 0) {
      return 'Expired';
    }
    
    const remainingMinutes = Math.floor(remainingMs / (1000 * 60));
    const hours = Math.floor(remainingMinutes / 60);
    const minutes = remainingMinutes % 60;
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else {
      return `${minutes}m`;
    }
  }

  // Format request time (e.g., "14:30")
  formatRequestTime(date: Date): string {
    const requestDate = new Date(date);
    return requestDate.toLocaleTimeString('en-US', { 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: false 
    });
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    if (this.mobileMenuOpen) {
      this.profileMenuOpen = false;
    }
  }

  toggleProfileMenu() {
    this.profileMenuOpen = !this.profileMenuOpen;
    if (this.profileMenuOpen) {
      this.mobileMenuOpen = false;
    }
  }

  async logout() {
    await this.authService.logout();
  }
}


