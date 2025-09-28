describe('Landing Page', () => {
  beforeEach(() => {
    cy.clearAuth();
    cy.visit('/');
  });

  it('should display the landing page', () => {
    // Check if the page loads
    cy.url().should('include', '/');
    
    // Check for key elements on landing page
    cy.get('body').should('be.visible');
    
    // Look for common landing page elements
    cy.get('ion-content').should('exist');
  });

  it('should have navigation elements', () => {
    // Check if there are navigation elements or buttons
    cy.get('ion-button, button, a').should('have.length.greaterThan', 0);
  });

  it('should be responsive', () => {
    // Test mobile viewport
    cy.viewport(375, 667);
    cy.get('body').should('be.visible');
    
    // Test tablet viewport
    cy.viewport(768, 1024);
    cy.get('body').should('be.visible');
    
    // Test desktop viewport
    cy.viewport(1280, 720);
    cy.get('body').should('be.visible');
  });
});
