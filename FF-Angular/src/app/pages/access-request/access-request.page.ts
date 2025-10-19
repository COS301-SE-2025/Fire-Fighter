import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

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
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.accessRequestForm = this.fb.group({
      contactNumber: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      priorityLevel: ['', Validators.required],
      accessGroup: ['', Validators.required],
      businessJustification: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(500)]]
    });
  }

  async onSubmit() {
    if (this.accessRequestForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.errorMsg = null;
      this.successMsg = null;

      try {
        const formData = this.accessRequestForm.value;
        console.log('🔄 Submitting access request:', formData);
        
        // Get current user's Firebase UID from the auth service
        const currentUser = await this.getCurrentUser();
        if (!currentUser) {
          throw new Error('No authenticated user found. Please register again.');
        }
        
        // Prepare access request data
        const accessRequestData = {
          firebaseUid: currentUser.uid,
          requestPriority: formData.priorityLevel,
          requestDepartment: formData.accessGroup,
          phoneNumber: formData.contactNumber,
          justification: formData.businessJustification
        };
        
        // Submit to backend
        await this.authService.submitAccessRequest(accessRequestData).toPromise();
        console.log('✅ Access request submitted successfully');
        
        this.successMsg = 'Access request submitted successfully! Your request is now pending approval.';
        
        // Redirect to inactive-account page after a short delay
        setTimeout(() => {
          this.router.navigate(['/inactive-account']);
        }, 2000);
        
      } catch (error: any) {
        console.error('❌ Access request submission error:', error);
        this.errorMsg = error.error?.error || error.message || 'Failed to submit access request. Please try again.';
      } finally {
        this.isSubmitting = false;
      }
    }
  }
  
  /**
   * Get current authenticated user from AuthService
   */
  private getCurrentUser(): Promise<any> {
    return new Promise((resolve) => {
      this.authService.user$.subscribe((user) => {
        resolve(user);
      });
    });
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