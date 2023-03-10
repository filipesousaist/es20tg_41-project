package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRepository
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
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class CreateClarificationServiceSpockPerformanceTest extends Specification{

    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_CONTENT = "o que ?? uma classe?"


    static final String CLARIFICATION_TITLE = "Duvida interssante."
    static final String CLARIFICATION_REQUEST_TEXT = "N??o percebi porque ?? que a op????o correta ?? 1?."

    static final String NAME = "Name"
    static final String USERNAME = "Useranme"
    static final String TEACHER_NAME = "Ant??nio Rito"
    static final String TEACHER_USERNAME = "Ant??nio Rito"

    static final User.Role ROLE = User.Role.STUDENT

    static final String COURSE_NAME = "Course1"
    static final Course.Type COURSE_TYPE = Course.Type.TECNICO

    static final String COURSE_EXECUTION_ACRONYM = "C1"
    static final String COURSE_EXECUTION_ACADEMIC_TERM = "T1";

    static final String OPTION_CONTENT = "Resposta A"

    static final String CLARIFICATION_TEXT = "A op????o correta ?? 1), porque sim."

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
    ClarificationRepository discussionRepository

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

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def user1

    def teacher1

    def question1

    def course

    def courseExecution

    def quizAnswer
    def questionAnswer

    def request

    def setup (){

        course = new Course(COURSE_NAME, COURSE_TYPE);
        courseExecution = new CourseExecution(course, COURSE_EXECUTION_ACRONYM, COURSE_EXECUTION_ACADEMIC_TERM, COURSE_TYPE)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)

        teacher1 = userService.createUser(TEACHER_NAME, TEACHER_USERNAME, User.Role.TEACHER)
        Set courses = new HashSet<CourseExecution>()
        courses.add(courseExecution)
        teacher1.setCourseExecutions(courses)
        userRepository.save(teacher1)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setKey(quizService.getMaxQuizKey() + 1)
        quiz.setCourseExecution(courseExecution);
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quizRepository.save(quiz)

        question1 = new Question()
        question1.setKey(1)
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.setCourse(course)
        questionRepository.save(question1)


        def quizQuestion = new QuizQuestion(quiz, question1, 1)
        quizQuestionRepository.save(quizQuestion)

        /*def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_REQUEST_TEXT)*/

        request = new ClarificationRequest()
        request.setQuestion(question1)
        clarificationRequestRepository.save(request)

        1.upto(1, {
            user1 = userService.createUser(NAME + it, USERNAME + it, ROLE)
            user1.addCourse(courseExecution)
            userRepository.save(user1)
            quizAnswer = new QuizAnswer(user1, quiz)
            quizAnswerRepository.save(quizAnswer)
            questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
            questionAnswerRepository.save(questionAnswer)
            quizAnswer.addQuestionAnswer(questionAnswer)
            user1.addQuizAnswer(quizAnswer)
            answerService.concludeQuiz(user1, quiz.getId())
            answerService.submitAnswer(user1, quiz.getId(), new StatementAnswerDto(questionAnswer))
            request.setStudent(user1)
            //discussionService.submitClarificationRequest(question1.getId(), clarificationRequestDto)
            clarificationRequestRepository.save(request)

        })

    }


    def "performance testing to create 1000 clarifications"() {

        def clarificationDto = new ClarificationDto()
        clarificationDto.setUserId(teacher1.getId())

        when:
        1.upto(1, {
            clarificationDto.setText(CLARIFICATION_TEXT + it)
            discussionService.createClarification(it,clarificationDto)
        })

        then:
        true
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
