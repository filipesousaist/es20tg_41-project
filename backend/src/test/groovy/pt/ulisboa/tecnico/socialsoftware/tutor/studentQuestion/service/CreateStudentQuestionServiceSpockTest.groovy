package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateStudentQuestionServiceSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"

    @Autowired
    StudentQuestionRepository StudentQuestionRepository

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


    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User()
        user.setKey(1)
        userRepository.save(user)

    }

    def "create studentQuestion"(){
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PROPOSED.name())
        questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter));

        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        studentQuestionDto.setQuestionDto(questionDto)
        and: "a userId"
        def userId = userRepository.findAll().get(0).getId()

        when:
        studentQuestionService.createStudentQuestion(course.getId(), userId, studentQuestionDto)

        then: "the correct student question is inside the repository"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getQuestion().getKey() == 1
        result.getQuestion().getStatus() == Question.Status.PROPOSED
        result.getQuestion().getTitle() == QUESTION_TITLE
        result.getQuestion().getContent() == QUESTION_CONTENT
        result.getQuestion().getImage() == null
        result.getQuestion().getOptions().size() == 1
        result.getQuestion().getCourse().getName() == COURSE_NAME
        def resOption = result.getQuestion().getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()
        result.getUser() == user
        user.getStudentQuestions()contains(result)
    }

    def "student question is not created when content is null"(){
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent("")
        questionDto.setStatus(Question.Status.PROPOSED.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        studentQuestionDto.setQuestionDto(questionDto)
        and: "a userId"
        def userId = userRepository.findAll().get(0).getId()

        when:
        studentQuestionService.createStudentQuestion(course.getId(), userId, studentQuestionDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_MISSING_DATA
        studentQuestionRepository.findAll().size() == 0
    }

    def "student question is not created when question is null"(){
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        and: "a userId"
        def userId = userRepository.findAll().get(0).getId()

        when:
        studentQuestionService.createStudentQuestion(course.getId(), userId, studentQuestionDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_IS_MISSING
        studentQuestionRepository.findAll().size() == 0
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}