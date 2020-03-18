package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;

import java.io.Serializable;

public class ClarificationDto implements Serializable {

    private Integer id;

    private Integer key;

    private String text;

    public ClarificationDto(){}

    public ClarificationDto(Clarification clarification){
        this.id = clarification.getId();
        this.key = clarification.getKey();
        this.text = clarification.getText();
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ClarificationDto{" +
                "id=" + id +
                ", key=" + key +
                ", text='" + text + '\'' +
                '}';
    }
}
