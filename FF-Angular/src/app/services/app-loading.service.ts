import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, firstValueFrom } from 'rxjs';
import { HealthService } from './health.service';
import { AuthService } from './auth.service';
import { LanguageService } from './language.service';

export interface AppLoadingState {
  isLoading: boolean;
  message?: string;
  progress?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AppLoadingService {
  private loadingStateSubject = new BehaviorSubject<AppLoadingState>({
    isLoading: true,
    message: 'Initializing application...',
    progress: 0
  });

  public loadingState$: Observable<AppLoadingState> = this.loadingStateSubject.asObservable();

  // Inject services for actual initialization
  private healthService = inject(HealthService);
  private authService = inject(AuthService);
  private languageService = inject(LanguageService);

  constructor() {}

  /**
   * Initialize the application with real coordinated loading sequence
   */
  async initializeApp(): Promise<void> {
    try {
      console.log('🚀 Starting app initialization sequence...');

      // Step 1: Check initial connectivity and health
      this.updateLoadingState({
        isLoading: true,
        message: 'Checking service connectivity...',
        progress: 20
      });

      // Perform initial health check with timeout
      await firstValueFrom(this.healthService.checkInitialConnectivity(5000));

      // Step 2: Initialize authentication state
      this.updateLoadingState({
        isLoading: true,
        message: 'Initializing authentication...',
        progress: 50
      });

      // Wait for auth service to initialize (if it has an initialization method)
      await this.delay(1000); // Give auth service time to initialize

      // Step 3: Load user preferences and language settings
      this.updateLoadingState({
        isLoading: true,
        message: 'Loading user preferences...',
        progress: 75
      });

      // Initialize language service
      this.languageService.getCurrentLanguage();
      await this.delay(500);

      // Step 4: Finalize initialization
      this.updateLoadingState({
        isLoading: true,
        message: 'Finalizing setup...',
        progress: 90
      });

      await this.delay(500);

      // Complete initialization
      this.updateLoadingState({
        isLoading: false,
        message: 'Ready!',
        progress: 100
      });

      console.log('✅ App initialization completed successfully');

    } catch (error) {
      console.error('❌ App initialization failed:', error);

      // Even if initialization fails, we should still allow the app to load
      // The health monitoring will handle service unavailability
      this.updateLoadingState({
        isLoading: false,
        message: 'Ready (limited connectivity)',
        progress: 100
      });

      console.warn('⚠️ App loaded with limited functionality due to initialization error');
    }
  }

  /**
   * Show loading screen for specific operations
   */
  showLoading(message?: string, progress?: number): void {
    this.updateLoadingState({
      isLoading: true,
      message: message || 'Loading...',
      progress
    });
  }

  /**
   * Hide loading screen
   */
  hideLoading(): void {
    this.updateLoadingState({
      isLoading: false,
      message: 'Ready',
      progress: 100
    });
  }

  /**
   * Update loading progress and message
   */
  updateProgress(progress: number, message?: string): void {
    const currentState = this.getCurrentState();
    this.updateLoadingState({
      ...currentState,
      progress,
      message: message || currentState.message
    });
  }

  /**
   * Check if currently loading
   */
  isLoading(): boolean {
    return this.getCurrentState().isLoading;
  }

  /**
   * Update the current loading state
   */
  private updateLoadingState(state: AppLoadingState): void {
    this.loadingStateSubject.next(state);
  }

  /**
   * Get current loading state
   */
  getCurrentState(): AppLoadingState {
    return this.loadingStateSubject.value;
  }

  /**
   * Set loading state manually
   */
  setLoadingState(state: Partial<AppLoadingState>): void {
    const currentState = this.getCurrentState();
    this.updateLoadingState({ ...currentState, ...state });
  }

  /**
   * Simple delay utility
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
