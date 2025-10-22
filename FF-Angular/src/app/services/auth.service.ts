// src/app/services/auth.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
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

  // Token monitoring service will be injected when needed
  private tokenMonitoringService: any = null;

  constructor() {
    // Initialize auth state restoration when service is created
    this.initializeAuthState();
  }

  /**
   * Initialize token monitoring service (lazy injection to avoid circular dependencies)
   */
  private initializeTokenMonitoring(): void {
    if (!this.tokenMonitoringService) {
      // Lazy injection to avoid circular dependencies
      import('./token-monitoring.service').then(({ TokenMonitoringService }) => {
        // We'll manually instantiate to avoid circular dependency
        // In a real scenario, you might want to use a different approach
        console.log('📊 TOKEN MONITORING: Service loaded');
      });
    }
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
      
      // Store expiration time (assuming 1 hour expiration as per backend config)
      const expirationTime = new Date(Date.now() + (60 * 60 * 1000)); // 1 hour
      localStorage.setItem('jwt_expiration', expirationTime.toISOString());
      
      this.jwtTokenSubject.next(token);
      console.log('✅ JWT token stored successfully with expiration:', expirationTime);

      // Start token monitoring after successful token storage
      this.startTokenMonitoring();

    } catch (error) {
      console.error('Failed to store JWT token:', error);
    }
  }

    /**
   * Start token monitoring (lazy load to avoid circular dependencies)
   */
  private startTokenMonitoring(): void {
    try {
      import('./token-monitoring.service').then(({ TokenMonitoringService }) => {
        // Create a new instance manually to avoid circular dependencies
        const monitoring = new (TokenMonitoringService as any)();
        if (monitoring.startMonitoring) {
          monitoring.startMonitoring();
          console.log('📊 Token monitoring started');
        }
      }).catch(error => {
        console.warn('⚠️ Could not start token monitoring:', error);
      });
    } catch (error) {
      console.warn('⚠️ Token monitoring service not available:', error);
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
   * Get all users (Admin Only)
   * Endpoint: GET /api/users/admin/all
   */
  getAllUsersAsAdmin(): Observable<any> {
    console.log('🔵 GET ALL USERS AS ADMIN REQUEST');

    return this.http.get(`${environment.apiUrl}/users/admin/all`).pipe(
      tap((response: any) => {
        console.log('✅ Get all users as admin successful:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Get all users as admin failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in getAllUsersAsAdmin - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Get pending user approvals (Admin Only)
   * Endpoint: GET /api/registration/admin/pending
   */
  getPendingApprovals(): Observable<any> {
    console.log('🔵 GET PENDING APPROVALS REQUEST');

    return this.http.get(`${environment.apiUrl}/registration/admin/pending`).pipe(
      tap((response: any) => {
        console.log('✅ Get pending approvals successful:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Get pending approvals failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in getPendingApprovals - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Approve user registration (Admin Only)
   * Endpoint: PUT /api/registration/admin/approve
   */
  approveUserRegistration(decision: any): Observable<any> {
    console.log('🔵 APPROVE USER REGISTRATION REQUEST:', decision);

    return this.http.put(`${environment.apiUrl}/registration/admin/approve`, decision).pipe(
      tap((response: any) => {
        console.log('✅ Approve user registration successful:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Approve user registration failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in approveUserRegistration - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Reject user registration (Admin Only)
   * Endpoint: PUT /api/registration/admin/reject
   */
  rejectUserRegistration(decision: any): Observable<any> {
    console.log('🔵 REJECT USER REGISTRATION REQUEST:', decision);

    return this.http.put(`${environment.apiUrl}/registration/admin/reject`, decision).pipe(
      tap((response: any) => {
        console.log('✅ Reject user registration successful:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Reject user registration failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in rejectUserRegistration - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Submit user registration request (for new users)
   * Endpoint: POST /api/registration/submit
   */
  submitRegistrationRequest(registrationData: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/registration/submit`, registrationData).pipe(
      catchError((error: any) => {
        if (this.isConnectionError(error)) {
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
          this.router.navigate(['/service-down']);
          return throwError(() => new Error('Service temporarily unavailable'));
        }
        return throwError(() => error);
      })
    );
  }

  /**
   * Submit system access request details
   * Endpoint: POST /api/registration/access-request
   */
  submitAccessRequest(accessRequestData: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/registration/access-request`, accessRequestData).pipe(
      catchError((error: any) => {
        if (this.isConnectionError(error)) {
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
          this.router.navigate(['/service-down']);
          return throwError(() => new Error('Service temporarily unavailable'));
        }
        return throwError(() => error);
      })
    );
  }

  /**
   * Get registration status for current user
   * Endpoint: GET /api/registration/status/{firebaseUid}
   */
  getRegistrationStatus(firebaseUid: string): Observable<any> {
    console.log('🔵 GET REGISTRATION STATUS:', firebaseUid);

    return this.http.get(`${environment.apiUrl}/registration/status/${firebaseUid}`).pipe(
      tap((response: any) => {
        console.log('✅ Registration status retrieved:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Get registration status failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in getRegistrationStatus - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Update user account status (Enable/Disable)
   * Endpoint: PUT /api/users/{firebaseUid}/admin/status
   */
  updateUserAccountStatus(adminUid: string, targetUid: string, isAuthorized: boolean): Observable<any> {
    console.log('🔵 UPDATE USER ACCOUNT STATUS:', {
      adminUid,
      targetUid,
      isAuthorized
    });

    const headers = new HttpHeaders({
      'X-Firebase-UID': adminUid
    });

    return this.http.put(
      `${environment.apiUrl}/users/${targetUid}/admin/status?isAuthorized=${isAuthorized}`,
      {},
      { headers }
    ).pipe(
      tap((response: any) => {
        console.log('✅ Account status updated successfully:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Update account status failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateUserAccountStatus - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Check user authorization status
   * Endpoint: GET /api/users/{firebaseUid}/authorized
   */
  checkUserAuthorization(firebaseUid: string): Observable<boolean> {
    return this.http.get<boolean>(
      `${environment.apiUrl}/users/${firebaseUid}/authorized`
    ).pipe(
      catchError((error: any) => {
        if (this.isConnectionError(error)) {
          return throwError(() => new Error('Service temporarily unavailable'));
        }
        return throwError(() => error);
      })
    );
  }

  /**
   * Update user department as Admin
   * Endpoint: PUT /api/users/{firebaseUid}/admin/department
   */
  updateUserDepartmentAsAdmin(adminUid: string, targetUid: string, department: string): Observable<any> {
    console.log('🔵 UPDATE USER DEPARTMENT AS ADMIN:', {
      adminUid,
      targetUid,
      department
    });

    const headers = new HttpHeaders({
      'X-Firebase-UID': adminUid
    });

    return this.http.put(
      `${environment.apiUrl}/users/${targetUid}/admin/department?department=${encodeURIComponent(department)}`,
      {},
      { headers }
    ).pipe(
      tap((response: any) => {
        console.log('✅ Department updated successfully:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Update department failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateUserDepartmentAsAdmin - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Get user's access groups
   * Endpoint: GET /api/users/{firebaseUid}/access-groups
   */
  getUserAccessGroups(firebaseUid: string): Observable<any> {
    console.log('🔵 GET USER ACCESS GROUPS:', { firebaseUid });

    return this.http.get(
      `${environment.apiUrl}/users/${firebaseUid}/access-groups`
    ).pipe(
      tap((response: any) => {
        console.log('✅ Access groups retrieved:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Get access groups failed:', error);

        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in getUserAccessGroups - redirecting to service down page');
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
          this.router.navigate(['/service-down']);
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        return throwError(() => error);
      })
    );
  }

  /**
   * Update user's access groups as Admin
   * Endpoint: PUT /api/users/{firebaseUid}/admin/access-groups
   */
  updateUserAccessGroupsAsAdmin(adminUid: string, targetUid: string, groupIds: string[]): Observable<any> {
    console.log('🔵 UPDATE USER ACCESS GROUPS AS ADMIN:', {
      adminUid,
      targetUid,
      groupIds
    });

    const headers = new HttpHeaders({
      'X-Firebase-UID': adminUid
    });

    return this.http.put(
      `${environment.apiUrl}/users/${targetUid}/admin/access-groups`,
      { groupIds },
      { headers }
    ).pipe(
      tap((response: any) => {
        console.log('✅ Access groups updated successfully:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Update access groups failed:', error);

        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateUserAccessGroupsAsAdmin - redirecting to service down page');
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
          this.router.navigate(['/service-down']);
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        return throwError(() => error);
      })
    );
  }

  /**
   * Update user Dolibarr ID as Admin
   * Endpoint: PUT /api/users/{firebaseUid}/admin/dolibarr-id
   */
  updateUserDolibarrIdAsAdmin(targetFirebaseUid: string, dolibarrId: string): Observable<any> {
    const params = new HttpParams().set('dolibarrId', dolibarrId);
    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    console.log('🔵 UPDATE USER DOLIBARR ID AS ADMIN REQUEST:');
    console.log('  Target Firebase UID:', targetFirebaseUid);
    console.log('  Dolibarr ID:', dolibarrId);

    return this.http.put(`${environment.apiUrl}/users/${targetFirebaseUid}/admin/dolibarr-id`, params.toString(), { headers }).pipe(
      tap((response: any) => {
        console.log('✅ Update user Dolibarr ID as admin successful:', response);
      }),
      catchError((error: any) => {
        console.error('❌ Update user Dolibarr ID as admin failed:', error);

        // Check for connection errors
        if (this.isConnectionError(error)) {
          console.error('🔌 Connection error detected in updateUserDolibarrIdAsAdmin - redirecting to service down page');

          // Store the last successful connection time
          localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());

          // Redirect to service down page
          this.router.navigate(['/service-down']);

          // Return a specific error for connection issues
          return throwError(() => new Error('Service temporarily unavailable'));
        }

        // For other errors, return the original error
        return throwError(() => error);
      })
    );
  }

  /**
   * Update user Dolibarr ID (DEPRECATED - Use updateUserDolibarrIdAsAdmin instead)
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

    // Subscribe to Firebase auth state changes
    this.user$.subscribe(async (firebaseUser) => {
      if (firebaseUser) {
        // Skip verification if user is on registration flow pages
        const currentUrl = this.router.url;
        if (currentUrl.includes('/access-request') || 
            currentUrl.includes('/register') || 
            currentUrl.includes('/inactive-account')) {
          return;
        }
        
        // Try to restore from localStorage first
        const restoredProfile = this.restoreUserData();
        
        if (restoredProfile) {
          // We have cached data, verify it's still valid by checking with backend
          try {
            // Re-verify with backend to ensure data is current
            const freshProfile = await this.verifyUserWithBackend(firebaseUser);
            this.storeUserData(freshProfile);
          } catch (error: any) {
            // Check if it's a connection error
            if (this.isConnectionError(error)) {
              localStorage.setItem('lastSuccessfulConnection', new Date().toISOString());
              this.router.navigate(['/service-down']);
            }
          }
        } else {
          // No cached data, verify with backend
          
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
