package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, User.Role role) {

        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, getMaxUserNumber() + 1, role);
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getEnrolledCoursesAcronyms(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getEnrolledCoursesAcronyms();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
        courseExecution.addUser(user);
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

       return xmlExporter.export(userRepository.findAll());
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationRequest> getClarificationRequests(int userId){
        return new ArrayList<>(userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId))
                .getClarificationRequests());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Clarification> getClarifications(int userId){
        return new ArrayList<>(userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId))
                .getClarifications());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoTeacher() {
        User user = this.userRepository.findByUsername(Demo.TEACHER_USERNAME);
        if (user == null)
            return createUser("Demo Teacher", Demo.TEACHER_USERNAME, User.Role.TEACHER);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoStudent() {
        User user = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);
        if (user == null)
            return createUser("Demo Student", Demo.STUDENT_USERNAME, User.Role.STUDENT);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoAdmin() {
        User user =  this.userRepository.findByUsername(Demo.ADMIN_USERNAME);
        if (user == null)
            return createUser("Demo Admin", Demo.ADMIN_USERNAME, User.Role.DEMO_ADMIN);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> getAnsweredQuestions(int userId){
        return userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId))
                .getQuizAnswers()
                .stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .distinct()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = DateHandler.now().toString();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, User.Role.STUDENT);

        User demoUser = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);

        CourseExecution courseExecution = demoUser.getCourseExecutions().stream().findAny().orElse(null);

        if (courseExecution != null) {
            courseExecution.addUser(newDemoUser);
            newDemoUser.addCourse(courseExecution);
        }

        return newDemoUser;
    }

}
