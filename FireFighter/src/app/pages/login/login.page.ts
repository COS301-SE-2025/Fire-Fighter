// src/app/pages/login/login.page.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
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
    RouterLink
  ]
})
export class LoginPage implements OnInit {
  loginForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  async login() {
    try {
      this.errorMsg = null;
      console.log('Starting Google login process...');
      
      // Test if AuthService is properly initialized
      if (!this.auth) {
        console.error('❌ AuthService is not properly injected');
        this.errorMsg = 'Authentication service error. Please check console.';
        return;
      }
      
      console.log('Calling auth service signInWithGoogle method...');
      const user = await this.auth.signInWithGoogle();
      console.log('✅ Login successful:', user.displayName || user.email);
      
      // Navigation will be handled by the auth service after successful backend verification
      this.auth.navigateToDashboard();
    } catch (err: any) {
      console.error('Login failed:', err);
      
      // Handle Firebase authentication errors (before backend verification)
      if (err.code && err.code.startsWith('auth/')) {
        if (err.code === 'auth/popup-closed-by-user') {
          this.errorMsg = 'Sign-in was cancelled. Please try again.';
        } else if (err.code === 'auth/popup-blocked') {
          this.errorMsg = 'Pop-up was blocked. Please allow pop-ups and try again.';
        } else if (err.code === 'auth/unauthorized-domain') {
          this.errorMsg = 'This domain is not authorized for Firebase authentication. Please contact support.';
        } else if (err.code === 'auth/operation-not-allowed') {
          this.errorMsg = 'Google Sign-In is not enabled. Please contact support.';
        } else {
          this.errorMsg = `Google authentication failed: ${err.code}. Please try again.`;
        }
        return;
      }
      
      // Handle specific backend verification errors
      if (err.status === 400 || err.status === 401) {
        this.errorMsg = 'Account verification failed. Your Google account may not be authorized. Please contact support.';
      } else if (err.status === 500) {
        this.errorMsg = 'Server error during account verification. Please try again later.';
      } else if (err.message && err.message.includes('backend')) {
        this.errorMsg = 'Account verification failed. Please check your connection and try again.';
      } else {
        this.errorMsg = `Google login failed: ${err.message || 'Unknown error'}. Please try again.`;
      }
    }
  }

  async ngOnInit() {
    // Initialize the login form for email/password authentication
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
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



  navigateToAbout() {
    this.router.navigate(['/landing']);
  }
}