/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// Custom command to bypass Firebase authentication for testing
Cypress.Commands.add('bypassAuth', () => {
  // Mock Firebase user in localStorage for testing
  cy.window().then((win) => {
    win.localStorage.setItem('firebase:authUser:test-api-key:[DEFAULT]', JSON.stringify({
      uid: 'test-user-123',
      email: 'test@firefighter.com',
      displayName: 'Test User',
      emailVerified: true
    }));
    
    // Mock JWT token
    win.localStorage.setItem('jwtToken', 'mock-jwt-token-for-testing');
  });
});

// Custom command to clear authentication
Cypress.Commands.add('clearAuth', () => {
  cy.clearLocalStorage();
  cy.clearCookies();
});

// Declare the custom commands for TypeScript
declare global {
  namespace Cypress {
    interface Chainable {
      bypassAuth(): Chainable<void>
      clearAuth(): Chainable<void>
    }
  }
}
