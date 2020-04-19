package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AdministrationService administrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;

    @Autowired
    private DiscussionService discussionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String username = ((User) authentication.getPrincipal()).getUsername();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(username).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseDto courseDto = administrationService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(username, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(username, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(username, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "STUDENT_QUESTION.ACCESS":
                    return userHasExecutionOfStudentQuestion(username, id);
                case "QUESTION.ANSWERED":
                    return userHasAnsweredQuestion(username, id);
                case "CLARIFICATION.REQUEST.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, discussionService.findClarificationRequestCourse(id).getCourseId());
                case "CLARIFICATION.ACCESS":
                    return userHasThisClarificationRequest(username, id);
                default: return false;
            }
        }

        return false;
    }

    private boolean userHasAnExecutionOfTheCourse(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseId() == id);
    }

    private boolean userHasThisExecution(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseExecutionId() == id);
    }

    private boolean userHasExecutionOfStudentQuestion(String username, int studentQuestionId) {
        int questionId = studentQuestionRepository.findById(studentQuestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND, studentQuestionId))
                .getQuestion()
                .getId();
        return userHasAnExecutionOfTheCourse(username,
                questionService.findQuestionCourse(questionId).getCourseId());
    }

    private boolean userHasAnsweredQuestion(String username, int id){
        return userService.getAnsweredQuestions(username).stream()
                .anyMatch(question -> question.getId() == id);
    }

    private boolean userHasThisClarificationRequest(String username, int id){
        return userService.getClarificationRequests(username).stream()
                .anyMatch(clarificationRequest -> clarificationRequest.getId() == id);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
