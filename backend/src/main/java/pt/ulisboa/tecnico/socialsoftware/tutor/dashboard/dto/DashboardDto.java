package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class DashboardDto implements Serializable {
    private int userId;
    private String username;
    private String name;

    /*Tournaments Stats*/
    private Integer totalTournaments;
    private Integer highestResult;

    // TODO: insert each functionality related stats

    public DashboardDto(DashboardStats dashboardStats) {
        User user = dashboardStats.getUser();
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.totalTournaments = dashboardStats.getTotalTournaments();
        this.highestResult = dashboardStats.getHighestResult();
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

    public Integer getTotalTournaments() {
        return totalTournaments;
    }

    public void setTotalTournaments(Integer totalTournaments) {
        this.totalTournaments = totalTournaments;
    }

    public Integer getHighestResult() {
        return highestResult;
    }

    public void setHighestResult(Integer highestResult) {
        this.highestResult = highestResult;
    }
}
