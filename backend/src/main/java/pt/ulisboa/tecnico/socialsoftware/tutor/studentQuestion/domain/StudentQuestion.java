package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;


import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
public class StudentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    private Question question;

    public StudentQuestion(){
    }

    public StudentQuestion(Course course, User user, StudentQuestionDto studentQuestionDto){
        checkQuestion(studentQuestionDto);

        this.user = user;

        this.question = new Question(course, studentQuestionDto.getQuestionDto());

        user.addStudentQuestion(this);
    }

    private void checkQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto.getQuestionDto() == null){
            throw new TutorException(QUESTION_IS_MISSING);
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user;}

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
