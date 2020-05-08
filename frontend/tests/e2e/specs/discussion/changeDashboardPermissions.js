describe('Change Dashboard Permissions Walkthrough(Discussion)', () => {
  beforeEach(() => {
    // Login with student to create questions
    cy.simpleDemoStudentLogin();
    cy.contains('User').click();
    cy.contains('Dashboard').click();
  });

  afterEach(() => {
    cy.contains('Logout').click({ force: true });
  });

  it('Change dashboard permissions', () => {
    cy.changeDashboardDiscussionPermissions(true, false);
  });

  it('Check if permissions are still correct and close', () => {
    cy.checkDashboardDiscussionPermissions(true, false);
  });

  it('Change dashboard permissions and don\'t save', () => {
    cy.changeDashboardDiscussionPermissions(false, true, false);
    // They should not have changed since previous tests
    cy.checkDashboardDiscussionPermissions(true, false);
  });
});
