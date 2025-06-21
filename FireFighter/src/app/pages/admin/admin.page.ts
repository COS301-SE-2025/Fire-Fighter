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

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, IonContent, NavbarComponent, RouterLink],
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.scss']
})
export class AdminPage {
  activeTicketsCount = 3; // Number of mock records

  activeEmergencyRequests: EmergencyRequest[] = [
    { id: 'REQ-001', requester: 'Alice Smith', reason: 'Fire in server room', status: 'Open' },
    { id: 'REQ-002', requester: 'Bob Johnson', reason: 'Smoke detected in lab', status: 'In Progress' },
    { id: 'REQ-003', requester: 'Carol Lee', reason: 'Sprinkler malfunction', status: 'Open' }
  ];
}
