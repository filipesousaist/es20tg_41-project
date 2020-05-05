describe('See Clarification', () => {

    beforeEach(() => {
        cy.simpleDemoStudentLogin()
    });

    afterEach(() => {
        cy.contains('Logout').click()
    });


    it('login and see a clarification associated with an answered question', () => {
        cy.get('[data-cy="QuizzesButton"]').click()
        cy.contains('Solved').click()
        cy.contains("Allocation viewtype").click()
        cy.contains('View Clarification Request').click()
        cy.wait(2000)
        cy.get('[data-cy="closeButton"').click()
    });

});
