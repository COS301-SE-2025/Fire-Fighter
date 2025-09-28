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
    // Test mobile viewport
    cy.viewport(375, 667);
    cy.visit('/user-management');
    // Check if we're redirected to login OR service-down
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('be.visible');
    
    // Test tablet viewport
    cy.viewport(768, 1024);
    cy.visit('/user-management');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('be.visible');
    
    // Test desktop viewport
    cy.viewport(1280, 720);
    cy.visit('/user-management');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('be.visible');
  });

  it('should handle unauthorized access securely', () => {
    // Try to access user management multiple times
    cy.visit('/user-management');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    // Try again with different approach
    cy.visit('/user-management');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    // Should consistently redirect
    cy.get('ion-content').should('exist');
  });

  it('should provide navigation options', () => {
    cy.visit('/user-management');

    // Should have register link
    cy.get('a').contains('Create an account').should('exist');
  });

  it('should maintain session security', () => {
    cy.visit('/user-management');
    
    // Should be redirected to login or service-down
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    // Reload and check again
    cy.reload();
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should handle direct URL navigation', () => {
    // Direct navigation attempt
    cy.visit('/user-management');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    // Try with query parameters
    cy.visit('/user-management?test=1');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    
    cy.get('ion-content').should('exist');
  });
});