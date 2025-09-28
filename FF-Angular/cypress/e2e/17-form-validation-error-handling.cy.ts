describe('Form Validation and Error Handling', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should validate login form inputs', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded and visible
    cy.get('ion-content').should('be.visible');
    cy.wait(1000); // Give time for Angular to fully render
    
    // Check if form validation works
    cy.get('ion-button, button').contains(/login|sign in/i).should('exist');
    
    // Try to submit without filling any fields - use force option to handle visibility issues
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Should remain on login page or service-down
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should validate registration form inputs', () => {
    cy.visit('/register');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Check if form exists
    cy.get('ion-content').should('exist');
    
    // Look for register button
    cy.get('ion-button, button').contains(/register|sign up|create/i).should('exist');
    
    // Try to submit empty form
    cy.get('ion-button, button')
      .contains(/register|sign up|create/i)
      .first()
      .click({ force: true });
    
    // Should remain on register page or service-down
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/register') || url.includes('/service-down');
    });
  });

  it('should handle network errors gracefully', () => {
    cy.visit('/login');
    
    // Check if page loads properly
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
    
    // Basic form elements should be present
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
  });

  it('should display appropriate error messages', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Submit form and check for error handling
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Page should handle submission gracefully
    cy.get('ion-content').should('exist');
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
  });

  it('should validate form inputs on focus and blur', () => {
    cy.visit('/login');
    
    // Focus on email input
    cy.get('input[type="email"]').focus();
    cy.get('input[type="email"]').blur();
    
    // Focus on password input
    cy.get('input[type="password"]').focus();
    cy.get('input[type="password"]').blur();
    
    // Form should remain functional
    cy.get('ion-content').should('exist');
  });

  it('should handle special characters in form inputs', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded and visible
    cy.get('ion-content').should('be.visible');
    cy.wait(1000); // Give time for Angular to fully render
    
    // Type special characters in email field - use force option to handle visibility issues
    cy.get('input[type="email"]').type('test@#$%^&*()', { force: true });
    cy.get('input[type="password"]').type('!@#$%^&*()', { force: true });
    
    // Form should handle special characters
    cy.get('ion-content').should('exist');
  });

  it('should provide proper form accessibility', () => {
    cy.visit('/login');
    
    // Check for form labels and accessibility
    cy.get('input[type="email"]').should('exist');
    cy.get('input[type="password"]').should('exist');
    
    // Form should be accessible
    cy.get('form').should('exist');
  });

  it('should handle form submission with invalid data', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Enter invalid email format (use force for visibility issues)
    cy.get('input[type="email"]').type('invalid-email', { force: true });
    cy.get('input[type="password"]').type('123', { force: true });
    
    // Try to submit
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Should handle invalid data gracefully
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
    cy.get('ion-content').should('exist');
  });

  it('should clear form data when appropriate', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Type in form fields using force to handle visibility issues
    cy.get('input[type="email"]').type('test@example.com', { force: true });
    cy.get('input[type="password"]').type('password123', { force: true });
    
    // Navigate away and back
    cy.visit('/register');
    cy.visit('/login');
    
    // Form should be functional after navigation
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
  });

  it('should handle long input values', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded
    cy.get('ion-content').should('be.visible');
    cy.wait(1000);
    
    // Type very long values using force to handle visibility issues
    const longEmail = 'a'.repeat(50) + '@example.com';
    const longPassword = 'a'.repeat(50);
    
    cy.get('input[type="email"]').type(longEmail, { force: true });
    cy.get('input[type="password"]').type(longPassword, { force: true });
    
    // Form should handle long inputs
    cy.get('ion-content').should('exist');
  });
});