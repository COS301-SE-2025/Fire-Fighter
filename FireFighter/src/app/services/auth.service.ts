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
import { Observable, BehaviorSubject }           from 'rxjs';
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

interface UserVerificationResponse {
  userId: string;
  username: string;
  email: string;
  department: string;
  isAuthorized: boolean;
  isAdmin: boolean;
  role: string;
  createdAt: string;
  lastLogin: string;
  userRoles: Array<{
    id: number;
    role: {
      id: number;
      name: string;
    };
    assignedAt: string;
    assignedBy: string;
  }>;
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

  // Admin status tracking
  private isAdminSubject = new BehaviorSubject<boolean>(false);
  public isAdmin$ = this.isAdminSubject.asObservable();

  // User profile data
  private userProfileSubject = new BehaviorSubject<UserVerificationResponse | null>(null);
  public userProfile$ = this.userProfileSubject.asObservable();

  /**
   * Check if current user is an admin
   */
  isCurrentUserAdmin(): boolean {
    return this.isAdminSubject.value;
  }

  /**
   * Get current user profile
   */
  getCurrentUserProfile(): UserVerificationResponse | null {
    return this.userProfileSubject.value;
  }

  /**
   * Clear admin status and user profile on logout
   */
  private clearUserData(): void {
    this.isAdminSubject.next(false);
    this.userProfileSubject.next(null);
  }

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
   * Verify user with backend API and store admin status
   */
  private async verifyUserWithBackend(user: User, department: string = 'Default Department'): Promise<UserVerificationResponse> {
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
      const response = await this.http.post<UserVerificationResponse>(`${environment.apiUrl}/users/verify`, params.toString(), { headers }).toPromise();
      console.log('User verified with backend:', response);
      
      if (response) {
        // Store the admin status and user profile
        this.isAdminSubject.next(response.isAdmin);
        this.userProfileSubject.next(response);
        
        console.log('User admin status:', response.isAdmin);
        return response;
      }
      
      throw new Error('No response from backend verification');
    } catch (error) {
      console.error('Failed to verify user with backend:', error);
      console.error('Error details:', error);
      // Clear user data on verification failure
      this.clearUserData();
      
      // IMPORTANT: Sign out the user from Firebase if backend verification fails
      // This ensures Firebase auth state stays consistent with our app state
      try {
        if (Capacitor.isNativePlatform()) {
          await FirebaseAuthentication.signOut();
        }
        await signOut(this.auth);
      } catch (signOutError) {
        console.error('Error signing out after backend verification failure:', signOutError);
      }
      
      throw error;
    }
  }

  /**
   * Detect if the user is on Safari browser
   */
  private isSafari(): boolean {
    const userAgent = navigator.userAgent;
    const isSafari = /^((?!chrome|android).)*safari/i.test(userAgent) || 
                     /(iPad|iPhone|iPod).*Safari/i.test(userAgent);
    console.log('Safari detection - User Agent:', userAgent);
    console.log('Safari detection result:', isSafari);
    return isSafari;
  }

  /**
   * Sign in with Google using popup method (fallback for when redirect fails)
   */
  private async signInWithPopupFallback(): Promise<User> {
    console.log('Attempting popup fallback...');
    const provider = new GoogleAuthProvider();
    provider.addScope('email');
    provider.addScope('profile');
    
    const credential = await signInWithPopup(this.auth, provider);
    return credential.user;
  }

  /**
   * Sign in with Google via a popup/webflow on web or native on mobile
   */
  async signInWithGoogle(): Promise<User> {
    let user: User;
    
    console.log('Starting Google sign-in process...');
    
    // For mobile (Android/iOS) use the native Google Sign In
    if (Capacitor.isNativePlatform()) {
      console.log('Using native platform Google sign-in');
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
      console.log('Using web platform Google sign-in');
      const provider = new GoogleAuthProvider();
      // Add extra scopes for better compatibility
      provider.addScope('email');
      provider.addScope('profile');
      
      if (this.isSafari()) {
        console.log('Safari detected, using redirect method');
        // Safari: Use redirect-based authentication for better compatibility
        await signInWithRedirect(this.auth, provider);
        // Note: This will cause a page redirect, so we won't reach the return statement
        // The actual sign-in completion will be handled by checkForRedirectResult
        throw new Error('Redirect initiated - should not reach this point');
      } else {
        console.log('Non-Safari browser, using popup method');
        // Other browsers: Use popup-based authentication
        const credential = await signInWithPopup(this.auth, provider);
        user = credential.user;
      }
    }

    console.log('Firebase authentication successful, verifying with backend...');
    // Verify user with backend after successful Firebase authentication
    await this.verifyUserWithBackend(user);
    
    console.log('Backend verification successful');
    return user;
  }

  /**
   * Sign in with Google using popup method (force popup instead of redirect)
   * This can be used as a manual fallback when redirect authentication fails
   */
  async signInWithGooglePopup(): Promise<User> {
    console.log('Starting Google sign-in with forced popup...');
    
    if (Capacitor.isNativePlatform()) {
      // On native platforms, use the same native method
      return this.signInWithGoogle();
    }
    
    // Force popup method regardless of browser
    const user = await this.signInWithPopupFallback();
    
    console.log('Firebase popup authentication successful, verifying with backend...');
    await this.verifyUserWithBackend(user);
    
    console.log('Backend verification successful');
    return user;
  }

  /**
   * Clear the redirect processing flag (useful for manual retry)
   */
  clearRedirectProcessingFlag(): void {
    (window as any)._firebaseRedirectProcessed = false;
    console.log('Redirect processing flag cleared');
  }

  /**
   * Check for redirect result when the page loads (for Safari redirect-based auth)
   * This should only be called once per page load to avoid multiple processing attempts
   */
  async checkForRedirectResult(): Promise<User | null> {
    // Use a static flag to ensure this only runs once per page load
    if ((window as any)._firebaseRedirectProcessed) {
      console.log('Redirect result already processed this page load');
      return null;
    }
    
    try {
      console.log('Checking for redirect result...');
      const result = await getRedirectResult(this.auth);
      
      // Mark as processed regardless of result to prevent multiple attempts
      (window as any)._firebaseRedirectProcessed = true;
      
      if (result?.user) {
        console.log('Redirect result found, user:', result.user.email);
        console.log('Verifying user with backend after redirect...');
        
        try {
          // Verify user with backend after successful Firebase authentication
          await this.verifyUserWithBackend(result.user);
          console.log('Backend verification successful for redirect user');
          return result.user;
        } catch (backendError) {
          console.error('Backend verification failed for redirect user:', backendError);
          // The verifyUserWithBackend method already handles sign-out on failure
          // Re-throw the error so the calling code can handle it appropriately
          throw backendError;
        }
      }
      
      console.log('No redirect result found');
      return null;
    } catch (error: any) {
      // Mark as processed even on error to prevent infinite retry loops
      (window as any)._firebaseRedirectProcessed = true;
      
      console.error('Error checking redirect result:', error);
      
      // If this is a Firebase auth error (not a backend verification error), 
      // we should handle it differently
      if (error.code && error.code.startsWith('auth/')) {
        console.error('Firebase auth error during redirect:', error.code);
      }
      
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
    
    // Clear user data on sign out
    this.clearUserData();
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