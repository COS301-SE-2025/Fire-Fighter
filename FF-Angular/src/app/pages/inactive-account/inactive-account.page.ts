import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule, ToastController } from '@ionic/angular';
import { Router } from '@angular/router';

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
    private toastController: ToastController
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
   */
  backToLogin() {
    this.router.navigate(['/login']);
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