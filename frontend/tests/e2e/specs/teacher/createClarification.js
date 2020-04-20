describe('Create Clarification', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Questions').click()
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and try to create a clarification without text', () => {
    cy.get('[data-cy="searchBox"]').type(
      'InstallStyle'
    );
    cy.createClarification('');
    cy.closeErrorMessage();
  });

  it('login and create a clarification for five clarification requests', () => {
    cy.get('[data-cy="searchBox"]').type(
      'Consider a stakeholder that is particularly concerned about the total cost of the project. When it comes to describing the system using allocation viewtypes s'
    );
    cy.createClarification('You have to do this in order to understand');
    cy.get('[data-cy="closeButton"]').click();
    cy.get('[data-cy="searchBox"]')
      .clear()
      .type('InstallStyle');
    cy.createClarification('You have to do this in order to understand');
    cy.get('[data-cy="closeButton"]').click();
    cy.get('[data-cy="searchBox"]')
      .clear()
      .type('In the software architecture of a system, the Deployment a');
    cy.createClarification('You have to do this in order to understand');
    cy.get('[data-cy="closeButton"]').click();
    cy.get('[data-cy="searchBox"]')
      .clear()
      .type('WorkAssignment');
    cy.createClarification('You have to do this in order to understand');
    cy.get('[data-cy="closeButton"]').click();
    cy.get('[data-cy="searchBox"]')
      .clear()
      .type('An architecture c');
    cy.createClarification('You have to do this in order to understand');
  });
});
