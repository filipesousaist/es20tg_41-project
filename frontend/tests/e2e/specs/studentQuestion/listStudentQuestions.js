describe('List Student Questions', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Check if created student questions are listed', () => {
    cy.contains('titleee').should('exist');
    cy.contains('Q2').should('exist');
    cy.contains('Q3').should('exist');
    cy.contains('Q4').should('exist');
    cy.contains('PROPOSED').should('exist');
  });

  it('Test search bar', () => {
    cy.get('[data-cy="searchBar"]').type('Q3');
    cy.contains('Q3').should('exist');
    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Content 3');
    cy.contains('Q3').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Q4');
    cy.contains('Q4').should('exist');
    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Content 4');
    cy.contains('Q4').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('random');
    cy.contains('Q3').should('not.exist');
    cy.contains('Q4').should('not.exist');
    cy.contains('random').should('not.exist');
  });

  it('View student question', () => {
    cy.contains('Q3')
      .parent()
      .find('[data-cy="showStudentQuestionButton"]')
      .click();

    cy.contains('Content 3').should('exist');
    cy.contains('A1').should('exist');
    cy.contains('A2').should('exist');
    cy.contains('A3').should('exist');
    cy.contains('A4').should('exist');
  });
});
