package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdByUser;

    @ManyToMany
    private Set<User> studentsEnrolled = new HashSet<>();

    @ManyToMany(mappedBy = "tournaments")
    private List<Topic> titles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_executions_id")
    private CourseExecution courseExecution;

    @OneToOne(mappedBy = "tournament")
    private Quiz tournamentQuiz = null;

    private String name;
    private LocalDateTime beginningTime;
    private LocalDateTime endingTime;
    private int numberOfQuestions;

    private Boolean isClosed = false;

    public Tournament() {}

    public Tournament(User student, String tournamentName, List<Topic> titlesList, LocalDateTime initialTime, LocalDateTime endTime, int nQuestions, CourseExecution courseEx) {

        checkParameters(titlesList, initialTime, endTime, nQuestions);

        name = tournamentName;
        createdByUser = student;
        titles = titlesList;
        beginningTime = initialTime;
        endingTime = endTime;
        numberOfQuestions = nQuestions;
        courseExecution = courseEx;
    }

    private void checkParameters(List<Topic> topics, LocalDateTime initialTime, LocalDateTime endTime, int nQuestions) {
        if (topics == null) {
            throw new TutorException(TOPICS_IS_EMPTY);
        }
        if (initialTime.isAfter(endTime)) {
            throw new TutorException(END_BEFORE_BEGINS);
        }
        if (nQuestions <= 0) {
            throw new TutorException(INVALID_QUESTIONS_NUMBER, numberOfQuestions);
        }
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User user) {
        this.createdByUser = user;
    }

    public List<Topic> getTitle() {
        return titles;
    }

    public LocalDateTime getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(LocalDateTime beginningTime) {
        this.beginningTime = beginningTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Set<User> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public void addStudentEnrolled(User user) {

        if (this.studentsEnrolled.contains(user) && !user.getUsername().equals("Demo-Student")) {
            throw new TutorException(STUDENT_ALREADY_ENROLLED);
        }
        if (this.isClosed) {
            throw new TutorException(TOURNAMENT_IS_CLOSED);
        }
        this.studentsEnrolled.add(user);
    }

    public void removeStudentEnrolled(User user) {

        if (!this.studentsEnrolled.contains(user)) {
            throw new TutorException(STUDENT_NOT_ENROLLED);
        }
        this.studentsEnrolled.remove(user);
    }

    public Integer getId() {
        return id;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean closed) {
        isClosed = closed;
    }

    public List<Topic> getTitles() {
        return titles;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Quiz getTournamentQuiz() {
        return tournamentQuiz;
    }

    public void setTournamentQuiz(Quiz tournamentQuiz) {
        this.tournamentQuiz = tournamentQuiz;
    }

    public void remove(int studentId) {

        if (this.createdByUser.getId() != studentId)
            throw new TutorException(STUDENT_DIDNT_CREATE_TOURNAMENT);

        if (this.isClosed)
            throw new TutorException((TOURNAMENT_IS_CLOSED));

        this.createdByUser.removeTournamentCreated(this);

        for(User user: this.studentsEnrolled) {
            user.getTournamentsEnrolled().remove(this);
        }
        this.studentsEnrolled = null;

        for(Topic topic: this.titles) {
            topic.getTournaments().remove(this);
        }
        this.titles = null;

        this.tournamentQuiz = null;

        this.courseExecution.getTournaments().remove(this);
        this.courseExecution = null;
    }

    public Integer getHighestResult(User user) {

        if (tournamentQuiz == null) {
            throw new TutorException(QUIZ_NOT_FOUND);
        }

        int rank = 1;

        HashMap<User, Integer> studentsRank = new HashMap<>();

        for (User u: this.studentsEnrolled) {
            int value = (int) u.getQuizAnswers().stream()
                    .map(QuizAnswer::getQuestionAnswers)
                    .flatMap(Collection::stream)
                    .map(QuestionAnswer::getOption)
                    .filter(Objects::nonNull)
                    .filter(Option::getCorrect).count();
            studentsRank.put(u, value);
        }

        studentsRank.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()));


        int correct = studentsRank.get(studentsRank.keySet().toArray()[0]);
        Iterator it = studentsRank.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int value = (int) pair.getValue();

            if (correct > value) {
                correct = value;
                rank++;
            }

            User user2 = (User) pair.getKey();

            if (user2.getId().equals(user.getId())) {
                break;
            }
        }
        return rank;
    }
}