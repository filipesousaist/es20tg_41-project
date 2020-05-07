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
    public static final String STUDENT_NAME_2 = "Student2"
    public static final String STUDENT_USERNAME_2 = "ist12"
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
    def student1
    def student2
    def teacher

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
        student2 = new User(STUDENT_NAME_2, STUDENT_USERNAME_2, 3, User.Role.STUDENT)
        userRepository.save(student2)
        courseExecution.addUser(student2)
        student2.addCourse(courseExecution)

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
        def result = dashboardService.getDashboardStats(student1.getId(), courseExecutionId)
        then:
        result != null
        def myDashboardStats = result.get(1)
        myDashboardStats != null
        myDashboardStats.getName() == STUDENT_NAME
        myDashboardStats.getUsername() == STUDENT_USERNAME
        myDashboardStats.getNumProposedQuestions() == 0
        myDashboardStats.getNumAcceptedQuestions() == 0
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

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats = result.get(1)
        myDashboardStats != null
        myDashboardStats.getNumProposedQuestions() == 4
        myDashboardStats.getNumAcceptedQuestions() == 1 // only student question 1
    }

    def "List student1 and course students stats"() {
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

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats1 = result.get(1)
        // student1 stats
        myDashboardStats1 != null
        myDashboardStats1.getNumProposedQuestions() == 3
        myDashboardStats1.getNumAcceptedQuestions() == 1
        // student2 stats visible in student1 dashboard
        def myDashboardStats2 = result.get(0)
        myDashboardStats2 != null
        myDashboardStats2.getNumProposedQuestions() == -1
        myDashboardStats2.getNumAcceptedQuestions() == 2

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

        then: "student's dashboard stats contain the expected values"
        result != null
        def myDashboardStats1 = result.get(1)
        // student1 stats
        myDashboardStats1 != null
        myDashboardStats1.getNumProposedQuestions() == 3
        myDashboardStats1.getNumAcceptedQuestions() == 1
        // student2 stats visible in student1 dashboard
        def myDashboardStats2 = result.get(0)
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
