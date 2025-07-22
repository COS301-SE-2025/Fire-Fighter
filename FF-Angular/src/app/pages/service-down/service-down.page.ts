import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonContent } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-service-down',
  templateUrl: './service-down.page.html',
  styleUrls: ['./service-down.page.scss'],
  standalone: true,
  imports: [CommonModule, IonContent]
})
export class ServiceDownPage implements OnInit, OnDestroy {
  isRetrying = false;
  lastConnectionTime: string | null = null;
  estimatedRecovery: string | null = null;
  currentTime = new Date();
  
  private timeUpdateSubscription?: Subscription;
  private retryAttempts = 0;
  private maxRetryAttempts = 3;

  constructor(private router: Router) {}

  ngOnInit() {
    this.initializeServiceDownPage();
    this.startTimeUpdates();
    this.setEstimatedRecovery();
  }

  ngOnDestroy() {
    if (this.timeUpdateSubscription) {
      this.timeUpdateSubscription.unsubscribe();
    }
  }

  private initializeServiceDownPage() {
    // Get last connection time from localStorage if available
    const lastConnection = localStorage.getItem('lastSuccessfulConnection');
    if (lastConnection) {
      this.lastConnectionTime = new Date(lastConnection).toLocaleString();
    }

    // Set current time
    this.currentTime = new Date();
  }

  private startTimeUpdates() {
    // Update current time every minute
    this.timeUpdateSubscription = interval(60000).subscribe(() => {
      this.currentTime = new Date();
    });
  }

  private setEstimatedRecovery() {
    // Set a realistic estimated recovery time (15-30 minutes from now)
    const estimatedMinutes = Math.floor(Math.random() * 15) + 15;
    const recoveryTime = new Date();
    recoveryTime.setMinutes(recoveryTime.getMinutes() + estimatedMinutes);
    this.estimatedRecovery = recoveryTime.toLocaleTimeString([], { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  async retryConnection() {
    if (this.isRetrying || this.retryAttempts >= this.maxRetryAttempts) {
      return;
    }

    this.isRetrying = true;
    this.retryAttempts++;

    try {
      // Simulate connection attempt
      await this.simulateConnectionAttempt();
      
      // If successful, navigate back to dashboard
      this.router.navigate(['/dashboard']);
      
    } catch (error) {
      console.error('Connection retry failed:', error);
      
      if (this.retryAttempts >= this.maxRetryAttempts) {
        // Show message that max retries reached
        this.showMaxRetriesMessage();
      }
    } finally {
      this.isRetrying = false;
    }
  }

  private async simulateConnectionAttempt(): Promise<void> {
    return new Promise((resolve, reject) => {
      // Simulate network delay
      setTimeout(() => {
        // For demo purposes, randomly succeed or fail
        // In real implementation, this would be an actual API call
        const success = Math.random() > 0.7; // 30% success rate for demo
        
        if (success) {
          // Store successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
          resolve();
        } else {
          reject(new Error('Connection failed'));
        }
      }, 2000);
    });
  }

  private showMaxRetriesMessage() {
    // In a real app, you might show a toast or modal
    console.log('Maximum retry attempts reached. Please try again later.');
  }

  goToOfflineMode() {
    // Navigate to a limited offline version or cached data view
    // For now, navigate to dashboard with offline flag
    this.router.navigate(['/dashboard'], { 
      queryParams: { offline: 'true' } 
    });
  }

  // Method to check if we should show retry button
  get canRetry(): boolean {
    return !this.isRetrying && this.retryAttempts < this.maxRetryAttempts;
  }

  // Method to get retry button text
  get retryButtonText(): string {
    if (this.isRetrying) {
      return 'Connecting...';
    }
    
    if (this.retryAttempts >= this.maxRetryAttempts) {
      return 'Max Retries Reached';
    }
    
    return this.retryAttempts > 0 
      ? `Retry Connection (${this.retryAttempts}/${this.maxRetryAttempts})` 
      : 'Retry Connection';
  }
}
