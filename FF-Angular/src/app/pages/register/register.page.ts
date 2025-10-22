import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { Auth, createUserWithEmailAndPassword, signInWithPopup, GoogleAuthProvider } from '@angular/fire/auth';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonContent, RouterLink]
})
export class RegisterPage implements OnInit {
  registerForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;
  private firebaseAuth = inject(Auth);

  departments = [
    { value: 'it', label: 'Information Technology (IT)' },
    { value: 'hr', label: 'Human Resources (HR)' },
    { value: 'finance', label: 'Finance & Accounting' },
    { value: 'operations', label: 'Operations' },
    { value: 'sales', label: 'Sales & Marketing' },
    { value: 'logistics', label: 'Logistics & Supply Chain' },
    { value: 'management', label: 'Management & Executive' },
    { value: 'customer-service', label: 'Customer Service' },
    { value: 'legal', label: 'Legal & Compliance' },
    { value: 'admin', label: 'Administration' },
    { value: 'other', label: 'Other' }
  ];

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      department: ['', Validators.required],
      contactNumber: ['']
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
    if (!this.registerForm.valid || this.isSubmitting) return;

    this.isSubmitting = true;
    this.errorMsg = null;
    
    try {
      const { email, username, password, department, contactNumber } = this.registerForm.value;
      
      // Create Firebase account
      const userCredential = await createUserWithEmailAndPassword(this.firebaseAuth, email, password);
      const firebaseUid = userCredential.user.uid;
      
      // Submit registration to backend
      const registrationData = {
        firebaseUid,
        username,
        email,
        department,
        contactNumber: contactNumber || '',
        registrationMethod: 'email',
        requestedAccessGroups: [],
        businessJustification: '',
        priorityLevel: 'MEDIUM'
      };
      
      await this.auth.submitRegistrationRequest(registrationData).toPromise();
      
      // Navigate to access-request page
      this.router.navigate(['/access-request'], { 
        queryParams: { firebaseUid },
        replaceUrl: true
      });
      
    } catch (err: any) {
      this.errorMsg = this.getErrorMessage(err);
    } finally {
      this.isSubmitting = false;
    }
  }

  async registerWithGoogle() {
    if (this.isSubmitting) return;
    
    this.isSubmitting = true;
    this.errorMsg = null;
    
    try {
      // Sign in with Google
      const provider = new GoogleAuthProvider();
      provider.addScope('email');
      provider.addScope('profile');
      provider.setCustomParameters({ prompt: 'select_account' });
      
      const credential = await signInWithPopup(this.firebaseAuth, provider);
      const user = credential.user;
      
      // Submit registration to backend
      const registrationData = {
        firebaseUid: user.uid,
        username: user.displayName || user.email?.split('@')[0] || 'user',
        email: user.email,
        department: this.registerForm.get('department')?.value || 'Not specified',
        contactNumber: this.registerForm.get('contactNumber')?.value || '',
        registrationMethod: 'google_sso',
        requestedAccessGroups: [],
        businessJustification: '',
        priorityLevel: 'MEDIUM'
      };
      
      await this.auth.submitRegistrationRequest(registrationData).toPromise();
      
      // Navigate to access-request page
      this.router.navigate(['/access-request'], { 
        queryParams: { firebaseUid: user.uid },
        replaceUrl: true
      });
      
    } catch (err: any) {
      this.errorMsg = this.getErrorMessage(err);
    } finally {
      this.isSubmitting = false;
    }
  }

  private getErrorMessage(err: any): string {
    if (err.status === 409) {
      return 'A registration request already exists for this account. Please wait for admin approval.';
    }
    if (err.status === 400) {
      return 'Invalid registration data. Please check all fields and try again.';
    }
    if (err.message === 'Service temporarily unavailable') {
      return 'Unable to connect to the server. Please try again later.';
    }
    if (err.code === 'auth/email-already-in-use') {
      return 'This email address is already in use. Please use a different email or try logging in.';
    }
    if (err.code === 'auth/popup-closed-by-user') {
      return 'Sign-in was cancelled. Please try again.';
    }
    if (err.code === 'auth/popup-blocked') {
      return 'Pop-up was blocked. Please allow pop-ups and try again.';
    }
    return err.error?.error || err.message || 'Registration failed. Please try again.';
  }

  navigateToAbout() {
    this.router.navigate(['/landing']);
  }
}
