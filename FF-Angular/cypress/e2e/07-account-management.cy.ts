describe('Account Management', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/account');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing account', () => {
    cy.visit('/account');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have working form elements on login redirect', () => {
    cy.visit('/account');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should show proper page structure on login redirect', () => {
    cy.visit('/account');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
  });

  it('should maintain responsive design on login redirect', () => {
    cy.visit('/account');
    
    // Test different viewports
    cy.viewport(375, 667); // Mobile
    cy.get('ion-content').should('be.visible');
    
    cy.viewport(768, 1024); // Tablet
    cy.get('ion-content').should('be.visible');
    
    cy.viewport(1280, 720); // Desktop
    cy.get('ion-content').should('be.visible');
  });

  it('should have navigation options on login page', () => {
    cy.visit('/account');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });
});