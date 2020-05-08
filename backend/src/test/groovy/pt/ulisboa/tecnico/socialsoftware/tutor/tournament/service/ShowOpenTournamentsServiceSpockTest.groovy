package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString
import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString

@DataJpaTest
class ShowOpenTournamentsServiceSpockTest extends Specification{

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
    def tournamentDto1
    def tournamentDto2
    def courseEx1
    def topicDto1
    def topicDto2
    def topicDto3
    def topicList1
    def topicList2

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
        topicDto3 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)
        topicDto3.setName(TOPIC_NAME3)

        def topic1 = new Topic(course, topicDto1)
        def topic2 = new Topic(course, topicDto2)
        def topic3 = new Topic(course, topicDto3)
        def question1 = new Question()
        question1.setKey(1)
        question1.setTitle("Question Title")
        question1.setContent("Question Content")
        question1.setCourse(course)
        question1.getTopics().add(topic1)
        question1.getTopics().add(topic2)
        question1.getTopics().add(topic3)
        topic1.getQuestions().add(question1)
        topic2.getQuestions().add(question1)
        topic3.getQuestions().add(question1)
        topicRepository.save(topic1)
        topicRepository.save(topic2)
        topicRepository.save(topic3)
        questionRepository.save(question1)

        topicList1 = new ArrayList<TopicDto>()
        topicList2 = new ArrayList<TopicDto>()
        topicList1.add(topicDto1)
        topicList1.add(topicDto2)
        topicList2.add(topicDto1)
        topicList2.add(topicDto2)
        topicList2.add(topicDto3)

        tournamentDto1 = new TournamentDto()
        tournamentDto1.setTopics(topicList1)
        tournamentDto1.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto1.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto1.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

        tournamentDto2 = new TournamentDto()
        tournamentDto2.setTopics(topicList1)
        tournamentDto2.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto2.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto2.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
    }

    def "a student creates a tournament and lists all the open tournaments"() {
        given: "student creates a tournament"
        tournamentDto1 = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(), tournamentDto1)

        when:
        List<TournamentDto> openTournaments = tournamentService.getAllOpenTournament()

        then:"the openTournaments have return the correct values "
        openTournaments.size() == 1
        def tournamentDto3 = openTournaments.get(0)
        topicList1.size() == tournamentDto3.getTopics().size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            tournamentDto3.getTopics().get(i) == topicList1.get(i)
        }
        tournamentDto3.getBeginningTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        tournamentDto3.getEndingTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
        tournamentDto3.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
    }

    def "a student creates several tournament and lists all the open tournaments"() {
        given: "student creates several tournaments"
        tournamentDto1 = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(),tournamentDto1)
        tournamentDto2 = tournamentService.createNewTournament(user2.getId(), courseEx1.getCourseExecutionId(),tournamentDto2)

        when:
        List<TournamentDto> openTournaments = tournamentService.getAllOpenTournament()

        then:"the openTournaments have return the correct values "
        openTournaments.size() == 2
        def tournamentDto3 = openTournaments.get(0)
        topicList1.size() == tournamentDto3.getTopics().size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            tournamentDto3.getTopics().get(i) == topicList1.get(i)
        }
        tournamentDto3.getBeginningTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        tournamentDto3.getEndingTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
        tournamentDto3.getNumberOfQuestions() == NUMBER_OF_QUESTIONS

        def tournamentDto4 = openTournaments.get(1)
        topicList1.size() == tournamentDto4.getTopics().size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            tournamentDto4.getTopics().get(i) == topicList1.get(i)
        }
        tournamentDto3.getBeginningTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        tournamentDto3.getEndingTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
        tournamentDto3.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
    }

    def "a student creates a tournament and it's closed and lists all the open tournaments"() {
        given: "student creates several tournament"
        tournamentDto1 = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(),tournamentDto1)
        tournamentRepository.findAll().get(0).setIsClosed(true)

        when:
        List<TournamentDto> openTournaments = tournamentService.getAllOpenTournament()

        then:"and it shows only the open Tournament"
        openTournaments.size() == 0
    }

    def "a student creates several tournaments and one is closed and one is opened and lists all the open tournaments"() {
        given: "student creates several tournament"
        tournamentDto1 = tournamentService.createNewTournament(user1.getId(), courseEx1.getCourseExecutionId(),tournamentDto1)
        tournamentRepository.findAll().get(0).setIsClosed(true)
        tournamentDto2 = tournamentService.createNewTournament(user2.getId(), courseEx1.getCourseExecutionId(),tournamentDto2)

        when:
        List<TournamentDto> openTournaments = tournamentService.getAllOpenTournament()

        then:"and it shows only the open Tournament"
        openTournaments.size() == 1
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
