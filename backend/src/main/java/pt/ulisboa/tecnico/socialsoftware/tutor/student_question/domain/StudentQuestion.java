package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "student_questions")
public class StudentQuestion {
    public enum Status {
        PROPOSED, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROPOSED;

    @OneToMany
    private Set<QuestionEvaluation> questionEvaluations = new HashSet<>();

    public StudentQuestion() {
    }

    public StudentQuestion(Course course, User user, StudentQuestionDto studentQuestionDto){
        checkQuestion(studentQuestionDto);

        addUser(user);

        this.question = new Question(course, studentQuestionDto.getQuestionDto());

        user.addStudentQuestion(this);
    }

    private void addUser(User user) {
        this.user = user;
        DashboardStats stats = user.getDashboardStats();
        stats.setNumProposedQuestions(stats.getNumProposedQuestions() + 1);
    }


    private void checkQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto.getQuestionDto() == null){
            throw new TutorException(QUESTION_IS_MISSING);
        }

        if (studentQuestionDto.getQuestionDto().getCreationDate() == null) {
            studentQuestionDto.getQuestionDto().setCreationDate(DateHandler.toISOString(DateHandler.now()));
        }

    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user;}

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        boolean fromAccepted = this.status.equals(Status.ACCEPTED);
        boolean toAccepted = status.equals(Status.ACCEPTED);
        // From accepted to different status
        if (fromAccepted && !toAccepted) {
            DashboardStats stats = user.getDashboardStats();
            stats.setNumAcceptedQuestions(stats.getNumAcceptedQuestions() - 1);
        }
        else if (!fromAccepted && toAccepted) {
            DashboardStats stats = user.getDashboardStats();
            stats.setNumAcceptedQuestions(stats.getNumAcceptedQuestions() + 1);
        }


        // From different status to accepted
        this.status = status;
    }

    public Set<QuestionEvaluation> getQuestionEvaluations() { return questionEvaluations; }

    public void addQuestionEvaluation(QuestionEvaluation questionEvaluation) { questionEvaluations.add(questionEvaluation); }

    public void makeAvailable() {
        if (status == Status.ACCEPTED)
            question.setStatus(Question.Status.AVAILABLE);
        else
            throw new TutorException(STUDENT_QUESTION_NEEDS_ACCEPTANCE);
    }

    public void updateByTeacher(StudentQuestionDto studentQuestionDto) {
        if (status == Status.ACCEPTED)
            this.question.update(studentQuestionDto.getQuestionDto());
        else
            throw new TutorException(STUDENT_QUESTION_NEEDS_ACCEPTANCE);
    }

    public void updateByStudent(StudentQuestionDto studentQuestionDto) {
        if (status == Status.REJECTED) {
            this.question.update(studentQuestionDto.getQuestionDto());
            setStatus(Status.PROPOSED);
        }
        else
            throw new TutorException(STUDENT_QUESTION_IS_NOT_REJECTED);
    }

    public boolean isAccepted() {
        return status.equals(Status.ACCEPTED);
    }
}
