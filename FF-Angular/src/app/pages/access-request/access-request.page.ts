import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { Router, ActivatedRoute } from '@angular/router';
import { NavController } from '@ionic/angular/standalone';
import { AuthService } from '../../services/auth.service';
import { Auth } from '@angular/fire/auth';

@Component({
  selector: 'app-access-request',
  templateUrl: './access-request.page.html',
  styleUrls: ['./access-request.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    IonContent
  ]
})
export class AccessRequestPage implements OnInit {
  accessRequestForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;
  successMsg: string | null = null;
  firebaseUid: string | null = null;
  private firebaseAuth = inject(Auth);
  private navCtrl = inject(NavController);

  // Priority levels
  priorityLevels = [
    { value: 'High', label: 'High Priority', description: 'Critical emergency requiring immediate attention' },
    { value: 'Medium', label: 'Medium Priority', description: 'Important but not immediately critical' },
    { value: 'Low', label: 'Low Priority', description: 'Routine access request' }
  ];

  // Departments from registration flow
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
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Get Firebase UID from query params or current user
    this.route.queryParams.subscribe(params => {
      this.firebaseUid = params['firebaseUid'] || this.firebaseAuth.currentUser?.uid || null;
      
      if (!this.firebaseUid) {
        this.errorMsg = 'Session expired. Please register again.';
        setTimeout(() => this.router.navigate(['/register']), 3000);
      }
    });

    this.accessRequestForm = this.fb.group({
      department: ['', Validators.required],
      contactNumber: ['', Validators.pattern(/^[0-9\s\-\(\)\.]+$/)],
      priorityLevel: ['MEDIUM', Validators.required],
      businessJustification: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(500)]]
    });
  }

  async onSubmit() {
    if (!this.accessRequestForm.valid || this.isSubmitting || !this.firebaseUid) {
      if (!this.firebaseUid) {
        this.errorMsg = 'Session expired. Please register again.';
      }
      return;
    }

    this.isSubmitting = true;
    this.errorMsg = null;
    this.successMsg = null;

    try {
      const formData = this.accessRequestForm.value;
      
      // Prepare request data for backend
      const requestData = {
        firebaseUid: this.firebaseUid,
        department: formData.department,
        requestPriority: formData.priorityLevel,
        phoneNumber: formData.contactNumber || '',
        justification: formData.businessJustification,
        requestedAccessGroups: []
      };
      
      await this.authService.submitAccessRequest(requestData).toPromise();
      
      this.successMsg = 'Access request submitted successfully! Redirecting to account status page...';
      
      // DO NOT sign out the user - keep them logged in with Firebase
      // so they can check their authorization status on the inactive-account page
      
      // Use navigateRoot to clear navigation history and prevent back navigation
      // This prevents security issue where user can go back and access dashboard
      setTimeout(() => {
        this.navCtrl.navigateRoot('/inactive-account', { 
          animationDirection: 'forward' 
        });
      }, 2000);
      
    } catch (error: any) {
      this.errorMsg = error.error?.error || error.message || 'Failed to submit access request. Please try again.';
      // Only reset isSubmitting on error so user can try again
      this.isSubmitting = false;
    }
  }

  getCharacterCount(): number {
    return this.accessRequestForm.get('businessJustification')?.value?.length || 0;
  }

  getPriorityDescription(): string {
    const selectedValue = this.accessRequestForm.get('priorityLevel')?.value;
    const priority = this.priorityLevels.find(p => p.value === selectedValue);
    return priority?.description || '';
  }

}
