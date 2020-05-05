package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TournamentDto createNewTournament(Integer userId, Integer courseExId, TournamentDto tournamentDto){

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (!user.getRole().equals(User.Role.STUDENT)) {
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime begin = LocalDateTime.parse(tournamentDto.getBeginningTime(), inputFormatter);
        LocalDateTime end = LocalDateTime.parse(tournamentDto.getEndingTime(), inputFormatter);
        int numberOfQuestions = tournamentDto.getNumberOfQuestions();

        CourseExecution courseEx = courseExecutionRepository.findById(courseExId).orElseThrow(() -> new TutorException(INVALID_COURSE_EXECUTION));
        List<Topic> topics = getTopics(tournamentDto, courseEx);
        String name = tournamentDto.getName();

        if (tournamentRepository.findTournamentByName(name).orElse(null) != null) {

            throw new TutorException(INVALID_TOURNAMENT_NAME);
        }

        Tournament tournament = new Tournament(user, name, topics, begin, end, numberOfQuestions, courseEx);

        user.addTournamentCreatedByMe(tournament);

        entityManager.persist(tournament);
        courseEx.addTournament(tournament);
        for (Topic topic : topics) {
            topic.addTournament(tournament);
        }

        return new TournamentDto(tournament);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    List<Topic> getTopics(TournamentDto tournamentDto, CourseExecution courseEx) {
        List<TopicDto> titlesDto = tournamentDto.getTopics();
        if (titlesDto == null || titlesDto.isEmpty()) {
            throw new TutorException(EMPTY_TOPIC);
        }
        List<Topic> topics = new ArrayList<>();
        for (TopicDto dto : titlesDto) {
            Topic topic;
            if (dto.getId() == null)
                topic = topicRepository.optionalFindTopicByName(courseEx.getCourse().getId(), dto.getName()).orElseThrow(() -> new TutorException(NO_SUCH_TOPIC));
            else {
                topic = topicRepository.findById(dto.getId()).orElseThrow(() -> new TutorException(NO_SUCH_TOPIC));
                if (!topic.getCourse().getId().equals(courseEx.getCourse().getId()))
                    throw new TutorException(NO_SUCH_TOPIC);
            }
            topics.add(topic);
        }
        return topics;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollTournament(Integer studentId, Integer tournamentId) {

        User user = getUser(studentId, tournamentId);

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));

        tournament.addStudentEnrolled(user);
        user.addTournamentEnrolled(tournament);

        if (tournament.getTournamentQuiz() == null && tournament.getStudentsEnrolled().size() > 1) {
            createTournamentQuiz(tournament);
        }

        return new TournamentDto(tournament);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void unenrollTournament(Integer studentId, Integer tournamentId) {

        User user = getUser(studentId, tournamentId);

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));

        tournament.removeStudentEnrolled(user);
        user.removeTournamentEnrolled(tournament);
    }

    private void createTournamentQuiz(Tournament tournament) {

        Quiz quiz = new Quiz();
        quiz.setAvailableDate(tournament.getBeginningTime());
        quiz.setConclusionDate(tournament.getEndingTime());
        quiz.setCourseExecution(tournament.getCourseExecution());
        quiz.setType("TOURNAMENT");

        List<Question> possibleQuestions = new ArrayList<>();
        for (Topic topic: tournament.getTitles()) {
            possibleQuestions.addAll(topic.getQuestions());
        }

        Random rand = new Random();
        for (int i = 0; i < tournament.getNumberOfQuestions(); i++) {
            int randomIndex = rand.nextInt(possibleQuestions.size());

            QuizQuestion quizQuestion = new QuizQuestion(quiz, possibleQuestions.get(randomIndex), quiz.getQuizQuestions().size());
            quizQuestionRepository.save(quizQuestion);

            quiz.addQuizQuestion(quizQuestion);
            possibleQuestions.remove(randomIndex);
        }
        quiz.setTournament(tournament);
        tournament.setTournamentQuiz(quiz);
        quizRepository.save(quiz);
    }

    private User getUser(Integer studentId, Integer tournamentId) {
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
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer studentID, Integer tournamentId) {

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND));

        if (tournament.getTournamentQuiz() != null) {
            tournament.getTournamentQuiz().remove();

            Set<QuizQuestion> quizQuestions = new HashSet<>(tournament.getTournamentQuiz().getQuizQuestions());

            quizQuestions.forEach(QuizQuestion::remove);
            quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

            quizRepository.delete(tournament.getTournamentQuiz());
        }

        tournament.remove(studentID);

        tournamentRepository.delete(tournament);

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(TournamentDto::new)
                .sorted(Comparator
                        .comparing(TournamentDto::getBeginningTime)
                        .thenComparing(TournamentDto::getEndingTime)
                        .thenComparing(TournamentDto::getNumberOfQuestions))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getAllOpenTournament() {

        return tournamentRepository.findAll().stream()
                .filter(tournament -> !tournament.getIsClosed())
                .map(TournamentDto::new)
                .sorted(Comparator
                        .comparing(TournamentDto::getBeginningTime)
                        .thenComparing(TournamentDto::getEndingTime)
                        .thenComparing(TournamentDto::getNumberOfQuestions))
                .collect(Collectors.toList());
    }
}