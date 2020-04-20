describe('Create Clarification Request', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login answer a quiz and try to create a clarification request for the first question without title', () => {
    cy.get('[data-cy="QuizzesButton"]').click();
    cy.contains('Available').click();
    cy.answerQuiz('Allocation viewtype');
    cy.createClarificationRequest('', 'I do not know what to do.');
    cy.closeErrorMessage();
  });

  it('login and try to create a clarification request for the first question without text', () => {
    cy.get('[data-cy="QuizzesButton"]').click();
    cy.contains('Solved').click();
    cy.contains('Allocation viewtype').click();
    cy.createClarificationRequest('I have a doubt.', '');
    cy.closeErrorMessage();
  });

  it('login and create a clarification request for each question in the answered quiz', () => {
    cy.get('[data-cy="QuizzesButton"]').click();
    cy.contains('Solved').click();
    cy.contains('Allocation viewtype').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
    cy.get('[data-cy="nextQuestionButton"]').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
    cy.get('[data-cy="nextQuestionButton"]').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
    cy.get('[data-cy="nextQuestionButton"]').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
    cy.get('[data-cy="nextQuestionButton"]').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
  });

  it('login answer a quiz and try to create another clarification request for the first question', () => {
    cy.get('[data-cy="QuizzesButton"]').click();
    cy.contains('Solved').click();
    cy.contains('Allocation viewtype').click();
    cy.createClarificationRequest(
      'I have a doubt.',
      'I do not know what to do.'
    );
    cy.closeErrorMessage();
  });
});
