package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardPermissionsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "dashboard_stats")
public class DashboardStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    /*Tournaments Stats*/
    private int totalTournaments;
    private int highestResult;
    private int numProposedQuestions;
    private int numAcceptedQuestions;

    private int numClarificationRequests;
    private int numAnsweredClarificationRequests;

    private boolean showNumProposedQuestions = true;
    private boolean showNumAcceptedQuestions = true;

    private boolean showTotalTournaments = true;
    private boolean showHighestResult = true;

    private boolean showNumClarificationRequests = true;
    private boolean showNumAnsweredClarificationRequests = true;

    // TODO: insert each functionality's related stats
    public DashboardStats() {

    }

    public DashboardStats(User user) {

        this.user = user;
        this.totalTournaments = user.getTotalTournaments();
        this.highestResult = user.getHighestResult();
        this.numProposedQuestions = user.getNumProposedQuestions();
        this.numAcceptedQuestions = user.getNumAcceptedQuestions();
        this.numClarificationRequests = user.getNumClarificationRequests();
        this.numAnsweredClarificationRequests = user.getNumAnsweredClarificationRequests();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public int getNumAcceptedQuestions() {
        return numAcceptedQuestions;
    }

    public void setNumAcceptedQuestions(int numAcceptedQuestions) {
        this.numAcceptedQuestions = numAcceptedQuestions;
    }

    public int getNumProposedQuestions() {
        return numProposedQuestions;
    }

    public void setNumProposedQuestions(int numProposedQuestions) {
        this.numProposedQuestions = numProposedQuestions;
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

    public void updatePermissions(DashboardPermissionsDto dashboardPermissionsDto) {
        this.showNumProposedQuestions = dashboardPermissionsDto.getShowNumProposedQuestions();
        this.showNumAcceptedQuestions = dashboardPermissionsDto.getShowNumAcceptedQuestions();
        this.showHighestResult = dashboardPermissionsDto.getShowHighestResult();
        this.showTotalTournaments = dashboardPermissionsDto.getShowTotalTournaments();
        this.showNumClarificationRequests = dashboardPermissionsDto.getShowNumClarificationRequests();
        this.showNumAnsweredClarificationRequests = dashboardPermissionsDto.getShowNumAnsweredClarificationRequests();
    }
}
