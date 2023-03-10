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

Cypress.Commands.add('simpleDemoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
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
    cy.contains(oldtitle)
      .get('[data-cy="editButton"]')
      .scrollIntoView()
      .click({ force: true });
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

Cypress.Commands.add(
  'checkDashboardStudentTournamentStats',
  (studentName, part, result) => {
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="totalTournaments"]')
      .contains(part.toString());
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="highestResult"]')
      .contains(result.toString());
  }
);

Cypress.Commands.add(
  'checkDashboardStudentQuestionsStats',
  (studentName, numProposed, numAccepted) => {
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="numProposedQuestions"]')
      .contains(numProposed.toString());
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="numAcceptedQuestions"]')
      .contains(numAccepted.toString());
  }
);

Cypress.Commands.add(
  'changeDashboardStudentQuestionsPermissions',
  (
    changeShowNumProposedQuestions,
    changeShowNumAcceptedQuestions,
    save = true
  ) => {
    cy.get('[data-cy="permissionsButton"]').click({ force: true });

    if (changeShowNumProposedQuestions)
      cy.contains('Show number of proposed questions').click();
    if (changeShowNumAcceptedQuestions)
      cy.contains('Show number of accepted questions').click();

    if (save) cy.contains('Save').click();
    else cy.contains('Close').click();
  }
);

Cypress.Commands.add(
  'checkDashboardStudentQuestionsPermissions',
  (showNumProposedQuestions, showNumAcceptedQuestions) => {
    cy.get('[data-cy="permissionsButton"]').click({ force: true });

    cy.contains('Show number of proposed questions')
      .parent()
      .children()
      .find('input')
      .should(showNumProposedQuestions ? 'be.checked' : 'not.checked');

    cy.contains('Show number of accepted questions')
      .parent()
      .children()
      .find('input')
      .should(showNumAcceptedQuestions ? 'be.checked' : 'not.checked');

    cy.contains('Close').click();
  }
);

Cypress.Commands.add(
  'checkDashboardDiscussionStats',
  (studentName, numRequests, numAnswers) => {
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="numClarificationRequests"]')
      .contains(numRequests.toString());
    grandparent(cy.contains(studentName), 4)
      .children()
      .find('[data-cy="numAnsweredClarificationRequests"]')
      .contains(numAnswers.toString());
  }
);

Cypress.Commands.add(
  'changeDashboardDiscussionPermissions',
  (
    changeShowNumClarificationRequests,
    changeShowNumAnsweredClarificationRequests,
    save = true
  ) => {
    cy.get('[data-cy="permissionsButton"]').click({ force: true });

    if (changeShowNumClarificationRequests)
      cy.contains('Show number of clarification requests').click();
    if (changeShowNumAnsweredClarificationRequests)
      cy.contains('Show number of answered clarification requests').click();

    if (save) cy.contains('Save').click();
    else cy.contains('Close').click();
  }
);



Cypress.Commands.add(
  'checkDashboardDiscussionPermissions',
  (showNumClarificationRequests, showNumAnsweredClarificationRequests) => {
    cy.get('[data-cy="permissionsButton"]').click({ force: true });

    cy.contains('Show number of clarification requests')
      .parent()
      .children()
      .find('input')
      .should(showNumClarificationRequests ? 'be.checked' : 'not.checked');

    cy.contains('Show number of answered clarification requests')
      .parent()
      .children()
      .find('input')
      .should(showNumAnsweredClarificationRequests ? 'be.checked' : 'not.checked');

    cy.contains('Close').click(); 
  }
);








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
    .children()
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

Cypress.Commands.add('deleteTournament', name => {
  cy.contains('Enroll').click({ force: true });
  cy.contains(name)
    .parent()
    .find('[data-cy="delete"]')
    .click();
});

Cypress.Commands.add('participateTournament', name => {
  cy.contains('Participate').click({ force: true });
  cy.contains(name)
    .parent()
    .find('[data-cy="Answer"]')
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
  cy.contains('Select beginning').click({ force: true });
  cy.get(':nth-child(1) > #undefined-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .now').click({ force: true });
  cy.get('[data-cy="Select ending"]').click({ force: true });
  cy.get(':nth-child(2) > #undefined-wrapper > .datetimepicker > .datepicker > .pickers-container > #undefined-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(34) > .datepicker-day-text').click({force:true});
  cy.get('[data-cy=saveButton]').click();
});

Cypress.Commands.add(
  'createTournamentEmptyTopics',
  (name, numberOfQuestions, day2) => {
    cy.contains('Create').click({ force: true });
    cy.contains('New Tournament').click({ force: true });
    cy.get('[data-cy="tournamentName"]').type(name);
    cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
    cy.contains('Select beginning').click({ force: true });
    cy.get(':nth-child(1) > #undefined-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .now').click({ force: true });
    cy.get('[data-cy="Select ending"]').click({ force: true });
    cy.get(':nth-child(2) > #undefined-wrapper > .datetimepicker > .datepicker > .pickers-container > #undefined-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(34) > .datepicker-day-text').click({force:true});
    cy.get('[data-cy=saveButton]').click();
  }
);

Cypress.Commands.add(
  'createTournamentWrongDates',
  (name, numberOfQuestions, day2) => {
    cy.contains('Create').click({ force: true });
    cy.contains('New Tournament').click({ force: true });
    cy.get('[data-cy="tournamentName"]').type(name);
    cy.get('[data-cy="Number of questions"]').type(numberOfQuestions);
    cy.contains('Select ending').click({ force: true });
    cy.contains('Select beginning').click({ force: true });
    cy.get(':nth-child(1) > #undefined-wrapper > .datetimepicker > .datepicker > .datepicker-buttons-container > .now').click({ force: true });
    cy.contains('Select ending').click({ force: true });
    cy.get(':nth-child(2) > #undefined-wrapper > .datetimepicker > .datepicker > .pickers-container > #undefined-picker-container-DatePicker > .calendar > .month-container > :nth-child(1) > .datepicker-days > :nth-child(12) > .datepicker-day-text').click({force:true});
    cy.get('[data-cy=saveButton]').click();
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

Cypress.Commands.add('createClarificationSummary', text => {
  cy.get('[data-cy="clarificationRequests"]').click();
  if (!!text) cy.get('[data-cy="clarificationSummary"]').type(text);
  cy.get('[data-cy="submitSummary"]').click();
});

function grandparent(element, n) {
  for (let i = 0; i < n; i++) element = element.parent();
  return element;
}