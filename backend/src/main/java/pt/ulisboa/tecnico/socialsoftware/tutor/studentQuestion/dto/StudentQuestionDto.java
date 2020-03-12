package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentQuestionDto implements Serializable {

    private Integer id;

    private QuestionDto questionDto;

    private UserDto userDto;

    public StudentQuestionDto(){
    }

    public StudentQuestionDto(StudentQuestion studentQuestion) {

        this.id = studentQuestion.getId();

        this.questionDto = new QuestionDto(studentQuestion.getQuestion());

        this.userDto = new UserDto(studentQuestion.getUser());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public UserDto getUser() { return userDto; }

    public void setUser(UserDto userDto) { this.userDto = userDto;}

    public QuestionDto getQuestionDto() {
        return questionDto;
    }

    public void setQuestionDto(QuestionDto questionDto) {
        this.questionDto = questionDto;
    }
}