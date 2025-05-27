// src/app/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { Auth, authState }    from '@angular/fire/auth';
import {
  GoogleAuthProvider,
  signInWithPopup,
  signOut,
  User,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  sendPasswordResetEmail,
  signInWithCredential
}                               from 'firebase/auth';
import { Observable }           from 'rxjs';
import { FirebaseAuthentication } from '@capacitor-firebase/authentication';
import { Capacitor }            from '@capacitor/core';
import { Platform }             from '@ionic/angular/standalone';
import { Router, NavigationExtras } from '@angular/router';
import { NavController }        from '@ionic/angular/standalone';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Grab the injected Auth instance
  private auth = inject(Auth);
  private platform = inject(Platform);
  private router = inject(Router);
  private navCtrl = inject(NavController);

  // Expose an Observable of the current user (null if signed out)
  user$: Observable<User | null> = authState(this.auth);

  /**
   * Navigate to dashboard and prevent going back to login/register
   */
  navigateToDashboard(): void {
    // Use Ionic NavController for better transition control
    // Root navigation makes this the new history root - can't go back
    this.navCtrl.navigateRoot('/dashboard', { 
      animationDirection: 'forward'
    });

    // Alternative approach using Angular Router (if NavController doesn't work well)
    // this.router.navigate(['/dashboard'], { 
    //   replaceUrl: true // Replace URL in history instead of pushing
    // });
  }

  /**
   * Sign in with Google via a popup/webflow on web or native on mobile
   */
  async signInWithGoogle(): Promise<User> {
    // For mobile (Android/iOS) use the native Google Sign In
    if (Capacitor.isNativePlatform()) {
      // Sign in with Google on native platform
      const result = await FirebaseAuthentication.signInWithGoogle();
      
      // The user is now signed in on the native layer, but we need to sign in on the web layer too
      if (result.credential) {
        // Sign in with the credential on the web layer
        const credential = GoogleAuthProvider.credential(
          result.credential.idToken, 
          result.credential.accessToken
        );
        const userCredential = await signInWithCredential(this.auth, credential);
        return userCredential.user;
      } else {
        throw new Error('No credential returned from native Google sign-in');
      }
    } else {
      // On web, use the Firebase popup sign-in
      const provider = new GoogleAuthProvider();
      const credential = await signInWithPopup(this.auth, provider);
      return credential.user;
    }
  }

  /**
   * Sign in with email and password
   */
  async signInWithEmail(email: string, password: string): Promise<User> {
    if (Capacitor.isNativePlatform()) {
      // Use Capacitor plugin for native platforms
      const result = await FirebaseAuthentication.signInWithEmailAndPassword({
        email,
        password
      });
      // Return the Firebase user from the result
      if (result.user) {
        const userCredential = await signInWithEmailAndPassword(this.auth, email, password);
        return userCredential.user;
      } else {
        throw new Error('No user returned from native email/password sign-in');
      }
    } else {
      // Use Firebase web SDK for browser
      const credential = await signInWithEmailAndPassword(this.auth, email, password);
      return credential.user;
    }
  }

  /**
   * Create a new user with email and password
   */
  async createUserWithEmail(email: string, password: string): Promise<User> {
    if (Capacitor.isNativePlatform()) {
      // Use Capacitor plugin for native platforms
      const result = await FirebaseAuthentication.createUserWithEmailAndPassword({
        email,
        password
      });
      
      if (result.user) {
        // Also create on web layer to keep them in sync
        const userCredential = await createUserWithEmailAndPassword(this.auth, email, password);
        return userCredential.user;
      } else {
        throw new Error('No user returned from native account creation');
      }
    } else {
      // Use Firebase web SDK for browser
      const credential = await createUserWithEmailAndPassword(this.auth, email, password);
      return credential.user;
    }
  }

  /**
   * Send password reset email
   */
  async resetPassword(email: string): Promise<void> {
    if (Capacitor.isNativePlatform()) {
      await FirebaseAuthentication.sendPasswordResetEmail({ email });
    } else {
      await sendPasswordResetEmail(this.auth, email);
    }
  }

  /**
   * Sign out the current user
   */
  async signOut(): Promise<void> {
    if (Capacitor.isNativePlatform()) {
      // Sign out on native layer
      await FirebaseAuthentication.signOut();
    }
    // Always sign out on web layer
    await signOut(this.auth);
  }

  /**
   * Sign out and navigate to login page
   */
  async logout(): Promise<void> {
    await this.signOut();
    this.navCtrl.navigateRoot('/login', { 
      animationDirection: 'back' 
    });
  }
}