package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
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

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

@DataJpaTest
class RemoveTournamentServiceSpockTest extends Specification {

    static final String NAME_1 = "Rafael"
    static final String USERNAME_1 = "VivaRafael"
    static final User.Role ROLE = User.Role.STUDENT

    static final String NAME_2 = "Miguel"
    static final String USERNAME_2 = "miguelpsdias"

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
    def user2
    def userDto
    def tournamentDto
    def courseDto1
    def courseEx1
    def topicDto1
    def topicDto2
    def topicList1

    def setup() {

        user1 = userService.createUser(NAME_1, USERNAME_1, ROLE)
        userRepository.save(user1)

        user2 = userService.createUser(NAME_2, USERNAME_2, ROLE)
        userRepository.save(user2)

        courseDto1 = new CourseDto(COURSE_NAME, ACRONYM, ACADEMIC_TERM)
        courseEx1 = courseService.createTecnicoCourseExecution(courseDto1)

        topicDto1 = new TopicDto()
        topicDto2 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)

        topicDto1 = topicService.createTopic(courseEx1.getCourseId(), topicDto1)
        topicDto2 = topicService.createTopic(courseEx1.getCourseId(), topicDto2)
        topicList1 = new ArrayList<TopicDto>()
        topicList1.add(topicDto1)
        topicList1.add(topicDto2)


        userDto = new UserDto(user1)
        tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

    }

    def "delete a tournament with 0 students enrolled"() {
        given: "a tournament"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

        when:
        tournamentService.removeTournament(user1.getId(), tournamentDto.getId())

        then:
        tournamentRepository.findAll().size() == 0
        user1.getTournamentsCreatedByMe().size() == 0
        user1.getTournamentsEnrolled().size() == 0
    }

    def "delete a tournament with 2 students enrolled"() {

        given: "a tournament and 2 students enrolled"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())
        tournamentService.enrollTournament(user2.getId(), tournamentDto.getId())

        when:
        tournamentService.removeTournament(user1.getId(), tournamentDto.getId())

        then:
        tournamentRepository.findAll().size() == 0
        user1.getTournamentsCreatedByMe().size() == 0
        user1.getTournamentsEnrolled().size() == 0
        user2.getTournamentsEnrolled().size() == 0
    }

    def "the student that didnt create tries to delete the tournament"() {

        given: "a tournament and 2 students enrolled"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())
        tournamentService.enrollTournament(user2.getId(), tournamentDto.getId())

        when:
        tournamentService.removeTournament(user2.getId(), tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_DIDNT_CREATE_TOURNAMENT
        tournamentRepository.findAll().size() == 1
        user1.getTournamentsCreatedByMe().size() == 1
        user1.getTournamentsEnrolled().size() == 1
        user2.getTournamentsEnrolled().size() == 1
    }

    def "the student tries to delete a tournament that doesnt exist"() {

        when:
        tournamentService.removeTournament(user2.getId(),1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_FOUND
        tournamentRepository.findAll().size() == 0
        user1.getTournamentsCreatedByMe().size() == 0
        user1.getTournamentsEnrolled().size() == 0
        user2.getTournamentsEnrolled().size() == 0
    }

    def "the student tries to delete a tournament that does not exist"() {

        given: "a tournament closed"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())
        tournamentService.enrollTournament(user2.getId(), tournamentDto.getId())
        tournamentRepository.findAll().get(0).setIsClosed(true)

        when:
        tournamentService.removeTournament(user1.getId(),tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_IS_CLOSED
        tournamentRepository.findAll().size() == 1
        user1.getTournamentsCreatedByMe().size() == 1
        user1.getTournamentsEnrolled().size() == 1
        user2.getTournamentsEnrolled().size() == 1
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
