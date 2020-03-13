package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto;

import java.io.Serializable;

public class QuestionEvaluationDto implements Serializable {
    private Integer id;
    private Boolean approved;
    private String justification;

    public QuestionEvaluationDto() {

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
