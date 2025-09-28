import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take, filter, timeout, catchError } from 'rxjs/operators';
import { combineLatest, of, timer } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NavController } from '@ionic/angular/standalone';

// Modern functional route guard for Angular 16+
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const navCtrl = inject(NavController);

  return authService.user$.pipe(
    take(1),
    map(user => {
      // If there's a user, they're authenticated, allow access
      if (user) {
        return true;
      }

      // User isn't authenticated, redirect to login
      navCtrl.navigateRoot('/login', { animationDirection: 'forward' });
      return false;
    })
  );
};

// Route guard to prevent authenticated users from accessing login/register pages
export const redirectLoggedInToHome: CanActivateFn = () => {
  const authService = inject(AuthService);
  const navCtrl = inject(NavController);

  return authService.user$.pipe(
    take(1),
    map(user => {
      // If user is already logged in, redirect to dashboard
      if (user) {
        navCtrl.navigateRoot('/dashboard', { animationDirection: 'forward' });
        return false;
      }
      
      // Not logged in, allow access to login/register pages
      return true;
    })
  );
};

// Admin guard to protect admin-only pages
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const navCtrl = inject(NavController);

  return combineLatest([authService.user$, authService.isAdmin$]).pipe(
    // Wait for the auth state to be properly initialized
    // Skip initial emissions where user exists but admin status might not be loaded yet
    filter(([user, isAdmin]) => {
      // If no user, we can proceed (will redirect to login)
      if (!user) return true;
      
      // If user exists, wait for admin status to be determined
      // This prevents the race condition where user is loaded but admin status is still false
      const userProfile = authService.getCurrentUserProfile();
      return userProfile !== null || isAdmin === true;
    }),
    take(1),
    timeout(10000), // 10 second timeout to prevent infinite waiting
    map(([user, isAdmin]) => {
      // First check if user is authenticated
      if (!user) {
        navCtrl.navigateRoot('/login', { animationDirection: 'forward' });
        return false;
      }

      // Then check if user is admin
      if (!isAdmin) {
        // User is authenticated but not admin, redirect to dashboard
        navCtrl.navigateRoot('/dashboard', { 
          animationDirection: 'forward',
          queryParams: { error: 'admin_access_denied' }
        });
        return false;
      }

      // User is authenticated and is admin, allow access
      return true;
    }),
    catchError((error) => {
      console.error('Admin guard timeout or error:', error);
      // On timeout or error, redirect to dashboard for safety
      navCtrl.navigateRoot('/dashboard', { 
        animationDirection: 'forward',
        queryParams: { error: 'auth_timeout' }
      });
      return of(false);
    })
  );
}; 