package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.QuestionEvaluationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll;

@DataJpaTest
class MakeStudentQuestionAvailableServiceSpockTest extends Specification {
    private static final String COURSE_NAME = "Engenharia de Software";
    private static final String ACRONYM = "ES";
    private static final String ACADEMIC_TERM = "2 SEM";
    private static final String QUESTION_TITLE = "Question 1";
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    private static final String OPTION1_CONTENT = "Option 1";
    private static final String OPTION2_CONTENT = "Option 2";
    private static final String STUDENT_NAME = "Student Name";
    private static final String STUDENT_USERNAME = "Student Username";
    private static final int STUDENT_KEY = 1;

    @Autowired
    StudentQuestionService studentQuestionService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentQuestionRepository studentQuestionRepository;

    def studentQuestion
    def studentQuestionID

    def setup() {
        // Create course and course execution
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create question
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setCreationDate(DateHandler.toISOString(DateHandler.now()))

        // Create 2 options, and add them to question
        def option1Dto = new OptionDto()
        option1Dto.setContent(OPTION1_CONTENT)
        option1Dto.setCorrect(true)
        def option2Dto = new OptionDto()
        option2Dto.setContent(OPTION2_CONTENT)
        option2Dto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(option1Dto)
        options.add(option2Dto)
        questionDto.setOptions(options)

        // Create student
        def student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // Create student question, and get ID
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setQuestionDto(questionDto)
        studentQuestion = new StudentQuestion(course, student, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionID = studentQuestionRepository.findAll().get(0).id
    }

    def "make accepted student question available"() {
        given: "the studentQuestion is accepted"
        studentQuestion.setStatus(StudentQuestion.Status.ACCEPTED)

        when: "it is made available"
        studentQuestionService.makeStudentQuestionAvailable(studentQuestionID)

        then: "it is available"
        def updatedStudentQuestion = studentQuestionService.findById(studentQuestionID)
        updatedStudentQuestion != null
        def updatedQuestion = updatedStudentQuestion.getQuestion()
        updatedQuestion != null
        updatedQuestion.getStatus() == Question.Status.AVAILABLE
    }

    @Unroll("invalid status: #status || #errorMessage")
    def "try to make unaccepted studentQuestion available"() {
        given: "the studentQuestion is accepted"
        studentQuestion.setStatus(status)

        when: "it is made available"
        studentQuestionService.makeStudentQuestionAvailable(studentQuestionID)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_QUESTION_TEACHER_NOT_IN_COURSE
        and: "the question is not made available"
        def updatedStudentQuestion = studentQuestionService.findById(studentQuestionID)
        updatedStudentQuestion != null
        def updatedQuestion = updatedStudentQuestion.getQuestion()
        updatedQuestion != null
        updatedQuestion.getStatus() != Question.Status.AVAILABLE

        where:
        status                      ||  errorMessage
        Question.Status.DISABLED    ||  ErrorMessage.STUDENT_QUESTION_NEEDS_ACCEPTANCE
        Question.Status.REMOVED     ||  ErrorMessage.STUDENT_QUESTION_NEEDS_ACCEPTANCE
    }
}
