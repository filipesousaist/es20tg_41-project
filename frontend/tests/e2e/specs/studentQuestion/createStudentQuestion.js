describe('Create Student Questions', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates 4 student questions', () => {
    cy.createStudentQuestion('titleee', 'pergunta?', '1', '2', '3', '4');
    cy.createStudentQuestion('Q2', 'What\'s 1 + 1?', '1', '2', '3', '4');
    cy.createStudentQuestion('Q3', 'Content 3', 'A1', 'A2', 'A3', 'A4');
    cy.createStudentQuestion('Q4', 'Content 4', 'A1', 'A2', 'A3', 'A4');
  });

  it('login creates a student question and gets an error', () => {
    cy.createStudentQuestionWithOneOption('bad question', 'pergunta?', '1');

    cy.closeErrorMessage();

    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();
  });
});
