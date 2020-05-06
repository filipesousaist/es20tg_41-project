package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Comment;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.CommentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.CommentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Collection;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;


import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DiscussionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto submitClarificationRequest(Integer questionId, ClarificationRequestDto clarificationRequestDto){

        Question question = getQuestion(questionId);

        User user = getUser(clarificationRequestDto.getUserId());

        checkClarificationRequest(clarificationRequestDto.getTitle(), ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMPTY);

        checkClarificationRequest(clarificationRequestDto.getText(), ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMPTY);

        List<Question> questions = user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .collect(Collectors.toList());


        checkQuestionAnswer(question, questions);

        checkForDuplicateClarificationRequest(question, user);

        ClarificationRequest clarificationRequest = createClarificationRequest(clarificationRequestDto, question, user);
        clarificationRequest.setCreationDate(DateHandler.now());
        return new ClarificationRequestDto(clarificationRequest);
    }

    private User getUser(Integer id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new TutorException(USER_NOT_FOUND,id));
    }

    private ClarificationRequest createClarificationRequest(ClarificationRequestDto clarificationRequestDto, Question question, User user) {
        ClarificationRequest clarificationRequest = new ClarificationRequest(user, question, clarificationRequestDto);
        this.clarificationRequestRepository.save(clarificationRequest);
        question.addClarificationRequest(clarificationRequest);
        user.addClarificationRequest(clarificationRequest);
        return clarificationRequest;
    }

    private void checkForDuplicateClarificationRequest(Question question, User user) {
        if(this.clarificationRequestRepository.findByUserIdAndQuestionId(user.getId(), question.getId()).isPresent())
            throw new TutorException(ErrorMessage.CLARIFICATION_REQUEST_ALREADY_EXISTS);
    }

    private void checkQuestionAnswer(Question question, List<Question> questions) {
        if(!questions.contains(question))
            throw new TutorException(ErrorMessage.QUESTION_ANSWER_NOT_FOUND);
    }

    private void checkClarificationRequest(String title, ErrorMessage clarificationRequestTitleIsEmpty) {
        if (title == null || title.trim().equals(""))
            throw new TutorException(clarificationRequestTitleIsEmpty);
    }

    private CourseExecution getCourseExecution(Integer courseExecutionId) {
        return this.courseExecutionRepository.findById(courseExecutionId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND));
    }

    private Question getQuestion(Integer questionId) {
        return this.questionRepository.findById(questionId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));
    }



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(int clarificationRequestId, ClarificationDto clarificationDto){

        User teacher = getUser(clarificationDto.getUserId());

        ClarificationRequest clarificationRequest = getClarificationRequest(clarificationRequestId);

        List<Course> courses = getCourses(teacher);

        checkQuestionCourse(teacher, clarificationRequest, courses);

        checkClarificationText(clarificationDto);

        checkForDuplicateClarification(clarificationRequest, teacher);

        Clarification clarification = createClarification(clarificationDto, teacher, clarificationRequest);

        clarification.setCreationDate(DateHandler.now());

        return new ClarificationDto(clarification);
    }

    private Clarification createClarification(ClarificationDto clarificationDto, User teacher, ClarificationRequest clarificationRequest) {
        Clarification clarification = new Clarification(teacher, clarificationRequest, clarificationDto);
        clarificationRequest.setClarification(clarification);
        teacher.addClarification(clarification);
        clarificationRepository.save(clarification);
        return clarification;
    }

    private void checkForDuplicateClarification(ClarificationRequest clarificationRequest, User teacher){
        if(this.clarificationRepository.findByUserIdAndClarificationRequestId(teacher.getId(), clarificationRequest.getId()).isPresent())
            throw new TutorException(ErrorMessage.CLARIFICATION_ALREADY_EXISTS);
    }

    private void checkClarificationText(ClarificationDto clarificationDto) {
        if(clarificationDto.getText() == null) {
            throw new TutorException(ErrorMessage.CLARIFICATION_NOT_CONSISTENT, "Text");
        }

        else if (clarificationDto.getText().trim().equals("")){
            throw new TutorException(ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY);
        }
    }

    private void checkClarificationSummary(ClarificationDto clarificationDto) {
        if(clarificationDto.getSummary() == null) {
            throw new TutorException(CLARIFICATION_NOT_CONSISTENT, "Summary");
        }

        else if (clarificationDto.getSummary().trim().equals("")) {
            throw new TutorException(ErrorMessage.CLARIFICATION_SUMMARY_IS_EMPTY);
        }
    }

    private void checkQuestionCourse(User teacher, ClarificationRequest clarificationRequest, List<Course> courses) {
        if(!courses.contains(clarificationRequest.getQuestion().getCourse())){
            throw new TutorException(ErrorMessage.TEACHER_COURSE_EXECUTION_MISMATCH,
                    teacher.getId(), clarificationRequest.getQuestion().getCourse().getId());
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarificationSummary(int clarificationId, ClarificationDto clarificationDto){

        Clarification clarification = getClarification(clarificationId);

        checkClarificationSummary(clarificationDto);

        clarification.setSummary(clarificationDto.getSummary());

        return new ClarificationDto(clarification);
    }

    private List<Course> getCourses(User teacher) {
        return teacher.getCourseExecutions()
                    .stream()
                    .map(CourseExecution::getCourse)
                    .collect(Collectors.toList());
    }

    private ClarificationRequest getClarificationRequest(Integer clarificationRequestId) {
        return clarificationRequestRepository.findById(clarificationRequestId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND));
    }

    private Clarification getClarification(Integer clarificationId) {
        return clarificationRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND));
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findClarificationRequestCourse(Integer clarificationRequestId){
        return this.clarificationRequestRepository.findById(clarificationRequestId)
                .map(ClarificationRequest::getQuestion)
                .map(Question::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_REQUEST_NOT_FOUND, clarificationRequestId));

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto getClarificationByRequest(Integer clarificationRequestId){
        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationRequestId)
                .orElseThrow(() -> new TutorException(CLARIFICATION_REQUEST_NOT_FOUND));

        if (clarificationRequest.getClarification() == null)
            throw new TutorException(CLARIFICATION_REQUEST_HAS_NO_CLARIFICATION);

        return new ClarificationDto(clarificationRequest.getClarification());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CommentDto createComment(Integer clarificationId, CommentDto commentDto){
        Clarification clarification = checkClarifcation(clarificationId);

        checkCommentText(commentDto);

        User user = getUser(commentDto, clarification);

        Comment comment = getComment(commentDto, clarification, user);
        return new CommentDto(comment);
    }

    private Comment getComment(CommentDto commentDto, Clarification clarification, User user) {
        Comment comment = new Comment(commentDto, user);
        comment.setCreationDate(DateHandler.now());
        commentRepository.save(comment);
        clarification.addComment(comment);
        return comment;
    }

    private Clarification checkClarifcation(Integer clarificationId) {
        return clarificationRepository.findById(clarificationId)
                    .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    private User getUser(CommentDto commentDto, Clarification clarification) {
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, commentDto.getUserId()));
        if(!user.getId().equals(clarification.getTeacher().getId())
           && !user.getId().equals(clarification.getClarificationRequest().getStudent().getId()))
            throw new TutorException(COMMENT_INVALID_USER, user.getId());
        return user;
    }

    private void checkCommentText(CommentDto commentDto) {
        String text = commentDto.getText();
        if(text == null || text.trim().equals(""))
            throw new TutorException(COMMENT_EMPTY_TEXT);
    }
}
