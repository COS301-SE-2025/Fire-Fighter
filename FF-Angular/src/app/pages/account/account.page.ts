import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { User } from 'firebase/auth';
import { ToastController } from '@ionic/angular';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-account',
  templateUrl: './account.page.html',
  styleUrls: ['./account.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, RouterModule, NavbarComponent],
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
export class AccountPage implements OnInit, OnDestroy {
  user$: Observable<User | null>;
  isAdmin$: Observable<boolean>;
  private subscription: Subscription = new Subscription();

  // Contact number management
  userProfile: any = null;
  isContactNumberEditOpen = false;
  editingContactNumber = '';
  isUpdatingContact = false;
  contactModalAnimationState = 'hidden';

  // Dolibarr ID management
  isDolibarrIdEditOpen = false;
  editingDolibarrId = '';
  isUpdatingDolibarrId = false;
  dolibarrModalAnimationState = 'hidden';

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastController: ToastController
  ) {
    this.user$ = this.authService.user$;
    this.isAdmin$ = this.authService.isAdmin$;
  }

  ngOnInit() {
    // Subscribe to user profile changes
    this.subscription.add(
      this.authService.userProfile$.subscribe(profile => {
        console.log('👤 Account page - user profile updated:', profile);
        this.userProfile = profile;
      })
    );

    // Also get the current profile immediately
    this.userProfile = this.authService.getCurrentUserProfile();
    console.log('👤 Account page - initial profile:', this.userProfile);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  // Get user role - uses the actual admin status from the API
  getUserRole(): string {
    return this.authService.isCurrentUserAdmin() ? 'Administrator' : 'User';
  }

  // Get user display name safely
  getUserDisplayName(user: User | null): string {
    if (!user) return 'FireFighter User';
    
    if (user.displayName) {
      return user.displayName;
    }
    
    if (user.email) {
      const emailParts = user.email.split('@');
      return emailParts.length > 0 ? emailParts[0] : 'FireFighter User';
    }
    
    return 'FireFighter User';
  }

  // Get user initials for avatar fallback
  getUserInitials(user: User | null): string {
    if (!user) return 'U';
    
    const displayName = user.displayName;
    if (displayName) {
      const names = displayName.split(' ');
      if (names.length >= 2) {
        return (names[0][0] + names[names.length - 1][0]).toUpperCase();
      }
      return names[0][0].toUpperCase();
    }
    
    const email = user.email;
    if (email) {
      return email[0].toUpperCase();
    }
    
    return 'U';
  }

  // Format join date
  getJoinDate(user: User | null): string {
    if (!user?.metadata?.creationTime) return 'Unknown';
    
    const date = new Date(user.metadata.creationTime);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  // Format last sign in
  getLastSignIn(user: User | null): string {
    if (!user?.metadata?.lastSignInTime) return 'Unknown';
    
    const date = new Date(user.metadata.lastSignInTime);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));
    
    if (diffInHours < 1) {
      return 'Just now';
    } else if (diffInHours < 24) {
      return `${diffInHours} hour${diffInHours > 1 ? 's' : ''} ago`;
    } else {
      const diffInDays = Math.floor(diffInHours / 24);
      return `${diffInDays} day${diffInDays > 1 ? 's' : ''} ago`;
    }
  }

  // Track if profile image failed to load
  profileImageError = false;

  // Handle image loading errors
  onImageError(event: any) {
    console.log('Profile image failed to load:', event);
    this.profileImageError = true;
  }

  // Check if we should show profile image
  shouldShowProfileImage(user: User | null): boolean {
    return !!(user?.photoURL && user.photoURL.trim() !== '' && !this.profileImageError);
  }

  async logout() {
    try {
      await this.authService.logout();
      this.router.navigate(['/login']);
    } catch (error) {
      console.error('Logout error:', error);
    }
  }

  // Contact number management methods
  toggleContactNumberEdit() {
    this.isContactNumberEditOpen = true;
    this.editingContactNumber = this.userProfile?.contactNumber || '';
    // Trigger animation
    setTimeout(() => {
      this.contactModalAnimationState = 'visible';
    }, 10);
  }

  cancelContactNumberEdit() {
    this.contactModalAnimationState = 'hidden';
    // Wait for animation to complete before hiding modal
    setTimeout(() => {
      this.isContactNumberEditOpen = false;
      this.editingContactNumber = '';
    }, 150);
  }

  // Contact number validation
  isValidContactNumber(): boolean {
    const contactNumber = this.editingContactNumber.trim();
    // Check if it's exactly 10 digits
    return /^[0-9]{10}$/.test(contactNumber);
  }

  // Handle input to only allow numbers
  onContactNumberInput(event: any) {
    const input = event.target;
    const value = input.value;

    // Remove any non-digit characters
    const numbersOnly = value.replace(/[^0-9]/g, '');

    // Limit to 10 digits
    const limitedValue = numbersOnly.substring(0, 10);

    // Update the model and input value
    this.editingContactNumber = limitedValue;
    input.value = limitedValue;
  }

  async saveContactNumber() {
    if (!this.userProfile?.userId) {
      await this.presentToast('User not found. Please try logging in again.', 'danger');
      return;
    }

    // Validate contact number
    if (!this.isValidContactNumber()) {
      await this.presentToast('Please enter a valid 10-digit phone number.', 'warning');
      return;
    }

    this.isUpdatingContact = true;

    try {
      const updatedProfile = await this.authService.updateContactNumber(
        this.userProfile.userId,
        this.editingContactNumber.trim()
      ).toPromise();

      if (updatedProfile) {
        // The auth service will automatically update the userProfile$ observable
        // but we can also update our local reference for immediate UI update
        this.userProfile = updatedProfile;

        // Close the modal with animation
        this.contactModalAnimationState = 'hidden';
        setTimeout(() => {
          this.isContactNumberEditOpen = false;
          this.editingContactNumber = '';
        }, 150);

        await this.presentToast('Contact number updated successfully!', 'success');
      }
    } catch (error: any) {
      console.error('Failed to update contact number:', error);

      let errorMessage = 'Failed to update contact number. Please try again.';
      if (error.message === 'Service temporarily unavailable') {
        errorMessage = 'Service is temporarily unavailable. Please try again later.';
      }

      await this.presentToast(errorMessage, 'danger');
    } finally {
      this.isUpdatingContact = false;
    }
  }

  private async presentToast(message: string, color: 'success' | 'danger' | 'warning' = 'success') {
    const toast = await this.toastController.create({
      message,
      duration: 3000,
      position: 'bottom',
      color,
    });
    await toast.present();
  }

  // Dolibarr ID management methods
  toggleDolibarrIdEdit() {
    this.isDolibarrIdEditOpen = true;
    this.editingDolibarrId = this.userProfile?.dolibarrId || '';
    // Trigger animation
    setTimeout(() => {
      this.dolibarrModalAnimationState = 'visible';
    }, 10);
  }

  cancelDolibarrIdEdit() {
    this.dolibarrModalAnimationState = 'hidden';
    // Wait for animation to complete before hiding modal
    setTimeout(() => {
      this.isDolibarrIdEditOpen = false;
      this.editingDolibarrId = '';
    }, 150);
  }

  // Dolibarr ID validation
  isValidDolibarrId(): boolean {
    const dolibarrId = this.editingDolibarrId.trim();
    // Allow empty string or any alphanumeric string (adjust validation as needed)
    return dolibarrId === '' || /^[a-zA-Z0-9_-]+$/.test(dolibarrId);
  }

  // Handle input to allow alphanumeric characters
  onDolibarrIdInput(event: any) {
    const input = event.target;
    const value = input.value;

    // Allow alphanumeric, underscores, and hyphens
    const cleanValue = value.replace(/[^a-zA-Z0-9_-]/g, '');

    // Update the model and input value
    this.editingDolibarrId = cleanValue;
    input.value = cleanValue;
  }

  async saveDolibarrId() {
    // Validate Dolibarr ID
    if (!this.isValidDolibarrId()) {
      await this.presentToast('Please enter a valid Dolibarr ID (alphanumeric characters only).', 'warning');
      return;
    }

    this.isUpdatingDolibarrId = true;

    try {
      const response = await this.authService.updateDolibarrId(this.editingDolibarrId.trim()).toPromise();

      if (response) {
        // Update local profile
        if (this.userProfile) {
          this.userProfile.dolibarrId = this.editingDolibarrId.trim();
        }

        // Close the modal with animation
        this.dolibarrModalAnimationState = 'hidden';
        setTimeout(() => {
          this.isDolibarrIdEditOpen = false;
          this.editingDolibarrId = '';
        }, 150);

        await this.presentToast('Dolibarr ID updated successfully!', 'success');
      }
    } catch (error: any) {
      console.error('Failed to update Dolibarr ID:', error);

      let errorMessage = 'Failed to update Dolibarr ID. Please try again.';
      if (error.message === 'Service temporarily unavailable') {
        errorMessage = 'Service is temporarily unavailable. Please try again later.';
      }

      await this.presentToast(errorMessage, 'danger');
    } finally {
      this.isUpdatingDolibarrId = false;
    }
  }
}
