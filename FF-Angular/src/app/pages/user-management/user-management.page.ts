import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language.service';

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

interface PendingApproval {
  userId: string;
  username: string;
  email: string;
  department: string;
  contactNumber: string;
  createdAt: string;
  registrationMethod: string;
  requestedAccess: string;
  businessJustification: string;
  priorityLevel: 'High' | 'Medium' | 'Low';
}

interface AccessGroup {
  id: string;
  name: string;
  description: string;
  enabled: boolean;
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.page.html',
  styleUrls: ['./user-management.page.scss'],
  standalone: true,
  imports: [IonContent, IonRefresher, IonRefresherContent, CommonModule, FormsModule, NavbarComponent, TranslateModule],
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

  // Tab management
  activeTab = 'users';

  // Mock pending approval data
  pendingApprovals: PendingApproval[] = [
    {
      userId: 'pending_001',
      username: 'sarah.mueller',
      email: 'sarah.mueller@bmw.com',
      department: 'Financial Emergency Response',
      contactNumber: '+49 89 382 12345',
      createdAt: '2025-01-15T09:23:45Z',
      registrationMethod: 'Google SSO',
      requestedAccess: 'Financial Emergency Group Access',
      businessJustification: 'Need emergency access for financial crisis management and budget approval workflows during out-of-hours incidents. Request is for accessing critical financial systems during potential BMW Group emergency situations that require immediate budget approvals and resource allocation.',
      priorityLevel: 'High'
    },
    {
      userId: 'pending_002',
      username: 'marcus.schmidt',
      email: 'm.schmidt@bmw.com',
      department: 'IT Infrastructure Support',
      contactNumber: '+49 89 382 67890',
      createdAt: '2025-01-14T14:17:32Z',
      registrationMethod: 'Azure AD',
      requestedAccess: 'Logistics Emergency Group Access',
      businessJustification: 'Required access to coordinate emergency logistics and supply chain responses. Will be supporting critical infrastructure maintenance and ensuring business continuity during emergency situations.',
      priorityLevel: 'Medium'
    }
  ];

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

  // Department management
  isDepartmentModalOpen = false;
  selectedUserDepartment = '';
  isUpdatingDepartment = false;
  departmentModalAnimationState = 'hidden';

  // Access Groups management
  isAccessGroupsModalOpen = false;
  isUpdatingAccessGroups = false;
  accessGroupsModalAnimationState = 'hidden';

  // Account status management
  isAccountStatusModalOpen = false;
  accountStatusModalAnimationState = 'hidden';
  isUpdatingAccountStatus = false;
  availableAccessGroups: AccessGroup[] = [
    {
      id: 'financial',
      name: 'Financial Emergency Group',
      description: 'Budget approvals, financial crisis management, monetary systems access',
      enabled: false
    },
    {
      id: 'hr',
      name: 'HR Emergency Group',
      description: 'Human resources emergency protocols, employee data access, privacy-sensitive operations',
      enabled: false
    },
    {
      id: 'management',
      name: 'Management Emergency Group',
      description: 'Executive-level emergency protocols, strategic decision making, high-level coordination',
      enabled: false
    },
    {
      id: 'logistics',
      name: 'Logistics Emergency Group',
      description: 'Supply chain coordination, infrastructure maintenance, business continuity',
      enabled: false
    }
  ];

  constructor(
    private authService: AuthService,
    private languageService: LanguageService
  ) { }

  ngOnInit() {
    this.loadUsers();
  }

  doRefresh(event: any) {
    this.loadUsers().then(() => {
      event.target.complete();
    });
  }

  // Tab management methods
  switchTab(tabId: string) {
    this.activeTab = tabId;
    console.log('Switched to tab:', tabId);
  }

