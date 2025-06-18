// src/app/pages/login/login.page.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent, IonButton } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgClass,
    IonContent,
    IonButton,
    RouterLink
  ]
})
export class LoginPage implements OnInit, OnDestroy {
  loginForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;

  // Add debugging properties
  isSafari = false;
  userAgent = '';
  private authSubscription: any;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  async login() {
    try {
      this.errorMsg = null;
      const user = await this.auth.signInWithGoogle();
      console.log('Logged in as', user.displayName);
      this.auth.navigateToDashboard();
    } catch (err: any) {
      console.error('Login failed', err);
      
      // Handle Safari redirect case (this is expected behavior)
      if (err.message === 'Redirect initiated - should not reach this point') {
        // This is normal for Safari - the redirect is happening
        return;
      }
      
      // Handle specific backend verification errors
      if (err.status === 400 || err.status === 401) {
        this.errorMsg = 'Account verification failed. Please contact support.';
      } else if (err.status === 500) {
        this.errorMsg = 'Server error. Please try again later.';
      } else {
        this.errorMsg = 'Google login failed. Please try again.';
      }
    }
  }

  async ngOnInit() {
    // Debug information
    this.userAgent = navigator.userAgent;
    this.isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent) || 
                   /(iPad|iPhone|iPod).*Safari/i.test(navigator.userAgent);
    
    console.log('Browser detection:', {
      userAgent: this.userAgent,
      isSafari: this.isSafari,
      platform: navigator.platform
    });

    // we'll use this later for validation, but right now it's just for form state
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Set up auth state subscription to automatically navigate when user is authenticated
    this.authSubscription = this.auth.user$.subscribe(user => {
      console.log('Auth state changed:', !!user, user?.email);
      if (user) {
        console.log('User is authenticated, navigating to dashboard...');
        this.auth.navigateToDashboard();
      }
    });

    // Check for redirect result (for Safari users returning from Google OAuth)
    try {
      console.log('Checking for redirect result...');
      const user = await this.auth.checkForRedirectResult();
      console.log('Redirect result:', user ? 'User found' : 'No user found');
      
      if (user) {
        console.log('User signed in via redirect:', user.displayName, user.email);
        // The auth state subscription will handle navigation
      }
    } catch (error) {
      console.error('Error handling redirect result:', error);
      this.errorMsg = 'Google login failed. Please try again.';
    }
  }

  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  async onSubmit() {
    if (this.loginForm.valid) {
      try {
        this.isSubmitting = true;
        this.errorMsg = null;
        
        const { email, password } = this.loginForm.value;
        const user = await this.auth.signInWithEmail(email, password);
        
        console.log('Logged in as', user.email);
        this.auth.navigateToDashboard();
      } catch (err: any) {
        console.error('Login failed', err);
        
        // Handle Firebase auth errors
        if (err.code === 'auth/user-not-found' || err.code === 'auth/wrong-password') {
          this.errorMsg = 'Invalid email or password.';
        } else if (err.code === 'auth/too-many-requests') {
          this.errorMsg = 'Too many failed login attempts. Please try again later.';
        } else if (err.status === 400 || err.status === 401) {
          this.errorMsg = 'Account verification failed. Please contact support.';
        } else if (err.status === 500) {
          this.errorMsg = 'Server error. Please try again later.';
        } else {
          this.errorMsg = 'Login failed. Please try again.';
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }

  // Add Safari-specific test method
  async testSafariLogin() {
    try {
      this.errorMsg = null;
      console.log('Testing Safari-specific login method...');
      await this.auth.signInWithGoogleSafariMode();
    } catch (err: any) {
      console.error('Safari test login failed', err);
      if (err.message !== 'Redirect initiated - should not reach this point') {
        this.errorMsg = 'Safari test login failed. Error: ' + err.message;
      }
    }
  }
}