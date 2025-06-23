import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Observable } from 'rxjs';

interface EmergencyRequest {
  id: string;
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
  imports: [CommonModule, IonContent, NavbarComponent, FormsModule],
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss']
})
export class AdminPage implements OnInit {
  isAdmin$: Observable<boolean>;
  userProfile$: Observable<any>;

  constructor(private authService: AuthService) {
    this.isAdmin$ = this.authService.isAdmin$;
    this.userProfile$ = this.authService.userProfile$;
  }

  activeEmergencyRequests: EmergencyRequest[] = [
    {
      id: 'REQ-001',
      requester: 'Alice Smith',
      reason: 'Fire in server room',
      status: 'Open',
      accessStart: '2024-06-10 09:00',
      accessEnd: '2024-06-10 12:00',
      system: 'Server Room A',
      justification: 'Critical server overheating, needs immediate fix.',
      logs: ['2024-06-10 09:01: Access granted', '2024-06-10 09:15: Entered server room'],
      email: 'alice.smith@example.com',
      phone: '555-123-4567'
    },
    {
      id: 'REQ-002',
      requester: 'Bob Johnson',
      reason: 'Smoke detected in lab',
      status: 'In Progress',
      accessStart: '2024-06-10 10:00',
      accessEnd: '2024-06-10 13:00',
      system: 'Lab 2B',
      justification: 'Investigate smoke alarm and ensure safety.',
      logs: ['2024-06-10 10:01: Access granted'],
      email: 'bob.johnson@example.com',
      phone: '555-987-6543'
    },
    {
      id: 'REQ-003',
      requester: 'Carol Lee',
      reason: 'Sprinkler malfunction',
      status: 'Open',
      accessStart: '2024-06-10 11:00',
      accessEnd: '2024-06-10 14:00',
      system: 'Main Hall',
      justification: 'Sprinkler system not activating, needs repair.',
      logs: ['2024-06-10 11:01: Access granted'],
      email: 'carol.lee@example.com',
      phone: '555-555-5555'
    }
  ];

  get activeTicketsCount() {
    return this.activeEmergencyRequests.length;
  }

  // Modal state for revocation reason
  showRevocationModal = false;
  revocationReason = '';
  revocationTarget: string | 'bulk' | null = null;

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

  // Confirm revocation (single or bulk)
  confirmRevocation() {
    if (!this.revocationReason.trim()) return;
    let revokedIds: string[] = [];
    if (this.revocationTarget === 'bulk') {
      revokedIds = this.activeEmergencyRequests
        .filter(req => this.selectedActiveIds.has(req.id))
        .map(req => req.id);
      this.activeEmergencyRequests = this.activeEmergencyRequests.map(req => {
        if (this.selectedActiveIds.has(req.id)) {
          return { ...req, status: 'revoked', revocationReason: this.revocationReason };
        }
        return req;
      });
      this.selectedActiveIds.clear();
    } else if (typeof this.revocationTarget === 'string') {
      revokedIds = [this.revocationTarget];
      this.activeEmergencyRequests = this.activeEmergencyRequests.map(req =>
        req.id === this.revocationTarget ? { ...req, status: 'revoked', revocationReason: this.revocationReason } : req
      );
    }
    // Remove revoked requests from the table
    this.activeEmergencyRequests = this.activeEmergencyRequests.filter(req => !revokedIds.includes(req.id));
    this.showRevocationModal = false;
    this.revocationTarget = null;
    this.revocationReason = '';
  }

  cancelRevocation() {
    this.showRevocationModal = false;
    this.revocationTarget = null;
    this.revocationReason = '';
  }

  requestHistory: EmergencyRequestHistory[] = [
    {
      id: 'REQ-004',
      requester: 'David Kim',
      reason: 'False alarm',
      status: 'Closed',
      completedAt: '2024-06-01 10:15',
      auditLog: [
        { action: 'Created', by: 'David Kim', at: '2024-06-01 09:00' },
        { action: 'Reviewed', by: 'Admin Alice', at: '2024-06-01 09:30' },
        { action: 'Closed', by: 'Admin Bob', at: '2024-06-01 10:15', reason: 'Confirmed false alarm' }
      ]
    },
    {
      id: 'REQ-005',
      requester: 'Eva Green',
      reason: 'Routine drill',
      status: 'Closed',
      completedAt: '2024-06-02 14:30',
      auditLog: [
        { action: 'Created', by: 'Eva Green', at: '2024-06-02 13:00' },
        { action: 'Approved', by: 'Admin Alice', at: '2024-06-02 13:10' },
        { action: 'Closed', by: 'Admin Bob', at: '2024-06-02 14:30', reason: 'Drill completed' }
      ]
    },
    {
      id: 'REQ-006',
      requester: 'Frank Moore',
      reason: 'Fire in kitchen',
      status: 'Resolved',
      completedAt: '2024-06-03 09:45',
      auditLog: [
        { action: 'Created', by: 'Frank Moore', at: '2024-06-03 08:30' },
        { action: 'Dispatched', by: 'Admin Alice', at: '2024-06-03 08:35' },
        { action: 'Resolved', by: 'Admin Bob', at: '2024-06-03 09:45', reason: 'Fire extinguished, area cleared' }
      ]
    }
  ];

