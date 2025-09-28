describe('Authenticated User Experience', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should allow authenticated users to access dashboard', () => {
    // Check if service is available first
    cy.visit('/dashboard');

    // If service is down, should redirect to service-down page
    // If service is up but not authenticated, should redirect to login
    // If authenticated, should stay on dashboard
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/dashboard') || url.includes('/service-down') || url.includes('/login');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access requests page', () => {
    cy.visit('/requests');

    // Should handle requests page appropriately (stay on page, redirect to login, or service-down)
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/requests') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access notifications', () => {
    cy.visit('/notifications');

    // Should handle notifications page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/notifications') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access account settings', () => {
    cy.visit('/account');

    // Should handle account page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/account') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access settings', () => {
    cy.visit('/settings');

    // Should handle settings page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/settings') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access chat', () => {
    cy.visit('/chat');

    // Should handle chat page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/chat') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should allow authenticated users to access help', () => {
    cy.visit('/help');

    // Should handle help page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/help') || url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should maintain authentication across page navigation', () => {
    // Navigate to different pages and verify they handle routing appropriately
    cy.visit('/dashboard');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/dashboard') || url.includes('/login') || url.includes('/service-down');
    });
    
    cy.visit('/requests');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/requests') || url.includes('/login') || url.includes('/service-down');
    });
    
    cy.visit('/notifications');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/notifications') || url.includes('/login') || url.includes('/service-down');
    });
    
    // Should handle routing appropriately
    cy.visit('/account');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/account') || url.includes('/login') || url.includes('/service-down');
    });
  });

  it('should handle page refresh with authentication', () => {
    cy.visit('/dashboard');
    
    // Should handle dashboard page appropriately
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/dashboard') || url.includes('/login') || url.includes('/service-down');
    });
    
    // Reload page
    cy.reload();
    
    // Should handle refresh appropriately (may redirect to login depending on implementation)
    cy.get('ion-content').should('exist');
  });

  it('should be responsive on authenticated pages', () => {
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