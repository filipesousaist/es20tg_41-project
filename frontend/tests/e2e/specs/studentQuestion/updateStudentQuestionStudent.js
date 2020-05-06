describe('Update a rejected student question', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Student Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Change title and first option content from a rejected student question', () => {
    cy.updateStudentQuestion('Q2', 'New Q2', 'New option content');

    // Question should still be in list
    cy.contains('New Q2').should('exist');
  });

  it('Try to change a proposed question', () => {
    cy.failClickEditButton('Q3');
  });
});