  exportHistoryToCSV() {
    const headers = [
      'ID', 'Requester', 'Reason', 'Status', 'Completed At', 'Last Action', 'Action By', 'Action At'
    ];
    const rows = this.filteredAndSortedHistory.map(req => [
      req.id,
      req.requester,
      req.reason,
      req.status,
      req.completedAt,
      req.lastAction || '',
      req.actionBy || '',
      req.actionAt || ''
    ]);
    const csvContent = [headers, ...rows].map(e => e.map(field => '"' + String(field).replace(/"/g, '""') + '"').join(',')).join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'requests-history.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // Search, filter, and sort state
  searchQuery: string = '';
  statusFilter: string = '';
  sortOption: string = 'date';

  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'Open', label: 'Open' },
    { value: 'In Progress', label: 'In Progress' },
    { value: 'Closed', label: 'Closed' },
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
      filtered = filtered.slice().sort((a, b) => a.requester.localeCompare(b.requester));
    } else if (this.sortOption === 'urgency') {
      // For demo, sort by status: Open > In Progress > Resolved > Closed
      const order = { 'Open': 1, 'In Progress': 2, 'Resolved': 3, 'Closed': 4 };
      filtered = filtered.slice().sort((a, b) =>
        (order[a.status as keyof typeof order] ?? 99) - (order[b.status as keyof typeof order] ?? 99)
      );
    }
    return filtered;
  }

  // Expanded row state
  expandedRequestId: string | null = null;

  toggleExpandRequest(id: string) {
    this.expandedRequestId = this.expandedRequestId === id ? null : id;
  }

  // Search, filter, and sort state for Requests History
  historySearchQuery: string = '';
  historyStatusFilter: string = '';
  historySortOption: string = 'date';

  historyStatusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'Closed', label: 'Closed' },
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
      filtered = filtered.slice().sort((a, b) => a.requester.localeCompare(b.requester));
    } else if (this.historySortOption === 'reason') {
      filtered = filtered.slice().sort((a, b) => a.reason.localeCompare(b.reason));
    }
    return filtered;
  }

  // Expanded row state for Requests History
  expandedHistoryId: string | null = null;

  toggleExpandHistory(id: string) {
    this.expandedHistoryId = this.expandedHistoryId === id ? null : id;
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
    const headers = ['ID', 'Requester', 'Reason', 'Status', 'Access Start', 'Access End', 'System', 'Justification', 'Email', 'Phone'];
    const rows = selected.map(req => [req.id, req.requester, req.reason, req.status, req.accessStart, req.accessEnd, req.system, req.justification, req.email, req.phone]);
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

  exportActiveToCSV() {
    const headers = [
      'ID', 'Requester', 'Reason', 'Status', 'Access Start', 'Access End', 'System/Resource', 'Justification/Notes', 'Revoked By', 'Revoked At', 'Email', 'Phone'
    ];
    const rows = this.filteredAndSortedRequests.map(req => [
      req.id,
      req.requester,
      req.reason,
      req.status,
      req.accessStart,
      req.accessEnd,
      req.system,
      req.justification,
      req.revokedBy || '',
      req.revokedAt || '',
      req.email,
      req.phone
    ]);
    const csvContent = [headers, ...rows].map(e => e.map(field => '"' + String(field).replace(/"/g, '""') + '"').join(',')).join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'active-emergency-requests.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  exportFullAuditLogs() {
    const headers = ['Request ID', 'Requester', 'Action', 'By', 'At', 'Reason'];
    const rows: string[][] = [];
    this.requestHistory.forEach(req => {
      if (Array.isArray(req.auditLog)) {
        req.auditLog.forEach(log => {
          rows.push([
            req.id,
            req.requester,
            log.action,
            log.by,
            log.at,
            log.reason || ''
          ]);
        });
      }
    });
    const csvContent = [headers, ...rows].map(e => e.map(field => '"' + String(field).replace(/"/g, '""') + '"').join(',')).join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'full-audit-logs.csv');
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

  // --- Ensure mock data for new columns ---
  ngOnInit() {
    // The admin guard should prevent non-admins from reaching this page,
    // but we can also log the current user's admin status for debugging
    this.authService.isAdmin$.subscribe(isAdmin => {
      console.log('Current user admin status:', isAdmin);
    });

    // Add default values for new columns if missing
    this.activeEmergencyRequests.forEach(req => {
      if (!('revokedBy' in req)) req.revokedBy = '';
      if (!('revokedAt' in req)) req.revokedAt = '';
      if (!('revocationReason' in req)) req.revocationReason = '';
    });
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
          req.auditLog = [];
        }
      });
    }
  }
}
