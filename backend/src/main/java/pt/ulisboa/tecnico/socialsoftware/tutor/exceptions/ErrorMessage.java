package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),

    CLARIFICATION_REQUEST_NOT_FOUND("Clarification request not found with id %d"),
    CLARIFICATION_NOT_FOUND("Clarification not found with id %d"),


    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

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
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_IS_MISSING( "Question is null"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    // Tournament
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
    STUDENT_QUESTION_NEEDS_ACCEPTANCE("Student question needs to be accepted"),
    STUDENT_QUESTION_IS_NOT_REJECTED("Student question isn't rejected"),

    // Discussion
    CLARIFICATION_REQUEST_ALREADY_EXISTS("You can not request another clarification for this question"),
    CLARIFICATION_REQUEST_TITLE_IS_EMPTY("The Clarification Request title is empty"),
    CLARIFICATION_REQUEST_TEXT_IS_EMPTY("The Clarification Request text is empty"),
    CLARIFICATION_ALREADY_EXISTS("You can not submit another clarification for this request"),
    CLARIFICATION_TEXT_IS_EMPTY("The clarification text is empty"),
    CLARIFICATION_SUMMARY_IS_EMPTY("The clarification summary is empty"),
    CLARIFICATION_NOT_CONSISTENT("Field %s of clarification not consistent"),
    CLARIFICATION_REQUEST_HAS_NO_CLARIFICATION("The Clarification Request has no clarification associated with it."),

    COMMENT_INVALID_USER("The user did no asked for the clarification nor answered it."),
    COMMENT_EMPTY_TEXT("The comment text is empty!");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}