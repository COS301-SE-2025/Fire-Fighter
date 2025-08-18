import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
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
    private router: Router,
    private themeService: ThemeService
  ) {
    // Ensure theme service is initialized for status bar color
    this.themeService.getCurrentTheme();
  }

  async ngOnInit() {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
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
        
        console.log('🔄 Attempting email registration...');
        console.log('🔄 Firebase Auth instance:', !!this.auth);
        
        const { email, password } = this.registerForm.value;
        const user = await this.auth.createUserWithEmail(email, password);
        
        console.log('✅ Account created for', user.email);
        this.auth.navigateToDashboard();
      } catch (err: any) {
        console.error('❌ Registration failed:', err);
        console.error('❌ Error code:', err.code);
        console.error('❌ Error details:', err);
        
        if (err.code === 'auth/admin-restricted-operation') {
          this.errorMsg = 'Account creation is disabled. Please contact your administrator to enable user registration in Firebase Console.';
        } else if (err.code === 'auth/email-already-in-use') {
          this.errorMsg = 'Email is already in use.';
        } else {
          this.errorMsg = 'Registration failed. Please try again.';
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }

  async registerWithGoogle() {
    if (this.isSubmitting) return;
    
    try {
      this.isSubmitting = true;
      this.errorMsg = null;
      
      const user = await this.auth.signInWithGoogle();
      console.log('✅ Registration successful:', user.displayName);
      this.auth.navigateToDashboard();
      
    } catch (err: any) {
      console.error('❌ Google registration failed:', err);
      
      if (err.code === 'auth/admin-restricted-operation') {
        this.errorMsg = 'Account registration is currently disabled by the administrator.';
      } else if (err.code === 'auth/popup-closed-by-user') {
        this.errorMsg = 'Sign-in was cancelled. Please try again.';
      } else if (err.code === 'auth/popup-blocked') {
        this.errorMsg = 'Pop-up was blocked. Please allow pop-ups and try again.';
      } else {
        this.errorMsg = 'Google registration failed. Please try again.';
      }
    } finally {
      this.isSubmitting = false;
    }
  }



  navigateToAbout() {
    this.router.navigate(['/landing']);
  }
}
