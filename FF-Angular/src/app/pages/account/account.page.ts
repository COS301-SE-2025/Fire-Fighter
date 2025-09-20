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
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.page.html',
  styleUrls: ['./account.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, RouterModule, NavbarComponent, TranslateModule],
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



  constructor(
    private authService: AuthService,
    private router: Router,
    private toastController: ToastController,
    private languageService: LanguageService
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

  // Get translated user role
  getTranslatedRole(): string {
    const isAdmin = this.authService.isCurrentUserAdmin();
    return isAdmin ?
      this.languageService.getInstantTranslation('ACCOUNT.ROLES.ADMINISTRATOR') :
      this.languageService.getInstantTranslation('ACCOUNT.ROLES.USER');
  }

  // Get user display name safely
  getUserDisplayName(user: User | null): string {
    const fallbackName = this.languageService.getInstantTranslation('ACCOUNT.PROFILE.FIREFIGHTER_USER');

    if (!user) return fallbackName;

    if (user.displayName) {
      return user.displayName;
    }

    if (user.email) {
      const emailParts = user.email.split('@');
      return emailParts.length > 0 ? emailParts[0] : fallbackName;
    }

    return fallbackName;
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
    if (!user?.metadata?.creationTime) {
      return this.languageService.getInstantTranslation('ACCOUNT.PROFILE.UNKNOWN');
    }

    const date = new Date(user.metadata.creationTime);
    const locale = this.languageService.getCurrentLanguage() === 'de' ? 'de-DE' : 'en-US';
    return date.toLocaleDateString(locale, {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  // Format last sign in
  getLastSignIn(user: User | null): string {
    if (!user?.metadata?.lastSignInTime) {
      return this.languageService.getInstantTranslation('ACCOUNT.PROFILE.UNKNOWN');
    }

    const date = new Date(user.metadata.lastSignInTime);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));

    if (diffInHours < 1) {
      return this.languageService.getInstantTranslation('ACCOUNT.PROFILE.JUST_NOW');
    } else if (diffInHours < 24) {
      const hoursKey = diffInHours === 1 ? 'ACCOUNT.PROFILE.HOURS_AGO' : 'ACCOUNT.PROFILE.HOURS_AGO_PLURAL';
      return `${diffInHours} ${this.languageService.getInstantTranslation(hoursKey)}`;
    } else {
      const diffInDays = Math.floor(diffInHours / 24);
      const daysKey = diffInDays === 1 ? 'ACCOUNT.PROFILE.DAYS_AGO' : 'ACCOUNT.PROFILE.DAYS_AGO_PLURAL';
      return `${diffInDays} ${this.languageService.getInstantTranslation(daysKey)}`;
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
      const message = this.languageService.getInstantTranslation('ACCOUNT.TOAST.USER_NOT_FOUND');
      await this.presentToast(message, 'danger');
      return;
    }

    // Validate contact number
    if (!this.isValidContactNumber()) {
      const message = this.languageService.getInstantTranslation('ACCOUNT.TOAST.INVALID_PHONE');
      await this.presentToast(message, 'warning');
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

        const successMessage = this.languageService.getInstantTranslation('ACCOUNT.TOAST.UPDATE_SUCCESS');
        await this.presentToast(successMessage, 'success');
      }
    } catch (error: any) {
      console.error('Failed to update contact number:', error);

      let errorMessage = this.languageService.getInstantTranslation('ACCOUNT.TOAST.UPDATE_FAILED');
      if (error.message === 'Service temporarily unavailable') {
        errorMessage = this.languageService.getInstantTranslation('ACCOUNT.TOAST.SERVICE_UNAVAILABLE');
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


}
