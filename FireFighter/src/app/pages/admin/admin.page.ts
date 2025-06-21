import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

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
}

interface EmergencyRequestHistory {
  id: string;
  requester: string;
  reason: string;
  status: string;
  completedAt: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, IonContent, NavbarComponent, RouterLink, FormsModule],
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss']
})
export class AdminPage {
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

  revokeAccess(id: string) {
    this.activeEmergencyRequests = this.activeEmergencyRequests.filter(req => req.id !== id);
  }

  requestHistory: EmergencyRequestHistory[] = [
    { id: 'REQ-004', requester: 'David Kim', reason: 'False alarm', status: 'Closed', completedAt: '2024-06-01 10:15' },
    { id: 'REQ-005', requester: 'Eva Green', reason: 'Routine drill', status: 'Closed', completedAt: '2024-06-02 14:30' },
    { id: 'REQ-006', requester: 'Frank Moore', reason: 'Fire in kitchen', status: 'Resolved', completedAt: '2024-06-03 09:45' }
  ];

  exportHistoryToCSV() {
    const headers = ['ID', 'Requester', 'Reason', 'Status', 'Completed At'];
    const rows = this.requestHistory.map(req => [req.id, req.requester, req.reason, req.status, req.completedAt]);
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

  // Expanded row state for Requests History
  expandedHistoryId: string | null = null;

  toggleExpandHistory(id: string) {
    this.expandedHistoryId = this.expandedHistoryId === id ? null : id;
  }
}
