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
    console.log('🏥 Starting global health monitoring...');

    // Wait for initial app loading to complete before starting health monitoring
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
      // Skip health monitoring during initial load
      if (!this.isInitialLoadComplete) {
        return;
      }

      const currentRoute = this.router.url;
      this.isOnServiceDownPage = currentRoute === '/service-down';

      // If service becomes unhealthy and we're not already on service-down page
      if (!health.isHealthy && this.wasHealthy && !this.isOnServiceDownPage) {
        console.warn('🚨 Service became unhealthy, redirecting to service-down page');
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.router.navigate(['/service-down']);
      }

      // If service becomes healthy and we're on service-down page
      if (health.isHealthy && !this.wasHealthy && this.isOnServiceDownPage) {
        console.log('🎉 Service is healthy again, redirecting to dashboard');
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
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
      console.log('🏥 Stopped global health monitoring');
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
   * Force start monitoring (for testing or manual override)
   */
  forceStartMonitoring(): void {
    this.isInitialLoadComplete = true;
    this.startHealthSubscription();
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
