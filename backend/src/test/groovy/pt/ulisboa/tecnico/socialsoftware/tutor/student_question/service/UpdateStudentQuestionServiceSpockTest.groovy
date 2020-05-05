package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class UpdateStudentQuestionServiceSpockTest extends Specification{
    private static final String COURSE_NAME = "Engenharia de Software"
    private static final String ACRONYM = "ES"
    private static final String ACADEMIC_TERM = "2 SEM"
    private static final String QUESTION_TITLE = "Question 1"
    public static final String NEW_QUESTION_TITLE = 'new question title'
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    private static final String OPTION1_CONTENT = "Option 1"
    public static final String NEW_OPTION_CONTENT = "new optionId content"
    private static final String OPTION2_CONTENT = "Option 2"
    private static final String STUDENT_NAME = "Student Name"
    private static final String STUDENT_USERNAME = "Student Username"
    private static final int STUDENT_KEY = 1
    private static final String TEACHER_NAME = "Teacher Name"
    private static final String TEACHER_USERNAME = "Teacher Username"
    private static final int TEACHER_KEY = 2

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
    def studentQuestionDto
    def student

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
        student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // Create student question, and get ID
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setQuestionDto(questionDto)
        studentQuestion = new StudentQuestion(course, student, studentQuestionDto)
        studentQuestion.setStatus(StudentQuestion.Status.ACCEPTED)
        studentQuestionRepository.save(studentQuestion)
    }

    def "change accepted student question title and option 1 content"(){
        given: "a changed StudentQuestionDto"
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
        studentQuestionDto.getQuestionDto().setTitle(NEW_QUESTION_TITLE)
        studentQuestionDto.getQuestionDto().getOptions().get(0).setContent(NEW_OPTION_CONTENT)
        when: "a student question is changed"
        studentQuestionService.updateStudentQuestion(studentQuestion.getId(), studentQuestionDto)
        then: "the returned data are correct"
        def result = this.studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.ACCEPTED
        def resQuestion = result.getQuestion()
        resQuestion != null
        resQuestion.getKey() == 1
        resQuestion.getStatus() == Question.Status.DISABLED
        and: "student question title is changed"
        resQuestion.getTitle() == NEW_QUESTION_TITLE
        resQuestion.getContent() == QUESTION_CONTENT
        resQuestion.getImage() == null
        resQuestion.getOptions().size() == 1
        resQuestion.getCourse().getName() == COURSE_NAME
        def resOption = resQuestion.getOptions().get(0)
        resOption.getContent() == NEW_OPTION_CONTENT
        resOption.getCorrect()
        def resOption2 = resQuestion.getOptions().get(1)
        resOption2.getContent() == OPTION2_CONTENT
        !resOption2.getCorrect()
        result.getUser() == student
        student.getStudentQuestions()contains(result)
    }

    def "make question title empty"(){
        given: "a change studentQuestionDto"
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
        studentQuestionDto.getQuestionDto().setTitle('     ')
        when: "a student question is changed"
        studentQuestionService.updateStudentQuestion(studentQuestion.getId(), studentQuestionDto)
        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
