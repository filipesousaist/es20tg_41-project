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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString
import static pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler.toISOString
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.EMPTY_TOPIC
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.END_BEFORE_BEGINS
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_COURSE_EXECUTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_QUESTIONS_NUMBER
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS_TOURNAMENT
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

    static final int NUMBEROFQUESTIONS = 1

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
    @Autowired
    CourseExecutionRepository courseExecutionRepository
    @Autowired
    QuestionRepository questionRepository
    @Autowired
    TopicRepository topicRepository

    def student
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

        def course = new Course(COURSE_NAME1, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM1, ACADEMIC_TERM1, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        courseEx1 = new CourseDto(courseExecution)
        courseExecution.addUser(student)

        def course2 = new Course(COURSE_NAME2, Course.Type.TECNICO)
        courseRepository.save(course2)
        def courseExecution2 = new CourseExecution(course, ACRONYM2, ACADEMIC_TERM2, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)
        courseEx2 = new CourseDto(courseExecution2)
        courseExecution2.addUser(student)

        topicDto1 = new TopicDto()
        topicDto2 = new TopicDto()
        topicDto3 = new TopicDto()
        topicDto1.setName(TOPIC_NAME1)
        topicDto2.setName(TOPIC_NAME2)
        topicDto3.setName(TOPIC_NAME3)

        def topic1 = new Topic(course, topicDto1)
        def topic2 = new Topic(course, topicDto2)
        def topic3 = new Topic(course2, topicDto3)
        def question1 = new Question()
        question1.setKey(1)
        question1.setTitle("Question Title")
        question1.setContent("Question Content")
        question1.setCourse(course)
        def question2 = new Question()
        question2.setKey(2)
        question2.setTitle("Question Title")
        question2.setContent("Question Content")
        question2.setCourse(course2)
        question1.getTopics().add(topic1)
        question1.getTopics().add(topic2)
        question2.getTopics().add(topic3)
        topic1.getQuestions().add(question1)
        topic2.getQuestions().add(question1)
        topic3.getQuestions().add(question2)
        topicRepository.save(topic1)
        topicRepository.save(topic2)
        topicRepository.save(topic3)
        questionRepository.save(question1)
        questionRepository.save(question2)

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
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        def result = tournamentService.createNewTournament(student.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

        then: "the returned data are correct"

        topicList1.size() == result.getTopics().size()
        for (int i = 0 ; i != topicList1.size() ; i++) {
            result.getTopics().get(i) == topicList1.get(i)
        }
        result.getBeginningTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1))
        result.getEndingTime() == toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2))
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
        tournament.getCourseExecution().getCourse().getName() == COURSE_NAME1
        tournament.getCourseExecution().getAcronym() == ACRONYM1
        tournament.getCourseExecution().getAcademicTerm() == ACADEMIC_TERM1
    }

    @Unroll
    def "invalid arguments: numberOfQuestions=#numberOfQuestions || errorMessage=#errorMessage "() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(numberOfQuestions)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:

        numberOfQuestions || errorMessage
        0                 || INVALID_QUESTIONS_NUMBER
        -1                || INVALID_QUESTIONS_NUMBER
        -9                || INVALID_QUESTIONS_NUMBER
        3                 || NOT_ENOUGH_QUESTIONS_TOURNAMENT
    }

    def "A non existent course execution"() {

        given: "a student and a non existent course"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
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
        tournamentDto.setTopics(topicList2)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == NO_SUCH_TOPIC
    }

    def "a tournament ending date happens before the beginning date"() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTopics(topicList1)
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == END_BEFORE_BEGINS
    }

    def "Empty topic List"() {

        given: "a student"
        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTopics(new ArrayList<TopicDto>())
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(student.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

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
        tournamentDto.setTopics(topicList1)
        tournamentDto.setBeginningTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR1, MINUTE1)))
        tournamentDto.setEndingTime(toISOString(LocalDateTime.of(YEAR, MONTH, DAY, HOUR2, MINUTE2)))
        tournamentDto.setNumberOfQuestions(NUMBEROFQUESTIONS)

        when:
        tournamentService.createNewTournament(teacher.getId(), courseEx1.getCourseExecutionId(), tournamentDto)

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
