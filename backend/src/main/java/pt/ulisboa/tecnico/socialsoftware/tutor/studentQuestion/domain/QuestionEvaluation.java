package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.QuestionEvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_EVALUATION_MISSING_JUSTIFICATION;

@Entity
public class QuestionEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean approved;

    private String justification;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "student_question_id")
    private StudentQuestion studentQuestion;


    public QuestionEvaluation() {

    }

    public QuestionEvaluation(User teacher, StudentQuestion studentQuestion, QuestionEvaluationDto questionEvaluationDto) {
        String justification = questionEvaluationDto.getJustification();
        if (justification == null || justification.trim().isEmpty()) {
            throw new TutorException(QUESTION_EVALUATION_MISSING_JUSTIFICATION);
        }

        this.teacher = teacher;
        teacher.addQuestionEvaluation(this);
        this.studentQuestion = studentQuestion;
        studentQuestion.addQuestionEvaluation(this);

        this.approved = questionEvaluationDto.isApproved();
        this.justification = justification;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public StudentQuestion getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(StudentQuestion studentQuestion) {
        this.studentQuestion = studentQuestion;
    }
}
