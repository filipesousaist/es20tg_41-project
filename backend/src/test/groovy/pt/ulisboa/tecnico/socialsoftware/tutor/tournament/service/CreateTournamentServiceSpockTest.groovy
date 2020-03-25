package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.EMPTY_TOPIC
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.END_BEFORE_BEGINS
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_COURSE_EXECUTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_QUESTIONS_NUMBER
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_SUCH_TOPIC
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_NOT_A_STUDENT

@DataJpaTest
class CreateTournamentServiceSpockTest extends Specification {

    static final String NAME = "Joao"
    static final String USERNAME = "Joao1999"
    static final User.Role ROLE = User.Role.STUDENT

    static final String NAME_2 = "Rafael"
    static final String USERNAME_2 = "VivaRafael"

    static final int HOUR1 = 8
    static final int HOUR2 = 12
    static final int MINUTE1 = 0
    static final int MINUTE2 = 30
    static final int DAY = 2
    static final Month MONTH = Month.MARCH
    static final int YEAR = 2020

    static final int NUMBEROFQUESTIONS = 8

    static final String COURSE_NAME1 = "Software Architecture"
    static final String COURSE_NAME2 = "Artificial Intelligence"
    static final String ACRONYM1 = "SA"
    static final String ACADEMIC_TERM1 = "1º Semestre"
    static final String ACRONYM2 = "AI"
    static final String ACADEMIC_TERM2 = "2º Semestre"
    static final String TOPIC_NAME1 = "Algorithms"
    static final String TOPIC_NAME2 = "Machine Learning"
    static final String TOPIC_NAME3 = "Security"

    static final int ID = 1000

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

    def student
    def courseDto1
    def courseDto2
    def courseEx1
    def courseEx2
    def topicDto1
    def topicDto2
    def topicDto3
    def topicList1
    def topicList2

    def setup() {
        student = userService.createUser(NAME, USERNAME, ROLE)
        userRepository.save(student)

        courseDto1 = new CourseDto(COURSE_NAME1, ACRONYM1, ACADEMIC_TERM1)
        courseEx1 = courseService.createTecnicoCourseExecution(courseDto1)

        courseDto2 = new CourseDto(COURSE_NAME2, ACRONYM2, ACADEMIC_TERM2)
        courseEx2 = courseService.createTecnicoCourseExecution(courseDto2)

        topicDto1 = new TopicDto()
        topicDto2 = new TopicDto()
        topicDto3 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)
        topicDto3.setName(TOPIC_NAME3)

        topicDto1 = topicService.createTopic(courseEx1.getCourseId(), topicDto1)
        topicDto2 = topicService.createTopic(courseEx1.getCourseId(), topicDto2)
        topicDto3 = topicService.createTopic(courseEx2.getCourseId(), topicDto3)
        topicList1 = new ArrayList<TopicDto>()
        topicList2 = new ArrayList<TopicDto>()
        topicList1.add(topicDto1)
        topicList1.add(topicDto2)
        topicList2.add(topicDto1)
        topicList2.add(topicDto2)
        topicList2.add(topicDto3)
    }

    def "a student exists and he creates a new tournament"() {
        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        def result = tournamentService.createNewTournament(student.getId(), courseEx1.getCourseId(), tournamentDto)

        then: "the returned data are correct"

        topicList1.size() == result.getTitles().size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            result.getTitles().get(i) == topicList1.get(i)
        }
        result.getBeginningTime().equals(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        result.getEndingTime().equals(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        result.getNumberOfQuestions() == NUMBEROFQUESTIONS
        and: 'is in the database'
        tournamentRepository.findAll().size() == 1
        def tournament = tournamentRepository.findAll().get(0)
        tournament != null
        and: 'has the correct value'

        def topics = tournament.getTitles()
        def listDto = new ArrayList<>()
        for (Topic topic : topics) {
            listDto.add(new TopicDto(topic))
        }
        
        topicList1.size() == listDto.size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            listDto.get(i) == topicList1.get(i)
        }

        tournament.getBeginningTime().isEqual(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        tournament.getEndingTime().isEqual(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
        tournament.getNumberOfQuestions() == NUMBEROFQUESTIONS
    }

    @Unroll
    def "invalid arguments: numberOfQuestions=#numberOfQuestions || errorMessage=#errorMessage "() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(numberOfQuestions)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:

        numberOfQuestions || errorMessage
        0                 || INVALID_QUESTIONS_NUMBER
        -1                || INVALID_QUESTIONS_NUMBER
        -9                || INVALID_QUESTIONS_NUMBER
    }

    def "A non existent course execution"() {

        given: "a student and a non existent course"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), ID,  tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == INVALID_COURSE_EXECUTION
    }

    def "No topic in the course"() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList2)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == NO_SUCH_TOPIC
    }

    def "a tournament ending date happens before the beginning date"() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == END_BEFORE_BEGINS
    }

    def "Empty topic List"() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(new ArrayList<TopicDto>())
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == EMPTY_TOPIC
    }

    def "A teacher tries to create a tournament"() {
        given: "a teacher"
        def teacher = userService.createUser(NAME_2, USERNAME_2, User.Role.TEACHER)
        userRepository.save(teacher)
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitles(topicList1)
        tournamentDto.setBeginningTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setEndingTime(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(teacher.getId(), courseEx1.getCourseId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_IS_NOT_A_STUDENT
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
