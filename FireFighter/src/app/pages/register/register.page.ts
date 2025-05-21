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

  ngOnInit() {
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
        
        const { email, password } = this.registerForm.value;
        const user = await this.auth.createUserWithEmail(email, password);
        
        console.log('Account created for', user.email);
        this.router.navigate(['/dashboard']);
      } catch (err: any) {
        console.error('Registration failed', err);
        
        // Handle Firebase auth errors
        if (err.code === 'auth/email-already-in-use') {
          this.errorMsg = 'Email is already in use.';
        } else if (err.code === 'auth/weak-password') {
          this.errorMsg = 'Password is too weak.';
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
      const user = await this.auth.signInWithGoogle();
      console.log('Registered as', user.displayName);
      this.router.navigate(['/dashboard']);
    } catch (err) {
      console.error('Google registration failed', err);
      this.errorMsg = 'Google registration failed. Please try again.';
    }
  }
}
