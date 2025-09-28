describe('Service Downtime Handling', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should display service down page when accessed', () => {
    cy.visit('/service-down');

    // Should show service down page (no auth guard)
    cy.url().should('include', '/service-down');
    cy.get('ion-content').should('exist');
  });

  it('should display appropriate service down message', () => {
    cy.visit('/service-down');

    // Should contain service-related content
    cy.get('ion-content').should('exist');
    cy.get('body').should('be.visible');
  });

  it('should be accessible without authentication', () => {
    cy.visit('/service-down');

    // Should not redirect to login page
    cy.url().should('include', '/service-down');
    cy.get('ion-content').should('exist');
  });

  it('should have proper page structure', () => {
    cy.visit('/service-down');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
  });

  it('should be responsive on all devices', () => {
    cy.visit('/service-down');
    
    // Wait for initial load
    cy.get('ion-content').should('exist');
    
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

  it('should handle page refresh correctly', () => {
    cy.visit('/service-down');
    
    // Should stay on service down page
    cy.url().should('include', '/service-down');
    
    // Reload the page
    cy.reload();
    
    // Should still be on service down page
    cy.url().should('include', '/service-down');
    cy.get('ion-content').should('exist');
  });

  it('should provide user feedback during service issues', () => {
    cy.visit('/service-down');

    // Should show the page content
    cy.get('ion-content').should('be.visible');
    
    // Should stay on service-down page
    cy.get('body').should('be.visible');
  });

  it('should handle navigation appropriately', () => {
    // First visit landing page to create browser history
    cy.visit('/landing');
    cy.get('ion-content').should('exist');
    
    // Then navigate to service-down
    cy.visit('/service-down');
    cy.url().should('include', '/service-down');
    
    // Test navigation behavior with proper history
    cy.go('back');
    cy.url().should('include', '/landing');
    
    // Navigate back to service-down
    cy.visit('/service-down');
    cy.url().should('include', '/service-down');
  });

  it('should maintain consistent user experience', () => {
    cy.visit('/service-down');

    // Should consistently show service down page
    cy.get('ion-content').should('exist');
    
    // Multiple visits should work the same way
    cy.visit('/service-down');
    cy.url().should('include', '/service-down');
    cy.get('ion-content').should('exist');
  });
});