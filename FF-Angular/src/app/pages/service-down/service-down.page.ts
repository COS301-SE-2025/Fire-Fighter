import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { interval, Subscription, firstValueFrom } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { HealthService, ServiceHealth } from '../../services/health.service';
import { HealthMonitorService } from '../../services/health-monitor.service';

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
    private healthMonitorService: HealthMonitorService,
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
    
    // Update health monitor service status when leaving the service-down page
    this.healthMonitorService.setServiceDownPageStatus(false);
  }

  private initializeServiceDownPage() {
    // Update health monitor service status to indicate we're on service-down page
    this.healthMonitorService.setServiceDownPageStatus(true);

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

  private subscribeToHealthUpdates(): void {
    this.healthSubscription = this.healthService.health$.subscribe(health => {
      this.serviceHealth = health;

      if (health.isHealthy) {
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.retryAttempts = 0;
      }
    });

    // Perform initial health check
    this.healthService.checkHealth().subscribe();
  }

  private startTimeUpdates(): void {
    this.timeUpdateSubscription = interval(60000).subscribe(() => {
      this.currentTime = new Date();
    });
  }

  private setEstimatedRecovery(): void {
    const estimatedMinutes = Math.floor(Math.random() * 15) + 15;
    const recoveryTime = new Date();
    recoveryTime.setMinutes(recoveryTime.getMinutes() + estimatedMinutes);
    this.estimatedRecovery = recoveryTime.toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  async retryConnection(): Promise<void> {
    if (this.isRetrying || this.retryAttempts >= this.maxRetryAttempts) {
      return;
    }

    this.isRetrying = true;
    this.retryAttempts++;

    try {
      // Temporarily unsubscribe to avoid conflicts
      if (this.healthSubscription) {
        this.healthSubscription.unsubscribe();
      }

      // Use the health check method with timeout
      const health = await firstValueFrom(
        this.healthService.checkHealthWithTimeout(10000)
      );

      if (health?.isHealthy) {
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.retryAttempts = 0;
        this.serviceHealth = health;

        this.healthMonitorService.setServiceDownPageStatus(false);
        await this.router.navigate(['/dashboard']);
      } else {
        this.subscribeToHealthUpdates();
        throw new Error(health?.error || 'Service unavailable');
      }

    } catch (error) {
      this.subscribeToHealthUpdates();

      if (this.retryAttempts >= this.maxRetryAttempts) {
        this.resetRetriesAfterDelay();
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

  private resetRetriesAfterDelay(): void {
    setTimeout(() => {
      this.retryAttempts = 0;
    }, 30000);
  }

  goToOfflineMode(): void {
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