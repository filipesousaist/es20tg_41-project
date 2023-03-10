describe('Update an aproved student question', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Proposed Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Change title and first option content from an aproved student question', () => {
    cy.updateStudentQuestion('titlee', 'New title', 'New option content');

    // Question should still be in list
    cy.contains('New title').should('exist');
  });

  it('Try to change a proposed question', () => {
    cy.failClickEditButton('Q3');
  });
});
