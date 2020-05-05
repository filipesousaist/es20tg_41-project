package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;

@Service
public class DashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DashboardDto> getDashboardStats(Integer courseExecutionId){
        return courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId))
                .getUsers()
                .stream()
                .filter(user -> user.getRole().equals(User.Role.STUDENT))
                .map(User::getDashboardStats)
                .map(DashboardDto::new)
                .collect(Collectors.toList());
    }
}
