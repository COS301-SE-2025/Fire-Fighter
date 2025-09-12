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
import { catchError, tap } from 'rxjs/operators';
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
  contactNumber?: string;
  dolibarrId?: string;
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

interface JwtAuthResponse {
  token: string;
  user: {
    userId: string;
    username: string;
    email: string;
    department: string;
    isAdmin: boolean;
  };
}

interface FirebaseLoginRequest {
  idToken: string;
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

  // JWT Token management
  private jwtTokenSubject = new BehaviorSubject<string | null>(null);
  public jwtToken$ = this.jwtTokenSubject.asObservable();

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
    this.clearJwtToken();
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
   * JWT Token Management Methods
   */
  
  /**
   * Store JWT token securely
   */
  private storeJwtToken(token: string): void {
    try {
      localStorage.setItem('jwt_token', token);
      this.jwtTokenSubject.next(token);
      console.log('✅ JWT token stored successfully');
    } catch (error) {
      console.error('Failed to store JWT token:', error);
    }
  }

  /**
   * Get stored JWT token
   */
  getJwtToken(): string | null {
    try {
      return localStorage.getItem('jwt_token');
    } catch (error) {
      console.error('Failed to retrieve JWT token:', error);
      return null;
    }
  }

  /**
   * Clear JWT token
   */
  private clearJwtToken(): void {
    try {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('jwt_expiration');

      this.jwtTokenSubject.next(null);
      console.log('✅ JWT token cleared');
    } catch (error) {
      console.error('Failed to clear JWT token:', error);
    }
  }

    /**
   * Check if JWT token is expired or will expire soon
   */
  isTokenExpiredOrExpiringSoon(minutesThreshold: number = 5): boolean {
    try {
      const token = this.getJwtToken();
      if (!token) return true;

      const expirationTime = localStorage.getItem('jwt_expiration');
      if (!expirationTime) return true;

      const expiration = new Date(expirationTime);
      const threshold = new Date(Date.now() + (minutesThreshold * 60 * 1000));
      
      return expiration <= threshold;
    } catch (error) {
      console.error('Error checking token expiration:', error);
      return true;
    }
  }

  /**
   * Get token expiration time
   */
  getTokenExpiration(): Date | null {
    try {
      const expirationTime = localStorage.getItem('jwt_expiration');
      return expirationTime ? new Date(expirationTime) : null;
    } catch (error) {
      console.error('Error getting token expiration:', error);
      return null;
    }
  }
  
  /**
   * Refresh JWT token using Firebase token refresh
   */
  async refreshJwtToken(): Promise<boolean> {
    try {
      console.log('🔄 Refreshing JWT token...');
      
      const currentUser = this.auth.currentUser;
      if (!currentUser) {
        console.log('❌ No current user for token refresh');
        return false;
      }

      // Force refresh Firebase ID token
      const newIdToken = await currentUser.getIdToken(true);
      
      // Exchange for new JWT
      const response = await this.http.post<JwtAuthResponse>(
        `${this.getCurrentApiUrl()}/auth/refresh-token`,
        { idToken: newIdToken } as FirebaseLoginRequest
      ).toPromise();

      if (response) {
        this.storeJwtToken(response.token);
        console.log('✅ JWT token refreshed successfully');
        return true;
      } else {
        console.log('❌ No response from token refresh');
        return false;
      }
    } catch (error) {
      console.error('❌ JWT token refresh failed:', error);
      return false;
    }
  }

  /**
   * Auto-refresh token if it's expiring soon
   */
  async autoRefreshTokenIfNeeded(): Promise<void> {
    try {
      if (this.isTokenExpiredOrExpiringSoon(10)) { // 10 minutes threshold
        console.log('🔄 Token expiring soon, auto-refreshing...');
        await this.refreshJwtToken();
      }
    } catch (error) {
      console.error('Error in auto-refresh:', error);
    }
  }

  /**
   * Exchange Firebase ID token for backend JWT token
   */
  private async exchangeFirebaseTokenForJwt(firebaseUser: User): Promise<JwtAuthResponse> {
    try {
      console.log('🔄 Exchanging Firebase token for JWT...');
      
      // Get Firebase ID token
      const idToken = await firebaseUser.getIdToken();
      
      // Send to backend auth endpoint
      const response = await this.http.post<JwtAuthResponse>(
        `${this.getCurrentApiUrl()}/auth/firebase-login`,
        { idToken } as FirebaseLoginRequest
      ).toPromise();

      if (response) {
        // Store the JWT token
        this.storeJwtToken(response.token);
        
        console.log('✅ JWT token exchange successful:', {
          tokenReceived: !!response.token,
          userId: response.user.userId,
          username: response.user.username,
          isAdmin: response.user.isAdmin
        });

        return response;
      } else {
        throw new Error('No response received from JWT exchange');
      }
    } catch (error) {
      console.error('❌ JWT token exchange failed:', error);
      throw error;
    }
  }

  /**
   * Get current API URL from ApiConfigService
   */
  private getCurrentApiUrl(): string {
    // You may need to inject ApiConfigService here
    return environment.apiUrl; // Fallback to environment
  }

