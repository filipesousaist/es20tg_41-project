package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DiscussionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto submitClarificationRequest(Integer questionId, ClarificationRequestDto clarificationRequestDto){

        Question question = getQuestion(questionId);

        User user = getUser(clarificationRequestDto.getUsername());

        checkClarificationRequest(clarificationRequestDto.getTitle(), ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMTPY);

        checkClarificationRequest(clarificationRequestDto.getText(), ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMTPY);

        List<Question> questions = userService.getAnsweredQuestions(clarificationRequestDto.getUsername());

        checkQuestionAnswer(question, questions);

        checkForDuplicateClarificationRequest(question, user);

        ClarificationRequest clarificationRequest = createClarificationRequest(clarificationRequestDto, question, user);

        return new ClarificationRequestDto(clarificationRequest);
    }

    private User getUser(String username) {
        User user = this.userService.findByUsername(username);
        if(user == null) throw new TutorException(ErrorMessage.USER_NOT_FOUND);
        return user;
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

    private void checkClarificationRequest(String title, ErrorMessage clarificationRequestTitleIsEmtpy) {
        if (title == null || title.trim().equals(""))
            throw new TutorException(clarificationRequestTitleIsEmtpy);
    }

    private CourseExecution getCourseExecution(Integer courseExectutionId) {
        return this.courseExecutionRepository.findById(courseExectutionId)
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

        User teacher = getUser(clarificationDto.getUsername());

        ClarificationRequest clarificationRequest = getClarificationRequest(clarificationRequestId);

        List<Course> courses = getCourses(teacher);

        checkQuestionCourse(teacher, clarificationRequest, courses);

        checkClarificationText(clarificationDto);

        Clarification clarification = createClarification(clarificationDto, teacher, clarificationRequest);

        return new ClarificationDto(clarification);
    }

    private Clarification createClarification(ClarificationDto clarificationDto, User teacher, ClarificationRequest clarificationRequest) {
        Clarification clarification = new Clarification(teacher, clarificationRequest, clarificationDto);
        clarificationRequest.setClarification(clarification);
        teacher.addClarification(clarification);
        discussionRepository.save(clarification);
        return clarification;
    }

    private void checkClarificationText(ClarificationDto clarificationDto) {
        if(clarificationDto.getText() == null) {
            throw new TutorException(ErrorMessage.CLARIFICATION_NOT_CONSISTENT, "Text");
        }

        else if (clarificationDto.getText().trim().equals("")){
            throw  new TutorException(ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY);
        }
    }

    private void checkQuestionCourse(User teacher, ClarificationRequest clarificationRequest, List<Course> courses) {
        if(!courses.contains(clarificationRequest.getQuestion().getCourse())){
            throw new TutorException(ErrorMessage.TEACHER_COURSE_EXECUTION_MISMATCH,
                    teacher.getId(), clarificationRequest.getQuestion().getCourse().getId());
        }
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


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findClarificationRequestCourse(Integer clarificationRequestId){
        return this.clarificationRequestRepository.findById(clarificationRequestId)
                .map(ClarificationRequest::getQuestion)
                .map(Question::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_REQUEST_NOT_FOUND, clarificationRequestId));

    }



}
