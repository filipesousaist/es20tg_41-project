package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;

public class DashboardPermissionsDto {
    private boolean showNumProposedQuestions;
    private boolean showNumAcceptedQuestions;

    /*Tournaments Stats*/
    private boolean totalTournaments;
    private boolean highestResult;

    public DashboardPermissionsDto() {
        
    }

    public DashboardPermissionsDto(DashboardStats dashboardStats) {
        this.showNumProposedQuestions = dashboardStats.getShowNumProposedQuestions();
        this.showNumAcceptedQuestions = dashboardStats.getShowNumAcceptedQuestions();
        this.totalTournaments = dashboardStats.getShowTotalTournaments();
        this.highestResult = dashboardStats.getShowHighestResult();
    }

    public boolean getShowNumProposedQuestions() {
        return showNumProposedQuestions;
    }

    public void setShowNumProposedQuestions(boolean showNumProposedQuestions) {
        this.showNumProposedQuestions = showNumProposedQuestions;
    }

    public boolean getShowNumAcceptedQuestions() {
        return showNumAcceptedQuestions;
    }

    public void setShowNumAcceptedQuestions(boolean showNumAcceptedQuestions) {
        this.showNumAcceptedQuestions = showNumAcceptedQuestions;
    }

    public boolean getTotalTournaments() {
        return totalTournaments;
    }

    public void setTotalTournaments(boolean totalTournaments) {
        this.totalTournaments = totalTournaments;
    }

    public boolean getHighestResult() {
        return highestResult;
    }

    public void setHighestResult(boolean highestResult) {
        this.highestResult = highestResult;
    }
}
