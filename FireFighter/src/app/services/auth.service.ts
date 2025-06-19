// src/app/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Auth, authState }    from '@angular/fire/auth';
import {
  GoogleAuthProvider,
  signInWithPopup,
  signInWithRedirect,
  getRedirectResult,
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
import { environment }          from '../../environments/environment';

interface UserVerificationPayload {
  firebaseUid: string;
  username: string;
  email: string;
  department: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Grab the injected Auth instance
  private auth = inject(Auth);
  private platform = inject(Platform);
  private router = inject(Router);
  private navCtrl = inject(NavController);
  private http = inject(HttpClient);

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
   * Verify user with backend API
   */
  private async verifyUserWithBackend(user: User, department: string = 'Default Department'): Promise<void> {
    // Create form data using HttpParams for application/x-www-form-urlencoded format
    const params = new HttpParams()
      .set('firebaseUid', user.uid)
      .set('username', user.displayName || user.email?.split('@')[0] || 'Unknown User')
      .set('email', user.email || '')
      .set('department', department);

    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    try {
      const response = await this.http.post(`${environment.apiUrl}/users/verify`, params.toString(), { headers }).toPromise();
      console.log('User verified with backend:', response);
    } catch (error) {
      console.error('Failed to verify user with backend:', error);
      console.error('Error details:', error);
      throw error;
    }
  }

  /**
   * Detect if the user is on Safari browser
   */
  private isSafari(): boolean {
    const userAgent = navigator.userAgent;
    return /^((?!chrome|android).)*safari/i.test(userAgent) || 
           /(iPad|iPhone|iPod).*Safari/i.test(userAgent);
  }

  /**
   * Sign in with Google via a popup/webflow on web or native on mobile
   */
  async signInWithGoogle(): Promise<User> {
    let user: User;
    
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
        user = userCredential.user;
      } else {
        throw new Error('No credential returned from native Google sign-in');
      }
    } else {
      // On web, check if Safari and use appropriate method
      const provider = new GoogleAuthProvider();
      // Add extra scopes for better compatibility
      provider.addScope('email');
      provider.addScope('profile');
      
      if (this.isSafari()) {
        // Safari: Use redirect-based authentication for better compatibility
        await signInWithRedirect(this.auth, provider);
        // Note: This will cause a page redirect, so we won't reach the return statement
        // The actual sign-in completion will be handled by checkForRedirectResult
        throw new Error('Redirect initiated - should not reach this point');
      } else {
        // Other browsers: Use popup-based authentication
        const credential = await signInWithPopup(this.auth, provider);
        user = credential.user;
      }
    }

    // Verify user with backend after successful Firebase authentication
    await this.verifyUserWithBackend(user);
    
    return user;
  }

  /**
   * Check for redirect result when the page loads (for Safari redirect-based auth)
   */
  async checkForRedirectResult(): Promise<User | null> {
    try {
      const result = await getRedirectResult(this.auth);
      
      if (result?.user) {
        // Verify user with backend after successful Firebase authentication
        await this.verifyUserWithBackend(result.user);
        return result.user;
      }
      
      return null;
    } catch (error) {
      console.error('Error checking redirect result:', error);
      throw error;
    }
  }

  /**
   * Sign in with email and password
   */
  async signInWithEmail(email: string, password: string): Promise<User> {
    let user: User;
    
    if (Capacitor.isNativePlatform()) {
      // Use Capacitor plugin for native platforms
      const result = await FirebaseAuthentication.signInWithEmailAndPassword({
        email,
        password
      });
      // Return the Firebase user from the result
      if (result.user) {
        const userCredential = await signInWithEmailAndPassword(this.auth, email, password);
        user = userCredential.user;
      } else {
        throw new Error('No user returned from native email/password sign-in');
      }
    } else {
      // Use Firebase web SDK for browser
      const credential = await signInWithEmailAndPassword(this.auth, email, password);
      user = credential.user;
    }

    // Verify user with backend after successful Firebase authentication
    await this.verifyUserWithBackend(user);
    
    return user;
  }

  /**
   * Create a new user with email and password
   */
  async createUserWithEmail(email: string, password: string): Promise<User> {
    let user: User;
    
    if (Capacitor.isNativePlatform()) {
      // Use Capacitor plugin for native platforms
      const result = await FirebaseAuthentication.createUserWithEmailAndPassword({
        email,
        password
      });
      
      if (result.user) {
        // Also create on web layer to keep them in sync
        const userCredential = await createUserWithEmailAndPassword(this.auth, email, password);
        user = userCredential.user;
      } else {
        throw new Error('No user returned from native account creation');
      }
    } else {
      // Use Firebase web SDK for browser
      const credential = await createUserWithEmailAndPassword(this.auth, email, password);
      user = credential.user;
    }

    // Verify new user with backend after successful Firebase registration
    await this.verifyUserWithBackend(user);
    
    return user;
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