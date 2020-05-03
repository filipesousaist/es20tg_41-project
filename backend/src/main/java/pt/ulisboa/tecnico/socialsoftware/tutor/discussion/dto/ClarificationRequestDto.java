package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;

import java.io.Serializable;

public class ClarificationRequestDto implements Serializable {

    private Integer id;

    private String title;

    private String text;

    private int userId;

    private ClarificationDto clarification;

    private String creationDate;

    public ClarificationRequestDto(){}

    public ClarificationRequestDto(ClarificationRequest clarificationRequest){
        this.id = clarificationRequest.getId();
        this.title = clarificationRequest.getTitle();
        this.text = clarificationRequest.getText();
        this.userId = clarificationRequest.getStudent().getId();
        if (clarificationRequest.getClarification() != null){
            this.clarification = new ClarificationDto(clarificationRequest.getClarification());
        }
        this.creationDate = DateHandler.toISOString(clarificationRequest.getCreationDate());
    }


    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ClarificationDto getClarification() {
        return clarification;
    }

    public void setClarification(ClarificationDto clarification) {
        this.clarification = clarification;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
