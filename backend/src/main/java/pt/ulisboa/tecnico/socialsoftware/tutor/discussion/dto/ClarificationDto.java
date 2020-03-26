package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;

import java.io.Serializable;

public class ClarificationDto implements Serializable {

    private Integer id;

    private String text;

    private String username;

    public ClarificationDto(){}

    public ClarificationDto(Clarification clarification){
        this.id = clarification.getId();
        this.text = clarification.getText();
        this.username = clarification.getTeacher().getUsername();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ClarificationDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
