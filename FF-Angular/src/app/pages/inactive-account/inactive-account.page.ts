import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule, ToastController } from '@ionic/angular';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-inactive-account',
  templateUrl: './inactive-account.page.html',
  styleUrls: ['./inactive-account.page.scss'],
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule]
})
export class InactiveAccountPage implements OnInit {

  isCheckingStatus = false;

  constructor(
    private router: Router,
    private toastController: ToastController,
    private authService: AuthService
  ) { }

  ngOnInit() {
    // Force dark theme
    document.documentElement.classList.add('dark');
    document.body.classList.add('dark');
  }

  /**
   * Check the current account status (simulate API call)
   */
  async checkStatus() {
    this.isCheckingStatus = true;
    
    try {
      // Simulate API call to check account status
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // For demo purposes, show different messages based on random status
      const statuses = [
        'Your account is still pending administrator approval.',
        'Your account is under review. Please check back in 24-48 hours.',
        'Your account has been approved! Please try logging in again.',
        'Additional information is required. Please contact your administrator.'
      ];
      
      const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];
      
      // Show status message
      this.showStatusAlert(randomStatus);
      
    } catch (error) {
      console.error('Error checking account status:', error);
      this.showStatusAlert('Unable to check account status at this time. Please try again later or contact support.');
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
      console.log('🔄 Signing out and clearing tokens...');
      // Use the AuthService logout method which signs out and clears all tokens
      await this.authService.logout();
      console.log('✅ Successfully signed out and redirected to login');
    } catch (error) {
      console.error('❌ Error during sign out:', error);
      // Even if there's an error, navigate to login page
      this.router.navigate(['/login']);
    }
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
          role: 'cancel',
          handler: () => {
            console.log('Toast dismissed');
          }
        }
      ]
    });

    await toast.present();
  }

}