import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TokenStatusComponent } from '../../components/token-status.component';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { AdminService, AdminTicket } from '../../services/admin.service';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, catchError, startWith, switchMap } from 'rxjs/operators';
import { trigger, state, style, transition, animate } from '@angular/animations';

interface EmergencyRequest {
  id: string;
  databaseId?: number; // Added for API revocation
  requester: string;
  reason: string;
  status: string;
  accessStart: string;
  accessEnd: string;
  system: string;
  justification: string;
  logs: string[];
  email: string;
  phone: string;
  revokedBy?: string;
  revokedAt?: string;
  revocationReason?: string;
  rejectReason?: string; // Added for displaying reject reason from API
}

interface EmergencyRequestHistory {
  id: string;
  requester: string;
  reason: string;
  status: string;
  completedAt: string;
  auditLog?: AuditLogEntry[];
  lastAction?: string;
  actionBy?: string;
  actionAt?: string;
}

// Add to the interface for history records (if not already present)
interface AuditLogEntry {
  action: string;
  by: string;
  at: string;
  reason?: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, IonContent, IonRefresher, IonRefresherContent, NavbarComponent, TokenStatusComponent, FormsModule],
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss'],
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
export class AdminPage implements OnInit {
  isAdmin$: Observable<boolean>;
  userProfile$: Observable<any>;
  
  // User names mapping (userId -> username)
  usernames: { [userId: string]: string } = {};
  userEmails: { [userId: string]: string } = {};
  
  // Loading and error states
  loading = false;
  error: string | null = null;
  
  // Specific loading states for different data
  activeTicketsLoading = true;
  historyLoading = true;
  
