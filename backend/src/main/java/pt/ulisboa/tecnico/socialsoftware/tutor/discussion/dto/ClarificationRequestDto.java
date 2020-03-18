package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;

import java.io.Serializable;

public class ClarificationRequestDto implements Serializable {

    private Integer id;

    private Integer key;

    private String title;

    private String text;

    public ClarificationRequestDto(){}

    public ClarificationRequestDto(ClarificationRequest clarificationRequest){
        this.id = clarificationRequest.getId();
        this.key = clarificationRequest.getKey();
        this.title = clarificationRequest.getTitle();
        this.text = clarificationRequest.getText();
    }


    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
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
}
