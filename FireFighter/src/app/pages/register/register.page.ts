import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent, IonButton } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
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
export class RegisterPage implements OnInit {
  registerForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });

    // Check for redirect result (for Safari users returning from Google OAuth)
    try {
      const user = await this.auth.checkForRedirectResult();
      if (user) {
        // User successfully signed in via redirect, navigate to dashboard
        console.log('Redirect registration successful, navigating to dashboard');
        this.auth.navigateToDashboard();
      }
    } catch (error: any) {
      console.error('Error handling redirect result:', error);
      
      // Handle specific errors from redirect flow
      if (error.status === 400 || error.status === 401) {
        this.errorMsg = 'Account verification failed. Your Google account may not be authorized. Please contact support.';
      } else if (error.status === 500) {
        this.errorMsg = 'Server error during account verification. Please try again later.';
      } else if (error.code && error.code.startsWith('auth/')) {
        if (error.code === 'auth/popup-closed-by-user') {
          // This shouldn't happen with redirect, but just in case
          this.errorMsg = 'Sign-in was cancelled. Please try again.';
        } else {
          this.errorMsg = 'Google authentication failed. Please try again.';
        }
      } else if (error.message && error.message.includes('backend')) {
        this.errorMsg = 'Account verification failed. Please check your connection and try again.';
      } else {
        this.errorMsg = 'Google registration failed. Please try again.';
      }
    }
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    
    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    } else {
      form.get('confirmPassword')?.setErrors(null);
    }
    
    return null;
  }

  async onSubmit() {
    if (this.registerForm.valid) {
      try {
        this.isSubmitting = true;
        this.errorMsg = null;
        
        const { email, password } = this.registerForm.value;
        const user = await this.auth.createUserWithEmail(email, password);
        
        console.log('Account created for', user.email);
        this.auth.navigateToDashboard();
      } catch (err: any) {
        console.error('Registration failed', err);
        
        // Handle Firebase auth errors
        if (err.code === 'auth/email-already-in-use') {
          this.errorMsg = 'Email is already in use.';
        } else if (err.code === 'auth/weak-password') {
          this.errorMsg = 'Password is too weak.';
        } else if (err.status === 400 || err.status === 401) {
          this.errorMsg = 'Account verification failed. Please contact support.';
        } else if (err.status === 500) {
          this.errorMsg = 'Server error. Please try again later.';
        } else {
          this.errorMsg = 'Registration failed. Please try again.';
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }

  async registerWithGoogle() {
    try {
      this.errorMsg = null;
      console.log('Starting Google registration process...');
      const user = await this.auth.signInWithGoogle();
      console.log('Registered as', user.displayName);
      this.auth.navigateToDashboard();
    } catch (err: any) {
      console.error('Google registration failed', err);
      console.error('Error object:', JSON.stringify(err, null, 2));
      
      // Handle Safari redirect case (this is expected behavior)
      if (err.message === 'Redirect initiated - should not reach this point') {
        console.log('Safari redirect initiated - this is expected');
        // This is normal for Safari - the redirect is happening
        return;
      }
      
      // Handle Firebase authentication errors (before backend verification)
      if (err.code && err.code.startsWith('auth/')) {
        if (err.code === 'auth/popup-closed-by-user') {
          this.errorMsg = 'Sign-in was cancelled. Please try again.';
        } else if (err.code === 'auth/popup-blocked') {
          this.errorMsg = 'Pop-up was blocked. Please allow pop-ups and try again.';
        } else {
          this.errorMsg = 'Google authentication failed. Please try again.';
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
        this.errorMsg = 'Google registration failed. Please try again.';
      }
    }
  }

  async tryPopupRegister() {
    try {
      this.errorMsg = null;
      console.log('Trying popup registration as fallback...');
      
      // Clear the redirect processing flag to allow a fresh start
      this.auth.clearRedirectProcessingFlag();
      
      const user = await this.auth.signInWithGooglePopup();
      console.log('Popup registration successful, registered as', user.displayName);
      this.auth.navigateToDashboard();
    } catch (err: any) {
      console.error('Popup registration failed', err);
      
      // Handle Firebase authentication errors (before backend verification)
      if (err.code && err.code.startsWith('auth/')) {
        if (err.code === 'auth/popup-closed-by-user') {
          this.errorMsg = 'Sign-in was cancelled. Please try again.';
        } else if (err.code === 'auth/popup-blocked') {
          this.errorMsg = 'Pop-up was blocked. Please allow pop-ups and try again.';
        } else {
          this.errorMsg = 'Google authentication failed. Please try again.';
        }
        return;
      }
      
      // Handle backend verification errors
      if (err.status === 400 || err.status === 401) {
        this.errorMsg = 'Account verification failed. Your Google account may not be authorized. Please contact support.';
      } else if (err.status === 500) {
        this.errorMsg = 'Server error during account verification. Please try again later.';
      } else if (err.message && err.message.includes('backend')) {
        this.errorMsg = 'Account verification failed. Please check your connection and try again.';
      } else {
        this.errorMsg = 'Popup registration failed. Please try again.';
      }
    }
  }

  navigateToAbout() {
    this.router.navigate(['/landing']);
  }
}
