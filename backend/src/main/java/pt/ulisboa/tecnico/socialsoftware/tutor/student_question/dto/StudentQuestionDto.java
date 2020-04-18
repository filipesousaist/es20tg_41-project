package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.io.Serializable;

public class StudentQuestionDto implements Serializable {

    private Integer id;

    private QuestionDto questionDto;

    private UserDto userDto;

    private Integer ser;

    public StudentQuestionDto(){
    }

    public StudentQuestionDto(StudentQuestion studentQuestion) {

        this.id = studentQuestion.getId();

        this.questionDto = new QuestionDto(studentQuestion.getQuestion());

        this.userDto = new UserDto(studentQuestion.getUser());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public UserDto getUserDto() { return userDto; }

    public void setUserDto(UserDto userDto) { this.userDto = userDto;}

    public QuestionDto getQuestionDto() {
        return questionDto;
    }

    public void setQuestionDto(QuestionDto questionDto) {
        this.questionDto = questionDto;
    }

    public Integer getSer() {
        return ser;
    }

    public void setSer(Integer ser) {
        this.ser = ser;
    }
}

