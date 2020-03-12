package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
public class StudentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    private List<String> options = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public StudentQuestion(){
    }

    public StudentQuestion(Course course, User user, StudentQuestionDto studentQuestionDto){

        checkConsistentQuestion(studentQuestionDto);

        this.title = studentQuestionDto.getTitle();

        this.content = studentQuestionDto.getContent();

        this.options = studentQuestionDto.getOptions();

        this.user = user;

        this.course = course;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    private void checkConsistentQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto.getTitle().trim().length() == 0 ||
                studentQuestionDto.getContent().trim().length() == 0 ||
                studentQuestionDto.getOptions().contains(null) ||
                studentQuestionDto.getOptions().contains("") ) {
            throw new TutorException(STUDENT_QUESTION_MISSING_DATA);
        }
    }
}
