describe('Create Question Evaluation Walkthrough', () => {
  beforeEach(() => {
    // Login with student to create questions
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Create 2 student questions and evaluate them', () => {
    cy.createStudentQuestion(
      'Q1',
      'What\'s the correct answer?',
      'Answer 1',
      'Answer 2',
      'Answer 3',
      'Answer 4'
    );
    cy.createStudentQuestion('Q2', 'What\'s 1 + 1?', '1', '2', '3', '4');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.contains('Proposed Questions').click();
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 3); // 2 student questions + search bar
    cy.createQuestionEvaluation('Q1', false, 'Bad question.');
    cy.wait(500);
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 2); // 1 student question + search bar
    cy.createQuestionEvaluation('Q2', true, 'Good question.');
    cy.get('[data-cy="proposedQuestionsTable"]')
      .find('tr')
      .should('have.length', 2); //"No data available" message + search bar
  });

  it('Create question evaluation without approval/rejection', () => {
    cy.createStudentQuestion('Q3', 'Question 3', 'A1', 'A2', 'A3', 'A4');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Proposed Questions').click();

    cy.createQuestionEvaluation('Q3', null, 'Justification');
    cy.closeErrorMessage();
  });

  it('Create question evaluation without justification', () => {
    cy.createStudentQuestion('Q4', 'Question 4', 'A1', 'A2', 'A3', 'A4');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Proposed Questions').click();

    cy.createQuestionEvaluation('Q4', true, null);
    cy.closeErrorMessage();
  });

  it('Test close button', () => {
    cy.createStudentQuestion('Q5', 'Question 5', 'A1', 'A2', 'A3', 'A4');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Proposed Questions').click();

    // Click evaluate question and then close the window
    cy.clickEvaluateQuestion('Q5');
    cy.get('[data-cy="cancelButton"]').click();
    // Try again to make sure the window was closed correctly
    cy.clickEvaluateQuestion('Q5');
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('Test search bar', () => {
    cy.createStudentQuestion('Q6', 'Question 6', 'A1', 'A2', 'A3', 'A4');
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.contains('Management').click();
    cy.contains('Proposed Questions').click();

    cy.get('[data-cy="searchBar"]').type('Q6');
    cy.contains('Q6').should('exist');

    cy.get('[data-cy="searchBar"]').clear().type('PROPOSED');
    cy.contains('Q6').should('exist');

    cy.get('[data-cy="searchBar"]').clear().type('random');
    cy.contains('Q6').should('not.exist');
  });
});
