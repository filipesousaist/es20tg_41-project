package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),

    CLARIFICATION_REQUEST_NOT_FOUND("Clarification request not found with id %d"),
    CLARIFICATION_NOT_FOUND("Clarification not found with id %d"),

    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),


    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %s not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),
    TEACHER_COURSE_EXECUTION_MISMATCH("Teacher %d does not have course %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_IS_MISSING( "Question is null"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    TOPICS_IS_EMPTY("The Tournament topics is empty"),
    EMPTY_TOPIC("Empty topic"),
    END_BEFORE_BEGINS("The tournament ends before it begins"),
    INVALID_QUESTIONS_NUMBER("Invalid question number : %d"),
    INVALID_COURSE_EXECUTION("The tournament is not associated with this course execution"),

    TOURNAMENT_NOT_FOUND("The tournament doesn't exist"),
    STUDENT_ALREADY_ENROLLED("The student is already enrolled"),
    STUDENT_NOT_ENROLLED("The student is not enrolled"),
    TOURNAMENT_IS_CLOSED("The tournament has already closed"),
    INVALID_TOURNAMENT_NAME("Tournament name already in use."),
    NO_SUCH_TOPIC("No such topic in the course"),
    USER_IS_NOT_A_STUDENT("The user trying to enroll is not a student"),
    STUDENT_DIDNT_CREATE_TOURNAMENT("The user cant delete a tournament that did not create"),

    // StudentQuestion
    STUDENT_QUESTION_NOT_FOUND("Student question not found with id %d"),
    QUESTION_EVALUATION_MISSING_JUSTIFICATION("Student question has no justification"),
    STUDENT_QUESTION_TEACHER_NOT_IN_COURSE("Teacher is not in course of student question"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    CLARIFICATION_REQUEST_ALREADY_EXISTS("You can not request another clarification for this question"),
    CLARIFICATION_REQUEST_TITLE_IS_EMTPY("The Clarification Request title is empty"),
    CLARIFICATION_REQUEST_TEXT_IS_EMTPY("The Clarification Request text is empty"),
    CLARIFICATION_ALREADY_EXISTS("You can not submit another clarification for this request"),
    CLARIFICATION_TEXT_IS_EMPTY("The clarification text is empty"),
    CLARIFICATION_NOT_CONSISTENT("Field %s of clarification not consistent"),
    CLARIFICATION_REQUEST_HAS_NO_CLARIFICATION("The Clarification Request has no clarification associated with it.");



    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}