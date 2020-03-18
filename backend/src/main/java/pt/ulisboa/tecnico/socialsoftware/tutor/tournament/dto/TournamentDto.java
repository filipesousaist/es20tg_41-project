package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentDto implements Serializable {

    CourseDto courseExecutionDto;
    UserDto userDto;
    Integer id;
    List<TopicDto> titles = new ArrayList<>();
    LocalDateTime beginningTime;
    LocalDateTime endingTime;
    int numberOfQuestions;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        courseExecutionDto = new CourseDto(tournament.getCourseExecution());
        userDto = new UserDto(tournament.getCreatedByUser());
        id = tournament.getId();
        beginningTime = tournament.getBeginningTime();
        endingTime = tournament.getEndingTime();
        numberOfQuestions = tournament.getNumberOfQuestions();

        List<Topic> tournamentTitles= tournament.getTitles();
        for (Topic var : tournamentTitles) {
            TopicDto topicDto = new TopicDto(var);
            titles.add(topicDto);
        }
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto user) {
        this.userDto = user;
    }

    public List<TopicDto> getTitles() {
        return titles;
    }

    public void setTitles(List<TopicDto> titles) {
        this.titles = titles;
    }

    public LocalDateTime getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(LocalDateTime beginningTime) {
        this.beginningTime = beginningTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CourseDto getCourseExecutionDto() {
        return courseExecutionDto;
    }

    public void setCourseExecutionDto(CourseDto courseExecutionDto) {
        this.courseExecutionDto = courseExecutionDto;
    }
}
