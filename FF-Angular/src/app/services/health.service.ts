import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, interval, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

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
  private apiUrl = `${environment.apiUrl}/health`;
  private healthSubject = new BehaviorSubject<ServiceHealth>({
    isHealthy: false,
    status: null,
    lastChecked: null,
    error: null
  });

  public health$ = this.healthSubject.asObservable();
  private isMonitoring = false;
  private monitoringInterval = 30000; // 30 seconds

  constructor(private http: HttpClient) {}

  /**
   * Start continuous health monitoring
   */
  startMonitoring(): void {
    if (this.isMonitoring) {
      return;
    }

    this.isMonitoring = true;
    console.log('🏥 Starting health monitoring...');

    // Initial health check
    this.checkHealth().subscribe();

    // Set up periodic health checks
    interval(this.monitoringInterval).pipe(
      switchMap(() => this.checkHealth())
    ).subscribe();
  }

  /**
   * Stop health monitoring
   */
  stopMonitoring(): void {
    this.isMonitoring = false;
    console.log('🏥 Stopping health monitoring...');
  }

  /**
   * Perform a single health check
   */
  checkHealth(): Observable<ServiceHealth> {
    return this.http.get<HealthStatus>(this.apiUrl).pipe(
      map((status: HealthStatus) => {
        const health: ServiceHealth = {
          isHealthy: status.status === 'UP',
          status: status,
          lastChecked: new Date(),
          error: null
        };
        
        console.log('🏥 Health check successful:', health);
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
        
        console.warn('🏥 Health check failed:', health);
        this.healthSubject.next(health);
        return of(health);
      })
    );
  }

  /**
   * Get detailed health information
   */
  getDetailedHealth(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${this.apiUrl}/detailed`).pipe(
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
