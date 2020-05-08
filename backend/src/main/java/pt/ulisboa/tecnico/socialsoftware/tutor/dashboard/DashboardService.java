package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardPermissionsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class DashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DashboardStatsDto> getDashboardStats(int userId, Integer courseExecutionId){
        User user1 = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        DashboardStatsDto userDashboardStats = new DashboardStatsDto(user1.getDashboardStats());
        userDashboardStats.setNumAcceptedQuestions(user1.getNumAcceptedQuestions());
        userDashboardStats.setNumProposedQuestions(user1.getNumProposedQuestions());
        userDashboardStats.setNumClarificationRequests(user1.getNumClarificationRequests());
        userDashboardStats.setNumAnsweredClarificationRequests(user1.getNumAnsweredClarificationRequests());
        List<DashboardStatsDto> stats = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId))
                .getUsers()
                .stream()
                .filter(user -> user.getRole().equals(User.Role.STUDENT) && !(user.getId().equals(userId)))
                .map(User::getDashboardStats)
                .map(DashboardStatsDto::new).collect(Collectors.toList());
        stats.add(userDashboardStats);
        return stats;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DashboardPermissionsDto getDashboardPermissions(int studentId) {
        return new DashboardPermissionsDto(
            userRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId))
                .getDashboardStats()
        );
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DashboardPermissionsDto updateDashboardPermissions(int studentId, DashboardPermissionsDto dashboardPermissionsDto) {
        userRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId))
                .getDashboardStats()
                .updatePermissions(dashboardPermissionsDto);
        return dashboardPermissionsDto;
    }


}
