describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates a new tournament', () => {
    cy.createTournament('Demo Tournament', '5', '18');
    //cy.deleteTournament('Demo Tournament');
  });

  it('creates a repeated tournament', () => {
    cy.createTournament('Demo Tournament1', '4', '18');
    cy.log('try to create with the same name');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.createTournament('Demo Tournament1', '4', '23');
    cy.closeErrorMessage();
  });

  it('tournament with more than the max permited in the questions', () => {
    cy.createTournament('Demo Tournament2', '26', '18');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('tournament with invalid dates', () => {
    cy.createTournament('Demo Tournament3', '4', '16');
    cy.closeErrorMessage();
  });

  it('tournament with no topics', () => {
    cy.createTournamentEmptyTopics('Demo Tournament4', '4', '18');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('creates a tournament then enrolls in it', () => {
    cy.createTournament('Demo Tournament5', '5', '18');
    cy.enrollTournament('Demo Tournament5');
  });

  it('withdraws from a already enrolled tournament', () => {
    cy.withdrawTournament('Demo Tournament5');
  });

  it('creates a tournament then enrolls in it and withdraws', () => {
    cy.createTournament('Demo Tournament6', '5', '18');
    cy.enrollTournament('Demo Tournament6');
    cy.withdrawTournament('Demo Tournament6');
  });
});
