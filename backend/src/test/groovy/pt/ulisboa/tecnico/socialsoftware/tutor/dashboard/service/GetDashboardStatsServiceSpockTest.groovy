package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
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

    @Autowired
    DashboardService dashboardService

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DashboardRepository dashboardRepository

    def course
    def courseExecution
    def student
    def teacher

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
    }
}
