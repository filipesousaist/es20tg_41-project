package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto;

import java.util.List;

@RestController
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/executions/{executionId}/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<DashboardDto> getDashboardStats(@PathVariable int executionId){
        return this.dashboardService.getDashboardStats(executionId);
    }
}
