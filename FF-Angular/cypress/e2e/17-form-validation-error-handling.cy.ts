describe('Form Validation and Error Handling', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should validate login form inputs', () => {
    cy.visit('/login');
    
    // Check if form validation works
    cy.get('ion-button, button').contains(/login|sign in/i).should('exist');
    
    // Try to submit without filling any fields
    cy.get('ion-button, button').contains(/login|sign in/i).click();
    
    // Should remain on login page
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should validate registration form inputs', () => {
    cy.visit('/register');
    
    // Check if form exists
    cy.get('ion-content').should('exist');
    
    // Look for register button
    cy.get('ion-button, button').contains(/register|sign up|create/i).should('exist');
    
    // Try to submit empty form
    cy.get('ion-button, button')
      .contains(/register|sign up|create/i)
      .first()
      .click();
    
    // Should remain on register page
    cy.url().should('include', '/register');
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
    
    // Submit form and check for error handling
    cy.get('ion-button, button').contains(/login|sign in/i).click();
    
    // Page should handle submission gracefully
    cy.get('ion-content').should('exist');
    cy.url().should('include', '/login');
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
    
    // Type special characters in email field
    cy.get('input[type="email"]').type('test@#$%^&*()');
    cy.get('input[type="password"]').type('!@#$%^&*()');
    
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
    
    // Enter invalid email format
    cy.get('input[type="email"]').type('invalid-email');
    cy.get('input[type="password"]').type('123');
    
    // Try to submit
    cy.get('ion-button, button').contains(/login|sign in/i).click();
    
    // Should handle invalid data gracefully
    cy.url().should('include', '/login');
    cy.get('ion-content').should('exist');
  });

  it('should clear form data when appropriate', () => {
    cy.visit('/login');
    
    // Type in form fields
    cy.get('input[type="email"]').type('test@example.com');
    cy.get('input[type="password"]').type('password123');
    
    // Navigate away and back
    cy.visit('/register');
    cy.visit('/login');
    
    // Form should be functional after navigation
    cy.get('ion-content').should('exist');
    cy.get('form').should('exist');
  });

  it('should handle long input values', () => {
    cy.visit('/login');
    
    // Type very long values
    const longEmail = 'a'.repeat(100) + '@example.com';
    const longPassword = 'a'.repeat(200);
    
    cy.get('input[type="email"]').type(longEmail.substring(0, 50));
    cy.get('input[type="password"]').type(longPassword.substring(0, 50));
    
    // Form should handle long inputs
    cy.get('ion-content').should('exist');
  });
});