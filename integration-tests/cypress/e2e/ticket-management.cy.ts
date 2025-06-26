describe('Ticket Management E2E Tests', () => {
  beforeEach(() => {
    cy.checkApiHealth();
    cy.login('testuser@example.com', 'testpassword');
  });

  it('should create a new ticket', () => {
    const ticketData = {
      title: `E2E Test Ticket ${Date.now()}`,
      description: 'This ticket was created during end-to-end testing',
      priority: 'HIGH'
    };

    cy.createTicket(ticketData);
    
    // Verify ticket appears in list
    cy.visit('/requests');
    cy.waitForAppToLoad();
    cy.get('[data-cy="ticket-list"]').should('contain', ticketData.title);
  });

  it('should view ticket details', () => {
    cy.visit('/requests');
    cy.waitForAppToLoad();
    
    // Click on first ticket in the list
    cy.get('[data-cy="ticket-item"]').first().click();
    
    // Should navigate to ticket details
    cy.url().should('include', '/tickets/');
    cy.get('[data-cy="ticket-details"]').should('be.visible');
    cy.get('[data-cy="ticket-title"]').should('not.be.empty');
    cy.get('[data-cy="ticket-description"]').should('not.be.empty');
  });

  it('should update ticket status', () => {
    cy.visit('/requests');
    cy.waitForAppToLoad();
    
    // Click on first ticket
    cy.get('[data-cy="ticket-item"]').first().click();
    
    // Update status
    cy.get('[data-cy="status-select"]').select('IN_PROGRESS');
    cy.get('[data-cy="save-button"]').click();
    
    // Verify success message
    cy.get('[data-cy="success-message"]')
      .should('be.visible')
      .and('contain', 'Ticket updated successfully');
  });

  it('should filter tickets by status', () => {
    cy.visit('/requests');
    cy.waitForAppToLoad();
    
    // Apply filter
    cy.get('[data-cy="status-filter"]').select('OPEN');
    
    // Verify filtered results
    cy.get('[data-cy="ticket-item"]').each(($el) => {
      cy.wrap($el).should('contain', 'Open');
    });
  });

  it('should search tickets', () => {
    cy.visit('/requests');
    cy.waitForAppToLoad();
    
    const searchTerm = 'test';
    cy.get('[data-cy="search-input"]').type(searchTerm);
    cy.get('[data-cy="search-button"]').click();
    
    // Verify search results contain the search term
    cy.get('[data-cy="ticket-item"]').each(($el) => {
      cy.wrap($el).should('contain.text', searchTerm);
    });
  });
}); 