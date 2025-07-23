import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HealthService } from './health.service';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HealthMonitorService {
  private healthSubscription?: Subscription;
  private wasHealthy = true;
  private isOnServiceDownPage = false;

  constructor(
    private healthService: HealthService,
    private router: Router
  ) {}

  /**
   * Start monitoring health status and handle navigation
   */
  startMonitoring(): void {
    console.log('🏥 Starting global health monitoring...');
    
    this.healthSubscription = this.healthService.health$.subscribe(health => {
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
  }

  /**
   * Check if currently on service down page
   */
  isOnServiceDown(): boolean {
    return this.isOnServiceDownPage;
  }
}
