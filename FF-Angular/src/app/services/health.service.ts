import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, interval, of, Subscription } from 'rxjs';
import { catchError, map, switchMap, tap, timeout } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ApiConfigService } from './api-config.service';

export interface HealthStatus {
  status: 'UP' | 'DOWN' | 'DEGRADED';
  timestamp: string;
  service: string;
  version: string;
  components?: {
    database: string;
    authentication: string;
    api: string;
  };
  system?: {
    'java.version': string;
    'spring.profiles.active': string;
  };
}

export interface ServiceHealth {
  isHealthy: boolean;
  status: HealthStatus | null;
  lastChecked: Date | null;
  error: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class HealthService {
  private healthSubject = new BehaviorSubject<ServiceHealth>({
    isHealthy: false,
    status: null,
    lastChecked: null,
    error: null
  });

  public health$ = this.healthSubject.asObservable();
  private isMonitoring = false;
  private monitoringInterval = 30000; // 30 seconds
  private monitoringSubscription?: Subscription;

  constructor(
    private http: HttpClient,
    private apiConfigService: ApiConfigService
  ) {}

  /**
   * Start continuous health monitoring
   */
  startMonitoring(): void {
    if (this.isMonitoring) {
      return;
    }

    this.isMonitoring = true;
    this.checkHealth().subscribe();

    this.monitoringSubscription = interval(this.monitoringInterval).pipe(
      switchMap(() => this.checkHealth())
    ).subscribe();
  }

  /**
   * Stop health monitoring
   */
  stopMonitoring(): void {
    this.isMonitoring = false;
    
    if (this.monitoringSubscription) {
      this.monitoringSubscription.unsubscribe();
      this.monitoringSubscription = undefined;
    }
  }

  /**
   * Perform a single health check
   */
  checkHealth(): Observable<ServiceHealth> {
    const currentApiUrl = this.apiConfigService.getCurrentApiUrlSync();
    const healthUrl = `${currentApiUrl}/health`;

    return this.http.get<HealthStatus>(healthUrl).pipe(
      map((status: HealthStatus) => {
        const health: ServiceHealth = {
          isHealthy: status && status.status === 'UP',
          status: status,
          lastChecked: new Date(),
          error: null
        };

        this.healthSubject.next(health);
        return health;
      }),
      catchError((error) => {
        const health: ServiceHealth = {
          isHealthy: false,
          status: null,
          lastChecked: new Date(),
          error: this.getErrorMessage(error)
        };

        this.healthSubject.next(health);
        return of(health);
      })
    );
  }

  /**
   * Perform initial connectivity check with timeout and retry logic
   * Used during app startup for better UX
   */
  checkInitialConnectivity(timeoutMs: number = 8000): Observable<ServiceHealth> {
    const currentApiUrl = this.apiConfigService.getCurrentApiUrlSync();
    const healthUrl = `${currentApiUrl}/health`;

    return this.http.get<HealthStatus>(healthUrl).pipe(
      timeout(timeoutMs),
      map((status: HealthStatus) => {
        const health: ServiceHealth = {
          isHealthy: status.status === 'UP',
          status: status,
          lastChecked: new Date(),
          error: null
        };

        this.healthSubject.next(health);
        return health;
      }),
      catchError((error) => {
        const health: ServiceHealth = {
          isHealthy: false,
          status: null,
          lastChecked: new Date(),
          error: this.getErrorMessage(error)
        };

        this.healthSubject.next(health);
        return of(health);
      })
    );
  }

  /**
   * Get detailed health information
   */
  getDetailedHealth(): Observable<HealthStatus> {
    const currentApiUrl = this.apiConfigService.getCurrentApiUrlSync();
    const detailedHealthUrl = `${currentApiUrl}/health/detailed`;

    return this.http.get<HealthStatus>(detailedHealthUrl).pipe(
      catchError((error) => {
        throw new Error(this.getErrorMessage(error));
      })
    );
  }

  /**
   * Get current health status synchronously
   */
  getCurrentHealth(): ServiceHealth {
    return this.healthSubject.value;
  }

  /**
   * Check if service is currently healthy
   */
  isServiceHealthy(): boolean {
    return this.healthSubject.value.isHealthy;
  }

  /**
   * Check health status with timeout
   * Useful for service-down page to test connectivity
   */
  checkHealthWithTimeout(timeoutMs: number = 8000): Observable<ServiceHealth> {
    const currentApiUrl = this.apiConfigService.getCurrentApiUrlSync();
    const healthUrl = `${currentApiUrl}/health`;

    return this.http.get<HealthStatus>(healthUrl).pipe(
      timeout(timeoutMs),
      map((status: HealthStatus) => {
        const health: ServiceHealth = {
          isHealthy: status && status.status === 'UP',
          status: status,
          lastChecked: new Date(),
          error: null
        };

        this.healthSubject.next(health);
        return health;
      }),
      catchError((error) => {
        console.error('❌ Health check failed:', {
          error: error.message,
          url: healthUrl
        });

        const health: ServiceHealth = {
          isHealthy: false,
          status: null,
          lastChecked: new Date(),
          error: this.getErrorMessage(error)
        };

        this.healthSubject.next(health);
        return of(health);
      })
    );
  }

  /**
   * Get user-friendly error message
   */
  private getErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Service Offline (Connection Failed)';
    } else if (error.status === 404) {
      return 'Health endpoint not found';
    } else if (error.status >= 500) {
      return 'Service Error';
    } else if (error.error?.message) {
      return error.error.message;
    } else {
      return 'Service Unavailable';
    }
  }
}
