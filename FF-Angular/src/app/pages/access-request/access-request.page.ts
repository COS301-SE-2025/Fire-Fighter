import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { Router, ActivatedRoute } from '@angular/router';
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

  // Priority levels
  priorityLevels = [
    { value: 'High', label: 'High Priority', description: 'Critical emergency requiring immediate attention' },
    { value: 'Medium', label: 'Medium Priority', description: 'Important but not immediately critical' },
    { value: 'Low', label: 'Low Priority', description: 'Routine access request' }
  ];

  // Access request groups based on FireFighter emergency groups
  accessGroups = [
    { 
      value: 'financial', 
      label: 'Financial Emergency Group', 
      description: 'Budget approvals, financial crisis management, monetary systems access' 
    },
    { 
      value: 'hr', 
      label: 'HR Emergency Group', 
      description: 'Human resources emergency protocols, employee data access, privacy-sensitive operations' 
    },
    { 
      value: 'management', 
      label: 'Management Emergency Group', 
      description: 'Executive-level emergency protocols, strategic decision making, high-level coordination' 
    },
    { 
      value: 'logistics', 
      label: 'Logistics Emergency Group', 
      description: 'Supply chain coordination, infrastructure maintenance, business continuity' 
    }
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
        console.error('❌ No Firebase UID found');
        this.errorMsg = 'Session expired. Please register again.';
        setTimeout(() => this.router.navigate(['/register']), 3000);
      } else {
        console.log('✅ Firebase UID:', this.firebaseUid);
      }
    });

    this.accessRequestForm = this.fb.group({
      contactNumber: ['', [Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      priorityLevel: ['MEDIUM', Validators.required],
      accessGroup: ['', Validators.required],
      businessJustification: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(500)]]
    });
  }

  async onSubmit() {
    if (this.accessRequestForm.valid && !this.isSubmitting && this.firebaseUid) {
      this.isSubmitting = true;
      this.errorMsg = null;
      this.successMsg = null;

      try {
        const formData = this.accessRequestForm.value;
        console.log('🔄 Submitting access request with justification...');
        
        // Prepare request data for backend
        const requestData = {
          firebaseUid: this.firebaseUid,
          requestPriority: formData.priorityLevel,
          phoneNumber: formData.contactNumber || '',
          justification: formData.businessJustification,
          requestedAccessGroups: [formData.accessGroup] // Convert single selection to array
        };
        
        console.log('Request data:', requestData);
        
        // Submit to backend API
        await this.authService.submitAccessRequest(requestData).toPromise();
        
        console.log('✅ Access request updated successfully');
        this.successMsg = 'Access request submitted successfully! Redirecting to account status page...';
        
        // Sign out the user since they're not approved yet
        await this.authService.logout();
        
        // Redirect to inactive-account page after a short delay
        setTimeout(() => {
          this.router.navigate(['/inactive-account'], {
            replaceUrl: true
          });
        }, 2000);
        
      } catch (error: any) {
        console.error('❌ Access request submission error:', error);
        this.errorMsg = error.error?.error || error.message || 'Failed to submit access request. Please try again.';
      } finally {
        this.isSubmitting = false;
      }
    } else if (!this.firebaseUid) {
      this.errorMsg = 'Session expired. Please register again.';
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

  getAccessGroupDescription(): string {
    const selectedValue = this.accessRequestForm.get('accessGroup')?.value;
    const group = this.accessGroups.find(g => g.value === selectedValue);
    return group?.description || '';
  }
}