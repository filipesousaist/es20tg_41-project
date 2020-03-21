package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StudentQuestionController {

    @Autowired
    private StudentQuestionService studentQuestionService;

    @PostMapping("courses/{courseId}/users/{userId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto createStudentQuestion(@PathVariable int courseId, @PathVariable Integer userId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        return studentQuestionService.createStudentQuestion(courseId, userId, studentQuestionDto);
    }

    //public List<StudentQuestionDto> getStudentQuestions() { return studentQuestionService.getStudentQuestions(); }

    //@GetMapping("/studentQuestions/{studentQuestionId}")
    //public StudentQuestionDto getStudentQuestion(@PathVariable Integer studentQuestionId) { return studentQuestionService.findById(studentQuestionId); }
}