package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.StudentQuestionRepository
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

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User()
        //user.setKey(1)
        userRepository.save(user)
    }

    def "create 2 student questions with 1 option and get student questions"() {
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name()) // mudar para PROPOSED
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
        def result = studentQuestionService.getStudentQuestions(user.getId())

        then:
        result.size() == 2
        def resStudentQuestion = result.get(0)
        and: "user is correct"
        resStudentQuestion.getUser() == user
        and: "question is correct"
        def resQuestion = resStudentQuestion.getQuestion()
        resQuestion.getId() != null
        resQuestion.getKey() == 1
        resQuestion.getStatus() == Question.Status.AVAILABLE
        resQuestion.getTitle() == QUESTION_TITLE
        resQuestion.getContent() == QUESTION_CONTENT
        resQuestion.getImage() == null
        resQuestion.getOptions().size() == 1
        resQuestion.getCourse().getName() == COURSE_NAME
        and: "option is correct"
        def resOption = resQuestion.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()
    }

    def "get student questions without creating any student question"() {
        given: "no student questions are created"

        when:
        def result = studentQuestionService.getStudentQuestions(user.getId())

        then:
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
