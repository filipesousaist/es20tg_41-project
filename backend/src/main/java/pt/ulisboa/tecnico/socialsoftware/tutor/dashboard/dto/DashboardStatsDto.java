package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class DashboardStatsDto implements Serializable {
    private int userId;
    private String username;
    private String name;

    private int numProposedQuestions;
    private int numAcceptedQuestions;

    // TODO: insert each functionality related stats

    public DashboardStatsDto(DashboardStats dashboardStats) {
        User user = dashboardStats.getUser();
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();

        this.numProposedQuestions = dashboardStats.getNumProposedQuestions();
        this.numAcceptedQuestions = dashboardStats.getNumAcceptedQuestions();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumProposedQuestions() {
        return numProposedQuestions;
    }

    public void setNumProposedQuestions(int numProposedQuestions) {
        this.numProposedQuestions = numProposedQuestions;
    }

    public int getNumAcceptedQuestions() {
        return numAcceptedQuestions;
    }

    public void setNumAcceptedQuestions(int numAcceptedQuestions) {
        this.numAcceptedQuestions = numAcceptedQuestions;
    }
}
