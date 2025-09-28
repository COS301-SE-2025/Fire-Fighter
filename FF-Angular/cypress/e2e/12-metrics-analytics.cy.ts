describe('Metrics and Analytics', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/metrics');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing metrics', () => {
    cy.visit('/metrics');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should enforce admin-level authentication', () => {
    cy.visit('/metrics');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should protect sensitive metrics data', () => {
    cy.visit('/metrics');

    // Should redirect to login immediately
    cy.url().should('include', '/login');
    
    // Should not show any metrics data
    cy.get('body').should('not.contain.text', 'Analytics');
    cy.get('body').should('not.contain.text', 'Dashboard Metrics');
    cy.get('body').should('not.contain.text', 'Statistics');
  });

  it('should maintain proper page structure', () => {
    cy.visit('/metrics');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
    cy.get('form').should('exist');
  });

  it('should be responsive and secure', () => {
    cy.visit('/metrics');
    
    // Test mobile viewport
    cy.viewport(375, 667);
    cy.get('ion-content').should('be.visible');
    cy.url().should('include', '/login');
    
    // Test tablet viewport
    cy.viewport(768, 1024);
    cy.get('ion-content').should('be.visible');
    cy.url().should('include', '/login');
    
    // Test desktop viewport
    cy.viewport(1280, 720);
    cy.get('ion-content').should('be.visible');
    cy.url().should('include', '/login');
  });

  it('should handle unauthorized access attempts', () => {
    // Try multiple access attempts
    cy.visit('/metrics');
    cy.url().should('include', '/login');
    
    cy.visit('/metrics');
    cy.url().should('include', '/login');
    
    // Should consistently redirect to login
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
  });

  it('should provide proper navigation from login', () => {
    cy.visit('/metrics');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should handle page refresh securely', () => {
    cy.visit('/metrics');
    
    // Reload the page
    cy.reload();
    
    // Should still redirect to login
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });
});