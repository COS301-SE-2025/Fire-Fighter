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

  // Expandable user details
  expandedUserId: string | null = null;
  animatingUsers = new Set<string>();
  closingUsers = new Set<string>();

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
      // Call the real API to get all users
      const response = await this.authService.getAllUsersAsAdmin().toPromise();

      if (response && response.users) {
        this.users = response.users;

        // Update statistics from API response if available, otherwise calculate locally
        if (response.statistics) {
          this.normalUsers = response.statistics.normalUsers;
          this.adminUsers = response.statistics.adminUsers;
        } else {
          this.updateStatistics();
        }

        this.filterUsers();
        console.log('✅ Users loaded successfully:', this.users.length, 'users');
      } else {
        console.error('❌ Invalid response format from getAllUsersAsAdmin');
        this.users = [];
        this.updateStatistics();
        this.filterUsers();
      }
    } catch (error: any) {
      console.error('❌ Error loading users:', error);

      // Handle specific error cases
      if (error.message === 'Service temporarily unavailable') {
        // Service down error is already handled by the auth service
        console.log('🔌 Service is down, user will be redirected');
      } else {
        // Show user-friendly error message
        alert('Failed to load users. Please try again or contact support if the problem persists.');
      }

      // Reset to empty state
      this.users = [];
      this.updateStatistics();
      this.filterUsers();
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

    // Apply role filter
    switch (this.selectedFilter) {
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

  // Toggle expandable user details
  toggleExpandUser(userId: string) {
    if (this.expandedUserId === userId) {
      // Collapsing - start closing animation
      this.closingUsers.add(userId);
      this.animatingUsers.add(userId);

      setTimeout(() => {
        this.expandedUserId = null;
        this.animatingUsers.delete(userId);
        this.closingUsers.delete(userId);
      }, 300); // Match animation duration
    } else {
      // Expanding - close any other expanded user first
      if (this.expandedUserId) {
        const previousId = this.expandedUserId;
        this.closingUsers.add(previousId);
        this.animatingUsers.add(previousId);

        setTimeout(() => {
          this.animatingUsers.delete(previousId);
          this.closingUsers.delete(previousId);
        }, 300);
      }

      // Start expanding the new user
      this.expandedUserId = userId;
      this.animatingUsers.add(userId);
      // Make sure it's not in the closing set
      this.closingUsers.delete(userId);

      setTimeout(() => {
        this.animatingUsers.delete(userId);
      }, 300);
    }
  }

  // Helper methods for user animation states
  isUserExpanded(id: string): boolean {
    return this.expandedUserId === id;
  }

  isUserAnimating(id: string): boolean {
    return this.animatingUsers.has(id);
  }

  getUserAnimationClass(id: string): string {
    if (this.isUserAnimating(id)) {
      // If the user is in the closing set, it should use the collapsed animation
      if (this.closingUsers.has(id)) {
        return 'collapsed';
      }
      // Otherwise, if it's expanded, use the expanded animation
      return this.isUserExpanded(id) ? 'expanded' : 'collapsed';
    }
    return '';
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
      // Use the new admin API to update user's Dolibarr ID
      const response = await this.authService.updateUserDolibarrIdAsAdmin(
        this.selectedUser.userId,
        this.editingDolibarrId.trim()
      ).toPromise();

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
      } else if (error.status === 403) {
        errorMessage = 'You do not have permission to update Dolibarr IDs. Administrator privileges required.';
      } else if (error.status === 404) {
        errorMessage = 'User not found. Please refresh the page and try again.';
      }

      alert(errorMessage);
    } finally {
      this.isUpdatingDolibarrId = false;
    }
  }

}
