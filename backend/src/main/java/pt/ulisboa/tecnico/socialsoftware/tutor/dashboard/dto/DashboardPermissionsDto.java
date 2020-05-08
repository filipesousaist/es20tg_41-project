package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;

public class DashboardPermissionsDto {
    private boolean showNumProposedQuestions;
    private boolean showNumAcceptedQuestions;
    private boolean showTotalTournaments;
    private boolean showHighestResult;
    private boolean showNumClarificationRequests;
    private boolean showNumAnsweredClarificationRequests;

    public DashboardPermissionsDto() {
        
    }

    public DashboardPermissionsDto(DashboardStats dashboardStats) {
        this.showNumProposedQuestions = dashboardStats.getShowNumProposedQuestions();
        this.showNumAcceptedQuestions = dashboardStats.getShowNumAcceptedQuestions();
        this.showTotalTournaments = dashboardStats.getShowTotalTournaments();
        this.showHighestResult = dashboardStats.getShowHighestResult();
        this.showNumClarificationRequests = dashboardStats.getShowNumClarificationRequests();
        this.showNumAnsweredClarificationRequests = dashboardStats.getShowNumAnsweredClarificationRequests();
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

    public boolean getShowTotalTournaments() {
        return showTotalTournaments;
    }

    public void setShowTotalTournaments(boolean showTotalTournaments) {
        this.showTotalTournaments = showTotalTournaments;
    }

    public boolean getShowHighestResult() {
        return showHighestResult;
    }

    public void setShowHighestResult(boolean showHighestResult) {
        this.showHighestResult = showHighestResult;
    }

    public boolean getShowNumClarificationRequests() {
        return showNumClarificationRequests;
    }

    public void setShowNumClarificationRequests(boolean showNumClarificationRequests) {
        this.showNumClarificationRequests = showNumClarificationRequests;
    }

    public boolean getShowNumAnsweredClarificationRequests() {
        return showNumAnsweredClarificationRequests;
    }

    public void setShowNumAnsweredClarificationRequests(boolean showNumAnsweredClarificationRequests) {
        this.showNumAnsweredClarificationRequests = showNumAnsweredClarificationRequests;
    }
}
