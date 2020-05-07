package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import io.swagger.models.auth.In;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClarificationDto implements Serializable {

    private Integer id;

    private String username;

    private String text;

    private Integer userId;

    private String summary;

    private Integer clairifcationRequestId;

    private String creationDate;

    private List<CommentDto> commentList = new ArrayList<>();

    public ClarificationDto(){}

    public ClarificationDto(Clarification clarification){
        this.id = clarification.getId();
        this.username = clarification.getTeacher().getUsername();
        this.text = clarification.getText();
        this.userId = clarification.getTeacher().getId();
        this.summary = clarification.getSummary();
        this.creationDate = DateHandler.toISOString(clarification.getCreationDate());
        this.commentList = clarification.getComments().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<CommentDto> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentDto> commentList) {
        this.commentList = commentList;
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
