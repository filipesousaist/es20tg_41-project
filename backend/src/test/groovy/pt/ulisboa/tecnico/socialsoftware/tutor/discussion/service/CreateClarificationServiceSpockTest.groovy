package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
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
import spock.lang.Unroll

@DataJpaTest
class CreateClarificationServiceSpockTest extends Specification {
    static final String COURSE_NAME_1 = "Software Architecture"
    static final String ACRONYM_1 = "AS1"
    static final String COURSE_NAME_2 = "CourseTwo"
    static final String ACRONYM_2 = "C12"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_CONTENT = "o que é uma classe?"
    static final String OPTION_CONTENT = "Resposta A"

    static final String NAME = "Joao"
    static final String USERNAME = "Joao123"
    static final User.Role ROLE = User.Role.STUDENT

    static final String TEACHER_NAME_1 = "António Rito"
    static final String TEACHER_USERNAME_1 = "António Rito"
    static final String TEACHER_NAME_2 = "Teacher Name"
    static final String TEACHER_USERNAME_2 = "Teacher Username"
    static final String CLARIFICATION_TEXT = "A opção correta é 1), porque sim."

    @Autowired
    DiscussionService discussionService

    @Autowired
    UserService userService

    @Autowired
    QuestionService questionService

    @Autowired
    QuizService quizService

    @Autowired
    DiscussionRepository discussionRepository

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
    def clarificationDto
    def question
    def user

    def setup(){

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


        clarificationDto = new ClarificationDto()

        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)

        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setCourse(course1)
        questionRepository.save(question)

        def option = new Option()
        option.setCorrect(false)
        option.setContent(OPTION_CONTENT)
        option.setQuestion(question)

        question.addOption(option)

        def quizQuestion = new QuizQuestion(quiz,question, 1)
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)

        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)

        request = new ClarificationRequest()
        request.setQuestion(question)
        request.setStudent(user)
        clarificationRequestRepository.save(request)
    }

    def "create Clarification" (){
        given:"A clarificationRequest"
        clarificationDto.setText(CLARIFICATION_TEXT)
        clarificationDto.setUsername(teacher1.getUsername())

        when:
        discussionService.createClarification(request.getId(), clarificationDto)

        then:"the values of the clarification are correct"
        def result = discussionRepository.findAll().get(0)
        result.getId() != null
        result.getText() == CLARIFICATION_TEXT
        result.getTeacher() == teacher1
        result.getClarificationRequest() == request

        and: "the clarification is in the repository"
        discussionRepository.count() == 1L

        and: "the clarification is created"
        teacher1.getClarifications().size() == 1L
        request.getClarification() != null

    }

    def "the request does not exist" (){
        given:"createClarification a clarification"
        clarificationDto.setText(CLARIFICATION_TEXT)
        clarificationDto.setUsername(teacher1.getUsername())

        when:
        discussionService.createClarification(-1, clarificationDto)

        then: "check for exceptions"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND

        and: "clarification is not added to the repository"
        discussionRepository.count() == 0L

        and: "clarification is not associated with the teacher"
        teacher1.getClarifications().size() == 0L
    }

    def "the teacher does not exist" (){
        given:"createClarification a clarification"
        clarificationDto.setText(CLARIFICATION_TEXT)
        clarificationDto.setUsername("Eu não existo")

        when:
        discussionService.createClarification(request.getId(), clarificationDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        and: "clarification is not added to the repository"
        discussionRepository.count() == 0L

        and: "clarification is not associated with clarification request"
        request.getClarification() == null
    }

    def "Teacher isn't in the same course as the question" (){
        given:"A teacher not in the same course as question"
        teacher2 = userService.createUser(TEACHER_NAME_2, TEACHER_USERNAME_2, User.Role.TEACHER)
        Set courses = new HashSet<CourseExecution>()
        courses.add(courseExecution2)
        teacher2.setCourseExecutions(courses)
        userRepository.save(teacher2)
        clarificationDto.setText(CLARIFICATION_TEXT)
        clarificationDto.setUsername(teacher2.getUsername())

        when:
        discussionService.createClarification(request.getId(), clarificationDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_COURSE_EXECUTION_MISMATCH

        and: "clarification is not added to the repository"
        discussionRepository.count() == 0L

        and: "clarification is not associated with teacher"
        teacher2.getClarifications().size() == 0L

        and: "clarification is not associated with clarification request"
        request.getClarification() == null
    }

    @Unroll
    def "invalid argument: text=#text" (){
        given: "a clarification dto"
        clarificationDto.setText(text)
        clarificationDto.setUsername(teacher1.getUsername())

        when:
        discussionService.createClarification(request.getId(), clarificationDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        and: "clarification is not added to the repository"
        discussionRepository.count() == 0L

        and: "clarification is not associated with teacher"
        teacher1.getClarifications().size() == 0L

        and: "clarification is not associated with clarification request"
        request.getClarification() == null

        where:
        text                || errorMessage
        null                || ErrorMessage.CLARIFICATION_NOT_CONSISTENT
        "    "              || ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY
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

