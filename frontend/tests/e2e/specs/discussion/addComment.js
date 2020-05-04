describe('Create Comment', () => {

  
    it('create a comment for a question after a teacher created a clarification', () => {
        cy.simpleDemoStudentLogin();
      cy.get('[data-cy="QuizzesButton"]').click();
      cy.contains('Solved').click();
      cy.contains('Allocation viewtype').click();
      cy.contains('View Clarification Request').click();
      cy.get('[data-cy=commentText]').type('I still dont get it.');
      cy.get('[data-cy=sendCommentButton]');
      cy.wait(2500);
      cy.contains('Logout').click();

    });


    it('create a comment for a question after a teacher created a clarification', () => {
        cy.demoTeacherLogin();
        cy.contains('Questions').click()
        cy.get('[data-cy="searchBox"]')
        .clear()
        .type('In the software architecture of a system, the Deployment architectural style of the allocation viewtype is best suited for');
        cy.get('[data-cy="clarificationRequests"]').click();
        cy.get('[data-cy=commentText]').type('Then Ill get into more detaisl.');
        cy.get('[data-cy=sendCommentButton]');
        cy.wait(2500);
        cy.contains('Logout').click();
        

      });
  
  });
  