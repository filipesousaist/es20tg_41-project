package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdByUser;

    @ManyToMany
    private Set<User> studentsEnrolled = new HashSet<>();

    @ManyToMany(mappedBy = "tournaments")
    private List<Topic> titles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_executions_id")
    private CourseExecution courseExecution;

    private LocalDateTime beginningTime;
    private LocalDateTime endingTime;
    private int numberOfQuestions;

    private Boolean isClosed = false;

    public Tournament() {}

    public Tournament(User student, List<Topic> titlesList, LocalDateTime initialTime, LocalDateTime endTime, int nQuestions, CourseExecution courseEx) {

        checkParameters(titlesList, initialTime, endTime, nQuestions);

        createdByUser = student;
        titles = titlesList;
        beginningTime = initialTime;
        endingTime = endTime;
        numberOfQuestions = nQuestions;
        courseExecution = courseEx;
    }

    private void checkParameters(List<Topic> topics, LocalDateTime initialTime, LocalDateTime endTime, int nQuestions) {
        if (topics == null) {
            //TODO check
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

        if (this.studentsEnrolled.contains(user)) {
            throw new TutorException(STUDENT_ALREADY_ENROLLED);
        }
        if (this.isClosed) {
            throw new TutorException(TOURNAMENT_IS_CLOSED);
        }
        this.studentsEnrolled.add(user);
    }

    public Integer getId() {
        return id;
    }

    public Boolean getClosed() {
        return isClosed;
    }

    public void setClosed(Boolean closed) {
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
}