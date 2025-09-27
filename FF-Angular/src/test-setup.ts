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
  isCurrentUserAdmin: jasmine.createSpy('isCurrentUserAdmin').and.returnValue(false), // Return boolean directly, not Observable
  getCurrentUserProfile: jasmine.createSpy('getCurrentUserProfile').and.returnValue(of({ username: 'Test User', email: 'test@example.com' })),
  getAllUsersAsAdmin: jasmine.createSpy('getAllUsersAsAdmin').and.returnValue(of([])), // Add missing method
};

// Mock TicketService for tests
export const mockTicketService = {
  getTickets: jasmine.createSpy('getTickets').and.returnValue(of([])),
  createTicket: jasmine.createSpy('createTicket').and.returnValue(of({})),
  updateTicket: jasmine.createSpy('updateTicket').and.returnValue(of({})),
  ticketCreated$: of(null), // Add the missing observable
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

// Mock LanguageService for tests
export const mockLanguageService = {
  currentLanguage$: of('en'),
  availableLanguages: [
    { code: 'en', name: 'English (UK)' },
    { code: 'de', name: 'Deutsch (German)' }
  ],
  setLanguage: jasmine.createSpy('setLanguage'),
  getCurrentLanguage: jasmine.createSpy('getCurrentLanguage').and.returnValue('en'),
  getCurrentLanguageObject: jasmine.createSpy('getCurrentLanguageObject').and.returnValue({ code: 'en', name: 'English (UK)' }),
  getLanguageByCode: jasmine.createSpy('getLanguageByCode').and.returnValue({ code: 'en', name: 'English (UK)' }),
  getTranslation: jasmine.createSpy('getTranslation').and.returnValue(of('Test Translation')),
  getInstantTranslation: jasmine.createSpy('getInstantTranslation').and.returnValue('Test Translation'),
};

// Mock HealthService for tests
export const mockHealthService = {
  healthStatus$: of({ isHealthy: true, lastCheck: new Date() }),
  startMonitoring: jasmine.createSpy('startMonitoring'),
  stopMonitoring: jasmine.createSpy('stopMonitoring'),
  checkHealth: jasmine.createSpy('checkHealth').and.returnValue(of({ isHealthy: true, lastCheck: new Date() })),
};

// Mock ChatbotService for tests
export const mockChatbotService = {
  getSuggestions: jasmine.createSpy('getSuggestions').and.returnValue(of({ available: true, suggestedQueries: [] })),
  sendMessage: jasmine.createSpy('sendMessage').and.returnValue(of({ response: 'Test response' })),
  isAdminQuery: jasmine.createSpy('isAdminQuery').and.returnValue(false),
  checkHealth: jasmine.createSpy('checkHealth').and.returnValue(of({ available: true })),
};

// Mock ApiConfigService for tests
export const mockApiConfigService = {
  getCurrentApiUrl: jasmine.createSpy('getCurrentApiUrl').and.returnValue(of('http://localhost:8080/api')),
  getCurrentApiUrlSync: jasmine.createSpy('getCurrentApiUrlSync').and.returnValue('http://localhost:8080/api'),
  switchToFallback: jasmine.createSpy('switchToFallback'),
  switchToPrimary: jasmine.createSpy('switchToPrimary'),
  isUsingFallbackApi: jasmine.createSpy('isUsingFallbackApi').and.returnValue(false),
  getPrimaryApiUrl: jasmine.createSpy('getPrimaryApiUrl').and.returnValue('http://localhost:8080/api'),
  getFallbackApiUrl: jasmine.createSpy('getFallbackApiUrl').and.returnValue('http://localhost:8080/api'),
  reset: jasmine.createSpy('reset'),
};

// Mock HealthMonitorService for tests
export const mockHealthMonitorService = {
  startMonitoring: jasmine.createSpy('startMonitoring'),
  stopMonitoring: jasmine.createSpy('stopMonitoring'),
};

// Mock AppLoadingService for tests
export const mockAppLoadingService = {
  setLoadingTimeout: jasmine.createSpy('setLoadingTimeout'),
  initializeApp: jasmine.createSpy('initializeApp').and.returnValue(Promise.resolve()),
  loadingState$: of({ isLoading: false, message: '', progress: 100 }),
  getCurrentState: jasmine.createSpy('getCurrentState').and.returnValue({ isLoading: false, message: '', progress: 100 }),
};

// Mock TokenMonitoringService for tests
export const mockTokenMonitoringService = {
  startMonitoring: jasmine.createSpy('startMonitoring'),
  stopMonitoring: jasmine.createSpy('stopMonitoring'),
};