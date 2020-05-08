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

    private int totalTournaments;
    private int highestResult;

    private int numClarificationRequests;
    private int numAnsweredClarificationRequests;

    public DashboardStatsDto(DashboardStats dashboardStats) {
        User user = dashboardStats.getUser();
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();


        if(dashboardStats.getShowNumAcceptedQuestions()){
            this.numAcceptedQuestions = dashboardStats.getNumAcceptedQuestions();
        }else{
            this.numAcceptedQuestions = -1;
        }

        if (dashboardStats.getShowNumProposedQuestions()) {
            this.numProposedQuestions = dashboardStats.getNumProposedQuestions();
        }else{
            this.numProposedQuestions = -1;
        }

        if (dashboardStats.getShowHighestResult()) {
            this.highestResult = user.getHighestResult();
        }else{
            this.highestResult = -1;
        }

        if (dashboardStats.getShowTotalTournaments()) {
            this.totalTournaments = user.getTotalTournaments();
        }else{
            this.totalTournaments = -1;
        }

        this.numClarificationRequests = dashboardStats.getShowNumClarificationRequests() ?
                dashboardStats.getNumClarificationRequests() : -1;

        this.numAnsweredClarificationRequests = dashboardStats.getShowNumAnsweredClarificationRequests() ?
                dashboardStats.getNumAnsweredClarificationRequests() : -1;

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

    public int getTotalTournaments() {
        return totalTournaments;
    }

    public void setTotalTournaments(int totalTournaments) {
        this.totalTournaments = totalTournaments;
    }

    public int getHighestResult() {
        return highestResult;
    }

    public void setHighestResult(int highestResult) {
        this.highestResult = highestResult;
    }

    public int getNumClarificationRequests() {
        return numClarificationRequests;
    }

    public void setNumClarificationRequests(int numClarificationRequests) {
        this.numClarificationRequests = numClarificationRequests;
    }

    public int getNumAnsweredClarificationRequests() {
        return numAnsweredClarificationRequests;
    }

    public void setNumAnsweredClarificationRequests(int numAnsweredClarificationRequests) {
        this.numAnsweredClarificationRequests = numAnsweredClarificationRequests;
    }
}
