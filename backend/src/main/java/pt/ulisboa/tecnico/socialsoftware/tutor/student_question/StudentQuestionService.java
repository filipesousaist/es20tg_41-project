package pt.ulisboa.tecnico.socialsoftware.tutor.student_question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.QuestionEvaluation;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.QuestionEvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudentQuestionService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;


    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto createStudentQuestion(int courseId, Integer userId, StudentQuestionDto studentQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StudentQuestion studentQuestion = new StudentQuestion(course, user,studentQuestionDto);
        entityManager.persist(studentQuestion);

        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionEvaluationDto createQuestionEvaluation(int teacherId, int studentQuestionId, QuestionEvaluationDto qeDto) {
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId).orElseThrow(
            () -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        QuestionEvaluation questionEvaluation = new QuestionEvaluation(teacher, studentQuestion, qeDto);
        entityManager.persist(questionEvaluation);

        return new QuestionEvaluationDto(questionEvaluation);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> getStudentQuestions(int userId) {
        return studentQuestionRepository.getByUserId(userId).stream()
            .map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> getNonRejectedStudentQuestions(int courseId) {
        return studentQuestionRepository.findAll().stream()
            .filter(studentQuestion ->
                studentQuestion.getQuestion().getCourse().getId() == courseId &&
                studentQuestion.getStatus() != StudentQuestion.Status.REJECTED
            )
            .map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto makeStudentQuestionAvailable(int studentQuestionId) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId).orElseThrow(
            () -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        studentQuestion.makeAvailable();
        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto updateStudentQuestion(int userId, int studentQuestionId, StudentQuestionDto studentQuestionDto) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId).orElseThrow(
                () -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.getRole() == User.Role.TEACHER){
            studentQuestion.updateByTeacher(studentQuestionDto);
        }
        else {
            studentQuestion.updateByStudent(studentQuestionDto);
        }

        return new StudentQuestionDto(studentQuestion);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeMaxStudentQuestions(User user, int value) {
        user.setMaxStudentQuestions(value);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto findById(Integer studentQuestionId) {
        return this.studentQuestionRepository.findById(studentQuestionId).map(StudentQuestionDto::new)
                .orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));
    }
}
