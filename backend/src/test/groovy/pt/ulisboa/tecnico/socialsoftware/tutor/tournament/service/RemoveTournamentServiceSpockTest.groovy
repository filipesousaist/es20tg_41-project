package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString
import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString

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
    static final int NUMBER_OF_QUESTIONS = 1
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
    @Autowired
    CourseExecutionRepository courseExecutionRepository
    @Autowired
    QuestionRepository questionRepository
    @Autowired
    TopicRepository topicRepository

    def user1
    def user2
    def userDto
    def tournamentDto
    def courseEx1
    def topicDto1
    def topicDto2
    def topicList1

    def setup() {

        user1 = userService.createUser(NAME_1, USERNAME_1, ROLE)
        userRepository.save(user1)

        user2 = userService.createUser(NAME_2, USERNAME_2, ROLE)
        userRepository.save(user2)

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        courseEx1 = new CourseDto(courseExecution)
        courseExecution.addUser(user1)
        courseExecution.addUser(user2)

        topicDto1 = new TopicDto()
        topicDto2 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)

        def topic1 = new Topic(course, topicDto1)
        def topic2 = new Topic(course, topicDto2)
        def question1 = new Question()
        question1.setKey(1)
        question1.setTitle("Question Title")
        question1.setContent("Question Content")
        question1.setCourse(course)
        question1.getTopics().add(topic1)
        question1.getTopics().add(topic2)
        topic1.getQuestions().add(question1)
        topic2.getQuestions().add(question1)
        topicRepository.save(topic1)
        topicRepository.save(topic2)
        questionRepository.save(question1)

        topicList1 = new ArrayList<TopicDto>()
        topicList1.add(topicDto1)
        topicList1.add(topicDto2)


        userDto = new UserDto(user1)
        tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
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

    def "delete a tournament with 1 students enrolled"() {

        given: "a tournament and 2 students enrolled"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())

        when:
        tournamentService.removeTournament(user1.getId(), tournamentDto.getId())

        then:
        tournamentRepository.findAll().size() == 0
        user1.getTournamentsCreatedByMe().size() == 0
        user1.getTournamentsEnrolled().size() == 0
        user2.getTournamentsEnrolled().size() == 0
    }

    def "the student that didnt create tries to delete the tournament"() {

        given: "a tournament and 1 students enrolled"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())

        when:
        tournamentService.removeTournament(user2.getId(), tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_DIDNT_CREATE_TOURNAMENT
    }

    def "the student tries to delete a tournament that doesnt exist"() {

        when:
        tournamentService.removeTournament(user2.getId(),1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_FOUND
    }

    def "the student tries to delete a tournament that does not exist"() {

        given: "a tournament closed"
        tournamentDto = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto)
        tournamentService.enrollTournament(user1.getId(), tournamentDto.getId())
        tournamentRepository.findAll().get(0).setIsClosed(true)

        when:
        tournamentService.removeTournament(user1.getId(),tournamentDto.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_IS_CLOSED
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
