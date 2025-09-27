describe('Dashboard Navigation', () => {
  beforeEach(() => {
    // Mock authentication for dashboard access
    cy.bypassAuth();
  });

  it('should access dashboard when authenticated', () => {
    cy.visit('/dashboard');
    cy.url().should('include', '/dashboard');
    
    // Check if dashboard content loads
    cy.get('ion-content').should('exist');
  });

  it('should have navigation menu', () => {
    cy.visit('/dashboard');
    
    // Look for navigation elements (navbar, menu, tabs)
    cy.get('app-navbar, ion-tabs, ion-menu, nav').should('exist');
  });

  it('should navigate to different sections', () => {
    cy.visit('/dashboard');
    
    // Test navigation to different pages
    const pages = [
      { name: 'notifications', selector: 'a[href*="notifications"], ion-tab-button[tab="notifications"]' },
      { name: 'requests', selector: 'a[href*="requests"], ion-tab-button[tab="requests"]' },
      { name: 'chat', selector: 'a[href*="chat"], ion-tab-button[tab="chat"]' },
      { name: 'account', selector: 'a[href*="account"], ion-tab-button[tab="account"]' }
    ];

    pages.forEach(page => {
      cy.get('body').then($body => {
        if ($body.find(page.selector).length > 0) {
          cy.get(page.selector).first().click();
          cy.url().should('include', page.name);
          cy.get('ion-content').should('exist');
        }
      });
    });
  });

  it('should display user-specific content', () => {
    cy.visit('/dashboard');
    
    // Should show some personalized content or user indicators
    cy.get('ion-content').should('contain.text', 'Dashboard').or('contain.text', 'Welcome');
  });
});
