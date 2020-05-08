describe('Create Question Evaluation Walkthrough', () => {
  beforeEach(() => {
    // Login with student to create questions
    cy.simpleDemoStudentLogin();
    cy.contains('User').click();
    cy.contains('Dashboard').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Get dashboard stats for Demo student', () => {
    cy.checkDashboardStudentQuestionsStats('Demo Student', 4, 1);
  });

  it('Test search bar and check if Student stats are private', () => {
    cy.get('[data-cy="searchBar"]').type('Student 700');
    cy.checkDashboardStudentQuestionsStats('Student 700', '-', '-');
  });
});
