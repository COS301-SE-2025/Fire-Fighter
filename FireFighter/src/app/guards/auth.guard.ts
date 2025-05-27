import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs/operators';
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