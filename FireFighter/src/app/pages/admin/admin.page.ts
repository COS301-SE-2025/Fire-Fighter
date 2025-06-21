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
    { id: 'REQ-001', requester: 'Alice Smith', reason: 'Fire in server room', status: 'Open' },
    { id: 'REQ-002', requester: 'Bob Johnson', reason: 'Smoke detected in lab', status: 'In Progress' },
    { id: 'REQ-003', requester: 'Carol Lee', reason: 'Sprinkler malfunction', status: 'Open' }
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

}
