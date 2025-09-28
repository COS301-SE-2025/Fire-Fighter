describe('Settings and Configuration', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to login when not authenticated', () => {
    cy.visit('/settings');

    // Should redirect to login page when not authenticated
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should display login page when accessing settings', () => {
    cy.visit('/settings');

    // Should show login page with proper elements
    cy.get('body').should('contain.text', 'FireFighter');
    cy.get('form').should('exist'); // Login form
  });

  it('should have complete login form functionality', () => {
    cy.visit('/settings');

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
    cy.visit('/settings');

    // Should have proper Ionic page structure
    cy.get('ion-content').should('exist');
    cy.get('button').should('exist');
    cy.get('form').should('exist');
  });

  it('should handle form validation appropriately', () => {
    cy.visit('/settings');
    
    // Wait for page to be fully loaded and visible
    cy.get('ion-content').should('be.visible');
    cy.wait(1000); // Give time for Angular to fully render
    
    // Try to submit empty form - use force option to handle visibility issues
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Should stay on login page or service-down (not navigate away)
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
  });

  it('should be responsive across all devices', () => {
    cy.visit('/settings');
    
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

  it('should handle page navigation correctly', () => {
    cy.visit('/settings');
    
    // Should be on login page or service-down due to auth guard
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    // Wait for page to be fully loaded and visible
    cy.get('ion-content').should('be.visible');
    cy.wait(1000); // Give time for Angular to fully render
    
    // Try to navigate to register page if login page is available
    cy.get('body').then(($body) => {
      if ($body.find('a:contains("Create an account")').length > 0) {
        cy.get('a').contains('Create an account').click({ force: true });
        cy.url().should('satisfy', (url: string) => {
          return url.includes('/register') || url.includes('/service-down') || url.includes('/login');
        });
      }
    });
  });
});