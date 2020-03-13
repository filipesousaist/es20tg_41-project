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

@DataJpaTest
class createClarificationServiceSpockTest extends Specification {
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
    static final String EMPTY_CLARIFICATION_TEXT = " "

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
    def clarification
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


        clarification = new ClarificationDto()
        clarification.setKey(discussionService.getMaxClarificationKey()+1)

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
        request.setKey(1)
        request.setQuestion(question)
        request.setStudent(user)
        discussionRepository.save(request)
    }

    def "create Clarification" (){
        given:"A clarificationRequest"
        clarification.setText(CLARIFICATION_TEXT)

        when:
        discussionService.createClarification(teacher1.getId(), request.getId(), clarification)

        then:"the correct clarification is inside the repository"
        discussionRepository.count() == 1L
        def result = discussionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getText() == CLARIFICATION_TEXT
        result.getTeacher() == teacher1
        result.getClarificationRequest() == request

    }

    def "the request does not exist" (){
        given:"createClarification a clarification"
        clarification.setText(CLARIFICATION_TEXT)

        when:
        discussionService.createClarification(teacher1.getId(), -1, clarification)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND
        discussionRepository.count() == 0L
    }

    def "the teacher does not exist" (){
        given:"createClarification a clarification"
        clarification.setText(CLARIFICATION_TEXT)

        when:
        discussionService.createClarification(-1, request.getId(), clarification)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
        discussionRepository.count() == 0L
    }

    def "Teacher isn't in the same course as the question" (){
        given:"A teacher not in the same course as question"
        teacher2 = userService.createUser(TEACHER_NAME_2, TEACHER_USERNAME_2, User.Role.TEACHER)
        Set courses = new HashSet<CourseExecution>()
        courses.add(courseExecution2)
        teacher2.setCourseExecutions(courses)
        userRepository.save(teacher2)

        when:
        discussionService.createClarification(teacher2.getId(), request.getId(), clarification)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_COURSE_EXECUTION_MISMATCH
        discussionRepository.count() == 0L
    }

    def "Clarification with null string as clarification" (){
        //Throws Exception
        given:"createClarification a clarification"
        clarification.setText(null)

        when:
        discussionService.createClarification(teacher1.getId(), request.getId(), clarification)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLARIFICATION_NOT_CONSISTENT
        discussionRepository.count() == 0L

    }

    def "Clarification with empty/only blank spaces" (){
        given:"createClarification a clarification"
        clarification.setText(EMPTY_CLARIFICATION_TEXT)

        when:
        discussionService.createClarification(teacher1.getId(), request.getId(), clarification)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY
        discussionRepository.count() == 0L
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

