// src/app/pages/login/login.page.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent, IonButton } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

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
      const user = await this.auth.signInWithGoogle();
      console.log('Logged in as', user.displayName);
      this.auth.navigateToDashboard();
    } catch (err: any) {
      console.error('Login failed', err);
      
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

  ngOnInit() {
    // we'll use this later for validation, but right now it's just for form state
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
}