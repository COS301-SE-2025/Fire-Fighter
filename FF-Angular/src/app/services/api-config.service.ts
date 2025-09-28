import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {
  private currentApiUrl = new BehaviorSubject<string>(environment.apiUrl);

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
   * Get the API URL from environment
   */
  getApiUrl(): string {
    return environment.apiUrl;
  }

  /**
   * Reset to environment API URL (useful for testing)
   */
  reset(): void {
    this.currentApiUrl.next(environment.apiUrl);
  }
}