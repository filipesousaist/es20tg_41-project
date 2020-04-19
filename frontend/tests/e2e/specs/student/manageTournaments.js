describe('Tournaments walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates a new tournament', () => {
    cy.createTournament('Demo Tournament', '5', '17', '18', '10', '11', '10', '30');
    //cy.deleteTournament('Demo Tournament');
  });

  it('creates a repeated tournament', () => {
    cy.createTournament('Demo Tournament1', '4', '17', '18', '10', '11', '10', '30');
    cy.log('try to create with the same name');
    cy.createTournament('Demo Tournament1', '10', '17', '18', '10', '11', '10', '30');

    cy.get('[data-cy="cancelButton"]').click();
  });

  it('tournament with more than the max permited in the qustions', () => {
    cy.createTournament('Demo Tournament2', '26', '17', '18', '10', '11', '10', '30');
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('tournament with invalid dates', () => {
    cy.createTournament('Demo Tournament3', '3', '19', '18', '10', '11', '10', '30');
    cy.get('[data-cy="cancelButton"]').click();
  });

  it('tournament with no topics', () => {
    cy.createTournamentEmptyTopics('Demo Tournament4', '3', '18', '19', '10', '11', '10', '30');
    cy.get('[data-cy="cancelButton"]').click();
  });
});
