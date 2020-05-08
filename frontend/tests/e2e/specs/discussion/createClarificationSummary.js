describe('Create Clarification Summary', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and try to create a summary for a clarification without text', () => {
    cy.get('[data-cy="searchBox"]').type(
      'In the software architecture of a system, the Deployment a'
    );
    cy.createClarificationSummary('');
    cy.closeErrorMessage();
  });

  it('login and create a summary for a clarification', () => {
    cy.get('[data-cy="searchBox"]').type(
      'In the software architecture of a system, the Deployment a'
    );
    cy.createClarificationSummary(
      'This is a summary of the discussion for this clarification'
    );
  });
});