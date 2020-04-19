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

Cypress.Commands.add('createTournament',
  (name, numberOfQuestions, day1, day2, hour1, hour2, minute1, minute2) => {
  cy.contains('Create').click();
  cy.get('[data-cy="Name of the tournament"]').type(name);
  cy.get('[data-cy="Select beginning"]').click();
  cy.contains(day1).click();
  cy.contains(hour1).click();
  cy.contains(minute1).click();
  cy.contains('OK').click();
  cy.get('[data-cy="Select ending"]').click();
  cy.contains(day2).click();
  cy.contains(hour2).click();
  cy.contains(minute2).click();
  cy.contains('OK').click();
  cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
  cy.get('[data-cy="Topics"]').click();
  cy.contains('Github').click();
  cy.contains('Chrome').click();
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('createTournamentEmptyTopics',
  (name, numberOfQuestions, day1, day2, hour1, hour2, minute1, minute2) => {
    cy.contains('Create').click();
    cy.get('[data-cy="Name of the tournament"]').type(name);
    cy.get('[data-cy="Select beginning"]').click();
    cy.contains(day1).click();
    cy.contains(hour1).click();
    cy.contains(minute1).click();
    cy.contains('OK').click();
    cy.get('[data-cy="Select ending"]').click();
    cy.contains(day2).click();
    cy.contains(hour2).click();
    cy.contains(minute2).click();
    cy.contains('OK').click();
    cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
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
  }
);
