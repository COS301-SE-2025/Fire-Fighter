// src/test-setup.ts
import { importProvidersFrom } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { provideAuth, getAuth, connectAuthEmulator } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';
import { RouteReuseStrategy } from '@angular/router';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';

// Mock Firebase config for testing
const testFirebaseConfig = {
  apiKey: "test-api-key",
  authDomain: "test.firebaseapp.com",
  projectId: "test-project",
  storageBucket: "test-project.appspot.com",
  messagingSenderId: "123456789",
  appId: "1:123456789:web:test123",
  measurementId: "G-TEST123"
};

// Create Firebase app for testing
const testApp = initializeApp(testFirebaseConfig);
const testAuth = getAuth(testApp);

// Connect to Auth emulator in test environment
if (typeof window !== 'undefined' && !('_FirebaseTest' in window)) {
  try {
    connectAuthEmulator(testAuth, 'http://localhost:9099', { disableWarnings: true });
    (window as any)._FirebaseTest = true;
  } catch (error) {
    // Emulator might already be connected
  }
}

// Mock TranslateLoader for testing
export class MockTranslateLoader implements TranslateLoader {
  getTranslation(lang: string) {
    return of({
      'COMMON.LOADING': 'Loading...',
      'COMMON.ERROR': 'Error',
      'COMMON.SUCCESS': 'Success',
      'NOTIFICATIONS.TITLE': 'Notifications',
      'ACCOUNT.TITLE': 'Account',
      'DASHBOARD.TITLE': 'Dashboard',
      'HELP.TITLE': 'Help',
      'SETTINGS.TITLE': 'Settings',
      'REQUESTS.TITLE': 'Requests',
      'CHAT.TITLE': 'Chat'
    });
  }
}

// Test providers that can be used in all tests
export const testProviders = [
  { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
  provideIonicAngular(),
  provideRouter([]),
  provideHttpClient(),
  provideHttpClientTesting(),
  provideFirebaseApp(() => testApp),
  provideAuth(() => testAuth),
  // Add TranslateModule for testing
  importProvidersFrom(
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useClass: MockTranslateLoader
      },
      defaultLanguage: 'en'
    })
  ),
];

// Mock user for testing
export const mockUser = {
  uid: 'test-uid',
  email: 'test@example.com',
  displayName: 'Test User',
  emailVerified: true,
  isAnonymous: false,
  metadata: {},
  providerData: [],
  refreshToken: 'test-refresh-token',
  tenantId: null,
  delete: jasmine.createSpy('delete'),
  getIdToken: jasmine.createSpy('getIdToken').and.returnValue(Promise.resolve('test-token')),
  getIdTokenResult: jasmine.createSpy('getIdTokenResult'),
  reload: jasmine.createSpy('reload'),
  toJSON: jasmine.createSpy('toJSON'),
};

// Mock AuthService for tests that don't need real Firebase
export const mockAuthService = {
  user$: of(null),
  userProfile$: of(null),
  isAdmin$: of(false),
  signInWithGoogle: jasmine.createSpy('signInWithGoogle'),
  signInWithEmail: jasmine.createSpy('signInWithEmail'),
  createUserWithEmail: jasmine.createSpy('createUserWithEmail'),
  resetPassword: jasmine.createSpy('resetPassword'),
  signOut: jasmine.createSpy('signOut'),
  logout: jasmine.createSpy('logout'),
  navigateToDashboard: jasmine.createSpy('navigateToDashboard'),
  getUserProfileById: jasmine.createSpy('getUserProfileById').and.returnValue(of({ username: 'Test User', email: 'test@example.com' })),
  isCurrentUserAdmin: jasmine.createSpy('isCurrentUserAdmin').and.returnValue(of(false)),
  getCurrentUserProfile: jasmine.createSpy('getCurrentUserProfile').and.returnValue(of({ username: 'Test User', email: 'test@example.com' })),
};

// Mock TicketService for tests
export const mockTicketService = {
  getTickets: jasmine.createSpy('getTickets').and.returnValue(of([])),
  createTicket: jasmine.createSpy('createTicket').and.returnValue(of({})),
  updateTicket: jasmine.createSpy('updateTicket').and.returnValue(of({})),
};

// Mock NotificationService for tests
export const mockNotificationService = {
  getNotifications: jasmine.createSpy('getNotifications').and.returnValue(of([])),
  markAsRead: jasmine.createSpy('markAsRead'),
  markAllAsRead: jasmine.createSpy('markAllAsRead'),
};

// Mock ThemeService for tests
export const mockThemeService = {
  toggleTheme: jasmine.createSpy('toggleTheme'),
  setTheme: jasmine.createSpy('setTheme'),
  getCurrentTheme: jasmine.createSpy('getCurrentTheme').and.returnValue(false),
  setStatusBarDark: jasmine.createSpy('setStatusBarDark'),
}; 