  // Data refresh trigger
  private refreshSubject = new BehaviorSubject<void>(undefined);

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private router: Router
  ) {
    this.isAdmin$ = this.authService.isAdmin$;
    this.userProfile$ = this.authService.userProfile$;
  }

  activeEmergencyRequests: EmergencyRequest[] = [];
  
  // Observable for active tickets from API
  activeTickets$: Observable<EmergencyRequest[]> = this.refreshSubject.pipe(
    switchMap(() => {
      this.activeTicketsLoading = true;
      return this.adminService.getActiveTickets();
    }),
    map(tickets => {
      this.activeTicketsLoading = false;
      const requests = tickets.map(ticket => this.adminService.mapAdminTicketToEmergencyRequest(ticket));
      
      // Populate usernames for active requests
      this.populateUserDetails(requests.map(r => r.requester));
      
      return requests;
    }),
    catchError(err => {
      this.activeTicketsLoading = false;
      this.error = err.message;
      console.error('Error loading active tickets:', err);
      return [];
    }),
    startWith([])
  );

  get activeTicketsCount() {
    return this.activeEmergencyRequests.length;
  }

  // Modal state for revocation reason
  showRevocationModal = false;
  revocationReason = '';
  revocationTarget: string | 'bulk' | null = null;

  // Modal state for export options
  showExportModal = false;
  exportModalAnimationState = 'hidden';
  exportType: 'active' | 'history' | 'audit' | null = null;
  exportStartDate = '';
  exportEndDate = '';
  isExporting = false;

  // Notification state
  showNotification = false;
  notificationMessage = '';
  notificationType: 'success' | 'error' = 'success';
  notificationAnimatingOut = false;

  // Ticket details animation state
  expandedTickets: Set<string> = new Set();
  animatingTickets: Set<string> = new Set();
  closingTickets: Set<string> = new Set();

  // Open modal for single revoke
  openRevokeModal(id: string) {
    this.revocationTarget = id;
    this.revocationReason = '';
    this.showRevocationModal = true;
  }

  // Open modal for bulk revoke
  openBulkRevokeModal() {
    this.revocationTarget = 'bulk';
    this.revocationReason = '';
    this.showRevocationModal = true;
  }

  // Open export modal
  openExportModal(type: 'active' | 'history' | 'audit') {
    this.exportType = type;
    this.exportStartDate = '';
    this.exportEndDate = '';
    this.showExportModal = true;
    // Small delay to ensure the modal is rendered before animating
    setTimeout(() => {
      this.exportModalAnimationState = 'visible';
    }, 10);
  }

  // Close export modal
  closeExportModal() {
    this.exportModalAnimationState = 'hidden';
    // Wait for animation to complete before hiding the modal
    setTimeout(() => {
      this.showExportModal = false;
      this.exportType = null;
      this.exportStartDate = '';
      this.exportEndDate = '';
      this.isExporting = false;
    }, 200);
  }

  // Show notification
  showNotificationBanner(message: string, type: 'success' | 'error' = 'success') {
    this.notificationMessage = message;
    this.notificationType = type;
    this.showNotification = true;
    this.notificationAnimatingOut = false;

    // Auto-hide after 5 seconds
    setTimeout(() => {
      this.hideNotification();
    }, 5000);
  }

  // Hide notification with animation
  hideNotification() {
    this.notificationAnimatingOut = true;

    // Wait for animation to complete before hiding
    setTimeout(() => {
      this.showNotification = false;
      this.notificationMessage = '';
      this.notificationAnimatingOut = false;
    }, 300); // Match animation duration
  }

  // Helper methods for ticket animation states
  isTicketExpanded(id: string): boolean {
    return this.expandedRequestId === id || this.expandedHistoryId === id;
  }

  isTicketAnimating(id: string): boolean {
    return this.animatingTickets.has(id);
  }

  getTicketAnimationClass(id: string): string {
    if (this.isTicketAnimating(id)) {
      // If the ticket is in the closing set, it should use the collapsed animation
      if (this.closingTickets.has(id)) {
        return 'collapsed';
      }
      // Otherwise, if it's expanded, use the expanded animation
      return this.isTicketExpanded(id) ? 'expanded' : 'collapsed';
    }
    return '';
  }

  // Confirm revocation (single or bulk)
  confirmRevocation() {
    if (!this.revocationReason.trim()) {
      this.error = 'Revocation reason is required';
      return;
    }
    
    this.loading = true;
    this.error = null;
    
    if (this.revocationTarget === 'bulk') {
      // Handle bulk revocation
      this.handleBulkRevocation();
    } else if (typeof this.revocationTarget === 'string') {
      // Handle single revocation
      this.handleSingleRevocation(this.revocationTarget);
    }
  }
  
  private handleSingleRevocation(ticketId: string) {
    // Find the ticket to get its database ID from the populated array
    const ticket = this.activeEmergencyRequests.find(t => t.id === ticketId);
    if (!ticket || !ticket.databaseId) {
      this.error = 'Ticket not found or missing database ID';
      this.loading = false;
      return;
    }
    
    console.log('Revoking ticket:', { ticketId, databaseId: ticket.databaseId, reason: this.revocationReason });
    
    this.adminService.revokeTicketById(ticket.databaseId, this.revocationReason).subscribe({
      next: (response) => {
        console.log('Ticket revoked successfully:', response);
        this.refreshData();
        this.closeRevocationModal();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error revoking ticket:', err);
        this.error = err.message || 'Failed to revoke ticket';
        this.loading = false;
      }
    });
  }
  
  private handleBulkRevocation() {
    // Get selected tickets from the populated array
    const selectedTickets = this.activeEmergencyRequests.filter(t => this.selectedActiveIds.has(t.id) && t.databaseId);
    
    if (selectedTickets.length === 0) {
      this.error = 'No valid tickets selected for revocation';
      this.loading = false;
      return;
    }
    
    console.log('Bulk revoking tickets:', selectedTickets.map(t => ({ ticketId: t.id, databaseId: t.databaseId })));
    
    // Revoke each selected ticket
    let completedCount = 0;
    let hasError = false;
    
    selectedTickets.forEach(ticket => {
      this.adminService.revokeTicketById(ticket.databaseId!, this.revocationReason).subscribe({
        next: (response) => {
          console.log('Ticket revoked successfully:', response);
          completedCount++;
          
          if (completedCount === selectedTickets.length && !hasError) {
            this.refreshData();
            this.closeRevocationModal();
            this.selectedActiveIds.clear();
            this.loading = false;
          }
        },
        error: (err) => {
          console.error('Error revoking ticket:', err);
          if (!hasError) {
            hasError = true;
            this.error = `Failed to revoke some tickets: ${err.message}`;
            this.loading = false;
          }
        }
      });
    });
  }
  
  private closeRevocationModal() {
    this.showRevocationModal = false;
    this.revocationTarget = null;
    this.revocationReason = '';
  }
  
  refreshData() {
    this.refreshSubject.next();
  }

  cancelRevocation() {
    this.showRevocationModal = false;
    this.revocationTarget = null;
    this.revocationReason = '';
  }

  requestHistory: EmergencyRequestHistory[] = [];
  
  // Observable for ticket history from API
  ticketHistory$: Observable<EmergencyRequestHistory[]> = this.refreshSubject.pipe(
    switchMap(() => {
      this.historyLoading = true;
      return this.adminService.getTicketHistory();
    }),
    map(tickets => {
      this.historyLoading = false;
      const history = tickets.map(ticket => this.mapAdminTicketToHistory(ticket));
      
      // Populate usernames for history requests (requester and actionBy)
      const allUserIds = new Set<string>();
      history.forEach(h => {
        allUserIds.add(h.requester);
        if (h.actionBy) allUserIds.add(h.actionBy);
        if (h.auditLog) {
          h.auditLog.forEach(log => allUserIds.add(log.by));
        }
      });
      this.populateUserDetails(Array.from(allUserIds));
      
      return history;
    }),
    catchError(err => {
      this.historyLoading = false;
      this.error = err.message;
      console.error('Error loading ticket history:', err);
      return [];
    }),
    startWith([])
  );
  
  // Helper method to map AdminTicket to EmergencyRequestHistory
  private mapAdminTicketToHistory(ticket: AdminTicket): EmergencyRequestHistory {
    // Use dateCompleted for completed/rejected tickets, otherwise use dateCreated
    const completedTimestamp = ticket.dateCompleted || ticket.dateCreated;
    
    // Build audit log based on ticket status
    const auditLog: AuditLogEntry[] = [
      { action: 'Created', by: ticket.userId, at: ticket.dateCreated }
    ];
    
    // Add completion/rejection entry if ticket is not active
    if (ticket.status === 'Rejected' && ticket.dateCompleted) {
      auditLog.push({
        action: 'Revoked',
        by: ticket.revokedBy || 'Admin',
        at: ticket.dateCompleted,
        reason: ticket.rejectReason || 'No reason provided'
      });
    } else if (ticket.status === 'Completed' && ticket.dateCompleted) {
      auditLog.push({
        action: 'Completed',
        by: 'System',
        at: ticket.dateCompleted
      });
    } else if (ticket.status === 'Closed' && ticket.dateCompleted) {
      auditLog.push({
        action: 'Completed',
        by: 'System',
        at: ticket.dateCompleted
      });
    }
    
    return {
      id: ticket.ticketId,
      requester: ticket.userId,
      reason: ticket.description,
      status: this.adminService.mapTicketStatus(ticket.status),
      completedAt: completedTimestamp,
      auditLog: auditLog
    };
  }



  // Search, filter, and sort state
  searchQuery: string = '';
  statusFilter: string = '';
  sortOption: string = 'date';

  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'Open', label: 'Open' },
    { value: 'Revoked', label: 'Revoked' },
    { value: 'Resolved', label: 'Resolved' }
  ];

  sortOptions = [
    { value: 'date', label: 'Request Date' },
    { value: 'requester', label: 'Requester' },
    { value: 'urgency', label: 'Urgency' }
  ];

  // Computed filtered and sorted requests
  get filteredAndSortedRequests() {
    let filtered = this.activeEmergencyRequests;
    // Search
    if (this.searchQuery.trim()) {
      const q = this.searchQuery.trim().toLowerCase();
      filtered = filtered.filter(req =>
        req.id.toLowerCase().includes(q) ||
        req.requester.toLowerCase().includes(q) ||
        req.reason.toLowerCase().includes(q) ||
        req.status.toLowerCase().includes(q)
      );
    }
    // Filter by status
    if (this.statusFilter) {
      filtered = filtered.filter(req => req.status === this.statusFilter);
    }
    // Sort
    if (this.sortOption === 'date') {
      filtered = filtered.slice().sort((a, b) => a.accessStart.localeCompare(b.accessStart));
    } else if (this.sortOption === 'requester') {
      filtered = filtered.slice().sort((a, b) => a.requester.toLowerCase().localeCompare(b.requester.toLowerCase()));
    } else if (this.sortOption === 'urgency') {
      // For demo, sort by status: Open > Revoked > Resolved
      const order = { 'Open': 1, 'Revoked': 2, 'Resolved': 3 };
      filtered = filtered.slice().sort((a, b) =>
        (order[a.status as keyof typeof order] ?? 99) - (order[b.status as keyof typeof order] ?? 99)
      );
    }
    return filtered;
  }

  // Expanded row state
  expandedRequestId: string | null = null;

  toggleExpandRequest(id: string) {
    if (this.expandedRequestId === id) {
      // Collapsing - start closing animation
      this.closingTickets.add(id);
      this.animatingTickets.add(id);

      setTimeout(() => {
        this.expandedRequestId = null;
        this.animatingTickets.delete(id);
        this.closingTickets.delete(id);
      }, 300); // Match animation duration
    } else {
      // Expanding - close any other expanded ticket first
      if (this.expandedRequestId) {
        const previousId = this.expandedRequestId;
        this.closingTickets.add(previousId);
        this.animatingTickets.add(previousId);

        setTimeout(() => {
          this.animatingTickets.delete(previousId);
          this.closingTickets.delete(previousId);
        }, 300);
      }

      // Start expanding the new ticket
      this.expandedRequestId = id;
      this.animatingTickets.add(id);
      // Make sure it's not in the closing set
      this.closingTickets.delete(id);

      setTimeout(() => {
        this.animatingTickets.delete(id);
      }, 300);
    }
  }

  // Search, filter, and sort state for Requests History
  historySearchQuery: string = '';
  historyStatusFilter: string = '';
  historySortOption: string = 'date';

  historyStatusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'Revoked', label: 'Revoked' },
    { value: 'Resolved', label: 'Resolved' }
  ];

  historySortOptions = [
    { value: 'date', label: 'Completed Date' },
    { value: 'requester', label: 'Requester' },
    { value: 'reason', label: 'Reason' }
  ];

  get filteredAndSortedHistory() {
    let filtered = this.requestHistory;
    // Search
    if (this.historySearchQuery.trim()) {
      const q = this.historySearchQuery.trim().toLowerCase();
      filtered = filtered.filter(req =>
        req.id.toLowerCase().includes(q) ||
        req.requester.toLowerCase().includes(q) ||
        req.reason.toLowerCase().includes(q) ||
        req.status.toLowerCase().includes(q)
      );
    }
    // Filter by status
    if (this.historyStatusFilter) {
      filtered = filtered.filter(req => req.status === this.historyStatusFilter);
    }
    // Sort
    if (this.historySortOption === 'date') {
      filtered = filtered.slice().sort((a, b) => a.completedAt.localeCompare(b.completedAt));
    } else if (this.historySortOption === 'requester') {
      filtered = filtered.slice().sort((a, b) => a.requester.toLowerCase().localeCompare(b.requester.toLowerCase()));
    } else if (this.historySortOption === 'reason') {
      filtered = filtered.slice().sort((a, b) => a.reason.toLowerCase().localeCompare(b.reason.toLowerCase()));
    }
    return filtered;
  }

  // Expanded row state for Requests History
  expandedHistoryId: string | null = null;

  toggleExpandHistory(id: string) {
    if (this.expandedHistoryId === id) {
      // Collapsing - start closing animation
      this.closingTickets.add(id);
      this.animatingTickets.add(id);

      setTimeout(() => {
        this.expandedHistoryId = null;
        this.animatingTickets.delete(id);
        this.closingTickets.delete(id);
      }, 300); // Match animation duration
    } else {
      // Expanding - close any other expanded ticket first
      if (this.expandedHistoryId) {
        const previousId = this.expandedHistoryId;
        this.closingTickets.add(previousId);
        this.animatingTickets.add(previousId);

        setTimeout(() => {
          this.animatingTickets.delete(previousId);
          this.closingTickets.delete(previousId);
        }, 300);
      }

      // Start expanding the new ticket
      this.expandedHistoryId = id;
      this.animatingTickets.add(id);
      // Make sure it's not in the closing set
      this.closingTickets.delete(id);

      setTimeout(() => {
        this.animatingTickets.delete(id);
      }, 300);
    }
  }

  // Bulk selection state for active requests
  selectedActiveIds: Set<string> = new Set();

  isAllActiveSelected() {
    return this.filteredAndSortedRequests.length > 0 && this.filteredAndSortedRequests.every(req => this.selectedActiveIds.has(req.id));
  }

  toggleSelectAllActive(event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.filteredAndSortedRequests.forEach(req => this.selectedActiveIds.add(req.id));
    } else {
      this.filteredAndSortedRequests.forEach(req => this.selectedActiveIds.delete(req.id));
    }
  }

  toggleSelectActive(id: string, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedActiveIds.add(id);
    } else {
      this.selectedActiveIds.delete(id);
    }
  }

  // Update revokeAccess and bulkRevokeSelected to use modal
  revokeAccess(id: string) {
    this.openRevokeModal(id);
  }
  bulkRevokeSelected() {
    this.openBulkRevokeModal();
  }

  bulkExportSelected() {
    const selected = this.filteredAndSortedRequests.filter(req => this.selectedActiveIds.has(req.id));
    if (selected.length === 0) return;
    const headers = ['ID', 'Requester', 'Reason', 'Status', 'Access Start', 'Access End', 'System', 'Justification', 'Reject Reason', 'Email', 'Phone'];
    const rows = selected.map(req => [
      req.id,
      this.usernames[req.requester] || req.requester,
      req.reason,
      req.status,
      req.accessStart,
      req.accessEnd,
      req.system,
      req.justification,
      req.rejectReason || '',
      this.userEmails[req.requester] || req.email,
      req.phone
    ]);
    const csvContent = [headers, ...rows].map(e => e.map(field => '"' + String(field).replace(/"/g, '""') + '"').join(',')).join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'selected-active-requests.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }



  // Full export uses modal (top button)
  exportFullAuditLogs() {
    this.openExportModal('audit');
  }

  // Direct export methods (section buttons)
  exportActiveToCSV() {
    this.downloadActiveTicketsCSV();
  }

  exportHistoryToCSV() {
    this.downloadHistoryCSV();
  }

  // Handle direct download export
  handleDirectDownload() {
    if (this.exportType === 'active') {
      this.downloadActiveTicketsCSV();
    } else if (this.exportType === 'history') {
      this.downloadHistoryCSV();
    } else if (this.exportType === 'audit') {
      this.downloadAuditLogsCSV();
    }
    this.closeExportModal();
  }

  // Handle email export
  handleEmailExport() {
    const currentUser = this.authService.getCurrentUserProfile();
    if (!currentUser) {
      alert('User not authenticated');
      return;
    }

    this.isExporting = true;

    const request = {
      userId: currentUser.userId,
      startDate: this.exportStartDate || undefined,
      endDate: this.exportEndDate || undefined
    };

    this.adminService.exportTicketsToEmail(request).subscribe({
      next: (response) => {
        // Display the actual API response message
        this.showNotificationBanner(
          response || 'Export request sent! You will receive the CSV file via email shortly.',
          'success'
        );
        this.closeExportModal();
      },
      error: (error) => {
        console.error('Export failed:', error);
        this.showNotificationBanner(
          'Export failed: ' + (error.error || error.message || 'Unknown error'),
          'error'
        );
        this.isExporting = false;
      }
    });
  }

  // Direct download methods (existing functionality)
  private downloadActiveTicketsCSV() {
    const headers = [
      'ID', 'Requester', 'Reason', 'Status', 'Access Start', 'Access End', 'System/Resource', 'Justification/Notes', 'Revoked By', 'Revoked At', 'Reject Reason', 'Email', 'Phone'
    ];
    const rows = this.filteredAndSortedRequests.map(req => [
      req.id,
      this.usernames[req.requester] || req.requester,
      req.reason,
      req.status,
      req.accessStart,
      req.accessEnd,
      req.system,
      req.justification,
      req.revokedBy ? (this.usernames[req.revokedBy] || req.revokedBy) : '',
      req.revokedAt || '',
      req.rejectReason || '',
      this.userEmails[req.requester] || req.email,
      req.phone
    ]);
    this.downloadCSV(headers, rows, 'active-emergency-requests.csv');
  }

  private downloadHistoryCSV() {
    const headers = [
      'ID', 'Requester', 'Reason', 'Status', 'Completed At', 'Last Action', 'Action By', 'Action At'
    ];
    const rows = this.filteredAndSortedHistory.map(req => [
      req.id,
      this.usernames[req.requester] || req.requester,
      req.reason,
      req.status,
      req.completedAt,
      req.lastAction || '',
      req.actionBy ? (this.usernames[req.actionBy] || req.actionBy) : '',
      req.actionAt || ''
    ]);
    this.downloadCSV(headers, rows, 'requests-history.csv');
  }

  private downloadAuditLogsCSV() {
    const headers = ['Request ID', 'Requester', 'Action', 'By', 'At', 'Reason'];
    const rows: string[][] = [];
    this.requestHistory.forEach(req => {
      if (Array.isArray(req.auditLog)) {
        req.auditLog.forEach(log => {
          rows.push([
            req.id,
            this.usernames[req.requester] || req.requester,
            log.action,
            this.usernames[log.by] || log.by,
            log.at,
            log.reason || ''
          ]);
        });
      }
    });
    this.downloadCSV(headers, rows, 'full-audit-logs.csv');
  }

  private downloadCSV(headers: string[], rows: string[][], filename: string) {
    const csvContent = [headers, ...rows].map(e => e.map(field => '"' + String(field).replace(/"/g, '""') + '"').join(',')).join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // --- Action stubs for new buttons ---
  extendAccess(id: string) {
    alert('Extend Access for request ' + id + ' (stub)');
  }

  viewAccessLogs(id: string) {
    alert('View Access Logs for request ' + id + ' (stub)');
  }

  contactRequester(id: string) {
    alert('Contact Requester for request ' + id + ' (stub)');
  }

  navigateToMetrics() {
    console.log('Navigate to metrics page');
    this.router.navigate(['/metrics']);
  }

  ngOnInit() {
    console.log('AdminPage initialized');
    
    // The admin guard should prevent non-admins from reaching this page,
    // but we can also log the current user's admin status for debugging
    this.authService.isAdmin$.subscribe(isAdmin => {
      console.log('Current user admin status:', isAdmin);
    });

    // Subscribe to active tickets to populate the local array for filtering
    this.activeTickets$.subscribe(tickets => {
      console.log('Admin page received tickets:', tickets);
      console.log('Database IDs:', tickets.map(t => ({ id: t.id, databaseId: t.databaseId })));
      this.activeEmergencyRequests = tickets;
      
      // Add default values for new columns if missing
      this.activeEmergencyRequests.forEach(req => {
        if (!('revokedBy' in req)) req.revokedBy = '';
        if (!('revokedAt' in req)) req.revokedAt = '';
        if (!('revocationReason' in req)) req.revocationReason = '';
      });
    });
    
    // Subscribe to ticket history to populate the local array for filtering
    this.ticketHistory$.subscribe(history => {
      this.requestHistory = history;
      
      // Ensure audit log and derived properties for history records
      if (this.requestHistory) {
        this.requestHistory.forEach((req: any) => {
          if (Array.isArray(req.auditLog) && req.auditLog.length > 0) {
            const last = req.auditLog[req.auditLog.length - 1];
            req.lastAction = last.action;
            req.actionBy = last.by;
            req.actionAt = last.at;
          } else {
            req.lastAction = '-';
            req.actionBy = '-';
            req.actionAt = '-';
            req.auditLog = req.auditLog || [];
          }
        });
      }
    });
  }

  private populateUserDetails(userIds: string[]) {
    // Deduplicate user IDs
    const uniqueUserIds = [...new Set(userIds)].filter(id => id); // Filter out any undefined/null IDs

    uniqueUserIds.forEach(userId => {
      // Handle special system values that don't need user lookup
      if (userId === 'System') {
        this.usernames[userId] = 'System';
        this.userEmails[userId] = 'system@firefighter.com';
        return;
      }

      if (userId === 'Admin') {
        this.usernames[userId] = 'Admin';
        this.userEmails[userId] = 'admin@firefighter.com';
        return;
      }

      // Avoid re-fetching if details are already known
      if (!this.usernames[userId] || !this.userEmails[userId]) {
        this.authService.getUserProfileById(userId).subscribe({
          next: (user) => {
            if (user) {
              this.usernames[userId] = user.username;
              this.userEmails[userId] = user.email;
            } else {
              this.usernames[userId] = 'Unknown User';
              this.userEmails[userId] = 'unknown@example.com';
            }
          },
          error: (err) => {
            console.error(`Error fetching user details for ID ${userId}:`, err);
            this.usernames[userId] = 'Unknown User';
            this.userEmails[userId] = 'unknown@example.com';
          }
        });
      }
    });
  }

  doRefresh(event: any) {
    this.refreshData();
    // Complete the refresh after a short delay to ensure data is loaded
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }
}
