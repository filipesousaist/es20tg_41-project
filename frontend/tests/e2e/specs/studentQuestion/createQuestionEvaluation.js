describe('Create Question Evaluation Walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Proposed Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Evaluate 2 student questions', () => {
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 5); // 4 student questions + search bar
    cy.createQuestionEvaluation('Q2', false, 'Bad question.');
    cy.wait(500);
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 4); // 3 student question + search bar
    cy.createQuestionEvaluation('titleee', true, 'Good question.');
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 4); // 3 student question + search bar
  });

  it('Create question evaluation without approval/rejection', () => {
    cy.createQuestionEvaluation('Q3', null, 'Justification');
    cy.closeErrorMessage();
  });

  it('Create question evaluation without justification', () => {
    cy.createQuestionEvaluation('Q4', true, null);
    cy.closeErrorMessage();
  });

  it('Test close button', () => {
    // Click evaluate question and then close the window
    cy.clickEvaluateQuestion('Q3');
    cy.get('[data-cy="cancelButton"]').click();
    // Try again to make sure the window was closed correctly
    cy.clickEvaluateQuestion('Q3');
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('Test search bar', () => {
    cy.get('[data-cy="searchBar"]').type('Q3');
    cy.contains('Q3').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('PROPOSED');
    cy.contains('Q3').should('exist');

    cy.get('[data-cy="searchBar"]')
      .clear()
      .type('random');
    cy.contains('Q3').should('not.exist');
  });
});
