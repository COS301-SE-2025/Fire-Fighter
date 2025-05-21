import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { 
  IonContent, 
  IonHeader, 
  IonTitle, 
  IonToolbar, 
  IonButtons, 
  IonButton,
  IonIcon
} from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import { logOutOutline } from 'ionicons/icons';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    IonContent, 
    IonHeader, 
    IonTitle, 
    IonToolbar, 
    IonButtons, 
    IonButton,
    IonIcon
  ]
})
export class DashboardPage implements OnInit {
  user$ = this.authService.user$;

  constructor(private authService: AuthService) {
    // Register icons
    addIcons({ logOutOutline });
  }

  ngOnInit() {
  }

  async logout() {
    await this.authService.logout();
  }
}
