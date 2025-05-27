import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { ToastController } from '@ionic/angular';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.page.html',
  styleUrls: ['./requests.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule, NavbarComponent]
})
export class RequestsPage implements OnInit {

  constructor(private toastController: ToastController) {}

  async presentToast() {
    const toast = await this.toastController.create({
      message: 'Access for ticket INC-1234 has been revoked.',
      duration: 3000,
      position: 'bottom',
      color: 'primary',
    });
    await toast.present();
  }

  ngOnInit() {
    this.presentToast();
  }

  filter = 'all';
  showToast = true;

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
