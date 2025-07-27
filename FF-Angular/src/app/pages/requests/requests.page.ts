import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { ToastController } from '@ionic/angular';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TicketService, Ticket } from '../../services/ticket.service';
import { calculateTimeAgo } from '../../services/mock-ticket-database';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.page.html',
  styleUrls: ['./requests.page.scss'],
  standalone: true,
  imports: [
    IonContent,
    IonRefresher,
    IonRefresherContent,
    CommonModule, FormsModule, NavbarComponent
  ],
  animations: [
    trigger('modalBackdrop', [
      state('hidden', style({
        opacity: 0
      })),
      state('visible', style({
        opacity: 1
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ]),
    trigger('modalPanel', [
      state('hidden', style({
        opacity: 0,
        transform: 'scale(0.95) translateY(-10px)'
      })),
      state('visible', style({
        opacity: 1,
        transform: 'scale(1) translateY(0)'
      })),
      transition('hidden => visible', [
        animate('250ms cubic-bezier(0.34, 1.56, 0.64, 1)')
      ]),
      transition('visible => hidden', [
        animate('200ms ease-in')
      ])
    ])
  ]
})
export class RequestsPage implements OnInit {
  filter = 'all';
  showToast = false;
  isModalOpen = false;
  isLoading = false;
  modalAnimationState: string = 'hidden';
  private _searchQuery = '';
  tickets: Ticket[] = [];
  error: string | null = null;
  private currentUid: string = '';

  // Add getter and setter for searchQuery
  get searchQuery(): string {
    return this._searchQuery;
  }

  set searchQuery(value: string) {
    this._searchQuery = value;
    console.log('Search query updated:', {
      query: value,
      timestamp: new Date().toISOString(),
      resultCount: this.filteredTickets.length
    });
  }

  newTicket = {
    requestDate: new Date().toISOString().split('T')[0],
    reason: '',
    userId: '', // This should be set from the auth service
    emergencyType: '',
    emergencyContact: '',
    duration: 60 // Duration in minutes, default 1 hour
  };

  // Add calculateTimeAgo function
  calculateTimeAgo = calculateTimeAgo;

  // Duration validation and utility methods
  getTotalDurationMinutes(): number {
    return this.newTicket.duration;
  }

  isDurationValid(): boolean {
    const totalMinutes = this.getTotalDurationMinutes();
    return totalMinutes >= 15 && totalMinutes <= 120;
  }

  getDurationErrorMessage(): string {
    const totalMinutes = this.getTotalDurationMinutes();
    if (totalMinutes < 15) {
      return 'Duration must be at least 15 minutes';
    }
    if (totalMinutes > 120) {
      return 'Duration cannot exceed 2 hours';
    }
    return '';
  }

  formatDuration(minutes: number): string {
    if (minutes < 60) {
      return `${minutes} minutes`;
    }
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    if (remainingMinutes === 0) {
      return `${hours} hour${hours > 1 ? 's' : ''}`;
    }
    return `${hours} hour${hours > 1 ? 's' : ''} ${remainingMinutes} minutes`;
  }

  incrementDuration(): void {
    if (this.newTicket.duration < 120) {
      this.newTicket.duration += 15;
    }
  }

  decrementDuration(): void {
    if (this.newTicket.duration > 15) {
      this.newTicket.duration -= 15;
    }
  }

  constructor(
    private toastController: ToastController,
    private ticketService: TicketService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadTickets();
    this.authService.user$.subscribe(user => {
      if (user) {
        this.currentUid = user.uid;
        this.loadTickets();
        // Also refresh notifications when user changes
        this.notificationService.forceRefresh();
      }
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
        // Filter tickets for current user only using user.uid
        const userTickets = tickets.filter(ticket => ticket.userId === this.currentUid);
        // Sort: Active first, then Completed, then Rejected, each by most recent dateCreated
        this.tickets = [...userTickets].sort((a, b) => {
          const statusOrder = (status: string) => status === 'Active' ? 0 : status === 'Completed' ? 1 : 2;
          const statusDiff = statusOrder(a.status) - statusOrder(b.status);
          if (statusDiff !== 0) return statusDiff;
          return new Date(b.dateCreated).getTime() - new Date(a.dateCreated).getTime();
        });
      });
  }

  /**
   * Refresh both tickets and notifications
   */
  refreshData(event?: any) {
    console.log('🔄 Refreshing tickets and notifications...');
    this.loadTickets();
    this.notificationService.forceRefresh();

    // Complete the refresh event if it exists (for pull-to-refresh)
    if (event) {
      setTimeout(() => {
        event.target.complete();
      }, 1000);
    }
  }

  get filteredTickets() {
    let filtered = this.tickets;
    
    // Apply search filter
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(ticket => 
        ticket.id.toLowerCase().includes(query) || 
        ticket.reason.toLowerCase().includes(query)
      );
    }

    // Apply status filter
    if (this.filter !== 'all') {
      if (this.filter.toLowerCase() === 'completed') {
        // Include both "Completed" and "Closed" tickets when "Completed" filter is selected
        filtered = filtered.filter(ticket => 
          ticket.status === 'Completed' || ticket.status === 'Closed'
        );
      } else {
        filtered = filtered.filter(ticket => 
          ticket.status.toLowerCase() === this.filter.toLowerCase()
        );
      }
    }

    return filtered;
  }

  get activeTicketsCount() {
    return this.tickets.filter(t => t.status === 'Active').length;
  }

  get completedTicketsCount() {
    return this.tickets.filter(t => t.status === 'Completed' || t.status === 'Closed').length;
  }

  get rejectedTicketsCount() {
    return this.tickets.filter(t => t.status === 'Rejected').length;
  }

  setOpen(isOpen: boolean) {
    if (isOpen) {
      this.isModalOpen = true;
      // Small delay to ensure the modal is rendered before animating
      setTimeout(() => {
        this.modalAnimationState = 'visible';
      }, 10);
    } else {
      this.modalAnimationState = 'hidden';
      // Wait for animation to complete before hiding the modal
      setTimeout(() => {
        this.isModalOpen = false;
        // Reset form when modal is closed
        this.newTicket = {
          requestDate: new Date().toISOString().split('T')[0],
          reason: '',
          userId: '', // This should be set from the auth service
          emergencyType: '',
          emergencyContact: '',
          duration: 60 // Default 1 hour
        };
      }, 200);
    }
  }

  async createTicket() {
    if (!this.newTicket.reason.trim()) {
      const toast = await this.toastController.create({
        message: 'Please provide a reason for your request',
        duration: 3000,
        position: 'bottom',
        color: 'warning',
      });
      await toast.present();
      return;
    }

     // Set userId to current user's uid
     this.newTicket.userId = this.currentUid;

    if (!this.isDurationValid()) {
      const toast = await this.toastController.create({
        message: this.getDurationErrorMessage(),
        duration: 3000,
        position: 'bottom',
        color: 'warning',
      });
      await toast.present();
      return;
    }

    // Log the ticket request data
    console.log('Creating new ticket with data:', {
      ...this.newTicket,
      timestamp: new Date().toISOString()
    });

    this.isLoading = true;
    this.ticketService.createTicket(this.newTicket)
      .pipe(
        catchError(async err => {
          const toast = await this.toastController.create({
            message: 'Failed to create ticket. Please try again.',
            duration: 3000,
            position: 'bottom',
            color: 'danger',
          });
          await toast.present();
          return of(null);
        }),
        finalize(() => {
          this.isLoading = false;
          this.setOpen(false);
        })
      )
      .subscribe(ticket => {
        if (ticket && 'id' in ticket) {
          this.tickets.unshift(ticket);
          this.presentToast('Ticket created successfully');
          // Refresh data to ensure we have the latest notifications
          setTimeout(() => {
            this.refreshData();
          }, 1500);
        }
      });
  }

  async presentToast(message: string) {
    const toast = await this.toastController.create({
      message,
      duration: 3000,
      position: 'bottom',
      color: 'success',
    });
    await toast.present();
  }

  

  trackByTicketId(index: number, ticket: Ticket): string {
    return ticket.id;
  }

  doRefresh(event: any) {
    this.refreshData(event);
  }
}


