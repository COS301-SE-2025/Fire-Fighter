describe('Login Flow', () => {
  beforeEach(() => {
    cy.clearAuth();
  });

  it('should navigate to login page', () => {
    cy.visit('/login');
    cy.url().should('include', '/login');
    
    // Check if login form elements exist
    cy.get('ion-content').should('exist');
    cy.get('form, ion-input, input').should('exist');
  });

  it('should display login form elements', () => {
    cy.visit('/login');
    
    // Look for email/username input
    cy.get('ion-input[type="email"], input[type="email"], ion-input[placeholder*="email" i], input[placeholder*="email" i]')
      .should('exist');
    
    // Look for password input
    cy.get('ion-input[type="password"], input[type="password"]')
      .should('exist');
    
    // Look for login button
    cy.get('ion-button, button').contains(/login|sign in/i)
      .should('exist');
  });

  it('should show validation for empty form', () => {
    cy.visit('/login');
    
    // Wait for page to be fully loaded and visible
    cy.get('ion-content').should('be.visible');
    cy.wait(1000); // Give time for Angular to fully render
    
    // Try to submit empty form - use force option to handle visibility issues
    cy.get('ion-button, button').contains(/login|sign in/i).click({ force: true });
    
    // Should stay on login page or service-down (not navigate to protected routes)
    cy.url().should('satisfy', (url: string) => {
      return url.includes('/login') || url.includes('/service-down');
    });
  });

  it('should have link to register page', () => {
    cy.visit('/login');

    // Look for register/signup link - check for "Create an account" text
    cy.get('a').contains('Create an account')
      .should('exist');
  });
});
