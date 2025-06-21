import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { RouterLink } from '@angular/router';

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
  imports: [CommonModule, IonContent, NavbarComponent, RouterLink],
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

  // Expanded row state
  expandedRequestId: string | null = null;

  toggleExpandRequest(id: string) {
    this.expandedRequestId = this.expandedRequestId === id ? null : id;
  }
}
