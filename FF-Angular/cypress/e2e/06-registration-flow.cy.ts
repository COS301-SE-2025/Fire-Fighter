describe('Registration Flow', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should navigate to registration page from login', () => {
    cy.visit('/login');
    
    // Click on "Create an account" link
    cy.get('a').contains('Create an account').click();
    
    // Should navigate to register page
    cy.url().should('include', '/register');
    cy.get('ion-content').should('exist');
  });

  it('should display registration form elements', () => {
    cy.visit('/register');
    
    // Check if registration form elements exist
    cy.get('form, ion-content').should('exist');
    
    // Look for common registration inputs
    cy.get('ion-input, input').should('have.length.greaterThan', 2);
    
    // Look for register button
    cy.get('ion-button, button').contains(/register|sign up|create/i)
      .should('exist');
  });

  it('should have link back to login page', () => {
    cy.visit('/register');
    
    // Should have a link back to login page
    cy.get('a, ion-button').contains(/login|sign in|back/i)
      .should('exist');
  });

  it('should show validation for empty form', () => {
    cy.visit('/register');
    
    // Try to submit empty form
    cy.get('ion-button, button')
      .contains(/register|sign up|create/i)
      .first()
      .click();
    
    // Should stay on register page (not navigate away)
    cy.url().should('include', '/register');
  });

  it('should be responsive on different screen sizes', () => {
    cy.visit('/register');
    
    // Test mobile viewport
    cy.viewport(375, 667);
    cy.get('ion-content').should('be.visible');
    
    // Test tablet viewport
    cy.viewport(768, 1024);
    cy.get('ion-content').should('be.visible');
    
    // Test desktop viewport
    cy.viewport(1280, 720);
    cy.get('ion-content').should('be.visible');
  });

  it('should have proper page structure', () => {
    cy.visit('/register');
    
    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
    
    // Should contain FireFighter branding
    cy.get('body').should('contain.text', 'FireFighter');
  });
});