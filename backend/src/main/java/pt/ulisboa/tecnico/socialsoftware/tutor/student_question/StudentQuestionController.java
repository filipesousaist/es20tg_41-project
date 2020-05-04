package pt.ulisboa.tecnico.socialsoftware.tutor.student_question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.QuestionEvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StudentQuestionController {

    @Autowired
    StudentQuestionService studentQuestionService;

    @PostMapping("courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto createStudentQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return studentQuestionService.createStudentQuestion(courseId, user.getId(), studentQuestionDto);
    }

    @PostMapping("studentQuestions/{studentQuestionId}/questionEvaluations")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENT_QUESTION.ACCESS')")
    public QuestionEvaluationDto createQuestionEvaluation(Principal principal, @PathVariable int studentQuestionId, @RequestBody QuestionEvaluationDto questionEvaluationDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return studentQuestionService.createQuestionEvaluation(user.getId(), studentQuestionId, questionEvaluationDto);
    }

    @GetMapping("studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<StudentQuestionDto> getStudentQuestions(Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return studentQuestionService.getStudentQuestions(user.getId());
    }

    @GetMapping("courses/{courseId}/studentQuestions/non-rejected")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getNonRejectedStudentQuestions(@PathVariable int courseId) {
        return studentQuestionService.getNonRejectedStudentQuestions(courseId);
    }

    @PutMapping("studentQuestions/{studentQuestionId}/available")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto makeStudentQuestionAvailable(@PathVariable int studentQuestionId) {
        return studentQuestionService.makeStudentQuestionAvailable(studentQuestionId);
    }
}