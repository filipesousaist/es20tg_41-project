package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clarification_requests")
public class ClarificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @Column(name="private")
    private boolean privacy;

    @Column(name="creationDate")
    private LocalDateTime creationDate;


    @OneToOne(mappedBy = "clarificationRequest")
    private Clarification clarification;

    @Autowired
    private String title;

    @Autowired
    private String text;

    public ClarificationRequest(){}

    public ClarificationRequest(User student, Question question, ClarificationRequestDto clarificationRequestDto) {
        this.id = clarificationRequestDto.getId();
        this.question = question;
        this.student = student;
        this.title = clarificationRequestDto.getTitle();
        this.text = clarificationRequestDto.getText();
        this.privacy = clarificationRequestDto.getPrivacy();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
        DashboardStats stats = this.student.getDashboardStats();
        stats.setNumAnsweredClarificationRequests(stats.getNumAnsweredClarificationRequests() + 1);
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

    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}

