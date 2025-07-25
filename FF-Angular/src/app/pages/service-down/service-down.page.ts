import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { HealthService, ServiceHealth } from '../../services/health.service';

@Component({
  selector: 'app-service-down',
  templateUrl: './service-down.page.html',
  styleUrls: ['./service-down.page.scss'],
  standalone: true,
  imports: [CommonModule, IonContent],
  providers: [DatePipe]
})
export class ServiceDownPage implements OnInit, OnDestroy {
  isRetrying = false;
  lastConnectionTime: string | null = null;
  estimatedRecovery: string | null = null;
  currentTime = new Date();
  serviceHealth: ServiceHealth | null = null;

  private timeUpdateSubscription?: Subscription;
  private healthSubscription?: Subscription;
  private retryAttempts = 0;
  private maxRetryAttempts = 3;

  constructor(
    private router: Router,
    private healthService: HealthService,
    private datePipe: DatePipe
  ) {}

  ngOnInit() {
    this.initializeServiceDownPage();
    this.startTimeUpdates();
    this.setEstimatedRecovery();
    this.subscribeToHealthUpdates();
  }

  ngOnDestroy() {
    if (this.timeUpdateSubscription) {
      this.timeUpdateSubscription.unsubscribe();
    }
    if (this.healthSubscription) {
      this.healthSubscription.unsubscribe();
    }
  }

  private initializeServiceDownPage() {
    // Get last connection time from localStorage if available
    const lastConnection = localStorage.getItem('lastSuccessfulConnection');
    if (lastConnection) {
      this.lastConnectionTime = this.formatDate(new Date(lastConnection));
    }

    // Set current time
    this.currentTime = new Date();

    // Get current health status
    this.serviceHealth = this.healthService.getCurrentHealth();
  }

  private formatDate(date: Date): string {
    return this.datePipe.transform(date, 'yyyy/MM/dd, HH:mm:ss') || '';
  }

  private subscribeToHealthUpdates() {
    // Subscribe to health updates
    this.healthSubscription = this.healthService.health$.subscribe(health => {
      this.serviceHealth = health;

      // If service becomes healthy, automatically redirect to dashboard
      if (health.isHealthy) {
        console.log('🎉 Service is back online! Redirecting to dashboard...');
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.router.navigate(['/dashboard']);
      }
    });

    // Perform an immediate health check when page loads
    this.healthService.checkHealth().subscribe();
  }

  private startTimeUpdates() {
    // Update current time every minute
    this.timeUpdateSubscription = interval(60000).subscribe(() => {
      this.currentTime = new Date();
    });
  }

  private setEstimatedRecovery() {
    // Set a realistic estimated recovery time (15-30 minutes from now)
    const estimatedMinutes = Math.floor(Math.random() * 15) + 15;
    const recoveryTime = new Date();
    recoveryTime.setMinutes(recoveryTime.getMinutes() + estimatedMinutes);
    this.estimatedRecovery = recoveryTime.toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  async retryConnection() {
    if (this.isRetrying || this.retryAttempts >= this.maxRetryAttempts) {
      return;
    }

    this.isRetrying = true;
    this.retryAttempts++;

    try {
      // Perform actual health check
      const health = await this.healthService.checkHealth().toPromise();

      if (health?.isHealthy) {
        // If successful, navigate back to dashboard
        console.log('🎉 Connection restored! Redirecting to dashboard...');
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.router.navigate(['/dashboard']);
      } else {
        throw new Error(health?.error || 'Service still unavailable');
      }

    } catch (error) {
      console.error('Connection retry failed:', error);

      if (this.retryAttempts >= this.maxRetryAttempts) {
        // Show message that max retries reached
        this.showMaxRetriesMessage();
      }
    } finally {
      this.isRetrying = false;
    }
  }

  // Get service status display text
  get serviceStatusText(): string {
    if (!this.serviceHealth) {
      return 'Checking...';
    }

    if (this.serviceHealth.isHealthy) {
      return 'Online';
    }

    return this.serviceHealth.error || 'Offline';
  }

  // Get service status CSS class
  get serviceStatusClass(): string {
    if (!this.serviceHealth) {
      return 'checking';
    }

    return this.serviceHealth.isHealthy ? 'online' : 'offline';
  }

  private showMaxRetriesMessage() {
    // In a real app, you might show a toast or modal
    console.log('Maximum retry attempts reached. Please try again later.');
  }

  goToOfflineMode() {
    // Navigate to a limited offline version or cached data view
    // For now, navigate to dashboard with offline flag
    this.router.navigate(['/dashboard'], { 
      queryParams: { offline: 'true' } 
    });
  }

  // Method to check if we should show retry button
  get canRetry(): boolean {
    return !this.isRetrying && this.retryAttempts < this.maxRetryAttempts;
  }

  // Method to get retry button text
  get retryButtonText(): string {
    if (this.isRetrying) {
      return 'Connecting...';
    }
    
    if (this.retryAttempts >= this.maxRetryAttempts) {
      return 'Max Retries Reached';
    }
    
    return this.retryAttempts > 0 
      ? `Retry Connection (${this.retryAttempts}/${this.maxRetryAttempts})` 
      : 'Retry Connection';
  }
}
