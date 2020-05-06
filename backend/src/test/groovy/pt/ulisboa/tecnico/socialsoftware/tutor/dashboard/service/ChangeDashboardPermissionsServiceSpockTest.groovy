package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ChangeDashboardPermissionsServiceSpockTest extends Specification {
    public static final String STUDENT_NAME = "Student1"
    public static final String STUDENT_USERNAME = "ist123123123"

    @Autowired
    DashboardService dashboardService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def student

    def setup() {
        def course = new Course("Software Engineering", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "SE", "SE12345", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create student and add him to course
        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
    }

    def "get, change and set permissions"() {
        given: "a student id"
        def studentId = student.getId()

        when: "permissions are got from the database, changed and resubmitted"
        def dashboardPermissionsDto = dashboardService.getDashboardPermissions(studentId)
        dashboardPermissionsDto.setShowNumProposedQuestions(false)
        dashboardPermissionsDto = dashboardService.setDashboardPermissions(studentId, dashboardPermissionsDto)

        then:
        dashboardPermissionsDto.getShowNumProposedQuestions() == false
        dashboardPermissionsDto.getShowNumAcceptedQuestions() == true
    }

    @TestConfiguration
    static class DashboardServiceImplTestContextConfiguration {
        @Bean
        DashboardService dashboardService() {
            return new DashboardService()
        }
    }
}
