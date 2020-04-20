// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
  cy.contains('Student Questions').click();
});


Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="teacherButton"]').click();
  cy.contains('Management').click();
});

Cypress.Commands.add(
  'createStudentQuestion',
  (title, question, option1, option2, option3, option4) => {
    cy.get('[data-cy="createButton"]').click();
    cy.get('[data-cy="Title"]')
      .scrollIntoView()
      .type(title, { force: true });
    cy.get('[data-cy="Question"]').type(question);
    cy.get('[data-cy="Content"]')
      .eq(0)
      .type(option1);
    cy.get('[data-cy="Correct"]')
      .eq(1)
      .check({ force: true });
    cy.get('[data-cy="Content"]')
      .eq(1)
      .type(option2);
    cy.get('[data-cy="Content"]')
      .eq(2)
      .type(option3);
    cy.get('[data-cy="Content"]')
      .eq(3)
      .type(option4);

    cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add(
  'createStudentQuestionWithOneOption',
  (title, question, option1) => {
    cy.get('[data-cy="createButton"]').click();
    cy.get('[data-cy="Title"]')
      .scrollIntoView()
      .type(title, { force: true });
    cy.get('[data-cy="Question"]').type(question);
    cy.get('[data-cy="Content"]')
      .eq(0)
      .type(option1);
    cy.get('[data-cy="Correct"]')
      .eq(0)
      .check({ force: true });
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('clickEvaluateQuestion', studentQuestionTitle => {
  cy.contains(studentQuestionTitle)
    .parent()
    .children()
    .should('have.length', 5)
    .find('[data-cy="evaluateButton"]')
    .scrollIntoView()
    .click({ force: true });
});

Cypress.Commands.add(
  'createQuestionEvaluation',
  (studentQuestionTitle, approved, justification) => {
    // Click on button to evaluate question
    cy.clickEvaluateQuestion(studentQuestionTitle);

    // Check approval or rejection (approved can be true, false or null)
    if (approved === true) cy.contains('Approve Question').click();
    else if (approved === false) cy.contains('Reject Question').click();

    // Type in justification
    if (justification)
      cy.get('[data-cy="justificationTextField"]').type(justification);

    // Submit question evaluation
    cy.get('[data-cy="submitButton"]').click();
  }
);

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  });

  Cypress.Commands.add('answerQuiz', (title) => {
    cy.contains(title).click()
    cy.contains("End Quiz").click()
    //cy.get(['data-cy=endQuizSure']).click()
    cy.contains("I'm sure").click()

});

Cypress.Commands.add('createClarificationRequest', (title, text) => {
    cy.contains("Request Clarification").click()
    if(!!title) cy.get('[data-cy="clarificationRequestTitle"]').type(title)
    if(!!text) cy.get('[data-cy="clarificationRequestText"]').type(text)
    cy.get('[data-cy="submitClarificationRequest"').click()
});

Cypress.Commands.add('createClarification', (text) => {
  cy.get('[data-cy="clarificationRequests"]').click();
  if(!!text) cy.get('[data-cy="clarificationText"]').type(text);
  cy.get('[data-cy="submitClarification"]').click();
});
