describe('Create Clarification Request', () => {

    beforeEach(() => {
        cy.simpleDemoStudentLogin();
    });

    afterEach(() => {
        cy.contains('Logout').click();
    });


    it('login answer a quiz and see all public clarification requests for an answered question in quiz', () => {
        cy.get('[data-cy="QuizzesButton"]').click();
        cy.contains('Available').click();
        cy.answerQuiz('Component-and-connector viewtype');
        cy.contains('View Public Clarification Requests').click();
        cy.contains('request_title').click();
        cy.contains('request_text');
        cy.contains('clarification_summary');

    });

    it('login answer a quiz and see all public clarification requests for an answered question in question answers menu', () => {
        cy.get('[data-cy="QuizzesButton"]').click();
        cy.contains('Answered Questions').click();
        cy.get('[data-cy="searchBox"]').type('ComponentAndConnectorS');
        cy.get('[data-cy="clarificationRequests"]').click();
        cy.contains('request_title').click();
        cy.contains('request_text');
        cy.contains('clarification_summary');
    });
});