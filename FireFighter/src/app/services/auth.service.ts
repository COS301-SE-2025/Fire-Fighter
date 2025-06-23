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

  usernames: { [userId: string]: string } = {};

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
    return this.http.get<UserVerificationResponse>(`${environment.apiUrl}/users/${userId}`);
  }
}