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

    private int numProposedQuestions;
    private int numAcceptedQuestions;

    private boolean showNumProposedQuestions = true;
    private boolean showNumAcceptedQuestions = true;

    // TODO: insert each functionality's related stats

    public DashboardStats() {

    }

    public DashboardStats(User user) {
        this.user = user;
        this.numProposedQuestions = user.getNumProposedQuestions();
        this.numAcceptedQuestions = user.getNumAcceptedQuestions();
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

    public void updatePermissions(DashboardPermissionsDto dashboardPermissionsDto) {
        this.showNumProposedQuestions = dashboardPermissionsDto.getShowNumProposedQuestions();
        this.showNumAcceptedQuestions = dashboardPermissionsDto.getShowNumAcceptedQuestions();
    }
}
