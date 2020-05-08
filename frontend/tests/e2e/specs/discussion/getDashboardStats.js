describe('Create Clarification Request Walkthrough', () => {
  beforeEach(() => {
    // Login with student to create clarification requests
    cy.simpleDemoStudentLogin();
    cy.contains('User').click();
    cy.contains('Dashboard').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Get dashboard stats for Demo student', () => {
    cy.checkDashboardDiscussionStats('Demo Student', 5, 5);
  });

  it('Test search bar and check if Student stats are private', () => {
    cy.get('[data-cy="searchBar"]').type('Student 700');
    cy.checkDashboardDiscussionStats('Student 700', '-', '-');
  });
});