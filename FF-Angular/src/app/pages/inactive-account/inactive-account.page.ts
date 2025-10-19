import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule, ToastController } from '@ionic/angular';
import { Router } from '@angular/router';
import { Auth } from '@angular/fire/auth';
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
  private auth = inject(Auth);
  private authService = inject(AuthService);

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
   * Check the current account status from backend
   */
  async checkStatus() {
    this.isCheckingStatus = true;
    
    try {
      // Get current user from Firebase
      const currentUser = this.auth.currentUser;
      
      if (!currentUser) {
        this.showStatusAlert('No authenticated user found. Please try logging in again.');
        return;
      }
      
      // Check registration status from backend
      const status: any = await this.authService.getRegistrationStatus(currentUser.uid).toPromise();
      console.log('📊 Registration status:', status);
      
      let message = '';
      if (status.status === 'PENDING') {
        message = 'Your account is still pending administrator approval. You will receive an email once your account has been reviewed.';
      } else if (status.status === 'APPROVED') {
        message = 'Your account has been approved! Please try logging in again.';
      } else if (status.status === 'REJECTED') {
        message = `Your account request was rejected. Reason: ${status.rejectionReason || 'Not specified'}`;
      } else if (status.status === 'REGISTERED') {
        if (status.authorized) {
          message = 'Your account is active and authorized. You can now log in.';
        } else {
          message = 'Your account exists but is not yet authorized. Please contact an administrator.';
        }
      } else {
        message = 'Your account status could not be determined. Please contact support.';
      }
      
      this.showStatusAlert(message);
      
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