  isTabActive(tabId: string): boolean {
    return this.activeTab === tabId;
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
    const currentLang = this.languageService.getCurrentLanguage();
    const locale = currentLang === 'de' ? 'de-DE' : 'en-US';
    return date.toLocaleDateString(locale, {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getTranslatedRole(isAdmin: boolean): string {
    return isAdmin ?
      this.languageService.getInstantTranslation('USER_MANAGEMENT.ROLES.ADMIN') :
      this.languageService.getInstantTranslation('USER_MANAGEMENT.ROLES.USER');
  }

  getUserCountText(count: number): string {
    return `${count} ${this.languageService.getInstantTranslation('USER_MANAGEMENT.MOBILE.USER_COUNT')}`;
  }

  getManageActionsTitle(username: string): string {
    return `${this.languageService.getInstantTranslation('USER_MANAGEMENT.ACTIONS.MANAGE_ACTIONS_FOR')} ${username}`;
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

  // Department management methods
  manageDepartment(user: User) {
    this.selectedUser = user;
    this.selectedUserDepartment = user.department || '';
    this.isDepartmentModalOpen = true;
    // Trigger animation after DOM update
    setTimeout(() => {
      this.departmentModalAnimationState = 'visible';
    }, 10);
  }

  closeDepartmentModal() {
    this.departmentModalAnimationState = 'hidden';
    setTimeout(() => {
      this.isDepartmentModalOpen = false;
      this.selectedUserDepartment = '';
      this.selectedUser = null;
    }, 150);
  }

  async saveDepartment() {
    if (!this.selectedUser || !this.selectedUserDepartment.trim()) {
      return;
    }

    this.isUpdatingDepartment = true;

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));

      // Update local user data
      if (this.selectedUser) {
        this.selectedUser.department = this.selectedUserDepartment.trim();

        // Update the user in the users array
        const userIndex = this.users.findIndex(u => u.userId === this.selectedUser!.userId);
        if (userIndex !== -1) {
          this.users[userIndex].department = this.selectedUserDepartment.trim();
        }

        // Update filtered users
        this.filterUsers();
      }

      // Close the modal
      this.closeDepartmentModal();
      alert('Department updated successfully!');

    } catch (error: any) {
      console.error('Failed to update department:', error);
      alert('Failed to update department. Please try again.');
    } finally {
      this.isUpdatingDepartment = false;
    }
  }

  // Access Groups management methods
  manageAccessGroups(user: User) {
    this.selectedUser = user;
    
    // Reset all groups to unchecked
    this.availableAccessGroups.forEach(group => {
      group.enabled = false;
    });

    // TODO: Load user's current access groups from API
    // For now, simulate some enabled groups
    if (user.department.includes('Financial')) {
      const financialGroup = this.availableAccessGroups.find(g => g.id === 'financial');
      if (financialGroup) financialGroup.enabled = true;
    }
    
    this.isAccessGroupsModalOpen = true;
    // Trigger animation after DOM update
    setTimeout(() => {
      this.accessGroupsModalAnimationState = 'visible';
    }, 10);
  }

  closeAccessGroupsModal() {
    this.accessGroupsModalAnimationState = 'hidden';
    setTimeout(() => {
      this.isAccessGroupsModalOpen = false;
      this.selectedUser = null;
      // Reset all groups
      this.availableAccessGroups.forEach(group => {
        group.enabled = false;
      });
    }, 150);
  }

  async saveAccessGroups() {
    if (!this.selectedUser) {
      return;
    }

    this.isUpdatingAccessGroups = true;

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));

      const enabledGroups = this.availableAccessGroups.filter(g => g.enabled);
      console.log('Saving access groups for user:', this.selectedUser.username, enabledGroups);

      // TODO: Send to API
      // await this.authService.updateUserAccessGroups(this.selectedUser.userId, enabledGroups);

      // Close the modal
      this.closeAccessGroupsModal();
      alert(`Access groups updated successfully for ${this.selectedUser.username}!`);

    } catch (error: any) {
      console.error('Failed to update access groups:', error);
      alert('Failed to update access groups. Please try again.');
    } finally {
      this.isUpdatingAccessGroups = false;
    }
  }

  // Account Status Management Methods
  toggleAccountStatus(user: any) {
    this.selectedUser = user;
    this.isAccountStatusModalOpen = true;
    // Trigger animation after DOM update
    setTimeout(() => {
      this.accountStatusModalAnimationState = 'visible';
    }, 10);
  }

  closeAccountStatusModal() {
    this.accountStatusModalAnimationState = 'hidden';
    setTimeout(() => {
      this.isAccountStatusModalOpen = false;
      this.selectedUser = null;
    }, 150);
  }

  async confirmAccountStatusChange() {
    if (!this.selectedUser) {
      return;
    }

    this.isUpdatingAccountStatus = true;

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));

      const action = this.selectedUser.isAuthorized ? 'disabled' : 'enabled';
      console.log(`Account ${action} for user:`, this.selectedUser.username);

      // TODO: Send to API
      // await this.authService.updateUserAccountStatus(this.selectedUser.userId, !this.selectedUser.isAuthorized);

      // Update the user's status locally for demo purposes
      this.selectedUser.isAuthorized = !this.selectedUser.isAuthorized;
      
      // Find and update the user in the users array
      const userIndex = this.users.findIndex(u => u.userId === this.selectedUser?.userId);
      if (userIndex !== -1) {
        this.users[userIndex].isAuthorized = this.selectedUser.isAuthorized;
      }

      // Close the modal
      this.closeAccountStatusModal();
      
      // Show success message
      const statusText = this.selectedUser.isAuthorized ? 'enabled' : 'disabled';
      alert(`Account ${statusText} successfully for ${this.selectedUser.username}!`);

    } catch (error: any) {
      console.error('Failed to update account status:', error);
      alert('Failed to update account status. Please try again.');
    } finally {
      this.isUpdatingAccountStatus = false;
    }
  }

}
