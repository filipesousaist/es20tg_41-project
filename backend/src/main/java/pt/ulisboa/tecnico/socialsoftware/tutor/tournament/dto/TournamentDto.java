package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TournamentDto implements Serializable {

    Integer id;
    String name;
    List<TopicDto> topics = new ArrayList<>();
    String beginningTime;
    String endingTime;
    int numberOfQuestions;
    List<String> studentsUsername = new ArrayList<>();
    boolean isClosed;
    String creatorName;
    QuizDto quizDto;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        id = tournament.getId();
        isClosed = tournament.getIsClosed();
        if (tournament.getBeginningTime() != null)
            beginningTime = tournament.getBeginningTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        if (tournament.getEndingTime() != null)
            endingTime = tournament.getEndingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        numberOfQuestions = tournament.getNumberOfQuestions();

        List<Topic> tournamentTitles= tournament.getTitles();
        for (Topic var : tournamentTitles) {
            TopicDto topicDto = new TopicDto(var);
            topics.add(topicDto);
        }
        name = tournament.getName();
        creatorName = tournament.getCreatedByUser().getUsername();

        Set<User> users = tournament.getStudentsEnrolled();
        for (User user : users) {
            studentsUsername.add(user.getUsername());
        }

        if (tournament.getTournamentQuiz() != null) {
            quizDto = new QuizDto(tournament.getTournamentQuiz(), true);
        }
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
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

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
