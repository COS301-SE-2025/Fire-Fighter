import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HealthService } from './health.service';
import { AppLoadingService, AppLoadingState } from './app-loading.service';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HealthMonitorService {
  private healthSubscription?: Subscription;
  private wasHealthy = true;
  private isOnServiceDownPage = false;
  private isInitialLoadComplete = false;

  constructor(
    private healthService: HealthService,
    private router: Router,
    private appLoadingService: AppLoadingService
  ) {}

  /**
   * Start monitoring health status and handle navigation
   * Only starts monitoring after initial app load is complete
   */
  startMonitoring(): void {
    this.appLoadingService.loadingState$.pipe(
      filter((state: AppLoadingState) => !state.isLoading)
    ).subscribe(() => {
      this.isInitialLoadComplete = true;
      this.startHealthSubscription();
    });
  }

  /**
   * Start the actual health subscription after initial load
   */
  private startHealthSubscription(): void {
    if (this.healthSubscription) {
      this.healthSubscription.unsubscribe();
    }

    this.healthSubscription = this.healthService.health$.subscribe(health => {
      if (!this.isInitialLoadComplete) {
        return;
      }

      const currentRoute = this.router.url;
      this.isOnServiceDownPage = currentRoute === '/service-down';

      // Navigate to service-down page when service becomes unhealthy
      if (!health.isHealthy && this.wasHealthy && !this.isOnServiceDownPage) {
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.router.navigate(['/service-down']);
      }

      // Navigate to dashboard when service recovers
      if (health.isHealthy && !this.wasHealthy && this.isOnServiceDownPage) {
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.isOnServiceDownPage = false;
        this.router.navigate(['/dashboard']);
      }

      this.wasHealthy = health.isHealthy;
    });
  }

  /**
   * Stop health monitoring
   */
  stopMonitoring(): void {
    if (this.healthSubscription) {
      this.healthSubscription.unsubscribe();
    }
    this.isInitialLoadComplete = false;
  }

  /**
   * Check if currently on service down page
   */
  isOnServiceDown(): boolean {
    return this.isOnServiceDownPage;
  }

  /**
   * Manually update service down page status (useful for manual retry scenarios)
   */
  setServiceDownPageStatus(status: boolean): void {
    this.isOnServiceDownPage = status;
    
    if (status && !this.healthSubscription) {
      this.forceStartMonitoring();
    }
  }

  /**
   * Force start monitoring (for testing or manual override)
   */
  forceStartMonitoring(): void {
    this.isInitialLoadComplete = true;
    this.startHealthSubscription();
    this.healthService.startMonitoring();
    this.healthService.checkHealth().subscribe();
  }

  /**
   * Reset monitoring state
   */
  reset(): void {
    this.stopMonitoring();
    this.wasHealthy = true;
    this.isOnServiceDownPage = false;
    this.isInitialLoadComplete = false;
  }


}
