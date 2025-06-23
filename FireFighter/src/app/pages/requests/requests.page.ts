import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { ToastController } from '@ionic/angular';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TicketService, Ticket } from '../../services/ticket.service';
import { calculateTimeAgo } from '../../services/mock-ticket-database';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.page.html',
  styleUrls: ['./requests.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, 
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
        animate('200ms cubic-bezier(0.25, 0.46, 0.45, 0.94)')
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
    emergencyContact: ''
  };

  // Add calculateTimeAgo function
  calculateTimeAgo = calculateTimeAgo;

  constructor(
    private toastController: ToastController,
    private ticketService: TicketService
  ) {}

  ngOnInit() {
    this.loadTickets();
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
        // Sort: Active first, then Completed, then Rejected, each by most recent dateCreated
        this.tickets = [...tickets].sort((a, b) => {
          const statusOrder = (status: string) => status === 'Active' ? 0 : status === 'Completed' ? 1 : 2;
          const statusDiff = statusOrder(a.status) - statusOrder(b.status);
          if (statusDiff !== 0) return statusDiff;
          return new Date(b.dateCreated).getTime() - new Date(a.dateCreated).getTime();
        });
      });
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
      filtered = filtered.filter(ticket => 
        ticket.status.toLowerCase() === this.filter.toLowerCase()
      );
    }

    return filtered;
  }

  get activeTicketsCount() {
    return this.tickets.filter(t => t.status === 'Active').length;
  }

  get completedTicketsCount() {
    return this.tickets.filter(t => t.status === 'Completed').length;
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
          emergencyContact: ''
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
}
