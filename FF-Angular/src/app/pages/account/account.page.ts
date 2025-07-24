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

@Component({
  selector: 'app-account',
  templateUrl: './account.page.html',
  styleUrls: ['./account.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, RouterModule, NavbarComponent]
})
export class AccountPage implements OnInit, OnDestroy {
  user$: Observable<User | null>;
  isAdmin$: Observable<boolean>;
  private subscription: Subscription = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.user$ = this.authService.user$;
    this.isAdmin$ = this.authService.isAdmin$;
  }

  ngOnInit() {
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
}
