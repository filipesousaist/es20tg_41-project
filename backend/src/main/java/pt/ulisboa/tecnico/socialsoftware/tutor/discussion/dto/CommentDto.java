package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Comment;

import java.io.Serializable;

public class CommentDto implements Serializable {
    private Integer id;

    private String text;

    private Integer userId;

    private String creationDate;

    public CommentDto(){}

    public CommentDto(Comment comment){
        this.id = comment.getId();
        this.text = comment.getText();
        this.userId = comment.getUser().getId();
        this.creationDate = DateHandler.toISOString(comment.getCreationDate());
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
