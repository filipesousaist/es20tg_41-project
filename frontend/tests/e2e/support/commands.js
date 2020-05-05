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

Cypress.Commands.add('demoStudentLoginT', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
  cy.contains('Tournaments').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
  cy.contains('Student Questions').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
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
  }
);

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

Cypress.Commands.add('clickEvaluateQuestion', studentQuestionTitle => {
  cy.clickProposedQuestionButton(studentQuestionTitle, 'evaluateButton');
});

Cypress.Commands.add(
  'clickMakeStudentQuestionAvailable',
  studentQuestionTitle => {
    cy.clickProposedQuestionButton(studentQuestionTitle, 'makeAvailableButton');
  }
);

Cypress.Commands.add(
  'clickProposedQuestionButton',
  (studentQuestionTitle, buttonName) => {
    cy.contains(studentQuestionTitle)
      .parent()
      .children()
      .should('have.length', 6)
      .find('[data-cy="' + buttonName + '"]')
      .scrollIntoView()
      .click({ force: true });
  }
);

Cypress.Commands.add('failClickMakeAvailableButton', studentQuestionTitle => {
  cy.contains(studentQuestionTitle)
    .parent()
    .children()
    .find('[data-cy="makeAvailableButton"]')
    .should('not.exist');
});

Cypress.Commands.add(
  'updateStudentQuestion',
  (oldtitle, newTitle, newOptionContent) => {
    cy.clickProposedQuestionButton(oldtitle, 'editButton');
    cy.get('[data-cy="Title"]')
      .scrollIntoView()
      .clear({ force: true })
      .type(newTitle, { force: true });
    cy.get('[data-cy="Content"]')
      .eq(0)
      .clear()
      .type(newOptionContent);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('failClickEditButton', studentQuestionTitle => {
  cy.contains(studentQuestionTitle)
    .parent()
    .children()
    .find('[data-cy="editButton"]')
    .should('not.exist');
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('enrollTournament', name => {
  cy.contains('Enroll').click({ force: true });
  cy.contains(name)
    .parent()
    .find('[data-cy="enroll"]')
    .click();
});

Cypress.Commands.add('withdrawTournament', name => {
  cy.contains('Enroll').click({ force: true });
  cy.contains(name)
    .parent()
    .find('[data-cy="withdraw"]')
    .click();
});

Cypress.Commands.add('createTournament', (name, numberOfQuestions, day2) => {
  cy.contains('Create').click({ force: true });
  cy.contains('New Tournament').click({ force: true });
  cy.get('[data-cy="tournamentName"]').type(name);
  cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
  cy.get('.col-sm-12 > .v-input > .v-input__control > .v-input__slot').click();
  cy.contains('Adventure Builder').click();
  cy.contains('Amazon Silk').click();
  cy.contains('New Tournament').click({ force: true });
  cy.contains('Select ending').click({ force: true });
  cy.contains(day2).click();
  cy.get('[style="left: 25%; top: 6.69873%;"] > span').click();
  cy.get(
    '.fade-transition-enter-active > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 6.69873%; top: 25%;"] > span'
  ).click();
  cy.contains('OK').click();
  cy.contains('Select beginning').click({ force: true });
  cy.get(
    '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-table > table > tbody > :nth-child(3) > :nth-child(6) > .v-btn'
  ).click();
  cy.get(
    '.v-window-item--active > .v-picker > .v-picker__body > .v-time-picker-clock__container > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 6.69873%; top: 25%;"] > span'
  ).click();
  cy.get(
    '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text > .v-btn__content'
  ).click();
  cy.get('[data-cy=saveButton] > .v-btn__content').click();
});

Cypress.Commands.add(
  'createTournamentEmptyTopics',
  (name, numberOfQuestions, day2) => {
    cy.contains('Create').click({ force: true });
    cy.contains('New Tournament').click({ force: true });
    cy.get('[data-cy="tournamentName"]').type(name);
    cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
    cy.contains('Select ending').click({ force: true });
    cy.contains(day2).click();
    cy.get('[style="left: 25%; top: 6.69873%;"] > span').click();
    cy.get(
      '.fade-transition-enter-active > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 6.69873%; top: 25%;"] > span'
    ).click();
    cy.contains('OK').click();
    cy.contains('Select beginning').click({ force: true });
    cy.get(
      '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__text > .v-tabs > .v-window > .v-window__container > .v-window-item > .v-picker > .v-picker__body > :nth-child(1) > .v-date-picker-table > table > tbody > :nth-child(3) > :nth-child(6) > .v-btn'
    ).click();
    cy.get(
      '.v-window-item--active > .v-picker > .v-picker__body > .v-time-picker-clock__container > .v-time-picker-clock > .v-time-picker-clock__inner > [style="left: 6.69873%; top: 25%;"] > span'
    ).click();
    cy.get(
      '.v-dialog__content--active > .v-dialog > .v-sheet > .v-card__actions > .green--text > .v-btn__content'
    ).click();
    cy.get('[data-cy=saveButton] > .v-btn__content').click();
  }
);

Cypress.Commands.add('closeErrorMessage', () => {
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
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('answerQuiz', title => {
  cy.contains(title).click();
  cy.contains('End Quiz').click();
  //cy.get(['data-cy=endQuizSure']).click()
  cy.contains('I\'m sure').click();
});

Cypress.Commands.add('createClarificationRequest', (title, text) => {
  cy.contains('Request Clarification').click();
  if (!!title) cy.get('[data-cy="clarificationRequestTitle"]').type(title);
  if (!!text) cy.get('[data-cy="clarificationRequestText"]').type(text);
  cy.get('[data-cy="submitClarificationRequest"').click();
});

Cypress.Commands.add('createClarification', text => {
  cy.get('[data-cy="clarificationRequests"]').click();
  if (!!text) cy.get('[data-cy="clarificationText"]').type(text);
  cy.get('[data-cy="submitClarification"]').click();
});
