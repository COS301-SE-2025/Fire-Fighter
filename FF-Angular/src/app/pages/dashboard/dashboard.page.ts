import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { TicketService, Ticket } from '../../services/ticket.service';
import { AdminService, AdminTicket } from '../../services/admin.service';
import { NotificationService } from '../../services/notification.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { calculateTimeAgo } from '../../services/mock-ticket-database';
import { catchError, finalize, take } from 'rxjs/operators';
import { of, Subscription } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

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
    IonRefresher,
    IonRefresherContent,
    NavbarComponent,
    TranslateModule
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
  private userSubscription?: Subscription;
  private ticketCreatedSubscription?: Subscription;
  private currentUserUid: string | null = null;

  // Add a public usernames map for template access
  public usernames: { [userId: string]: string } = {};

  // Add calculateTimeAgo function
  calculateTimeAgo = calculateTimeAgo;

  // Helper method to extract display name from userId
  private extractUserName(userId: string): string {
    // First check if we have the username in our usernames map
    if (this.usernames[userId]) {
      return this.usernames[userId];
    }

    // Fallback: extract name from email if userId is an email
    if (userId && userId.includes('@')) {
      const localPart = userId.split('@')[0];
      const nameParts = localPart.split('.');

      if (nameParts.length >= 2) {
        const firstName = nameParts[0].charAt(0).toUpperCase() + nameParts[0].slice(1);
        const lastName = nameParts[1].charAt(0).toUpperCase() + nameParts[1].slice(1);
        return `${firstName} ${lastName}`;
      }

      return localPart.charAt(0).toUpperCase() + localPart.slice(1);
    }

    // Final fallback
    return userId || 'Unknown User';
  }

  // Helper method to truncate long descriptions
  private truncateDescription(text: string, maxLength: number): string {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  constructor(
    private authService: AuthService,
    private ticketService: TicketService,
    private adminService: AdminService,
    private notificationService: NotificationService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    console.log('🔍 Dashboard - ngOnInit() called');
    this.subscribeToNotifications();
    this.checkForAdminAccessError();
    this.subscribeToTicketCreation();

    // Subscribe to user changes and load tickets when user is available
    console.log('🔍 Dashboard - Setting up user subscription');
    this.userSubscription = this.authService.user$.subscribe(user => {
      console.log('🔍 Dashboard - User subscription triggered:', user ? user.uid : 'null');
      if (user) {
        this.currentUserUid = user.uid;
        this.loadTickets();
      } else {
        this.currentUserUid = null;
        this.tickets = [];
      }
    });
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
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
    if (this.ticketCreatedSubscription) {
      this.ticketCreatedSubscription.unsubscribe();
    }
  }

  subscribeToNotifications() {
    let previousNotificationCount = 0;

    this.notificationSubscription = this.notificationService.getNotifications()
      .subscribe(notifications => {
        this.unreadNotificationsCount = notifications.filter(n => !n.read).length;

        // Check for new ticket creation notifications to auto-refresh dashboard
        if (notifications.length > previousNotificationCount && previousNotificationCount > 0) {
          // Look for new ticket_created notifications
          const newNotifications = notifications.slice(0, notifications.length - previousNotificationCount);
          const hasNewTicketNotification = newNotifications.some(notification =>
            notification.type === 'ticket_created' ||
            notification.message.includes('has been created') ||
            notification.title.includes('New Ticket Created')
          );

          if (hasNewTicketNotification) {
            console.log('🎫 New ticket detected, refreshing dashboard content...');
            // Refresh tickets without showing loading spinner for better UX
            this.refreshTicketsQuietly();
          }
        }

        previousNotificationCount = notifications.length;
      });
  }

  subscribeToTicketCreation() {
    // Subscribe to immediate ticket creation events for instant dashboard refresh
    this.ticketCreatedSubscription = this.ticketService.ticketCreated$.subscribe(newTicket => {
      console.log('🚀 Immediate ticket creation detected, refreshing dashboard content...');
      // Only refresh if the ticket belongs to the current user or if user is admin
      const isAdmin = this.authService.isCurrentUserAdmin();
      if (isAdmin || newTicket.userId === this.currentUserUid) {
        this.refreshTicketsQuietly();
      }
    });
  }

  loadTickets() {
    console.log('🔍 Dashboard - loadTickets() called');
    this.isLoading = true;
    this.error = null;
    this.loadTicketsInternal(true);
  }

  /**
   * Refresh tickets quietly without showing loading spinner
   */
  refreshTicketsQuietly() {
    this.loadTicketsInternal(false);
  }

  /**
   * Internal method to load tickets with optional loading indicator
   */
  private loadTicketsInternal(showLoading: boolean = true) {
    if (showLoading) {
      this.isLoading = true;
    }
    this.error = null;

    // Use observable to ensure we have the correct admin status
    this.authService.isAdmin$.pipe(
      take(1) // Take only the current value
    ).subscribe(isAdmin => {
      console.log('🔍 Dashboard - Is admin:', isAdmin, 'Current user UID:', this.currentUserUid);

      if (isAdmin) {
        // Admin: Load all tickets from the system
        this.adminService.getTicketHistory()
          .pipe(
            catchError(err => {
              this.error = 'Failed to load tickets. Please try again later.';
              return of([]);
            }),
            finalize(() => {
              if (showLoading) {
                this.isLoading = false;
              }
            })
          )
          .subscribe(adminTickets => {
            // Convert AdminTicket to Ticket format
            const convertedTickets = this.convertAdminTicketsToTickets(adminTickets);
            console.log('🔍 Dashboard - Admin loaded tickets:', convertedTickets.length, 'Active:', convertedTickets.filter(t => t.status === 'Active').length);

            // Store ALL tickets for active ticket filtering, but only process last 6 for dashboard stats
            this.tickets = convertedTickets;
            this.processTicketsForDashboard(convertedTickets.slice(0, 6));
          });
      } else {
        // Regular user: Load only their tickets
        if (this.currentUserUid) {
          this.ticketService.getTickets()
            .pipe(
              catchError(err => {
                this.error = 'Failed to load tickets. Please try again later.';
                return of([]);
              }),
              finalize(() => {
                if (showLoading) {
                  this.isLoading = false;
                }
              })
            )
            .subscribe(tickets => {
              // Filter tickets for current user only using user.uid
              const userTickets = tickets.filter(ticket => ticket.userId === this.currentUserUid);
              console.log('🔍 Dashboard - User loaded tickets:', userTickets.length, 'Active:', userTickets.filter(t => t.status === 'Active').length);
              console.log('🔍 Dashboard - Current user UID:', this.currentUserUid);
              console.log('🔍 Dashboard - All tickets user IDs:', tickets.map(t => t.userId));

              // Store ALL user tickets for active ticket filtering, but only process last 6 for dashboard stats
              const sortedTickets = userTickets.sort((a, b) => new Date(b.dateCreated).getTime() - new Date(a.dateCreated).getTime());
              this.tickets = sortedTickets;
              this.processTicketsForDashboard(sortedTickets.slice(0, 6));
            });
        } else {
          // No user found, clear tickets and stop loading
          this.tickets = [];
          if (showLoading) {
            this.isLoading = false;
          }
        }
      }
    });
  }

  /**
   * Convert AdminTicket array to Ticket array format
   */
  private convertAdminTicketsToTickets(adminTickets: AdminTicket[]): Ticket[] {
    return adminTickets.map(adminTicket => ({
      id: adminTicket.ticketId,
      status: adminTicket.status,
      dateCreated: new Date(adminTicket.dateCreated),
      reason: adminTicket.description,
      requestDate: adminTicket.requestDate,
      userId: adminTicket.userId,
      emergencyType: adminTicket.emergencyType,
      emergencyContact: adminTicket.emergencyContact,
      duration: adminTicket.duration || 60 // Default to 60 minutes if duration is undefined
    }));
  }

  /**
   * Process tickets for dashboard stats and username fetching (not for active tickets display)
   */
  private processTicketsForDashboard(tickets: Ticket[]): void {
    // Fetch usernames for unique userIds from ALL tickets (not just the 6 for dashboard)
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
    const activeTickets = this.tickets.filter(t => t.status === 'Active');
    return activeTickets.slice(0, 3);
  }

  // Emergency Response Statistics Properties
  get emergencyTypeBreakdown(): { [key: string]: number } {
    const breakdown: { [key: string]: number } = {
      'hr-emergency': 0,
      'financial-emergency': 0,
      'management-emergency': 0,
      'logistics-emergency': 0
    };

    this.tickets.forEach(ticket => {
      if (ticket.emergencyType && breakdown.hasOwnProperty(ticket.emergencyType)) {
        breakdown[ticket.emergencyType]++;
      }
    });

    return breakdown;
  }

  get mostCommonEmergencyType(): string {
    const breakdown = this.emergencyTypeBreakdown;
    const maxType = Object.keys(breakdown).reduce((a, b) =>
      breakdown[a] > breakdown[b] ? a : b
    );

    // Convert to display format
    const typeMap: { [key: string]: string } = {
      'hr-emergency': 'HR',
      'financial-emergency': 'Financial',
      'management-emergency': 'Management',
      'logistics-emergency': 'Logistics'
    };

    return typeMap[maxType] || 'N/A';
  }

  get averageResponseTime(): number {
    // Calculate average duration of all requested tickets
    if (this.tickets.length === 0) return 0;

    const totalDuration = this.tickets.reduce((sum, ticket) => sum + ticket.duration, 0);
    return Math.round(totalDuration / this.tickets.length);
  }

  get systemHealthScore(): number {
    const totalTickets = this.tickets.length;
    if (totalTickets === 0) return 100;

    const activeTickets = this.tickets.filter(t => t.status === 'Active').length;
    const rejectedTickets = this.tickets.filter(t => t.status === 'Rejected').length;

    // Calculate health score: 100% - (active tickets weight + rejected tickets weight)
    const activeWeight = (activeTickets / totalTickets) * 30; // Active tickets reduce health by up to 30%
    const rejectedWeight = (rejectedTickets / totalTickets) * 20; // Rejected tickets reduce health by up to 20%

    const healthScore = Math.max(50, 100 - activeWeight - rejectedWeight);
    return Math.round(healthScore);
  }

  get completionRate(): number {
    // Calculate percentage of tickets that are completed (not revoked) out of all tickets
    if (this.tickets.length === 0) return 0;

    const completedTickets = this.tickets.filter(t => t.status === 'Closed').length;
    const rate = (completedTickets / this.tickets.length) * 100;

    return Math.round(rate * 10) / 10;
  }

  get currentMonthTickets(): number {
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();

    return this.tickets.filter(ticket => {
      const ticketDate = new Date(ticket.dateCreated);
      return ticketDate.getMonth() === currentMonth &&
             ticketDate.getFullYear() === currentYear;
    }).length;
  }

  // Generate recent activities from tickets
  get recentActivities(): Activity[] {
    const activities: Activity[] = [];
    const isAdmin = this.authService.isCurrentUserAdmin();

    // For regular users, tickets are already filtered in loadTickets()
    // For admins, show all tickets (already loaded from admin service)
    const ticketsToShow = this.tickets.slice(0, 6);

    // Generate activities from filtered tickets
    ticketsToShow.forEach(ticket => {
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
          description: this.truncateDescription(ticket.reason, 80),
          user: this.extractUserName(ticket.userId),
          timeAgo: this.calculateTimeAgo(ticket.dateCreated),
          status: 'denied',
          timestamp: ticket.dateCreated
        });
      } else if (ticket.status === 'Closed') {
        activities.push({
          type: 'revoked',
          title: `Emergency access closed for ${ticketId}`,
          description: this.truncateDescription(ticket.reason, 80),
          user: this.extractUserName(ticket.userId),
          timeAgo: this.calculateTimeAgo(ticket.dateCreated),
          status: 'completed',
          timestamp: ticket.dateCreated
        });
      }
    });

    // Return activities sorted by timestamp (most recent first)
    return activities.sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
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

  doRefresh(event: any) {
    this.loadTickets();
    // Wait for the tickets to load then complete the refresh
    this.ticketService.getTickets().pipe(
      finalize(() => event.target.complete())
    ).subscribe();
  }
}


