package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetStudentQuestionsServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    private static final String STUDENT_NAME = "Student Name";
    private static final String STUDENT_USERNAME = "Student Username";
    private static final int STUDENT_KEY = 1;

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def user
    def userId

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        user.setKey(1)
        userRepository.save(user)

        userId = user.getId()
    }

    def "create 2 student question with 1 option and get student questions"() {
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        def time = DateHandler.toISOString(DateHandler.now())
        questionDto.setCreationDate(time)
        and: 'a optionDto'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)

        questionDto.setOptions(options)
        studentQuestionDto.setQuestionDto(questionDto)

        and: '2 studentQuestions'
        def studentQuestion1 = new StudentQuestion(course, user, studentQuestionDto)
        def studentQuestion2 = new StudentQuestion(course, user, studentQuestionDto)

        and: 'studentQuestions are in database'
        studentQuestionRepository.save(studentQuestion1)
        studentQuestionRepository.save(studentQuestion2)

        when:
        def result = studentQuestionService.getStudentQuestions(userId)

        then: "questions are received"
        result.size() == 2
        def resStudentQuestionDto = result.get(0)
        and: "user is correct"
        resStudentQuestionDto.getUserDto().getId() == userId
        and: "question is correct"

        def resQuestionDto = resStudentQuestionDto.getQuestionDto()
        resQuestionDto != null
        resQuestionDto.getId() != null
        resQuestionDto.getStatus() == Question.Status.DISABLED.toString()
        resQuestionDto.getTitle() == QUESTION_TITLE
        resQuestionDto.getContent() == QUESTION_CONTENT
        resQuestionDto.getImage() == null
        resQuestionDto.getOptions().size() == 1
        and: "option is correct"
        def resOptionDto = resQuestionDto.getOptions().get(0)
        resOptionDto.getContent() == OPTION_CONTENT
        resOptionDto.getCorrect()
    }

    def "get student questions without creating any student question"() {
        given: "no student questions are created"

        when:
        def result = studentQuestionService.getStudentQuestions(userId)

        then: "no student questions are returned"
        result.size() == 0
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
