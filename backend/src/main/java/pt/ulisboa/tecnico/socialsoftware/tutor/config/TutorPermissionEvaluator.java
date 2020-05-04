package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
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
    private CourseService courseService;

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
        int userId = ((User) authentication.getPrincipal()).getId();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(userId).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
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
                    CourseDto courseDto = courseService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(userId, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(userId, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(userId, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "STUDENT_QUESTION.ACCESS":
                    return userHasExecutionOfStudentQuestion(userId, id);
                case "QUESTION.ANSWERED":
                    return userHasAnsweredQuestion(userId, id);
                case "CLARIFICATION.REQUEST.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, discussionService.findClarificationRequestCourse(id).getCourseId());
                case "CLARIFICATION.ACCESS":
                    return userHasThisClarificationRequest(userId, id);
                case "SUMMARY.ACCESS":
                    return userHasThisClarification(userId, id);
                default: return false;
            }
        }

        return false;
    }

    private boolean userHasAnExecutionOfTheCourse(int userId, int courseId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseId() == courseId);
    }

    private boolean userHasThisExecution(int userId, int courseExecutionId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseExecutionId() == courseExecutionId);
    }

    private boolean userHasExecutionOfStudentQuestion(int userId, int studentQuestionId) {
        int questionId = studentQuestionRepository.findById(studentQuestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND, studentQuestionId))
                .getQuestion()
                .getId();
        return userHasAnExecutionOfTheCourse(userId,
                questionService.findQuestionCourse(questionId).getCourseId());
    }

    private boolean userHasAnsweredQuestion(int userId, int id){
        return userService.getAnsweredQuestions(userId).stream()
                .anyMatch(question -> question.getId() == id);
    }

    private boolean userHasThisClarificationRequest(int userId, int id){
        return userService.getClarificationRequests(userId).stream()
                .anyMatch(clarificationRequest -> clarificationRequest.getId() == id);
    }

    private boolean userHasThisClarification(int userId, int id){
        return userService.getClarifications(userId).stream()
                .anyMatch(clarification -> clarification.getId() == id);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
