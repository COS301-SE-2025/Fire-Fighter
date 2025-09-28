describe('Emergency Request Flow', () => {
  it('should redirect to login when not authenticated', () => {
    cy.visit('/requests');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing requests', () => {
    cy.visit('/requests');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have working form elements on login page', () => {
    cy.visit('/requests');

    // Should have email and password inputs
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
  });

  it('should show proper page structure', () => {
    cy.visit('/requests');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist'); // Should have buttons (login, etc.)
  });
});
