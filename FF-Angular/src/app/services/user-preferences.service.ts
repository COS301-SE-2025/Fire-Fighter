import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface UserPreferences {
  userId: string;
  emailNotificationsEnabled: boolean;
  emailTicketCreation: boolean;
  emailTicketCompletion: boolean;
  emailTicketRevocation: boolean;
  emailFiveMinuteWarning: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationSettings {
  criticalAlerts: boolean;
  sessionExpiry: boolean;
  requestUpdates: boolean;
  auditAlerts: boolean;
  maintenance: boolean;
  emailEnabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserPreferencesService {
  private apiUrl = `${environment.apiUrl}/user-preferences`;
  
  // BehaviorSubject to track current user preferences
  private userPreferencesSubject = new BehaviorSubject<UserPreferences | null>(null);
  public userPreferences$ = this.userPreferencesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Load preferences when user changes
    this.authService.user$.subscribe(user => {
      if (user?.uid) {
        this.loadUserPreferences(user.uid);
      } else {
        this.userPreferencesSubject.next(null);
      }
    });
  }

  /**
   * Load user preferences from the backend
   */
  private loadUserPreferences(userId: string): void {
    this.getUserPreferences(userId).subscribe({
      next: (preferences) => {
        this.userPreferencesSubject.next(preferences);
        console.log('✅ User preferences loaded:', preferences);
      },
      error: (error) => {
        console.error('❌ Failed to load user preferences:', error);
        // Create default preferences if none exist
        const defaultPreferences: UserPreferences = {
          userId: userId,
          emailNotificationsEnabled: false,
          emailTicketCreation: false,
          emailTicketCompletion: false,
          emailTicketRevocation: false,
          emailFiveMinuteWarning: false,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };
        this.userPreferencesSubject.next(defaultPreferences);
      }
    });
  }

  /**
   * Get user preferences from backend
   */
  getUserPreferences(userId: string): Observable<UserPreferences> {
    return this.http.get<UserPreferences>(`${this.apiUrl}/${userId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Update user preferences
   */
  updateUserPreferences(userId: string, preferences: Partial<UserPreferences>): Observable<UserPreferences> {
    return this.http.put<UserPreferences>(`${this.apiUrl}/${userId}`, preferences)
      .pipe(
        tap(updatedPreferences => {
          this.userPreferencesSubject.next(updatedPreferences);
          console.log('✅ User preferences updated:', updatedPreferences);
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Update a specific preference setting
   */
  updateSpecificPreference(userId: string, setting: string, enabled: boolean): Observable<UserPreferences> {
    return this.http.patch<UserPreferences>(`${this.apiUrl}/${userId}/${setting}`, { enabled })
      .pipe(
        tap(updatedPreferences => {
          this.userPreferencesSubject.next(updatedPreferences);
          console.log(`✅ Preference ${setting} updated:`, enabled);
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Enable all email notifications
   */
  enableAllEmailNotifications(userId: string): Observable<UserPreferences> {
    return this.http.post<UserPreferences>(`${this.apiUrl}/${userId}/enable-all`, {})
      .pipe(
        tap(updatedPreferences => {
          this.userPreferencesSubject.next(updatedPreferences);
          console.log('✅ All email notifications enabled');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Disable all email notifications
   */
  disableAllEmailNotifications(userId: string): Observable<UserPreferences> {
    return this.http.post<UserPreferences>(`${this.apiUrl}/${userId}/disable-all`, {})
      .pipe(
        tap(updatedPreferences => {
          this.userPreferencesSubject.next(updatedPreferences);
          console.log('✅ All email notifications disabled');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Reset preferences to default
   */
  resetToDefault(userId: string): Observable<UserPreferences> {
    return this.http.post<UserPreferences>(`${this.apiUrl}/${userId}/reset`, {})
      .pipe(
        tap(resetPreferences => {
          this.userPreferencesSubject.next(resetPreferences);
          console.log('✅ User preferences reset to default');
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Check if a specific preference is enabled
   */
  checkSpecificPreference(userId: string, setting: string): Observable<{setting: string, enabled: boolean}> {
    return this.http.get<{setting: string, enabled: boolean}>(`${this.apiUrl}/${userId}/check/${setting}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Convert UserPreferences to NotificationSettings format for compatibility
   */
  convertToNotificationSettings(preferences: UserPreferences | null): NotificationSettings {
    if (!preferences) {
      return {
        criticalAlerts: true,
        sessionExpiry: true,
        requestUpdates: true,
        auditAlerts: false,
        maintenance: false,
        emailEnabled: false
      };
    }

    return {
      criticalAlerts: true, // Always enabled for critical alerts
      sessionExpiry: true, // Always enabled for session expiry
      requestUpdates: preferences.emailTicketCreation || preferences.emailTicketCompletion || preferences.emailTicketRevocation,
      auditAlerts: false, // Not implemented yet
      maintenance: false, // Not implemented yet
      emailEnabled: preferences.emailNotificationsEnabled
    };
  }

  /**
   * Convert NotificationSettings to UserPreferences format
   */
  convertFromNotificationSettings(settings: NotificationSettings, userId: string): Partial<UserPreferences> {
    return {
      emailNotificationsEnabled: settings.emailEnabled,
      emailTicketCreation: settings.emailEnabled && settings.requestUpdates,
      emailTicketCompletion: settings.emailEnabled && settings.requestUpdates,
      emailTicketRevocation: settings.emailEnabled && settings.requestUpdates,
      emailFiveMinuteWarning: settings.emailEnabled && settings.requestUpdates
    };
  }

  /**
   * Get current user preferences synchronously
   */
  getCurrentPreferences(): UserPreferences | null {
    return this.userPreferencesSubject.value;
  }

  /**
   * Check if email notifications are enabled for current user
   */
  isEmailNotificationsEnabled(): boolean {
    const preferences = this.getCurrentPreferences();
    return preferences?.emailNotificationsEnabled || false;
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Server Error: ${error.status} - ${error.message}`;
      if (error.error && typeof error.error === 'string') {
        errorMessage = error.error;
      }
    }
    
    console.error('UserPreferencesService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
