package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class UpdateClarificationRequestPrivacySpockTest extends Specification {
    static final String COURSE_NAME_1 = "Software Architecture"
    static final String ACRONYM_1 = "AS1"
    static final String COURSE_NAME_2 = "CourseTwo"
    static final String ACRONYM_2 = "C12"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_CONTENT = "o que é uma classe?"

    static final String NAME = "Joao"
    static final String USERNAME = "Joao123"
    static final User.Role ROLE = User.Role.STUDENT

    static final String TEACHER_NAME_1 = "António Rito"
    static final String TEACHER_USERNAME_1 = "António Rito"

    @Autowired
    DiscussionService discussionService

    @Autowired
    UserService userService

    @Autowired
    QuestionService questionService

    @Autowired
    QuizService quizService

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def course1
    def course2
    def courseExecution1
    def courseExecution2
    def teacher1
    def teacher2
    def request
    def requestDto
    def question
    def user

    def setup() {

        user = userService.createUser(NAME, USERNAME, ROLE)
        userRepository.save(user)

        course1 = new Course(COURSE_NAME_1, Course.Type.TECNICO)
        courseRepository.save(course1)

        course2 = new Course(COURSE_NAME_2, Course.Type.TECNICO)
        courseRepository.save(course2)

        courseExecution1 = new CourseExecution(course1, ACRONYM_1, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution1)

        courseExecution2 = new CourseExecution(course2, ACRONYM_2, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)

        teacher1 = userService.createUser(TEACHER_NAME_1, TEACHER_USERNAME_1, User.Role.TEACHER)
        Set courses = new HashSet<CourseExecution>()
        courses.add(courseExecution1)
        teacher1.setCourseExecutions(courses)
        userRepository.save(teacher1)


        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setCourse(course1)
        questionRepository.save(question)


        def quizQuestion = new QuizQuestion(quiz, question, 1)
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)

        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)

        requestDto = new ClarificationRequestDto()

        request = new ClarificationRequest()
        request.setQuestion(question)
        request.setStudent(user)
        clarificationRequestRepository.save(request)
    }

    def "Turn a clarification Request public" () {
        given: "A clarification request to turn public"
        requestDto.setPrivacy(false)

        when:
        discussionService.updateClarificationRequestPrivacy(request.getId(), requestDto)

        then:"The clarification request is now public"
        def result = clarificationRequestRepository.findAll().get(0)
        !result.getPrivacy()
    }

    def "Turn a clarification Request private" () {
        given: "A clarification request to turn private"
        requestDto.setPrivacy(true)

        when:
        discussionService.updateClarificationRequestPrivacy(request.getId(), requestDto)

        then:"The clarification request is now public"
        def result = clarificationRequestRepository.findAll().get(0)
        result.getPrivacy()
    }

    def "Try to change the privacy of a clarification request that doesn't exist" () {
        given: "A clarificationDto"
        requestDto.setPrivacy(!request.getPrivacy())

        when:
        discussionService.updateClarificationRequestPrivacy(-1, requestDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND

        and: "the clarification request privacy didn't change"
        clarificationRequestRepository.findAll().get(0).getPrivacy() != requestDto.getPrivacy()
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        StatementService statementService() {
            return new StatementService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

    }
}
