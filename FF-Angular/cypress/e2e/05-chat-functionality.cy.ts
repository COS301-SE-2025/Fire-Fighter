describe('Chat Functionality', () => {
  beforeEach(() => {
    cy.bypassAuth();
  });

  it('should access chat page', () => {
    cy.visit('/chat');
    cy.url().should('include', '/chat');
    
    // Check if chat page loads
    cy.get('ion-content').should('exist');
  });

  it('should display chat interface', () => {
    cy.visit('/chat');
    
    // Look for chat input
    cy.get('ion-input, input, ion-textarea, textarea').should('exist');
    
    // Look for send button
    cy.get('ion-button, button').should('exist');
  });

  it('should show chat messages area', () => {
    cy.visit('/chat');
    
    // Look for messages container
    cy.get('.messages, .chat-messages, ion-list').should('exist');
  });

  it('should have suggested questions', () => {
    cy.visit('/chat');
    
    // Look for suggested questions or quick actions
    cy.get('body').should('contain.text', 'suggestions').or('contain.text', 'help');
  });

  it('should allow typing in chat input', () => {
    cy.visit('/chat');
    
    // Find and type in chat input
    cy.get('ion-input input, input, ion-textarea textarea, textarea').first().type('Hello');
    cy.get('ion-input input, input, ion-textarea textarea, textarea').first().should('have.value', 'Hello');
  });
});
