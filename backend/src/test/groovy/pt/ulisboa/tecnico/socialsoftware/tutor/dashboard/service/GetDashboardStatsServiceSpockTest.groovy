package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.QuestionEvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetDashboardStatsServiceSpockTest extends Specification {
    public static final String STUDENT_NAME = "Student1"
    public static final String STUDENT_USERNAME = "ist123123123"
    public static final String TEACHER_NAME = "Teacher1"
    public static final String TEACHER_USERNAME = "ist789789789"

    static final String QUESTION_TITLE = "question1"
    static final String QUESTION_CONTENT = "o que é uma classe?"

    public static final String CLARIFICATION_REQUEST_TEXT = "Dúvida interessante"
    public static final String CLARIFICATION_REQUEST_TITLE = "Não percebi porque é que a opção correta é 1?."
    public static final String CLARIFICATION_TEXT = "Esclarecimento para a clarificação."

    @Autowired
    DashboardService dashboardService

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuizService quizService

    @Autowired
    AnswerService answerService

    @Autowired
    DiscussionService discussionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    DashboardRepository dashboardRepository

    def course
    def courseExecution
    def student
    def teacher
    def quiz
    def question1
    def question2
    def quizQuestion
    def quizAnswer
    def questionAnswer

    def setup() {
        course = new Course("Software Engineering", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "SE", "SE12345", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create student and add him to course
        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)

        // Create teacher and add him to course
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        userRepository.save(teacher)
        courseExecution.addUser(teacher)
        teacher.addCourse(courseExecution)

        // Create quiz and add it to course execution
        quiz = new Quiz()
        quiz.setKey(quizService.getMaxQuizKey()+1)
        quiz.setCourseExecution(courseExecution);
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quizRepository.save(quiz)

        // Create 2 questions and add them to course
        question1 = new Question()
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.setCourse(course)
        questionRepository.save(question1)

        question2 = new Question()
        question2.setTitle(QUESTION_TITLE)
        question2.setContent(QUESTION_CONTENT)
        question2.setCourse(course)
        questionRepository.save(question2)

        // Create 2 quiz questions with questions, 2 quiz answers for student and 2 question answer for the quiz questions
        quizQuestion = new QuizQuestion(quiz, question1, 1)
        quizAnswer = new QuizAnswer(student, quiz)
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)
        student.addQuizAnswer(quizAnswer)

        answerService.concludeQuiz(student, quiz.getId())
        answerService.submitAnswer(student, quiz.getId(), new StatementAnswerDto(questionAnswer))

        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)

        quizQuestion = new QuizQuestion(quiz, question2, 1)
        quizAnswer = new QuizAnswer(student, quiz)
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        quizAnswer.addQuestionAnswer(questionAnswer)
        student.addQuizAnswer(quizAnswer)

        answerService.concludeQuiz(student, quiz.getId())
        answerService.submitAnswer(student, quiz.getId(), new StatementAnswerDto(questionAnswer))

        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
    }

    def "get dashboard stats and check if stats are at initial state"() {
        given:
        def courseExecutionId = courseExecution.getId()
        when:
        def result = dashboardService.getDashboardStats(courseExecutionId)
        then:
        result != null
        def myDashboardStats = result.get(0)
        myDashboardStats != null
        myDashboardStats.getName() == STUDENT_NAME
        myDashboardStats.getUsername() == STUDENT_USERNAME
        myDashboardStats.getNumProposedQuestions() == 0
        myDashboardStats.getNumAcceptedQuestions() == 0
        myDashboardStats.getNumClarificationRequests() == 0
        myDashboardStats.getNumAnsweredClarificationRequests() == 0
        // TODO: test other stats
    }

    def "propose and accept questions, and check stats"() {
        given: "4 student questions are created"
        def studentQuestionId1 = createStudentQuestion(1).getId()
        def studentQuestionId2 = createStudentQuestion(2).getId()
        def studentQuestionId3 = createStudentQuestion(3).getId()
        createStudentQuestion(4)
        and: "some are accepted and others rejected"
        createQuestionEvaluation(studentQuestionId1, true)
        createQuestionEvaluation(studentQuestionId2, false)
        createQuestionEvaluation(studentQuestionId3, true) // first accept
        createQuestionEvaluation(studentQuestionId3, false) // then reject the same one

        when:
        def result = dashboardService.getDashboardStats(courseExecution.getId())

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats = result.get(0)
        myDashboardStats != null
        myDashboardStats.getNumProposedQuestions() == 4
        myDashboardStats.getNumAcceptedQuestions() == 1 // only student question 1
    }

    def "Make and answer a clarification request, and check stats"() {
        given: "2 clarification requests are created"
        def clarificationRequestId1 = createClarificationRequest(question1).getId()
        createClarificationRequest(question2)
        and: "1 is answered and the other is not"
        createClarification(clarificationRequestId1, CLARIFICATION_TEXT)

        when:
        def result = dashboardService.getDashboardStats(courseExecution.getId())

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats = result.get(0)
        myDashboardStats != null
        myDashboardStats.getNumClarificationRequests() == 2
        myDashboardStats.getNumAnsweredClarificationRequests() == 1 // only 1 request answered
    }

    // Auxiliary methods:

    def createStudentQuestion(key) {
        def studentQuestionDto = new StudentQuestionDto()

        def questionDto = new QuestionDto()
        questionDto.setKey(key)
        questionDto.setTitle("QUESTION_TITLE")
        questionDto.setContent("QUESTION_CONTENT")
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setCreationDate(DateHandler.toISOString(DateHandler.now()));

        def optionDto = new OptionDto()
        optionDto.setContent("OPTION_CONTENT")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        studentQuestionDto.setQuestionDto(questionDto)

        studentQuestionService.createStudentQuestion(course.getId(), student.getId(), studentQuestionDto)
    }

    def createQuestionEvaluation(studentQuestionId, isApproved) {
        def questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification("JUSTIFICATION")
        questionEvaluationDto.setApproved(isApproved)

        studentQuestionService.createQuestionEvaluation(teacher.getId(), studentQuestionId, questionEvaluationDto)
    }

    def createClarificationRequest(question) {
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_REQUEST_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_REQUEST_TEXT)
        clarificationRequestDto.setUserId(student.getId())

        discussionService.submitClarificationRequest(question.getId(), clarificationRequestDto)
    }

    def createClarification(clarificationRequestId, clarificationText){
        def clarificationDto = new ClarificationDto()
        clarificationDto.setText(clarificationText)
        clarificationDto.setUserId(teacher.getId())

        discussionService.createClarification(clarificationRequestId, clarificationDto)
    }


    @TestConfiguration
    static class DashboardServiceImplTestContextConfiguration {

        @Bean
        DashboardService dashboardService() {
            return new DashboardService()
        }

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
        
        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }
    }
}
