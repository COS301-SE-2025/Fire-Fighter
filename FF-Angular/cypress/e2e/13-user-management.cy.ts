describe('User Management System', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/user-management');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing user management', () => {
    cy.visit('/user-management');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should require admin authentication', () => {
    cy.visit('/user-management');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should protect user management functionality', () => {
    cy.visit('/user-management');

    // Should redirect to login immediately
    cy.url().should('include', '/login');
    
    // Should not show any user management content
    cy.get('body').should('not.contain.text', 'User Management');
    cy.get('body').should('not.contain.text', 'Manage Users');
    cy.get('body').should('not.contain.text', 'User List');
  });

  it('should maintain proper security structure', () => {
    cy.visit('/user-management');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
    cy.get('form').should('exist');
  });

  it('should be responsive across devices', () => {
    cy.visit('/user-management');
    
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

  it('should handle unauthorized access securely', () => {
    // Try to access user management multiple times
    cy.visit('/user-management');
    cy.url().should('include', '/login');
    
    // Try again with different approach
    cy.visit('/user-management');
    cy.url().should('include', '/login');
    
    // Should consistently redirect
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
  });

  it('should provide navigation options', () => {
    cy.visit('/user-management');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should maintain session security', () => {
    cy.visit('/user-management');
    
    // Should be on login page
    cy.url().should('include', '/login');
    
    // Reload and check again
    cy.reload();
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should handle direct URL navigation', () => {
    // Direct navigation attempt
    cy.visit('/user-management');
    cy.url().should('include', '/login');
    
    // Try to go back and access again
    cy.go('back');
    cy.visit('/user-management');
    cy.url().should('include', '/login');
  });
});