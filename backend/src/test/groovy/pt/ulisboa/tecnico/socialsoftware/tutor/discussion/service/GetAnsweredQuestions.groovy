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
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Comment
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.CommentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.CommentRepository
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
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class GetAnsweredQuestions extends Specification{
    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_TITLE2 = "question2"

    static final String QUESTION_CONTENT = "o que é uma classe?"
    static final String QUESTION_CONTENT2 = "o que é uma classe2?"

    
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

    def question1
    def question2


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


        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution);
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quizRepository.save(quiz)

        def quiz2 = new Quiz()
        quiz2.setCourseExecution(courseExecution);
        quiz2.setType(Quiz.QuizType.GENERATED.toString())
        quizRepository.save(quiz2)

        question1 = new Question()
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.setCourse(course)
        questionRepository.save(question1)

        question2 = new Question()
        question2.setTitle(QUESTION_TITLE2)
        question2.setContent(QUESTION_CONTENT2)
        question2.setCourse(course)
        questionRepository.save(question2)

        def quizQuestion = new QuizQuestion(quiz, question1, 1)
        def quizQuestion2 = new QuizQuestion(quiz, question2, 1)

        def quizAnswer = new QuizAnswer(user1, quiz)
        def quizAnswer2 = new QuizAnswer(user1, quiz2)

        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        def questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion2, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        user1.addQuizAnswer(quizAnswer)

        answerService.concludeQuiz(user1, quiz.getId())
        answerService.concludeQuiz(user1, quiz2.getId())

        answerService.submitAnswer(user1, quiz.getId(), new StatementAnswerDto(questionAnswer))
        answerService.submitAnswer(user1, quiz2.getId(), new StatementAnswerDto(questionAnswer2))


        quizQuestionRepository.save(quizQuestion)
        quizQuestionRepository.save(quizQuestion2)

        quizAnswerRepository.save(quizAnswer)
        quizAnswerRepository.save(quizAnswer2)



    }

    def "the user exists, and get his answered questions" (){
        given: "userId"
        def userId = user1.getId()

        when:
        def result = userService.getAnsweredQuestions(userId)

        then: "check if result isn't null"
        result != null
        System.out.println(result)

        and:"result has 2 elements"
        result.size() == 2L

        and:"check if the first question is the correct one"
        result.get(0).getId() == question1.getId()

        and:"check if the first question is the correct one"
        result.get(1).getId() == question2.getId()

    }

    def "the user does not exist" () {
        given: "an invalid userId"
        def userId = -1

        when:
        userService.getAnsweredQuestions(userId)

        then:"an exception is thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND


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
