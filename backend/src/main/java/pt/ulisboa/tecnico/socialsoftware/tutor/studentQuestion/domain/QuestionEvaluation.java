package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.QuestionEvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

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
        this.teacher = teacher;
        this.studentQuestion = studentQuestion;
        this.approved = questionEvaluationDto.isApproved();
        this.justification = questionEvaluationDto.getJustification();
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
