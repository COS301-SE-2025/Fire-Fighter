import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {
  private currentApiUrl = new BehaviorSubject<string>(environment.apiUrl);
  private isUsingFallback = false;

  constructor() {}

  /**
   * Get the current API URL as an observable
   */
  getCurrentApiUrl(): Observable<string> {
    return this.currentApiUrl.asObservable();
  }

  /**
   * Get the current API URL synchronously
   */
  getCurrentApiUrlSync(): string {
    return this.currentApiUrl.value;
  }

  /**
   * Switch to fallback API URL
   */
  switchToFallback(): void {
    if (!this.isUsingFallback) {
      console.warn('🔄 Switching to fallback API URL:', environment.fallbackApiUrl);
      this.currentApiUrl.next(environment.fallbackApiUrl);
      this.isUsingFallback = true;
    }
  }

  /**
   * Switch back to primary API URL
   */
  switchToPrimary(): void {
    if (this.isUsingFallback) {
      console.log('🔄 Switching back to primary API URL:', environment.apiUrl);
      this.currentApiUrl.next(environment.apiUrl);
      this.isUsingFallback = false;
    }
  }

  /**
   * Check if currently using fallback API
   */
  isUsingFallbackApi(): boolean {
    return this.isUsingFallback;
  }

  /**
   * Get the primary API URL
   */
  getPrimaryApiUrl(): string {
    return environment.apiUrl;
  }

  /**
   * Get the fallback API URL
   */
  getFallbackApiUrl(): string {
    return environment.fallbackApiUrl;
  }

  /**
   * Reset to primary API URL (useful for testing connectivity)
   */
  reset(): void {
    this.currentApiUrl.next(environment.apiUrl);
    this.isUsingFallback = false;
  }
} 