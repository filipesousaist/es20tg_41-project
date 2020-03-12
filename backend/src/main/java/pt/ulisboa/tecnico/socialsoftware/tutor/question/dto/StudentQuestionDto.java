package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentQuestionDto implements Serializable {

    private Integer id;

    private String title;

    private String content;

    private List<String> options = new ArrayList<>();

    private UserDto userDto;

    public StudentQuestionDto() {
    }

    public StudentQuestionDto(StudentQuestion studentQuestion) {

        this.id = studentQuestion.getId();

        this.title = studentQuestion.getTitle();

        this.content = studentQuestion.getContent();

        this.options = studentQuestion.getOptions();

        this.userDto = new UserDto(studentQuestion.getUser());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public UserDto getUser() { return userDto; }

    public void setUser(UserDto userDto) { this.userDto = userDto;}

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addOption(String option){
        this.options.add(option);
    }
}
