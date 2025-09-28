describe('Admin Panel Access', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/admin');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing admin panel', () => {
    cy.visit('/admin');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have proper authentication requirements', () => {
    cy.visit('/admin');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Should have login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should maintain security by redirecting unauthenticated users', () => {
    cy.visit('/admin');

    // Should immediately redirect to login
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
    
    // Should not show admin content
    cy.get('body').should('not.contain.text', 'Admin Panel');
    cy.get('body').should('not.contain.text', 'User Management');
  });

  it('should have proper page structure on redirect', () => {
    cy.visit('/admin');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
    cy.get('form').should('exist');
  });

  it('should be responsive on all devices', () => {
    cy.visit('/admin');
    
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

  it('should handle direct URL access properly', () => {
    // Try to access admin directly
    cy.visit('/admin');
    
    // Should redirect to login
    cy.url().should('include', '/login');
    
    // Should not allow bypassing auth
    cy.go('back');
    cy.url().should('include', '/login');
  });

  it('should provide navigation options from login page', () => {
    cy.visit('/admin');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });
});