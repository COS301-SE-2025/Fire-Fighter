import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.page.html',
  styleUrls: ['./requests.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule]
})
export class RequestsPage implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  requests = [
    {
      id: 'INC-1234',
      status: 'Completed',
      timeAgo: 'about 3 hours ago',
      reason: 'Database server critical failure'
    },
    {
      id: 'SEC-9012',
      status: 'Completed',
      timeAgo: 'about 2 hours ago',
      reason: 'Security patch deployment'
    }
  ];


}
