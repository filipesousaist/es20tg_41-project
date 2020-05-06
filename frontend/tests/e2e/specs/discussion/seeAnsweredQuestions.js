describe('See Answered Questions', () => {
    beforeEach(() => {
        cy.simpleDemoStudentLogin();
    });

    afterEach(() => {
        cy.contains('Logout').click();
    });

    it('login and see answered questions', () => {
        cy.get('[data-cy="QuizzesButton"]').click();
        cy.contains('Answered Questions').click();
        cy.get('[data-cy="searchBox"]').type('ComponentAndConnectorS');
        cy.contains('Typically, Instant Messaging');
    });

});
