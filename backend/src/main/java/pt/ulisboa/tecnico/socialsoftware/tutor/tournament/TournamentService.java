package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TournamentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createNewTournament(Integer userId, Integer courseExId, TournamentDto tournamentDto){

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (!user.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime begin = LocalDateTime.parse(tournamentDto.getBeginningTime(), formatter);
        LocalDateTime end = LocalDateTime.parse(tournamentDto.getEndingTime(), formatter);
        int numberOfQuestions = tournamentDto.getNumberOfQuestions();

        CourseExecution courseEx = courseExecutionRepository.findById(courseExId).orElseThrow(() -> new TutorException(INVALID_COURSE_EXECUTION));
        List<Topic> topics = getTopics(tournamentDto, courseEx);
        Tournament tournament = new Tournament(user, topics, begin, end, numberOfQuestions, courseEx);
        user.addTournamentCreatedByMe(tournament);

        entityManager.persist(tournament);
        courseEx.addTournament(tournament);
        for (Topic topic : topics) {
            topic.addTournament(tournament);
        }

        return new TournamentDto(tournament);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    List<Topic> getTopics(TournamentDto tournamentDto, CourseExecution courseEx) {
        List<TopicDto> titlesDto = tournamentDto.getTitles();
        if (titlesDto == null || titlesDto.isEmpty()) {
            throw new TutorException(EMPTY_TOPIC);
        }
        List<Topic> topics = new ArrayList<>();
        for (TopicDto dto : titlesDto) {
            Topic topic = topicRepository.optionalFindTopicByName(courseEx.getCourse().getId(), dto.getName()).orElseThrow(() -> new TutorException(NO_SUCH_TOPIC));
            topics.add(topic);

        }
        return topics;
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollTournament(Integer studentId, Integer tournamentId) {

        if (tournamentId == null) {
            throw new TutorException(TOURNAMENT_NOT_FOUND);
        }

        if (studentId == null) {
            throw new TutorException(USER_NOT_FOUND, studentId);
        }

        User user = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (!user.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        }

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));

        tournament.addStudentEnrolled(user);
        user.addTournamentEnrolled(tournament);
        return new TournamentDto(tournament);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));

        tournament.remove();

        entityManager.remove(tournament);

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getAllOpenTournament() {
        return tournamentRepository.findAll().stream()
                .filter(tournament -> !tournament.getClosed())
                .map(TournamentDto::new)
                /*
                .sorted(Comparator
                        .comparing(TournamentDto::getCourseExecutionDtoName)
                        .thenComparing(TournamentDto::getEndingTime)
                        .thenComparing(TournamentDto::getBeginningTime)
                        .thenComparing(TournamentDto::getNumberOfQuestions))
                 */
                .collect(Collectors.toList());
    }
}