  /**
   * Verify user with backend API and store admin status
   * Endpoint: POST /api/users/verify
   * Content-Type: application/x-www-form-urlencoded
   */
  private async verifyUserWithBackend(user: User, department: string = 'Default Department'): Promise<UserVerificationResponse> {
    try {
      console.log('🔄 Verifying user with backend:', {
        uid: user.uid,
        email: user.email,
        displayName: user.displayName
      });

      // Prepare form data for backend verification
      const params = new HttpParams()
        .set('firebaseUid', user.uid)
        .set('username', user.displayName || user.email?.split('@')[0] || 'Unknown')
        .set('email', user.email || '')
        .set('department', department);

      const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
      };

      // Call backend verification endpoint
      const response = await this.http.post<UserVerificationResponse>(
        `${environment.apiUrl}/users/verify`,
        params.toString(),
        { headers }
      ).toPromise();

      if (response) {
        // Store user profile and admin status
        this.userProfileSubject.next(response);
        this.isAdminSubject.next(response.isAdmin);
        
        // Store in localStorage for persistence
        this.storeUserData(response);

        console.log('✅ User verification successful:', {
          userId: response.userId,
          username: response.username,
          isAdmin: response.isAdmin,
          isAuthorized: response.isAuthorized
        });

        // Exchange Firebase token for JWT
        try {
          await this.exchangeFirebaseTokenForJwt(user);
        } catch (jwtError) {
          console.warn('⚠️ JWT exchange failed, continuing with Firebase auth:', jwtError);
        }

        return response;
      } else {
        throw new Error('No response received from backend verification');
      }
    } catch (error: any) {
      console.error('❌ Backend verification failed:', error);

      // Check for connection errors
      if (this.isConnectionError(error)) {
        console.error('🔌 Connection error detected - redirecting to service down page');
        localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
        this.router.navigate(['/service-down']);
        throw new Error('Service temporarily unavailable');
      }

      // Re-throw other errors
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
    let user: User | null = null;
    
    console.log('Starting Google sign-in process...');
    
    if (Capacitor.isNativePlatform()) {
      console.log('Using native platform Google sign-in');
      const result = await FirebaseAuthentication.signInWithGoogle();
      
      if (result.credential) {
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
      console.log('Using web platform Google sign-in');
      const provider = new GoogleAuthProvider();
      provider.addScope('email');
      provider.addScope('profile');
      provider.setCustomParameters({
        prompt: 'select_account'
      });
      
      try {
        // Try popup first
        const credential = await signInWithPopup(this.auth, provider);
        user = credential.user;
      } catch (error: any) {
        console.log('Popup failed:', error.code);
        
        // If popup fails due to CORS/security, try redirect
        if (error.code === 'auth/popup-blocked' || 
            error.code === 'auth/popup-closed-by-user' ||
            error.code === 'auth/cancelled-popup-request' ||
            error.code === 'auth/admin-restricted-operation') {
          
          console.log('🔄 Popup blocked, trying redirect method...');
          
          // Import redirect methods
          const { signInWithRedirect, getRedirectResult } = await import('firebase/auth');
          
          // Check if we're returning from a redirect
          const redirectResult = await getRedirectResult(this.auth);
          if (redirectResult) {
            user = redirectResult.user;
          } else {
            // Start redirect flow
            await signInWithRedirect(this.auth, provider);
            // This will redirect the page, so we won't reach here
            throw new Error('Redirecting to Google sign-in...');
          }
        } else {
          throw error;
        }
      }
    }

    if (!user) {
      throw new Error('No user returned from Google sign-in');
    }

    console.log('✅ Firebase authentication successful');
    await this.verifyUserWithBackend(user);
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
      // Use Firebase web SDK for browser - SIMPLE APPROACH LIKE DEVELOP BRANCH
      const credential = await createUserWithEmailAndPassword(this.auth, email, password);
      user = credential.user;
    }

    console.log('Firebase user created successfully, verifying with backend...');
    // Use the working approach from develop branch
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
   * Update user contact number
   * Endpoint: PUT /api/users/{userId}/contact
   */
  updateContactNumber(userId: string, contactNumber: string): Observable<UserVerificationResponse> {
    const params = new HttpParams().set('contactNumber', contactNumber);
    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    return this.http.put<UserVerificationResponse>(
      `${environment.apiUrl}/users/${userId}/contact`,
      params.toString(),
      { headers }
    ).pipe(
      tap((updatedProfile: UserVerificationResponse) => {
        // Update the local user profile with the new contact number
        this.userProfileSubject.next(updatedProfile);
        this.storeUserData(updatedProfile);
      }),
      catchError((error: any) => {
        console.error('❌ Update contact number failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateContactNumber - redirecting to service down page');

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
   * Update user Dolibarr ID
   * Endpoint: PUT /api/users/dolibarr-id
   */
  updateDolibarrId(dolibarrId: string): Observable<any> {
    const params = new HttpParams().set('dolibarrId', dolibarrId);
    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    return this.http.put(`${environment.apiUrl}/users/dolibarr-id`, params.toString(), { headers }).pipe(
      tap((response: any) => {
        console.log('✅ Dolibarr ID updated successfully:', response);
        // Update the local user profile if we have one
        const currentProfile = this.userProfileSubject.value;
        if (currentProfile) {
          const updatedProfile = { ...currentProfile, dolibarrId };
          this.userProfileSubject.next(updatedProfile);
          this.storeUserData(updatedProfile);
        }
      }),
      catchError((error: any) => {
        console.error('❌ Update Dolibarr ID failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateDolibarrId - redirecting to service down page');

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
   * Get user Dolibarr ID
   * Endpoint: GET /api/users/dolibarr-id
   */
  getDolibarrId(): Observable<{ dolibarrId: string; firebaseUid: string }> {
    return this.http.get<{ dolibarrId: string; firebaseUid: string }>(`${environment.apiUrl}/users/dolibarr-id`).pipe(
      catchError((error: any) => {
        console.error('❌ Get Dolibarr ID failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in getDolibarrId - redirecting to service down page');

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
