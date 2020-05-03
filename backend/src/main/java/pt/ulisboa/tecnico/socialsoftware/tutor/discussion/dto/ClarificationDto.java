package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import io.swagger.models.auth.In;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;

import java.io.Serializable;

public class ClarificationDto implements Serializable {

    private Integer id;

    private String text;

    private Integer userId;

    public ClarificationDto(){}

    public ClarificationDto(Clarification clarification){
        this.id = clarification.getId();
        this.text = clarification.getText();
        this.userId = clarification.getTeacher().getId();
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ClarificationDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
