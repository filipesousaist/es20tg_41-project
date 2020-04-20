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
  cy.get('[data-cy="demoStudentButton"]').click();
  cy.contains('Tournaments').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
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
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);
