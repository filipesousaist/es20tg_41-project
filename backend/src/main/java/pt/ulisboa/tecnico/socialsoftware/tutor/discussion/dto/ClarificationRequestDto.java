package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;

import java.io.Serializable;

public class ClarificationRequestDto implements Serializable {

    private Integer id;

    private String title;

    private String text;

    private String username;

    public ClarificationRequestDto(){}

    public ClarificationRequestDto(ClarificationRequest clarificationRequest){
        this.id = clarificationRequest.getId();
        this.title = clarificationRequest.getTitle();
        this.text = clarificationRequest.getText();
        this.username = clarificationRequest.getStudent().getUsername();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
