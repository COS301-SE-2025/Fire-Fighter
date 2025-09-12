import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonItem, IonLabel, IonBadge, IonIcon, IonButton } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { timeOutline, refreshOutline, warningOutline, checkmarkCircleOutline } from 'ionicons/icons';
import { Subscription } from 'rxjs';
import { TokenMonitoringService } from '../services/token-monitoring.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-token-status',
  templateUrl: './token-status.component.html',
  styleUrls: ['./token-status.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    IonItem,
    IonLabel,
    IonBadge,
    IonIcon,
    IonButton
  ]
})
export class TokenStatusComponent implements OnInit, OnDestroy {
  private tokenMonitoringService = inject(TokenMonitoringService);
  private authService = inject(AuthService);
  
  tokenStatus: any = {
    isValid: false,
    expiresAt: null,
    timeUntilExpiry: 0,
    requiresRefresh: false
  };
  
  formattedTimeUntilExpiry = 'Unknown';
  isRefreshing = false;
  
  private subscription?: Subscription;

  constructor() {
    addIcons({ 
      timeOutline, 
      refreshOutline, 
      warningOutline, 
      checkmarkCircleOutline 
    });
  }

  ngOnInit() {
    // Subscribe to token status updates
    this.subscription = this.tokenMonitoringService.tokenStatus$.subscribe(status => {
      this.tokenStatus = status;
      this.formattedTimeUntilExpiry = this.tokenMonitoringService.getFormattedTimeUntilExpiry();
    });

    // Start monitoring if not already started
    this.tokenMonitoringService.startMonitoring();
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  async refreshToken() {
    this.isRefreshing = true;
    try {
      const success = await this.authService.refreshJwtToken();
      if (success) {
        console.log('✅ Manual token refresh successful');
      } else {
        console.log('❌ Manual token refresh failed');
      }
    } catch (error) {
      console.error('❌ Manual token refresh error:', error);
    } finally {
      this.isRefreshing = false;
    }
  }

  getStatusColor(): string {
    if (!this.tokenStatus.isValid) {
      return 'danger';
    } else if (this.tokenStatus.requiresRefresh) {
      return 'warning';
    } else {
      return 'success';
    }
  }

  getStatusIcon(): string {
    if (!this.tokenStatus.isValid) {
      return 'warning-outline';
    } else if (this.tokenStatus.requiresRefresh) {
      return 'time-outline';
    } else {
      return 'checkmark-circle-outline';
    }
  }

  getStatusText(): string {
    if (!this.tokenStatus.isValid) {
      return 'Expired';
    } else if (this.tokenStatus.requiresRefresh) {
      return 'Expiring Soon';
    } else {
      return 'Valid';
    }
  }
}