import { Injectable, inject } from '@angular/core';
import { Observable, interval, BehaviorSubject, timer } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

interface TokenStatus {
  isValid: boolean;
  expiresAt: Date | null;
  timeUntilExpiry: number; // milliseconds
  requiresRefresh: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TokenMonitoringService {
  private authService = inject(AuthService);
  
  private tokenStatusSubject = new BehaviorSubject<TokenStatus>({
    isValid: false,
    expiresAt: null,
    timeUntilExpiry: 0,
    requiresRefresh: false
  });

  public tokenStatus$ = this.tokenStatusSubject.asObservable();
  
  private monitoringInterval = 30000; // Check every 30 seconds
  private refreshThresholdMinutes = 10; // Refresh when 10 minutes left
  private isMonitoring = false;

  /**
   * Start monitoring token expiration
   */
  startMonitoring(): void {
    if (this.isMonitoring) {
      console.log('🔍 TOKEN MONITOR: Already monitoring');
      return;
    }

    console.log('🔍 TOKEN MONITOR: Starting token monitoring');
    this.isMonitoring = true;

    // Initial check
    this.checkTokenStatus();

    // Set up periodic monitoring
    interval(this.monitoringInterval).pipe(
      filter(() => this.isMonitoring),
      tap(() => this.checkTokenStatus())
    ).subscribe();
  }

  /**
   * Stop monitoring token expiration
   */
  stopMonitoring(): void {
    console.log('🔍 TOKEN MONITOR: Stopping token monitoring');
    this.isMonitoring = false;
    this.tokenStatusSubject.next({
      isValid: false,
      expiresAt: null,
      timeUntilExpiry: 0,
      requiresRefresh: false
    });
  }

  /**
   * Check current token status
   */
  private checkTokenStatus(): void {
    try {
      const token = this.authService.getJwtToken();
      const expiration = this.authService.getTokenExpiration();
      
      if (!token || !expiration) {
        this.tokenStatusSubject.next({
          isValid: false,
          expiresAt: null,
          timeUntilExpiry: 0,
          requiresRefresh: false
        });
        return;
      }

      const now = new Date();
      const timeUntilExpiry = expiration.getTime() - now.getTime();
      const minutesUntilExpiry = timeUntilExpiry / (1000 * 60);
      
      const status: TokenStatus = {
        isValid: timeUntilExpiry > 0,
        expiresAt: expiration,
        timeUntilExpiry: Math.max(0, timeUntilExpiry),
        requiresRefresh: minutesUntilExpiry <= this.refreshThresholdMinutes && minutesUntilExpiry > 0
      };

      this.tokenStatusSubject.next(status);

      // Log status periodically
      if (status.isValid) {
        console.log(`🔍 TOKEN MONITOR: Token valid, expires in ${Math.round(minutesUntilExpiry)} minutes`);
        
        if (status.requiresRefresh) {
          console.log('🔄 TOKEN MONITOR: Token needs refresh, triggering auto-refresh');
          this.authService.autoRefreshTokenIfNeeded().catch(error => {
            console.error('❌ TOKEN MONITOR: Auto-refresh failed:', error);
          });
        }
      } else {
        console.log('🔍 TOKEN MONITOR: Token expired or invalid');
      }

    } catch (error) {
      console.error('🔍 TOKEN MONITOR: Error checking token status:', error);
      this.tokenStatusSubject.next({
        isValid: false,
        expiresAt: null,
        timeUntilExpiry: 0,
        requiresRefresh: false
      });
    }
  }

  /**
   * Get current token status
   */
  getCurrentTokenStatus(): TokenStatus {
    return this.tokenStatusSubject.value;
  }

  /**
   * Check if token needs immediate refresh
   */
  needsImmediateRefresh(): boolean {
    const status = this.getCurrentTokenStatus();
    return status.requiresRefresh && status.isValid;
  }

  /**
   * Get formatted time until expiry
   */
  getFormattedTimeUntilExpiry(): string {
    const status = this.getCurrentTokenStatus();
    
    if (!status.isValid || status.timeUntilExpiry <= 0) {
      return 'Expired';
    }

    const minutes = Math.floor(status.timeUntilExpiry / (1000 * 60));
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (hours > 0) {
      return `${hours}h ${remainingMinutes}m`;
    } else {
      return `${remainingMinutes}m`;
    }
  }

  /**
   * Set refresh threshold (in minutes)
   */
  setRefreshThreshold(minutes: number): void {
    this.refreshThresholdMinutes = minutes;
    console.log(`🔍 TOKEN MONITOR: Refresh threshold set to ${minutes} minutes`);
  }

  /**
   * Set monitoring interval (in milliseconds)
   */
  setMonitoringInterval(milliseconds: number): void {
    this.monitoringInterval = milliseconds;
    console.log(`🔍 TOKEN MONITOR: Monitoring interval set to ${milliseconds}ms`);
  }
}