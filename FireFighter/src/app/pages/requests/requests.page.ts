import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar, IonModal, IonButton, IonInput, IonTextarea, IonLabel } from '@ionic/angular/standalone';
import { ToastController } from '@ionic/angular';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TicketService, Ticket } from '../../services/ticket.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.page.html',
  styleUrls: ['./requests.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonModal, IonButton, 
    IonInput, IonTextarea, IonLabel, CommonModule, FormsModule, NavbarComponent
  ]
})
export class RequestsPage implements OnInit {
  filter = 'all';
  showToast = false;
  isModalOpen = false;
  isLoading = false;
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
    userId: '' // This should be set from the auth service
  };

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
        this.tickets = tickets;
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
    return this.tickets.filter(t => t.status === 'Approved').length;
  }

  get pendingTicketsCount() {
    return this.tickets.filter(t => t.status === 'Pending').length;
  }

  get completedTicketsCount() {
    return this.tickets.filter(t => t.status === 'Completed').length;
  }

  setOpen(isOpen: boolean) {
    this.isModalOpen = isOpen;
    if (!isOpen) {
      // Reset form when modal is closed
      this.newTicket = {
        requestDate: new Date().toISOString().split('T')[0],
        reason: '',
        userId: '' // This should be set from the auth service
      };
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
}
