/// <reference types="cypress" />

declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>;
      logout(): Chainable<void>;
      createTicket(ticketData: any): Chainable<void>;
      waitForAppToLoad(): Chainable<void>;
      checkApiHealth(): Chainable<void>;
    }
  }
}

// Custom command to login
Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/login');
  cy.waitForAppToLoad();
  
  cy.get('[data-cy="email-input"]').type(email);
  cy.get('[data-cy="password-input"]').type(password);
  cy.get('[data-cy="login-button"]').click();
  
  // Wait for redirect to dashboard
  cy.url().should('include', '/dashboard');
  cy.get('[data-cy="dashboard-container"]').should('be.visible');
});

// Custom command to logout
Cypress.Commands.add('logout', () => {
  cy.get('[data-cy="user-menu"]').click();
  cy.get('[data-cy="logout-button"]').click();
  cy.url().should('include', '/login');
});

// Custom command to create a ticket
Cypress.Commands.add('createTicket', (ticketData) => {
  cy.visit('/requests');
  cy.waitForAppToLoad();
  
  cy.get('[data-cy="create-ticket-button"]').click();
  cy.get('[data-cy="ticket-title-input"]').type(ticketData.title);
  cy.get('[data-cy="ticket-description-input"]').type(ticketData.description);
  
  if (ticketData.priority) {
    cy.get('[data-cy="priority-select"]').select(ticketData.priority);
  }
  
  cy.get('[data-cy="submit-ticket-button"]').click();
  cy.get('[data-cy="success-message"]').should('be.visible');
});

// Custom command to wait for app to load
Cypress.Commands.add('waitForAppToLoad', () => {
  cy.get('ion-app').should('be.visible');
  // Wait for any loading spinners to disappear
  cy.get('ion-loading').should('not.exist');
  cy.get('[data-cy="loading-spinner"]').should('not.exist');
});

// Custom command to check API health
Cypress.Commands.add('checkApiHealth', () => {
  cy.request({
    method: 'GET',
    url: `${Cypress.env('apiUrl')}/health`,
    failOnStatusCode: false
  }).then((response) => {
    expect(response.status).to.eq(200);
  });
}); 