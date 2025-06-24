describe('Authentication Flow E2E Tests', () => {
  beforeEach(() => {
    cy.checkApiHealth();
  });

  it('should complete full login flow', () => {
    cy.visit('/login');
    cy.waitForAppToLoad();
    
    // Verify login page elements
    cy.get('[data-cy="email-input"]').should('be.visible');
    cy.get('[data-cy="password-input"]').should('be.visible');
    cy.get('[data-cy="login-button"]').should('be.visible');
    
    // Login with valid credentials
    cy.login('testuser@example.com', 'testpassword');
    
    // Verify successful login
    cy.url().should('include', '/dashboard');
    cy.get('[data-cy="user-welcome"]').should('contain', 'Welcome');
  });

  it('should show error for invalid credentials', () => {
    cy.visit('/login');
    cy.waitForAppToLoad();
    
    cy.get('[data-cy="email-input"]').type('invalid@example.com');
    cy.get('[data-cy="password-input"]').type('wrongpassword');
    cy.get('[data-cy="login-button"]').click();
    
    // Should show error message
    cy.get('[data-cy="error-message"]')
      .should('be.visible')
      .and('contain', 'Invalid credentials');
  });

  it('should redirect to login when accessing protected route without auth', () => {
    cy.visit('/dashboard');
    cy.url().should('include', '/login');
  });

  it('should complete logout flow', () => {
    // First login
    cy.login('testuser@example.com', 'testpassword');
    
    // Then logout
    cy.logout();
    
    // Should be redirected to login page
    cy.url().should('include', '/login');
  });
}); 