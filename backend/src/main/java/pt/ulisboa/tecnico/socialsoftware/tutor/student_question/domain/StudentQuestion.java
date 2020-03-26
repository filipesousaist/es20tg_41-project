package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany
    private Set<QuestionEvaluation> questionEvaluations = new HashSet<>();

    public StudentQuestion(){
    }

    public StudentQuestion(Course course, User user, StudentQuestionDto studentQuestionDto){
        checkQuestion(studentQuestionDto);

        this.user = user;

        this.question = new Question(course, studentQuestionDto.getQuestionDto());

        user.addStudentQuestion(this);
    }

    private void checkQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto.getQuestionDto() == null){
            throw new TutorException(QUESTION_IS_MISSING);
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

    public Set<QuestionEvaluation> getQuestionEvaluations() { return questionEvaluations; }

    public void addQuestionEvaluation(QuestionEvaluation questionEvaluation) { questionEvaluations.add(questionEvaluation); }
}
