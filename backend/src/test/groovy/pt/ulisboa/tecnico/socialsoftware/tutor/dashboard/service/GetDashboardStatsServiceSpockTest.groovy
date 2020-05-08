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
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardStatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class GetDashboardStatsServiceSpockTest extends Specification {
    public static final String STUDENT_NAME = "Student1"
    public static final String STUDENT_USERNAME = "ist123123123"
    public static final String STUDENT_NAME_2 = "Student2"
    public static final String STUDENT_USERNAME_2 = "ist12"
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
    def student1
    def student2
    def teacher
    def quiz
    def question1
    def question2
    def quizQuestion1
    def quizQuestion2

    def setup() {
        course = new Course("Software Engineering", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "SE", "SE12345", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create student1 and add him to course
        student1 = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student1)
        courseExecution.addUser(student1)
        student1.addCourse(courseExecution)

        //Create student2 and add him to course
        student2 = new User(STUDENT_NAME_2, STUDENT_USERNAME_2, 2, User.Role.STUDENT)
        userRepository.save(student2)
        courseExecution.addUser(student2)
        student2.addCourse(courseExecution)

        // Create teacher and add him to course
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 3, User.Role.TEACHER)
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

        // Create 2 quiz questions with questions, 2 quiz answers for 2 students and 2 question answer for the quiz questions
        quizQuestion1 = new QuizQuestion(quiz, question1, 1)
        def quizAnswer1 = new QuizAnswer(student1, quiz)
        def quizAnswer2 = new QuizAnswer(student2, quiz)
        def questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion1, 1)
        def questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion1, 1)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        student1.addQuizAnswer(quizAnswer1)
        student2.addQuizAnswer(quizAnswer2)

        answerService.concludeQuiz(student1, quiz.getId())
        answerService.submitAnswer(student1, quiz.getId(), new StatementAnswerDto(questionAnswer1))
        answerService.concludeQuiz(student2, quiz.getId())
        answerService.submitAnswer(student2, quiz.getId(), new StatementAnswerDto(questionAnswer2))

        quizQuestionRepository.save(quizQuestion1)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswerRepository.save(quizAnswer2)

        quizQuestion2 = new QuizQuestion(quiz, question2, 1)
        def quizAnswer3 = new QuizAnswer(student1, quiz)
        def quizAnswer4 = new QuizAnswer(student2, quiz)
        def questionAnswer3 = new QuestionAnswer(quizAnswer3, quizQuestion2, 1)
        def questionAnswer4 = new QuestionAnswer(quizAnswer4, quizQuestion2, 1)
        quizAnswer3.addQuestionAnswer(questionAnswer3)
        quizAnswer4.addQuestionAnswer(questionAnswer4)
        student1.addQuizAnswer(quizAnswer3)
        student2.addQuizAnswer(quizAnswer4)

        answerService.concludeQuiz(student1, quiz.getId())
        answerService.submitAnswer(student1, quiz.getId(), new StatementAnswerDto(questionAnswer3))
        answerService.concludeQuiz(student2, quiz.getId())
        answerService.submitAnswer(student2, quiz.getId(), new StatementAnswerDto(questionAnswer4))

        quizQuestionRepository.save(quizQuestion2)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswerRepository.save(quizAnswer2)
    }

    def "get dashboard stats and check if stats are at initial state"() {
        given:
        def courseExecutionId = courseExecution.getId()
        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecutionId)
        result.sort {a, b -> a.getUserId() - b.getUserId()}
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
        def studentQuestionId1 = createStudentQuestion(1, student1).getId()
        def studentQuestionId2 = createStudentQuestion(2, student1).getId()
        def studentQuestionId3 = createStudentQuestion(3, student1).getId()
        createStudentQuestion(4, student1)
        and: "some are accepted and others rejected"
        createQuestionEvaluation(studentQuestionId1, true)
        createQuestionEvaluation(studentQuestionId2, false)
        createQuestionEvaluation(studentQuestionId3, true) // first accept
        createQuestionEvaluation(studentQuestionId3, false) // then reject the same one

        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecution.getId())
        result.sort {a, b -> a.getUserId() - b.getUserId()}

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats = result.get(0)
        myDashboardStats != null
        myDashboardStats.getNumProposedQuestions() == 4
        myDashboardStats.getNumAcceptedQuestions() == 1 // only student question 1
    }


    def "Make and answer a clarification request, and check stats"() {
        given: "2 clarification requests are created"
        def clarificationRequestId1 = createClarificationRequest(question1.getId(), student1).getId()
        createClarificationRequest(question2.getId(), student1)
        and: "1 is answered and the other is not"
        createClarification(clarificationRequestId1, CLARIFICATION_TEXT)

        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecution.getId())
        result.sort {a, b -> a.getUserId() - b.getUserId()}

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats = result.get(0)
        myDashboardStats != null
        myDashboardStats.getNumClarificationRequests() == 2
        myDashboardStats.getNumAnsweredClarificationRequests() == 1 // only 1 request answered
    }

    def "List student1 and course students student question stats"() {
        given: "3 student question for student1"
        def studentQuestionId1 = createStudentQuestion(1, student1).getId()
        def studentQuestionId2 = createStudentQuestion(2, student1).getId()
        def studentQuestionId3 = createStudentQuestion(3, student1).getId()
        and: "some are accepted and others rejected"
        createQuestionEvaluation(studentQuestionId1, true)
        createQuestionEvaluation(studentQuestionId2, false)
        createQuestionEvaluation(studentQuestionId3, false)
        and: "3 student question for student2"
        def studentQuestionId4 = createStudentQuestion(4, student2).getId()
        def studentQuestionId5 = createStudentQuestion(5, student2).getId()
        def studentQuestionId6 = createStudentQuestion(6, student2).getId()
        and: "some are accepted and others rejected"
        createQuestionEvaluation(studentQuestionId4, true)
        createQuestionEvaluation(studentQuestionId5, false)
        createQuestionEvaluation(studentQuestionId6, true)
        and: "make student2 stats about proposed questions private"
        student2.getDashboardStats().setShowNumProposedQuestions(false)

        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecution.getId())
        result.sort {a, b -> a.getUserId() - b.getUserId()}

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats1 = result.get(0)
        // student1 stats
        myDashboardStats1 != null
        myDashboardStats1.getNumProposedQuestions() == 3
        myDashboardStats1.getNumAcceptedQuestions() == 1
        // student2 stats visible in student1 dashboard
        def myDashboardStats2 = result.get(1)
        myDashboardStats2 != null
        myDashboardStats2.getNumProposedQuestions() == -1
        myDashboardStats2.getNumAcceptedQuestions() == 2

    }

    def "List student1 and course students discussion stats"() {
        given: "2 clarification requests are created by student1"
        def clarificationRequestId1 = createClarificationRequest(question1.getId(), student1).getId()
        createClarificationRequest(question2.getId(), student1)
        and: "1 is answered and the other is not"
        createClarification(clarificationRequestId1, CLARIFICATION_TEXT)
        and: "2 clarification requests are created by student2"
        def clarificationRequestId2 = createClarificationRequest(question1.getId(), student2).getId()
        def clarificationRequestId3 = createClarificationRequest(question2.getId(), student2).getId()
        and: "both are answered"
        createClarification(clarificationRequestId2, CLARIFICATION_TEXT)
        createClarification(clarificationRequestId3, CLARIFICATION_TEXT)
        and: "student2 answered clarification requests private"
        student2.getDashboardStats().setShowNumAnsweredClarificationRequests(false)
        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecution.getId())
        result.sort {a, b -> a.getUserId() - b.getUserId()}

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats1 = result.get(0)
        // student1 stats
        myDashboardStats1 != null
        myDashboardStats1.getNumClarificationRequests() == 2
        myDashboardStats1.getNumAnsweredClarificationRequests() == 1 // only 1 request answered
        // student2 stats visible in student1 dashboard
        result != null
        def myDashboardStats2 = result.get(1)
        myDashboardStats2 != null
        myDashboardStats2.getNumClarificationRequests() == 2
        myDashboardStats2.getNumAnsweredClarificationRequests() == -1
    }

    def "make all stats from student1 private and list them only for him"() {
        given: "3 student question for student1"
        def studentQuestionId1 = createStudentQuestion(1, student1).getId()
        def studentQuestionId2 = createStudentQuestion(2, student1).getId()
        def studentQuestionId3 = createStudentQuestion(3, student1).getId()
        and: "some are accepted and others rejected"
        createQuestionEvaluation(studentQuestionId1, true)
        createQuestionEvaluation(studentQuestionId2, false)
        createQuestionEvaluation(studentQuestionId3, false)
        and: "make all stats private"
        student1.getDashboardStats().setShowNumProposedQuestions(false)
        student1.getDashboardStats().setShowNumAcceptedQuestions(false)
        and: "3 student question for student2"
        createStudentQuestion(4, student2).getId()
        createStudentQuestion(5, student2).getId()
        createStudentQuestion(6, student2).getId()

        when:
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecution.getId())
        result.sort {a, b -> a.getUserId() - b.getUserId()}

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats1 = result.get(0)
        // student1 stats
        myDashboardStats1 != null
        myDashboardStats1.getNumProposedQuestions() == 3
        myDashboardStats1.getNumAcceptedQuestions() == 1
        // student2 stats visible in student1 dashboard
        def myDashboardStats2 = result.get(1)
        myDashboardStats2 != null
        myDashboardStats2.getNumProposedQuestions() == 3
        myDashboardStats2.getNumAcceptedQuestions() == 0

    }

    // Auxiliary methods:

    def createStudentQuestion(key, student) {
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

    def createClarificationRequest(questionId, student) {
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setTitle(CLARIFICATION_REQUEST_TITLE)
        clarificationRequestDto.setText(CLARIFICATION_REQUEST_TEXT)
        clarificationRequestDto.setUserId(student.getId())

        discussionService.submitClarificationRequest(questionId, clarificationRequestDto)
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
        UserService userService() {
            return new UserService()
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
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}
