package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DashboardStats;

public class DashboardPermissionsDto {
    private boolean showNumProposedQuestions;
    private boolean showNumAcceptedQuestions;

    public DashboardPermissionsDto(DashboardStats dashboardStats) {
        this.showNumProposedQuestions = dashboardStats.getShowNumProposedQuestions();
        this.showNumAcceptedQuestions = dashboardStats.getShowNumAcceptedQuestions();
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
}
