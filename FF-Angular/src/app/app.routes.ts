import { Routes } from '@angular/router';
import { authGuard, redirectLoggedInToHome, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.page').then(m => m.LoginPage),
    canActivate: [redirectLoggedInToHome]
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.page').then( m => m.RegisterPage),
    canActivate: [redirectLoggedInToHome]
  },
  {
    path: 'inactive-account',
    loadComponent: () => import('./pages/inactive-account/inactive-account.page').then( m => m.InactiveAccountPage)
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.page').then(m => m.DashboardPage),
    canActivate: [authGuard]
  },
  {
    path: 'requests',
    loadComponent: () => import('./pages/requests/requests.page').then( m => m.RequestsPage),
    canActivate: [authGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./pages/notifications/notifications.page').then( m => m.NotificationsPage),
    canActivate: [authGuard]
  },
  {
    path: 'account',
    loadComponent: () => import('./pages/account/account.page').then( m => m.AccountPage),
    canActivate: [authGuard]
  },
  {
    path: 'help',
    loadComponent: () => import('./pages/help/help.page').then( m => m.HelpPage),
    canActivate: [authGuard]
  },
  {
    path: 'settings',
    loadComponent: () => import('./pages/settings/settings.page').then( m => m.SettingsPage),
    canActivate: [authGuard]
  },
  {
    path: 'landing',
    loadComponent: () => import('./pages/landing/landing.page').then( m => m.LandingPage)
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin/admin.page').then( m => m.AdminPage),
    canActivate: [adminGuard]
  },
  {
    path: 'metrics',
    loadComponent: () => import('./pages/metrics/metrics.page').then( m => m.MetricsPage),
    canActivate: [adminGuard]
  },
  {
    path: 'chat',
    loadComponent: () => import('./pages/chat/chat.page').then( m => m.ChatPage),
    canActivate: [authGuard]
  },
  {
    path: 'service-down',
    loadComponent: () => import('./pages/service-down/service-down.page').then( m => m.ServiceDownPage)
  },
  {
    path: 'user-management',
    loadComponent: () => import('./pages/user-management/user-management.page').then( m => m.UserManagementPage),
    canActivate: [adminGuard]
  },
  {
    path: 'access-request',
    loadComponent: () => import('./pages/access-request/access-request.page').then( m => m.AccessRequestPage)
    // No auth guard - this page is accessed after registration before approval
  },
  {
    path: '',
    redirectTo: 'landing',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'landing'
  }

];