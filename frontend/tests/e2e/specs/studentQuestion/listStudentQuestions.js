describe('List Student Questions', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create 2 questions and check if they are listed', () => {
    cy.createStudentQuestion('Question 1', 'Please answer', '1', '2', '3', '4');
    cy.createStudentQuestion(
      'Question 2',
      'Answer the question',
      '1',
      '2',
      '3',
      '4'
    );

    cy.contains('Question 1').should('exist');
    cy.contains('Question 2').should('exist');
    cy.contains('PROPOSED').should('exist');
  });

  it('Test search bar', () => {
    cy.createStudentQuestion('Question 3', 'Content 3', '1', '2', '3', '4');
    cy.createStudentQuestion('Question 4', 'Content 4', '1', '2', '3', '4');

    cy.get('[data-cy="searchBar"]').type('Question 3');
    cy.contains('Question 3').should('exist');
    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Content 3');
    cy.contains('Question 3').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Question 4');
    cy.contains('Question 4').should('exist');
    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('Content 4');
    cy.contains('Question 4').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('random');
    cy.contains('Question 3').should('not.exist');
    cy.contains('Question 4').should('not.exist');
    cy.contains('random').should('not.exist');
  });

  it('View student question', () => {
    cy.createStudentQuestion(
      'Question 5',
      'Content 5',
      'Ans A',
      'Ans B',
      'Ans C',
      'Ans D'
    );

    cy.contains('Question 5')
      .parent()
      .find('[data-cy="showStudentQuestionButton"]')
      .click();

    cy.contains('Content 5').should('exist');
    cy.contains('Ans A').should('exist');
    cy.contains('Ans B').should('exist');
    cy.contains('Ans C').should('exist');
    cy.contains('Ans D').should('exist');
  });
});
