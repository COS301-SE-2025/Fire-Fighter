describe('Application Routing and Navigation', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should redirect to landing page for root route', () => {
    cy.visit('/');
    
    // Should redirect to landing page
    cy.url().should('include', '/landing');
    cy.get('ion-content').should('exist');
  });

  it('should handle wildcard routes properly', () => {
    cy.visit('/non-existent-route');
    
    // Should redirect to landing page for invalid routes
    cy.url().should('include', '/landing');
    cy.get('ion-content').should('exist');
  });

  it('should handle multiple invalid routes', () => {
    // Test various invalid routes
    const invalidRoutes = ['/invalid', '/test123', '/random-page'];
    
    invalidRoutes.forEach(route => {
      cy.visit(route);
      cy.url().should('include', '/landing');
      cy.get('ion-content').should('exist');
    });
  });

  it('should maintain proper routing for authenticated routes', () => {
    const protectedRoutes = ['/dashboard', '/requests', '/notifications', '/account', '/settings', '/chat'];
    
    protectedRoutes.forEach(route => {
      cy.visit(route);
      // Should redirect to login for protected routes
      cy.url().should('include', '/login');
      cy.get('ion-content').should('exist');
    });
  });

  it('should maintain proper routing for admin routes', () => {
    const adminRoutes = ['/admin', '/metrics', '/user-management'];
    
    adminRoutes.forEach(route => {
      cy.visit(route);
      // Should redirect to login for admin routes
      cy.url().should('include', '/login');
      cy.get('ion-content').should('exist');
    });
  });

  it('should allow access to public routes', () => {
    const publicRoutes = ['/landing', '/login', '/register', '/service-down'];
    
    publicRoutes.forEach(route => {
      cy.visit(route);
      // Should allow access to public routes
      cy.url().should('include', route);
      cy.get('ion-content').should('exist');
    });
  });

  it('should handle browser navigation correctly', () => {
    // Navigate through different pages
    cy.visit('/landing');
    cy.url().should('include', '/landing');
    
    cy.visit('/login');
    cy.url().should('include', '/login');
    
    cy.visit('/register');
    cy.url().should('include', '/register');
    
    // Test back navigation
    cy.go('back');
    cy.url().should('include', '/login');
    
    cy.go('back');
    cy.url().should('include', '/landing');
  });

  it('should be responsive across all routes', () => {
    const testRoutes = ['/landing', '/login', '/register'];
    
    testRoutes.forEach(route => {
      cy.visit(route);
      
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
  });

  it('should handle page refresh on different routes', () => {
    const testRoutes = ['/landing', '/login', '/register'];
    
    testRoutes.forEach(route => {
      cy.visit(route);
      cy.url().should('include', route);
      
      cy.reload();
      cy.url().should('include', route);
      cy.get('ion-content').should('exist');
    });
  });
});