package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

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
    private Integer totalTournaments;
    private Integer highestResult;

    // TODO: insert each functionality's related stats

    public DashboardStats() {

    }

    public DashboardStats(User user) {

        this.user = user;
        this.totalTournaments = user.getTotalTournaments();
        this.highestResult = user.getHighestResult();
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
}
