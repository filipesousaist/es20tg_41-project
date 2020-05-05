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
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DashboardRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetDashboardStatsServiceSpockTest extends Specification {
    @Autowired
    DashboardService dashboardService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DashboardRepository dashboardRepository

    def setup() {
        def course = new Course("Software Engineering", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "SE", "SE12345", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user = new User("User", "ist123123123", 1, User.Role.STUDENT)
        userRepository.save(user)

        courseExecution.addUser(user)
    }

    def "get dashboard stats"() {
        given:
        def courseExecutionId = courseExecutionRepository.findAll().get(0).getId()
        when:
        def result = dashboardService.getDashboardStats(courseExecutionId)
        then:
        result != null
        def returnedDashboardStats = result.get(0)
        returnedDashboardStats != null
        returnedDashboardStats.getName() == "User"
        returnedDashboardStats.getUsername() == "ist123123123"
    }

    @TestConfiguration
    static class DashboardServiceImplTestContextConfiguration {

        @Bean
        DashboardService dashboardService() {
            return new DashboardService()
        }
    }
}
