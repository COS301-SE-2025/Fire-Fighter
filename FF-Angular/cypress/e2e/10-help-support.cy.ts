describe('Help and Support', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/help');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing help', () => {
    cy.visit('/help');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have complete authentication form', () => {
    cy.visit('/help');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
    
    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should maintain proper page structure', () => {
    cy.visit('/help');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
    cy.get('form').should('exist');
  });

  it('should handle empty form submission gracefully', () => {
    cy.visit('/help');
    
    // Try to submit empty form
    cy.get('ion-button, button').contains(/login|sign in/i).click();
    
    // Should stay on login page (not navigate away)
    cy.url().should('include', '/login');
  });

  it('should be responsive and accessible', () => {
    cy.visit('/help');
    
    // Test mobile viewport
    cy.viewport(375, 667);
    cy.get('ion-content').should('be.visible');
    cy.get('form').should('be.visible');
    
    // Test tablet viewport
    cy.viewport(768, 1024);
    cy.get('ion-content').should('be.visible');
    cy.get('form').should('be.visible');
    
    // Test desktop viewport
    cy.viewport(1280, 720);
    cy.get('ion-content').should('be.visible');
    cy.get('form').should('be.visible');
  });

  it('should provide proper navigation flow', () => {
    cy.visit('/help');
    
    // Should be on login page due to auth guard
    cy.url().should('include', '/login');
    
    // Should be able to navigate to register
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should maintain session state correctly', () => {
    cy.visit('/help');
    
    // Reload the page
    cy.reload();
    
    // Should still show login page
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
  });
});