package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentQuestionDto implements Serializable {

    private Integer id;

    private Question question;

    private User user;

    public StudentQuestionDto() {
    }

    public StudentQuestionDto(StudentQuestion studentQuestion) {
        //receive QuestionDTO ?
        this.id = studentQuestion.getId();

        this.question = studentQuestion.getQuestion();

        this.user = studentQuestion.getUser();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user;}

}
