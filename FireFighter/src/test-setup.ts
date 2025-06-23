// src/test-setup.ts
import { importProvidersFrom } from '@angular/core';
import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { provideAuth, getAuth, connectAuthEmulator } from '@angular/fire/auth';
import { provideRouter } from '@angular/router';
import { RouteReuseStrategy } from '@angular/router';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';
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

// Test providers that can be used in all tests
export const testProviders = [
  { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
  provideIonicAngular(),
  provideRouter([]),
  provideFirebaseApp(() => testApp),
  provideAuth(() => testAuth),
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
  signInWithGoogle: jasmine.createSpy('signInWithGoogle'),
  signInWithEmail: jasmine.createSpy('signInWithEmail'),
  createUserWithEmail: jasmine.createSpy('createUserWithEmail'),
  resetPassword: jasmine.createSpy('resetPassword'),
  signOut: jasmine.createSpy('signOut'),
  logout: jasmine.createSpy('logout'),
  navigateToDashboard: jasmine.createSpy('navigateToDashboard'),
}; 