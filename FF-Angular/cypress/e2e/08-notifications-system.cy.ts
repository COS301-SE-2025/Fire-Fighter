describe('Notifications System', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/notifications');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing notifications', () => {
    cy.visit('/notifications');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have working authentication form elements', () => {
    cy.visit('/notifications');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should show proper page structure on login redirect', () => {
    cy.visit('/notifications');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
  });

  it('should be responsive on different devices', () => {
    cy.visit('/notifications');
    
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

  it('should have proper navigation elements', () => {
    cy.visit('/notifications');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should handle page refresh properly', () => {
    cy.visit('/notifications');
    
    // Reload the page
    cy.reload();
    
    // Should still show login page
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });
});