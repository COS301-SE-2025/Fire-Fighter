import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '@angular/fire/auth';

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
  private firebaseAuth = inject(Auth);

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
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      department: ['', [Validators.required]],
      contactNumber: [''],
      requestedAccessGroups: [[]],
      businessJustification: ['', [Validators.maxLength(1000)]],
      priorityLevel: ['MEDIUM']
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
        
        console.log('🔄 Step 1: Creating Firebase account...');
        const { email, username, password, department, contactNumber, requestedAccessGroups, businessJustification, priorityLevel } = this.registerForm.value;
        
        // Step 1: Create Firebase account WITHOUT backend verification
        // We'll use the Firebase SDK directly to avoid triggering verifyUserWithBackend
        const { createUserWithEmailAndPassword } = await import('firebase/auth');
        const userCredential = await createUserWithEmailAndPassword(this.firebaseAuth, email, password);
        const user = userCredential.user;
        console.log('✅ Firebase account created:', user.uid);
        
        // Step 2: Submit registration request to backend (pending approval)
        console.log('🔄 Step 2: Submitting registration request to backend...');
        const registrationData = {
          firebaseUid: user.uid,
          username: username,
          email: email,
          department: department,
          contactNumber: contactNumber || '',
          registrationMethod: 'email',
          requestedAccessGroups: requestedAccessGroups || [],
          businessJustification: businessJustification || '',
          priorityLevel: priorityLevel || 'MEDIUM'
        };
        
        await this.auth.submitRegistrationRequest(registrationData).toPromise();
        console.log('✅ Registration request submitted successfully');
        
        // Step 3: Navigate to access-request page to collect additional information
        this.router.navigate(['/access-request']);
        
      } catch (err: any) {
        console.error('❌ Registration failed:', err);
        console.error('❌ Error details:', err);
        
        if (err.status === 409) {
          this.errorMsg = 'A registration request already exists for this email. Please wait for admin approval.';
        } else if (err.status === 400) {
          this.errorMsg = 'Invalid registration data. Please check all fields and try again.';
        } else if (err.message === 'Service temporarily unavailable') {
          this.errorMsg = 'Unable to connect to the server. Please try again later.';
        } else if (err.code === 'auth/email-already-in-use') {
          this.errorMsg = 'This email address is already in use. Please use a different email or try logging in.';
        } else {
          this.errorMsg = 'Registration failed: ' + (err.error?.error || err.message || 'Please try again.');
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
      
      console.log('🔄 Step 1: Signing in with Google...');
      // Sign in with Google WITHOUT triggering backend verification
      const { signInWithPopup, GoogleAuthProvider } = await import('firebase/auth');
      const provider = new GoogleAuthProvider();
      provider.addScope('email');
      provider.addScope('profile');
      provider.setCustomParameters({ prompt: 'select_account' });
      
      const credential = await signInWithPopup(this.firebaseAuth, provider);
      const user = credential.user;
      console.log('✅ Google sign-in successful:', user.displayName);
      
      // Step 2: Submit registration request to backend for approval
      console.log('🔄 Step 2: Submitting registration request to backend...');
      const registrationData = {
        firebaseUid: user.uid,
        username: user.displayName || user.email?.split('@')[0] || 'user',
        email: user.email,
        department: this.registerForm.get('department')?.value || 'Not specified',
        contactNumber: this.registerForm.get('contactNumber')?.value || '',
        registrationMethod: 'google_sso',
        requestedAccessGroups: this.registerForm.get('requestedAccessGroups')?.value || [],
        businessJustification: this.registerForm.get('businessJustification')?.value || '',
        priorityLevel: this.registerForm.get('priorityLevel')?.value || 'MEDIUM'
      };
      
      await this.auth.submitRegistrationRequest(registrationData).toPromise();
      console.log('✅ Registration request submitted successfully');
      
      // Step 3: Navigate to access-request page to collect additional information
      this.router.navigate(['/access-request']);
      
    } catch (err: any) {
      console.error('❌ Google registration failed:', err);
      
      if (err.code === 'auth/admin-restricted-operation') {
        this.errorMsg = 'Account registration is currently disabled by the administrator.';
      } else if (err.code === 'auth/popup-closed-by-user') {
        this.errorMsg = 'Sign-in was cancelled. Please try again.';
      } else if (err.code === 'auth/popup-blocked') {
        this.errorMsg = 'Pop-up was blocked. Please allow pop-ups and try again.';
      } else if (err.status === 409) {
        this.errorMsg = 'A registration request already exists for this account. Please wait for admin approval.';
      } else if (err.message === 'Service temporarily unavailable') {
        this.errorMsg = 'Unable to connect to the server. Please try again later.';
      } else {
        this.errorMsg = 'Google registration failed: ' + (err.error?.error || err.message || 'Please try again.');
      }
    } finally {
      this.isSubmitting = false;
    }
  }



  navigateToAbout() {
    this.router.navigate(['/landing']);
  }
}
