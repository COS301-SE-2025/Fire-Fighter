import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule, ToastController } from '@ionic/angular';
import { Router, NavigationStart } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Auth } from '@angular/fire/auth';
import { inject } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-inactive-account',
  templateUrl: './inactive-account.page.html',
  styleUrls: ['./inactive-account.page.scss'],
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule],
  animations: [
    trigger('modalBackdrop', [
      state('hidden', style({
        opacity: 0
      })),
      state('visible', style({
        opacity: 1
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ]),
    trigger('modalPanel', [
      state('hidden', style({
        opacity: 0,
        transform: 'scale(0.95) translateY(-10px)'
      })),
      state('visible', style({
        opacity: 1,
        transform: 'scale(1) translateY(0)'
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ])
  ]
})
export class InactiveAccountPage implements OnInit, OnDestroy {

  isCheckingStatus = false;
  private auth = inject(Auth);
  private location = inject(Location);

  // Success modal management
  isSuccessModalOpen = false;
  successModalAnimationState = 'hidden';

  // Store original theme state
  private hadDarkClass = false;
  
  // Subscription for router events
  private routerSubscription?: Subscription;

  constructor(
    private router: Router,
    private toastController: ToastController,
    private authService: AuthService
  ) { }

  ngOnInit() {
    // Store original theme state
    this.hadDarkClass = document.documentElement.classList.contains('dark');
    
    // Force dark theme for this page
    document.documentElement.classList.add('dark');
    document.body.classList.add('dark');
    
    // Prevent browser back button navigation
    // Push a dummy state to prevent going back to registration flow
    history.pushState(null, '', location.href);
    
    // Listen for popstate (back button) and prevent navigation
    window.addEventListener('popstate', this.preventBackNavigation);
    
    // Also monitor router navigation to prevent programmatic back navigation
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // If trying to navigate back to registration pages, redirect to login instead
        if (event.url.includes('/access-request') || event.url.includes('/register')) {
          this.router.navigate(['/login']);
        }
      }
    });
  }

  ngOnDestroy() {
    // Restore original theme state when leaving the page
    if (!this.hadDarkClass) {
      document.documentElement.classList.remove('dark');
      document.body.classList.remove('dark');
    }
    
    // Clean up event listener and subscription
    window.removeEventListener('popstate', this.preventBackNavigation);
    this.routerSubscription?.unsubscribe();
  }
  
  /**
   * Prevent back navigation to registration flow pages
   * This is a security measure to prevent unauthorized access
   */
  private preventBackNavigation = (event: PopStateEvent) => {
    // Push state again to prevent actual navigation
    history.pushState(null, '', location.href);
    
    // Optionally show a message to the user
    this.showStatusAlert('Please use the "Back to Login" button to navigate away from this page.');
  };

  /**
   * Check the current account status with backend API
   */
  async checkStatus() {
    this.isCheckingStatus = true;
    
    try {
      // Get current user from Firebase
      const currentUser = this.auth.currentUser;
      
      if (!currentUser) {
        await this.showStatusAlert('Authentication error. Please log in again.');
        await this.backToLogin();
        return;
      }

      // Call backend API to check if user is now authorized
      const isAuthorized = await this.authService.checkUserAuthorization(currentUser.uid).toPromise();

      if (isAuthorized) {
        // Show success modal
        this.showSuccessModal();
      } else {
        await this.showStatusAlert('Your account is still pending administrator approval. You will receive an email notification once your account has been activated.');
      }
      
    } catch (error: any) {
      if (error.message === 'Service temporarily unavailable') {
        await this.showStatusAlert('Unable to connect to the server. Please check your internet connection and try again.');
      } else {
        await this.showStatusAlert('Unable to check account status at this time. Please try again later or contact support.');
      }
    } finally {
      this.isCheckingStatus = false;
    }
  }

  /**
   * Navigate back to the login page
   * Clear all authentication tokens and sign out the user
   */
  async backToLogin() {
    try {
      // Use the AuthService logout method which signs out and clears all tokens
      await this.authService.logout();
    } catch (error) {
      // Even if there's an error, navigate to login page
      this.router.navigate(['/login']);
    }
  }

  /**
   * Show account activated success modal
   */
  showSuccessModal() {
    this.isSuccessModalOpen = true;
    // Trigger animation
    setTimeout(() => {
      this.successModalAnimationState = 'visible';
    }, 10);
  }

  /**
   * Close success modal and navigate to login
   */
  closeSuccessModal() {
    this.successModalAnimationState = 'hidden';
    setTimeout(() => {
      this.isSuccessModalOpen = false;
      this.backToLogin();
    }, 200);
  }

  /**
   * Show status toast message
   */
  private async showStatusAlert(message: string) {
    const toast = await this.toastController.create({
      message: message,
      duration: 5000,
      position: 'top',
      cssClass: 'custom-toast',
      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel'
        }
      ]
    });

    await toast.present();
  }

}