// src/app/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
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

  usernames: { [userId: string]: string } = {};

  // Initialization flag to prevent multiple init calls
  private initialized = false;

  constructor() {
    // Initialize auth state restoration when service is created
    this.initializeAuthState();
  }

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
    // Clear localStorage data
    localStorage.removeItem('firebase_user_profile');
    localStorage.removeItem('firebase_user_admin_status');
  }

  /**
   * Store user data in localStorage for persistence across page refreshes
   */
  private storeUserData(profile: UserVerificationResponse): void {
    try {
      localStorage.setItem('firebase_user_profile', JSON.stringify(profile));
      localStorage.setItem('firebase_user_admin_status', JSON.stringify(profile.isAdmin));
    } catch (error) {
      console.warn('Failed to store user data in localStorage:', error);
    }
  }

  /**
   * Restore user data from localStorage
   */
  private restoreUserData(): UserVerificationResponse | null {
    try {
      const profileData = localStorage.getItem('firebase_user_profile');
      const adminStatus = localStorage.getItem('firebase_user_admin_status');
      
      if (profileData && adminStatus) {
        const profile = JSON.parse(profileData) as UserVerificationResponse;
        const isAdmin = JSON.parse(adminStatus) as boolean;
        
        // Update the subjects with restored data
        this.userProfileSubject.next(profile);
        this.isAdminSubject.next(isAdmin);
        
        console.log('✅ User data restored from localStorage:', {
          userId: profile.userId,
          username: profile.username,
          isAdmin: isAdmin
        });
        
        return profile;
      }
    } catch (error) {
      console.warn('Failed to restore user data from localStorage:', error);
      // Clear potentially corrupted data
      this.clearUserData();
    }
    
    return null;
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
   * Endpoint: POST /api/users/verify
   * Content-Type: application/x-www-form-urlencoded
   */
  private async verifyUserWithBackend(user: User, department: string = 'Default Department'): Promise<UserVerificationResponse> {
    console.log('🔄 Starting backend user verification...');
    console.log('User data from Firebase:', {
      uid: user.uid,
      email: user.email,
      displayName: user.displayName,
      emailVerified: user.emailVerified
    });

    // Validate required fields according to API documentation
    if (!user.uid) {
      throw new Error('Firebase UID is required but missing');
    }
    if (!user.email) {
      throw new Error('User email is required but missing');
    }

    // Prepare username - ensure it's not empty
    const username = user.displayName || user.email.split('@')[0] || 'FireFighter User';
    
    // Create form data using HttpParams for application/x-www-form-urlencoded format
    // Matching the API documentation exactly:
    const params = new HttpParams()
      .set('firebaseUid', user.uid)           // Required: Firebase User ID (UID)
      .set('username', username)              // Required: User's display name
      .set('email', user.email)               // Required: User's email address
      .set('department', department);         // Optional: User's department/division

    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    console.log('📤 Sending verification request to:', `${environment.apiUrl}/users/verify`);
    console.log('Request parameters:', {
      firebaseUid: user.uid,
      username: username,
      email: user.email,
      department: department
    });

    try {
      const response = await this.http.post<UserVerificationResponse>(
        `${environment.apiUrl}/users/verify`, 
        params.toString(), 
        { headers }
      ).toPromise();
      
      console.log('✅ User verified successfully with backend:', response);
      
      if (response) {
        // Store the admin status and user profile
        this.isAdminSubject.next(response.isAdmin);
        this.userProfileSubject.next(response);
        
        // Persist to localStorage
        this.storeUserData(response);
        
        console.log('👤 User profile loaded:', {
          userId: response.userId,
          username: response.username,
          email: response.email,
          department: response.department,
          isAdmin: response.isAdmin,
          role: response.role,
          rolesCount: response.userRoles?.length || 0
        });
        
        return response;
      }
      
      throw new Error('No response received from backend verification');
    } catch (error: any) {
      console.error('❌ Backend verification failed:', error);

      // Check for connection errors (ERR_CONNECTION_REFUSED, network failures, etc.)
      if (this.isConnectionError(error)) {
        console.error('🔌 Connection error detected - redirecting to service down page');

        // Store the last successful connection time
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

        // Clear user data on connection failure
        this.clearUserData();

        // Redirect to service down page
        this.router.navigate(['/service-down']);

        // Don't throw the error to prevent further error handling
        return Promise.reject(new Error('Service temporarily unavailable'));
      }

      // Log detailed error information for debugging
      if (error.status) {
        console.error(`HTTP ${error.status}: ${error.statusText}`);
        console.error('Error URL:', error.url);
        if (error.error) {
          console.error('Error details:', error.error);
        }
      }

      // Clear user data on verification failure
      this.clearUserData();

      throw error;
    }
  }

  /**
   * Check if the error is a connection-related error
   */
  private isConnectionError(error: any): boolean {
    // Check for various connection error indicators
    if (error.message) {
      const message = error.message.toLowerCase();

      // Common connection error patterns
      if (message.includes('err_connection_refused') ||
          message.includes('connection refused') ||
          message.includes('net::err_connection_refused') ||
          message.includes('failed to fetch') ||
          message.includes('network error') ||
          message.includes('timeout') ||
          message.includes('connection timeout') ||
          message.includes('connection failed')) {
        return true;
      }
    }

    // Check for HTTP status codes that indicate service unavailability
    if (error.status === 0 ||           // Network error (no response)
        error.status === 502 ||         // Bad Gateway
        error.status === 503 ||         // Service Unavailable
        error.status === 504 ||         // Gateway Timeout
        error.status === 521 ||         // Web Server Is Down
        error.status === 522 ||         // Connection Timed Out
        error.status === 523) {         // Origin Is Unreachable
      return true;
    }

    // Check for error name patterns
    if (error.name === 'HttpErrorResponse' && error.status === 0) {
      return true;
    }

    // Check if error.error contains connection-related messages
    if (error.error && typeof error.error === 'string') {
      const errorString = error.error.toLowerCase();
      if (errorString.includes('connection') ||
          errorString.includes('network') ||
          errorString.includes('timeout')) {
        return true;
      }
    }

    return false;
  }

  /**
   * Sign in with Google via popup on web or native on mobile
   * Simplified flow: Firebase auth -> backend verification -> navigation
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
      // On web platforms, always use popup (simplified - no browser detection)
      console.log('Using web platform Google sign-in with popup');
      const provider = new GoogleAuthProvider();
      provider.addScope('email');
      provider.addScope('profile');
      
      const credential = await signInWithPopup(this.auth, provider);
      user = credential.user;
    }

    console.log('Firebase authentication successful, verifying with backend...');
    // Verify user with backend after successful Firebase authentication
    await this.verifyUserWithBackend(user);
    
    console.log('Backend verification successful');
    return user;
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

  /**
   * Get user profile by userId
   * Endpoint: GET /api/users/{userId}
   */
  getUserProfileById(userId: string): Observable<UserVerificationResponse> {
    return this.http.get<UserVerificationResponse>(`${environment.apiUrl}/users/${userId}`)
      .pipe(
        catchError((error: any) => {
          console.error('❌ Get user profile failed:', error);

          // Check for connection errors
          if (this.isConnectionError(error)) {
            console.error('🔌 Connection error detected in getUserProfileById - redirecting to service down page');

            // Store the last successful connection time
            localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

            // Redirect to service down page
            this.router.navigate(['/service-down']);

            // Return a meaningful error
            return throwError(() => new Error('Service temporarily unavailable'));
          }

          // Re-throw other errors
          return throwError(() => error);
        })
      );
  }

  /**
   * Initialize auth state restoration when the service is created
   * This handles the case where the user refreshes the page but Firebase auth persists
   */
  private initializeAuthState(): void {
    if (this.initialized) return;
    this.initialized = true;

    console.log('🔄 Initializing auth state...');

    // Subscribe to Firebase auth state changes
    this.user$.subscribe(async (firebaseUser) => {
      if (firebaseUser) {
        console.log('🔥 Firebase user found on init:', firebaseUser.email);
        
        // Try to restore from localStorage first
        const restoredProfile = this.restoreUserData();
        
        if (restoredProfile) {
          // We have cached data, verify it's still valid by checking with backend
          console.log('📋 User data restored from cache, verifying with backend...');
          
          try {
            // Re-verify with backend to ensure data is current
            const freshProfile = await this.verifyUserWithBackend(firebaseUser);

            // Update cache with fresh data
            this.storeUserData(freshProfile);

            console.log('✅ User data verified and updated');
          } catch (error: any) {
            console.warn('⚠️ Backend verification failed during init:', error);

            // Check if it's a connection error
            if (this.isConnectionError(error)) {
              console.warn('🔌 Connection error during initialization - keeping cached data and showing service down page');

              // Store the last successful connection time
              localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

              // Redirect to service down page
              this.router.navigate(['/service-down']);
            } else {
              // Keep using restored data if backend is temporarily unavailable for other reasons
              console.warn('⚠️ Non-connection error during init, using cached data');
            }
          }
        } else {
          // No cached data, verify with backend
          console.log('🌐 No cached data found, verifying with backend...');
          
          try {
            await this.verifyUserWithBackend(firebaseUser);
          } catch (error: any) {
            console.error('❌ Failed to verify user during initialization:', error);

            // Check if it's a connection error
            if (this.isConnectionError(error)) {
              console.error('🔌 Connection error during user verification - redirecting to service down page');

              // Store the last successful connection time
              localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

              // Clear any partial state
              this.clearUserData();

              // Redirect to service down page
              this.router.navigate(['/service-down']);
            } else {
              // Clear any partial state for other errors
              this.clearUserData();
            }
          }
        }
      } else {
        console.log('👋 No Firebase user found, clearing user data');
        // No Firebase user, clear all data
        this.clearUserData();
      }
    });
  }
}