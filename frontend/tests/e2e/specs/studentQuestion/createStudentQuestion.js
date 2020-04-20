describe('Create Student Questions', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates a student question', () => {
    cy.createStudentQuestion('titleee', 'pergunta?', '1', '2', '3', '4');
  });

  it('login creates a student question and gets an error', () => {
    cy.createStudentQuestionWithOneOption('titleee', 'pergunta?', '1');

    cy.closeErrorMessage();

    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();
  });
});
