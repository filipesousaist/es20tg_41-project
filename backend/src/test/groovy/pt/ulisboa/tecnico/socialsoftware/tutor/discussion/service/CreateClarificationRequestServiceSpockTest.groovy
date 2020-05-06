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
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz


import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CreateClarificationRequestServiceSpockTest extends Specification {


    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_CONTENT = "o que é uma classe?"


    static final String CLARIFICATION_TITLE = "Duvida interssante."
    static final String CLARIFICATION_TEXT = "Não percebi porque é que a opção correta é 1?."


    static final String NAME_1 = "Joao"
    static final String NAME_2 = "Manuel"
    static final String USERNAME_1 = "Joao123"
    static final String USERNAME_2 = "Manuel456"
    static final User.Role ROLE = User.Role.STUDENT

    static final String COURSE_NAME = "Course1"
    static final Course.Type COURSE_TYPE = Course.Type.TECNICO

    static final String COURSE_EXECUTION_ACRONYM = "C1"
    static final String COURSE_EXECUTION_ACADEMIC_TERM = "T1";


    @Autowired
    DiscussionService discussionService

    @Autowired
    UserService userService

    @Autowired
    QuestionService questionService

    @Autowired
    QuizService quizService

    @Autowired
    AnswerService answerService

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository


    User user1

    User user2

    def question1

    def course

    def courseExecution

    def setup(){

        course = new Course(COURSE_NAME, COURSE_TYPE);
        courseExecution = new CourseExecution(course, COURSE_EXECUTION_ACRONYM , COURSE_EXECUTION_ACADEMIC_TERM, COURSE_TYPE)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)

        user1 = userService.createUser(NAME_1, USERNAME_1, ROLE)
        user1.addCourse(courseExecution)
        userRepository.save(user1)
        user2 = userService.createUser(NAME_2, USERNAME_2, ROLE)
        userRepository.save(user2)



        def quiz = new Quiz()
        quiz.setKey(quizService.getMaxQuizKey()+1)
        quiz.setCourseExecution(courseExecution);
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quizRepository.save(quiz)

        question1 = new Question()
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.setCourse(course)
        questionRepository.save(question1)

        def quizQuestion = new QuizQuestion(quiz,question1, 1)
        def quizAnswer = new QuizAnswer(user1, quiz)
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)
        user1.addQuizAnswer(quizAnswer)

        answerService.concludeQuiz(user1, quiz.getId())
        answerService.submitAnswer(user1, quiz.getId(), new StatementAnswerDto(questionAnswer))

        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)

    }


    def "the question is does not exist" (){
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_TEXT)
        clarificationRequestDto.setUserId(user1.getId())

        when:
        discussionService.submitClarificationRequest(-1, clarificationRequestDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.QUESTION_NOT_FOUND

        and: " the clarification request is not added to the repository"
        clarificationRequestRepository.findAll().size() == 0

        and: "the clarification request is not associated with the question"
        question1.getClarificationRequests().size() == 0L

        and: "the clarification request is not associated with the user"
        user1.getClarificationRequests().size() == 0
    }

    def "the user does not exist" (){
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_TEXT)
        clarificationRequestDto.setUserId(-1)

        when:
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        and: " the clarification request is not added to the repository"
        clarificationRequestRepository.findAll().size() == 0

        and: "the clarification request is not associated with the question"
        question1.getClarificationRequests().size() == 0L

        and: "the clarification request is not associated with the user"
        user1.getClarificationRequests().size() == 0L
    }

    def "the question has not been answered by the student" (){
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_TEXT)
        clarificationRequestDto.setUserId(user2.getId())

        when:
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.QUESTION_ANSWER_NOT_FOUND

        and: " the clarification request is not added to the repository"
        clarificationRequestRepository.findAll().size() == 0L

        and: "the clarification request is not added associated with the question"
        question1.getClarificationRequests().size() == 0L

        and: "the clarification request is not associated with the user"
        user1.getClarificationRequests().size() == 0L
    }

    def "the question exists, the student exists, the student answered the question and creates Clarification Request" (){
        // creates a ClarificationRequest
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_TEXT)
        clarificationRequestDto.setUserId(user1.getId())


        when:
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        then: "the ClarificationRequest is in the repository"
        clarificationRequestRepository.findAll().size() == 1L
        def result = clarificationRequestRepository.findAll().get(0)
        result != null

        and: "the values are correct"
        result.getTitle() == CLARIFICATION_TITLE
        result .getText() == CLARIFICATION_TEXT
        result.getStudent() == user1
        and: "the clarification request is created"
        question1.getClarificationRequests().size() == 1L
        def clariReqs = new ArrayList<>(question1.getClarificationRequests()).get(0)
        clariReqs != null

        user1.getClarificationRequests().size() == 1L
        def clariReqs2 = new ArrayList<>(user1.getClarificationRequests()).get(0)
        clariReqs2 != null

    }

    def "there is a clarification already created for a certain question by a certain student" (){
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_TEXT)
        clarificationRequestDto.setUserId(user1.getId())
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        when:
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        then: "check for exceptions"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_REQUEST_ALREADY_EXISTS

        and: " the clarification request is not added to the repository"
        clarificationRequestRepository.findAll().size() == 1L

        and: "the clarification request is not associated with the question"
        question1.getClarificationRequests().size() == 1L

        and: "the clarification request is not associated with the user"
        user1.getClarificationRequests().size() == 1L
    }

    @Unroll
    def "invalid arguments: title=#title | text=#text" (){
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(title)
        clarificationRequestDto.setText(text)
        clarificationRequestDto.setUserId(user1.getId())

        when:
        discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)

        then: "check for excpetions"
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        and: " the clarification request is not added to the repository"
        clarificationRequestRepository.findAll().size() == 0L

        and: "the clarification request is not associated with the question"
        question1.getClarificationRequests().size() == 0L

        and: "the clarification request is not associated with the user"
        user1.getClarificationRequests().size() == 0L


        where:
        title                   | text                   || errorMessage
        null                    | CLARIFICATION_TEXT     || ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMPTY
        "       "               | CLARIFICATION_TEXT     || ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMPTY
        CLARIFICATION_TITLE     | null                   || ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMPTY
        CLARIFICATION_TITLE     | "      "               || ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMPTY

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
