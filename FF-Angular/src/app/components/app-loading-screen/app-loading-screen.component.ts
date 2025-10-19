import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppLoadingService, AppLoadingState } from '../../services/app-loading.service';
import { ThemeService } from '../../services/theme.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-loading-screen',
  templateUrl: './app-loading-screen.component.html',
  styleUrls: ['./app-loading-screen.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class AppLoadingScreenComponent implements OnInit, OnDestroy {
  loadingState: AppLoadingState = {
    isLoading: true,
    message: 'Starting FireFighter platform...',
    progress: 0
  };

  isDarkMode = false;
  private subscription?: Subscription;

  constructor(
    private appLoadingService: AppLoadingService,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    // Get current theme
    this.isDarkMode = this.themeService.getCurrentTheme();
    
    // Subscribe to loading state changes
    this.subscription = this.appLoadingService.loadingState$.subscribe(
      (state: AppLoadingState) => {
        this.loadingState = state;
      }
    );
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
