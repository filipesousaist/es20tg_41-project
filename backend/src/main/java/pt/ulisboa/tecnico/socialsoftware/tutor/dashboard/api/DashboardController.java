package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.DashboardService;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardPermissionsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/executions/{executionId}/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<DashboardStatsDto> getDashboardStats(@PathVariable int executionId){
        return dashboardService.getDashboardStats(executionId);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DashboardPermissionsDto getDashboardPermissions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return dashboardService.getDashboardPermissions(user.getId());
    }

    @PutMapping("/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DashboardPermissionsDto updateDashboardPermissions(
            Principal principal, @RequestBody DashboardPermissionsDto dashboardPermissionsDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return dashboardService.updateDashboardPermissions(user.getId(), dashboardPermissionsDto);
    }
}
