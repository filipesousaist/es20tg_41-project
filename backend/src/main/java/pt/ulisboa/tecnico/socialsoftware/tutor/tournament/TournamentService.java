package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public TournamentDto createNewTournament(Integer userId, TournamentDto tournamentDto){

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (!user.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        }

        LocalDateTime begin = tournamentDto.getBeginningTime();
        LocalDateTime end = tournamentDto.getEndingTime();
        int numberOfQuestions = tournamentDto.getNumberOfQuestions();

        CourseExecution courseEx = getCourseExecution(tournamentDto);
        List<Topic> topics = getTopics(tournamentDto, courseEx);

        Tournament tournament = new Tournament(user, topics, begin, end, numberOfQuestions, courseEx);
        user.addTournamentCreatedByMe(tournament);
        tournamentRepository.save(tournament);
        courseEx.addTournament(tournament);
        for (Topic topic : topics) {
            topic.addTournament(tournament);
        }

        return new TournamentDto(tournament);
    }

    private List<Topic> getTopics(TournamentDto tournamentDto, CourseExecution courseEx) {
        List<TopicDto> titlesDto = tournamentDto.getTitles();
        if (titlesDto == null || titlesDto.isEmpty()) {
            throw new TutorException(EMPTY_TOPIC);
        }
        List<Topic> topics = new ArrayList<>();
        for (TopicDto dto : titlesDto) {
            Topic topic = topicRepository.optionalFindTopicByName(courseEx.getId(), dto.getName()).orElseThrow(() -> new TutorException(NO_SUCH_TOPIC));
            topics.add(topic);

        }
        return topics;
    }

    private CourseExecution getCourseExecution(TournamentDto tournamentDto) {
        CourseDto courseDto =  tournamentDto.getCourseExecutionDto();
        CourseExecution courseEx;
        if (courseDto.getCourseType() == null) {
            throw new TutorException(INVALID_COURSE_EXECUTION);
        }
        if (courseDto.getCourseType().equals(Course.Type.EXTERNAL)) {
            courseEx = courseExecutionRepository.findByAcronymAcademicTermType(
                    courseDto.getAcronym(), courseDto.getAcademicTerm(), Course.Type.EXTERNAL.name()).orElse(null);
        } else {
            courseEx = courseExecutionRepository.findByAcronymAcademicTermType(
                    courseDto.getAcronym(), courseDto.getAcademicTerm(), Course.Type.TECNICO.name()).orElse(null);        }
        if (courseEx == null) {
            throw new TutorException(INVALID_COURSE_EXECUTION);
        }
        return courseEx;
    }

    public TournamentDto enrollTournament(UserDto student, TournamentDto tournamentdto) {

        if (tournamentdto.getId() == null) {
            throw new TutorException(TOURNAMENT_NOT_FOUND);
        }

        if (!student.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        }

        Tournament tournament = tournamentRepository.findTournamentById(tournamentdto.getId()).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));
        User user = userRepository.findById(student.getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, student.getId()));

        tournament.addStudentEnrolled(user);
        user.addTournamentEnrolled(tournament);
        return new TournamentDto(tournament);
    }
}