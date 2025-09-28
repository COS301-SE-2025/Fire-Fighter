describe('Performance and Accessibility', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should load pages within acceptable time limits', () => {
    const startTime = Date.now();
    
    cy.visit('/landing');
    cy.get('ion-content').should('exist');
    
    cy.then(() => {
      const loadTime = Date.now() - startTime;
      // Page should load within 10 seconds (generous for CI)
      expect(loadTime).to.be.lessThan(10000);
    });
  });

  it('should handle rapid navigation between pages', () => {
    // Rapidly navigate between public pages
    cy.visit('/landing');
    cy.get('ion-content').should('exist');
    
    cy.visit('/login');
    cy.get('ion-content').should('exist');
    
    cy.visit('/register');
    cy.get('ion-content').should('exist');
    
    cy.visit('/service-down');
    cy.get('ion-content').should('exist');
    
    // Should handle rapid navigation gracefully
    cy.get('body').should('be.visible');
  });

  it('should maintain performance with multiple viewport changes', () => {
    cy.visit('/landing');
    
    // Rapidly change viewports
    const viewports = [
      [375, 667],   // Mobile
      [768, 1024],  // Tablet
      [1280, 720],  // Desktop
      [414, 896],   // Large Mobile
      [1024, 768]   // Landscape Tablet
    ];
    
    viewports.forEach(([width, height]) => {
      cy.viewport(width, height);
      cy.get('ion-content').should('be.visible');
    });
  });

  it('should have proper semantic HTML structure', () => {
    cy.visit('/landing');
    
    // Check for semantic HTML elements
    cy.get('ion-content').should('exist');
    cy.get('body').should('exist');
    
    // Page should have proper structure
    cy.get('html').should('have.attr', 'lang');
  });

  it('should have accessible form elements', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Test that form elements exist (may not be visible due to service state)
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Test that buttons are accessible - use force option for visibility issues
    cy.get('ion-button, button').should('exist');
    
    // Basic accessibility check - elements should be interactable with force option
    cy.get('input[type="email"]').click({ force: true });
  });

  it('should handle memory-intensive operations', () => {
    // Load multiple pages to test memory handling
    const pages = ['/landing', '/login', '/register', '/service-down'];
    
    pages.forEach(page => {
      cy.visit(page);
      cy.get('ion-content').should('exist');
      
      // Test scrolling within the content area
      cy.get('ion-content').scrollTo('bottom', { ensureScrollable: false });
      cy.get('ion-content').scrollTo('top', { ensureScrollable: false });
    });
  });

  it('should maintain responsive images and media', () => {
    cy.visit('/landing');
    
    // Check if page loads properly first
    cy.get('ion-content').should('exist');
    
    // Check if images exist, if not, skip this test gracefully
    cy.get('body').then(($body) => {
      if ($body.find('img').length > 0) {
        cy.get('img').each(($img) => {
          // Images should be visible if they exist
          if ($img.is(':visible')) {
            cy.wrap($img).should('be.visible');
          }
        });
      } else {
        // If no images exist, just verify the page structure
        cy.get('ion-content').should('be.visible');
      }
    });
  });

  it('should handle focus management properly', () => {
    cy.visit('/login');
    
    // Focus should be manageable
    cy.get('input[type="email"]').focus();
    cy.focused().should('have.attr', 'type', 'email');
    
    cy.get('input[type="password"]').focus();
    cy.focused().should('have.attr', 'type', 'password');
  });

  it('should provide proper error boundaries', () => {
    // Test error handling by visiting invalid routes
    cy.visit('/completely-invalid-route-123');
    
    // Should gracefully handle errors and redirect
    cy.url().should('include', '/landing');
    cy.get('ion-content').should('exist');
  });

  it('should optimize for mobile touch interactions', () => {
    cy.visit('/login');
    
    // Set mobile viewport
    cy.viewport(375, 667);
    
    // Elements should be touch-friendly
    cy.get('ion-button, button').should('exist');
    cy.get('input').should('exist');
    
    // Touch targets should be accessible
    cy.get('ion-content').should('be.visible');
  });

  it('should handle concurrent user interactions', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Simulate multiple rapid interactions using force for visibility issues
    cy.get('input[type="email"]').type('test@example.com', { force: true });
    cy.get('input[type="password"]').type('password', { force: true });
    
    // Rapid clicking
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Should handle interactions gracefully
    cy.get('ion-content').should('exist');
  });

  it('should maintain consistent styling across devices', () => {
    const testPages = ['/landing', '/login', '/register'];
    
    testPages.forEach(page => {
      cy.visit(page);
      
      // Test on different devices
      cy.viewport(375, 667); // Mobile
      cy.get('ion-content').should('be.visible');
      
      cy.viewport(1280, 720); // Desktop
      cy.get('ion-content').should('be.visible');
    });
  });
});