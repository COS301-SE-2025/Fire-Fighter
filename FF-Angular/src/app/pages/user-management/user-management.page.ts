import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { trigger, state, style, transition, animate } from '@angular/animations';

interface User {
  userId: string;
  username: string;
  email: string;
  department: string;
  isAdmin: boolean;
  isAuthorized: boolean;
  lastLogin: string;
  createdAt: string;
  dolibarrId?: string;
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.page.html',
  styleUrls: ['./user-management.page.scss'],
  standalone: true,
  imports: [IonContent, IonRefresher, IonRefresherContent, CommonModule, FormsModule, NavbarComponent],
  animations: [
    trigger('modalBackdrop', [
      state('hidden', style({
        opacity: 0
      })),
      state('visible', style({
        opacity: 1
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ]),
    trigger('modalPanel', [
      state('hidden', style({
        opacity: 0,
        transform: 'scale(0.95) translateY(-10px)'
      })),
      state('visible', style({
        opacity: 1,
        transform: 'scale(1) translateY(0)'
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ])
  ]
})
export class UserManagementPage implements OnInit {

  // Statistics
  normalUsers = 0;
  adminUsers = 0;

  // User data
  users: User[] = [];
  filteredUsers: User[] = [];

  // Search and filter
  searchTerm = '';
  selectedFilter = 'all';

  // Loading state
  loading = false;

  // Dolibarr ID management
  isDolibarrIdEditOpen = false;
  editingDolibarrId = '';
  isUpdatingDolibarrId = false;
  dolibarrModalAnimationState = 'hidden';
  selectedUser: User | null = null;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.loadUsers();
  }

  doRefresh(event: any) {
    this.loadUsers().then(() => {
      event.target.complete();
    });
  }

  async loadUsers() {
    this.loading = true;
    try {
      // TODO: Replace with actual API call
      // Placeholder data for now
      this.users = [
        {
          userId: '1',
          username: 'john.doe',
          email: 'john.doe@bmw.com',
          department: 'IT',
          isAdmin: true,
          isAuthorized: true,
          lastLogin: '2024-01-15T10:30:00Z',
          createdAt: '2024-01-01T00:00:00Z',
          dolibarrId: 'DOL001'
        },
        {
          userId: '2',
          username: 'jane.smith',
          email: 'jane.smith@bmw.com',
          department: 'Operations',
          isAdmin: false,
          isAuthorized: true,
          lastLogin: '2024-01-14T15:45:00Z',
          createdAt: '2024-01-02T00:00:00Z',
          dolibarrId: 'DOL002'
        },
        {
          userId: '3',
          username: 'mike.wilson',
          email: 'mike.wilson@bmw.com',
          department: 'Security',
          isAdmin: false,
          isAuthorized: false,
          lastLogin: '2024-01-10T09:15:00Z',
          createdAt: '2024-01-03T00:00:00Z'
          // No Dolibarr ID set for this user
        }
      ];

      this.updateStatistics();
      this.filterUsers();
    } catch (error) {
      console.error('Error loading users:', error);
    } finally {
      this.loading = false;
    }
  }

  updateStatistics() {
    this.adminUsers = this.users.filter(user => user.isAdmin).length;
    this.normalUsers = this.users.filter(user => !user.isAdmin).length;
  }

  filterUsers() {
    let filtered = this.users;

    // Apply search filter
    if (this.searchTerm.trim()) {
      const searchLower = this.searchTerm.toLowerCase();
      filtered = filtered.filter(user =>
        user.username.toLowerCase().includes(searchLower) ||
        user.email.toLowerCase().includes(searchLower) ||
        user.department.toLowerCase().includes(searchLower)
      );
    }

    // Apply status filter
    switch (this.selectedFilter) {
      case 'active':
        filtered = filtered.filter(user => user.isAuthorized);
        break;
      case 'inactive':
        filtered = filtered.filter(user => !user.isAuthorized);
        break;
      case 'admin':
        filtered = filtered.filter(user => user.isAdmin);
        break;
      default:
        // 'all' - no additional filtering
        break;
    }

    this.filteredUsers = filtered;
  }

  getUserInitials(username: string): string {
    return username.split('.').map(part => part.charAt(0).toUpperCase()).join('');
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  editUser(user: User) {
    console.log('Edit user:', user);
    // TODO: Implement edit user functionality
    alert(`Edit user: ${user.username}`);
  }

  toggleUserStatus(user: User) {
    console.log('Toggle user status:', user);
    // TODO: Implement toggle user status functionality
    const action = user.isAuthorized ? 'deactivate' : 'activate';
    alert(`${action} user: ${user.username}`);
  }

  deleteUser(user: User) {
    console.log('Delete user:', user);
    // TODO: Implement delete user functionality
    if (confirm(`Are you sure you want to delete user: ${user.username}?`)) {
      alert(`Delete user: ${user.username}`);
    }
  }

  // Dolibarr ID management methods
  manageDolibarrId(user: User) {
    this.selectedUser = user;
    this.isDolibarrIdEditOpen = true;
    this.editingDolibarrId = user.dolibarrId || '';
    // Trigger animation
    setTimeout(() => {
      this.dolibarrModalAnimationState = 'visible';
    }, 10);
  }

  cancelDolibarrIdEdit() {
    this.dolibarrModalAnimationState = 'hidden';
    // Wait for animation to complete before hiding modal
    setTimeout(() => {
      this.isDolibarrIdEditOpen = false;
      this.editingDolibarrId = '';
      this.selectedUser = null;
    }, 150);
  }

  // Dolibarr ID validation
  isValidDolibarrId(): boolean {
    const dolibarrId = this.editingDolibarrId.trim();
    // Allow empty string or any alphanumeric string (adjust validation as needed)
    return dolibarrId === '' || /^[a-zA-Z0-9_-]+$/.test(dolibarrId);
  }

  // Handle input to allow alphanumeric characters
  onDolibarrIdInput(event: any) {
    const input = event.target;
    const value = input.value;

    // Allow alphanumeric, underscores, and hyphens
    const cleanValue = value.replace(/[^a-zA-Z0-9_-]/g, '');

    // Update the model and input value
    this.editingDolibarrId = cleanValue;
    input.value = cleanValue;
  }

  async saveDolibarrId() {
    if (!this.selectedUser) {
      console.error('No user selected for Dolibarr ID update');
      return;
    }

    // Validate Dolibarr ID
    if (!this.isValidDolibarrId()) {
      alert('Please enter a valid Dolibarr ID (alphanumeric characters only).');
      return;
    }

    this.isUpdatingDolibarrId = true;

    try {
      // TODO: Replace with actual API call to update user's Dolibarr ID
      // For now, we'll use the existing auth service method but we'll need to create a new one for admin updates
      const response = await this.authService.updateDolibarrId(this.editingDolibarrId.trim()).toPromise();

      if (response) {
        // Update local user data
        if (this.selectedUser) {
          this.selectedUser.dolibarrId = this.editingDolibarrId.trim();

          // Update the user in the users array
          const userIndex = this.users.findIndex(u => u.userId === this.selectedUser!.userId);
          if (userIndex !== -1) {
            this.users[userIndex].dolibarrId = this.editingDolibarrId.trim();
          }

          // Update filtered users if needed
          this.filterUsers();
        }

        // Close the modal with animation
        this.dolibarrModalAnimationState = 'hidden';
        setTimeout(() => {
          this.isDolibarrIdEditOpen = false;
          this.editingDolibarrId = '';
          this.selectedUser = null;
        }, 150);

        alert('Dolibarr ID updated successfully!');
      }
    } catch (error: any) {
      console.error('Failed to update Dolibarr ID:', error);

      let errorMessage = 'Failed to update Dolibarr ID. Please try again.';
      if (error.message === 'Service temporarily unavailable') {
        errorMessage = 'Service is temporarily unavailable. Please try again later.';
      }

      alert(errorMessage);
    } finally {
      this.isUpdatingDolibarrId = false;
    }
  }

}
