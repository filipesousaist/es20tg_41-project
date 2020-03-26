package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TournamentDto implements Serializable {

    Integer id;
    List<TopicDto> titles = new ArrayList<>();
    String beginningTime;
    String endingTime;
    int numberOfQuestions;
    List<String> studentsUsername = new ArrayList<>();

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        id = tournament.getId();
        if (tournament.getBeginningTime() != null)
            beginningTime = tournament.getBeginningTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (tournament.getEndingTime() != null)
            endingTime = tournament.getEndingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        numberOfQuestions = tournament.getNumberOfQuestions();

        List<Topic> tournamentTitles= tournament.getTitles();
        for (Topic var : tournamentTitles) {
            TopicDto topicDto = new TopicDto(var);
            titles.add(topicDto);
        }
        Set<User> users = tournament.getStudentsEnrolled();
        for (User user : users) {
            studentsUsername.add(user.getUsername());
        }
    }

    public List<TopicDto> getTitles() {
        return titles;
    }

    public void setTitles(List<TopicDto> titles) {
        this.titles = titles;
    }

    public String getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(String beginningTime) {
        this.beginningTime = beginningTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
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

    public List<String> getStudentsUsername() {
        return studentsUsername;
    }

    public void setStudentsUsername(List<String> studentsUsername) {
        this.studentsUsername = studentsUsername;
    }
}
