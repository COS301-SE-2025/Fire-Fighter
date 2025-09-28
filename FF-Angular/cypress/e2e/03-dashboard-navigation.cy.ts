describe('Dashboard Navigation', () => {
  it('should redirect to login when not authenticated', () => {
    cy.visit('/dashboard');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page elements', () => {
    cy.visit('/dashboard');

    // Should show login page with FireFighter branding
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('body').should('contain.text', 'Emergency Access Management System');
  });

  it('should have working navigation from login', () => {
    cy.visit('/dashboard');

    // Should be able to navigate to register from login page
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should show proper page structure', () => {
    cy.visit('/dashboard');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist'); // Login form should be present
  });
});
