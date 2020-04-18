package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

@DataJpaTest
class GetOpenTournamentsServiceSpockPerfomanceTest extends Specification{

    static final String NAME_1 = "Miguel"
    static final String USERNAME_1 = "miguelpsdias"
    static final String NAME_2 = "Demo-Student"
    static final String USERNAME_2 = "Demo-Student"
    static final User.Role ROLE = User.Role.STUDENT

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
    }



    def "performance testing to get all open tournaments"() {

        given:"A TournamentDto where 500 are opened and 500 are closed"
        tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

        1.upto(1, {

            tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        })

        def tournaments = tournamentRepository.findAll()
        for (Tournament tournament: tournaments) {

            tournament.setClosed(true)
        }

        1.upto(1, {
            tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        })

        when:
        1.upto(1, {
            tournamentService.getAllOpenTournament()
        })

        then:
        true
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
