describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLoginT();
  });

  afterEach(() => {
    cy.contains('Logout').click({force:true});
  });

  it('checks tournament stats', () => {
    cy.get(':nth-child(8) > .v-btn__content').click();
    cy.contains('Dashboard').click();
    cy.get('[data-cy="searchBar"]').type('username');
    cy.checkDashboardStudentTournamentStats('username', 1, 1);
  });

  it('login creates a new tournament', () => {
    cy.createTournament('Demo Tournament', '5', '18');
    cy.deleteTournament('Demo Tournament');
  });

  it('creates a repeated tournament', () => {
    cy.createTournament('Demo Tournament', '4', '18');
    cy.log('try to create with the same name');
    cy.contains('Logout').click();
    cy.demoStudentLoginT();
    cy.createTournament('Demo Tournament', '4', '23');
    cy.closeErrorMessage();
    cy.deleteTournament('Demo Tournament');
  });

  it('tournament with more than the max permited in the questions', () => {
    cy.createTournament('Demo Tournament', '26', '18');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('tournament with invalid dates', () => {
    cy.createTournamentWrongDates('Demo Tournament', '4', '16');
    cy.closeErrorMessage();
  });

  it('tournament with no topics', () => {
    cy.createTournamentEmptyTopics('Demo Tournament', '4', '18');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('creates a tournament then enrolls in it', () => {
    cy.createTournament('Demo Tournament', '5', '18');
    cy.enrollTournament('Demo Tournament');
    cy.deleteTournament('Demo Tournament');
  });

  it('withdraws from a already enrolled tournament', () => {
    cy.createTournament('Demo Tournament', '5', '18');
    cy.enrollTournament('Demo Tournament');
    cy.withdrawTournament('Demo Tournament');
    cy.deleteTournament('Demo Tournament');
  });

  it('participates on a tournament', () => {
    cy.createTournament('Demo Tournament Quiz Test', '5', '18');
    cy.enrollTournament('Demo Tournament');
    cy.participateTournament('Demo Tournament');
    cy.answerQuiz();
  });
});
