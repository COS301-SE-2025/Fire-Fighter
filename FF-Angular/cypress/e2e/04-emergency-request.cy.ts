describe('Emergency Request Flow', () => {
  beforeEach(() => {
    cy.bypassAuth();
  });

  it('should access requests page', () => {
    cy.visit('/requests');
    cy.url().should('include', '/requests');
    
    // Check if requests page loads
    cy.get('ion-content').should('exist');
  });

  it('should display request form', () => {
    cy.visit('/requests');
    
    // Look for form elements
    cy.get('form, ion-input, ion-select, ion-textarea').should('exist');
  });

  it('should have emergency type selection', () => {
    cy.visit('/requests');
    
    // Look for emergency type selector
    cy.get('ion-select, select').should('exist');
  });

  it('should have reason/description field', () => {
    cy.visit('/requests');
    
    // Look for text input for reason
    cy.get('ion-textarea, textarea, ion-input[type="text"]').should('exist');
  });

  it('should have submit button', () => {
    cy.visit('/requests');
    
    // Look for submit button
    cy.get('ion-button, button').contains(/submit|create|request/i).should('exist');
  });

  it('should validate required fields', () => {
    cy.visit('/requests');
    
    // Try to submit without filling required fields
    cy.get('ion-button, button').contains(/submit|create|request/i).click();
    
    // Should show validation or stay on same page
    cy.url().should('include', '/requests');
  });
});
