package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.Dashboard;

import java.io.Serializable;

public class DashboardDto implements Serializable {
    private Integer id;
    private String userName;
    private String usermame;

    // TODO: insert each functionality related stats

    public DashboardDto(Dashboard dashboardStats){
        this.id = dashboardStats.getId();
        this.usermame = dashboardStats.getUser().getUsername();
        this.userName = dashboardStats.getUser().getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsermame() {
        return usermame;
    }

    public void setUsermame(String usermame) {
        this.usermame = usermame;
    }
}
