package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clarifications")
public  class Clarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User teacher;

    @OneToOne
    @JoinColumn(name = "clarification_request_id")
    private ClarificationRequest clarificationRequest;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Autowired
    private String text;

    public Clarification(){
    }

    public Clarification(User teacher, ClarificationRequest request, ClarificationDto clarificationDto){
        this.id = clarificationDto.getId();
        this.teacher = teacher;
        this.clarificationRequest = request;
        this.text = clarificationDto.getText();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public ClarificationRequest getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}