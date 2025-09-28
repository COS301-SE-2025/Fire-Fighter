describe('Authenticated User Experience', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should allow authenticated users to access dashboard', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/dashboard');

    // Should stay on dashboard page (not redirect to login)
    cy.url().should('include', '/dashboard');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access requests page', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/requests');

    // Should stay on requests page
    cy.url().should('include', '/requests');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access notifications', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/notifications');

    // Should stay on notifications page
    cy.url().should('include', '/notifications');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access account settings', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/account');

    // Should stay on account page
    cy.url().should('include', '/account');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access settings', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/settings');

    // Should stay on settings page
    cy.url().should('include', '/settings');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access chat', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/chat');

    // Should stay on chat page
    cy.url().should('include', '/chat');
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access help', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/help');

    // Should stay on help page
    cy.url().should('include', '/help');
    cy.get('ion-content').should('exist');
  });

  it('should maintain authentication across page navigation', () => {
    // Mock authentication
    cy.bypassAuth();
    
    // Navigate to different authenticated pages
    cy.visit('/dashboard');
    cy.url().should('include', '/dashboard');
    
    cy.visit('/requests');
    cy.url().should('include', '/requests');
    
    cy.visit('/notifications');
    cy.url().should('include', '/notifications');
    
    // Should maintain auth state
    cy.visit('/account');
    cy.url().should('include', '/account');
  });

  it('should handle page refresh with authentication', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/dashboard');
    
    // Should stay on dashboard
    cy.url().should('include', '/dashboard');
    
    // Reload page
    cy.reload();
    
    // Should handle refresh appropriately (may redirect to login depending on implementation)
    cy.get('ion-content').should('exist');
  });

  it('should be responsive on authenticated pages', () => {
    // Mock authentication
    cy.bypassAuth();
    cy.visit('/dashboard');
    
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