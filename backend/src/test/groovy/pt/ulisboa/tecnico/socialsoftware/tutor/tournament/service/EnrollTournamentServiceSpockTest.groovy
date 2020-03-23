package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime
import java.time.Month
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_ALREADY_ENROLLED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_IS_CLOSED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_NOT_A_STUDENT


@DataJpaTest
class EnrollTournamentServiceSpockTest extends Specification {

    static final String NAME_1 = "Miguel"
    static final String USERNAME_1 = "miguelpsdias"
    static final User.Role ROLE = User.Role.STUDENT

    static final String NAME_2 = "Rafael"
    static final String USERNAME_2 = "VivaRafael"

    static final int HOUR1 = 8
    static final int HOUR2 = 12
    static final int MINUTE1 = 0
    static final int MINUTE2 = 30
    static final int DAY = 2
    static final Month MONTH = Month.APRIL
    static final int YEAR = 2020
    static final int NUMBER_OF_QUESTIONS = 5
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "SA"
    static final String ACADEMIC_TERM = "1º Semestre"
    static final String TOPIC_NAME1 = "Algorithms"
    static final String TOPIC_NAME2 = "Machine Learning"
    static final String TOPIC_NAME3 = "Security"


    @Autowired
    TournamentService tournamentService
    @Autowired
    UserService userService
    @Autowired
    CourseService courseService
    @Autowired
    TopicService topicService

    @Autowired
    UserRepository userRepository
    @Autowired
    TournamentRepository tournamentRepository
    @Autowired
    CourseRepository courseRepository

    def user1
    def userDto
    def tournamentDto
    def courseDto1
    def courseEx1
    def topicDto1
    def topicDto2
    def topicDto3
    def topicList1
    def topicList2

    def setup() {

        user1 = userService.createUser(NAME_1, USERNAME_1, ROLE)
        userRepository.save(user1)

        courseDto1 = new CourseDto(COURSE_NAME, ACRONYM, ACADEMIC_TERM)
        courseEx1 = courseService.createTecnicoCourseExecution(courseDto1)

        topicDto1 = new TopicDto()
        topicDto2 = new TopicDto()
        topicDto3 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)
        topicDto3.setName(TOPIC_NAME3)

        topicDto1 = topicService.createTopic(courseEx1.getCourseId(), topicDto1)
        topicDto2 = topicService.createTopic(courseEx1.getCourseId(), topicDto2)
        topicDto3 = topicService.createTopic(courseEx1.getCourseId(), topicDto3)
        topicList1 = new ArrayList<TopicDto>()
        topicList2 = new ArrayList<TopicDto>()
        topicList1.add(topicDto1)
        topicList1.add(topicDto2)
        topicList2.add(topicDto1)
        topicList2.add(topicDto2)
        topicList2.add(topicDto3)




        userDto = new UserDto(user1)
        tournamentDto = new TournamentDto()
        tournamentDto.setUserDto(userDto)
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setCourseExecutionDto(courseEx1)

    }

    def "a student exists and enrolls in a tournament"() {
        given: "student creates a tournament"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), tournamentDto)

        when:
        tournamentService.enrollTournament(userDto.getId(), tournamentDto)

        then:"is in the database"
        tournamentRepository.findAll().size() == 1
        def tournament = tournamentRepository.findAll().get(0)
        tournament != null
        and:"is enrolled"
        def student = tournament.getStudentsEnrolled().iterator().next()
        student.getName() == NAME_1
        student.getRole() == ROLE
        student.getUsername() == USERNAME_1

    }

    def "a student enrolling in a tournament that doesn't exists"() {
        when:
        tournamentService.enrollTournament(userDto.getId(), tournamentDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "a student enrolls in a tournament when he's already enrolled"() {
        given: "student creates a tournament"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), tournamentDto)
        when:
        tournamentService.enrollTournament(userDto.getId(), tournamentDto)
        tournamentService.enrollTournament(userDto.getId(), tournamentDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == STUDENT_ALREADY_ENROLLED
    }


    def "a student enrolls in a tournament where the ending date already passed"() {
        given: "student creates a tournament"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), tournamentDto)
        tournamentRepository.findAll().get(0).setClosed(true)

        when:
        tournamentService.enrollTournament(userDto.getId(), tournamentDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == TOURNAMENT_IS_CLOSED
    }

    def "a teacher tries to enroll in a tournament"() {
        given: "student creates a tournament"
        def user2 = userService.createUser(NAME_2, USERNAME_2, User.Role.TEACHER)
        userRepository.save(user2)
        tournamentDto = tournamentService.createNewTournament(user1.getId(), tournamentDto)
        def user2Dto = new UserDto(user2)

        when:
        tournamentService.enrollTournament(user2Dto.getId(), tournamentDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_IS_NOT_A_STUDENT
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService(){
            return new TournamentService()
        }

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        CourseService courseService() {
            return new CourseService()
        }

        @Bean
        TopicService topicService() {
            return new TopicService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}