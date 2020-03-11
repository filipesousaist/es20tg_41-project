package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class StudentQuestion {

    private Integer id;

    private Question question;

    private User user;

    public void StudentQuestion(){
    }

    public void StudentQuestion(Question question, User user){
        this.question = question;

        this.user = user;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user;}
}
