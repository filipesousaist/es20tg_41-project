package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.QuestionEvaluation;

import java.io.Serializable;

public class QuestionEvaluationDto implements Serializable {
    private Integer id;
    private Boolean approved;
    private String justification;

    public QuestionEvaluationDto() {

    }

    public QuestionEvaluationDto(QuestionEvaluation questionEvaluation) {
        this.id = questionEvaluation.getId();
        this.approved = questionEvaluation.isApproved();
        this.justification = questionEvaluation.getJustification();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
