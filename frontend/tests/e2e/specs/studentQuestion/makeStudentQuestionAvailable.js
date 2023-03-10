describe('Make Question Available', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.contains('Proposed Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Make accepted question available', () => {
    cy.clickMakeStudentQuestionAvailable('New title');

    // Question should still be in list and be available
    cy.contains('New title').should('exist');
    cy.contains('AVAILABLE').should('exist');
    // Make available button should have disappeared
    cy.failClickMakeAvailableButton('New title');
  });

  it('Try to make available a proposed question', () => {
    cy.failClickMakeAvailableButton('Q3');
  });
});
