// Import commands.js using ES2015 syntax:
import './commands';

// Alternatively you can use CommonJS syntax:
// require('./commands')

// Global configuration
Cypress.config('defaultCommandTimeout', 10000);
Cypress.config('requestTimeout', 10000);
Cypress.config('responseTimeout', 10000);

// Global before hook
beforeEach(() => {
  // Set viewport for consistent testing
  cy.viewport(1280, 720);
  
  // Clear local storage and cookies before each test
  cy.clearLocalStorage();
  cy.clearCookies();
}); 