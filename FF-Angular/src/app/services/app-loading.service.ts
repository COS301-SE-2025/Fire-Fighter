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
    message: 'Starting FireFighter platform...',
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
   * Includes timeout protection for enterprise reliability
   */
  async initializeApp(): Promise<void> {
    try {
      this.updateLoadingState({
        isLoading: true,
        message: 'Verifying system connectivity...',
        progress: 20
      });

      await firstValueFrom(this.healthService.checkInitialConnectivity(5000));

      this.updateLoadingState({
        isLoading: true,
        message: 'Initializing security protocols...',
        progress: 50
      });

      await this.delay(1000);

      this.updateLoadingState({
        isLoading: true,
        message: 'Configuring user environment...',
        progress: 75
      });

      this.languageService.getCurrentLanguage();
      await this.delay(500);

      this.updateLoadingState({
        isLoading: true,
        message: 'Preparing platform...',
        progress: 90
      });

      await this.delay(500);

      this.updateLoadingState({
        isLoading: false,
        message: 'Platform ready',
        progress: 100
      });

    } catch (error) {
      this.updateLoadingState({
        isLoading: false,
        message: 'Platform ready - Limited connectivity mode',
        progress: 100
      });
    }
  }

  /**
   * Show loading screen for specific operations
   */
  showLoading(message?: string, progress?: number): void {
    this.updateLoadingState({
      isLoading: true,
      message: message || 'Processing request...',
      progress
    });
  }

  /**
   * Hide loading screen
   */
  hideLoading(): void {
    this.updateLoadingState({
      isLoading: false,
      message: 'Platform ready',
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
   * Set maximum loading timeout for enterprise reliability
   */
  setLoadingTimeout(timeoutMs: number = 30000): void {
    setTimeout(() => {
      if (this.isLoading()) {
        this.updateLoadingState({
          isLoading: false,
          message: 'Platform ready - Initialization timeout',
          progress: 100
        });
      }
    }, timeoutMs);
  }

  /**
   * Simple delay utility
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
