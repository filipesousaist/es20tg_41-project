describe('Update Clarification Request Privacy', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Question').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and make a clarification summary public', () => {
    cy.get('[data-cy="searchBox"]').type(
      'In the software architecture of a system, the Deployment a'
    );
    cy.get('[data-cy="clarificationRequests"]').click();
    cy.get('[data-cy="requestMakePublic"]').click();
    cy.wait(2000);
  });
})