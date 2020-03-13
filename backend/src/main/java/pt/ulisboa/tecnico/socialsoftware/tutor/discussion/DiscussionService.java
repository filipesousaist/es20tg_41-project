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

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto submitClarificationRequest(Integer courseExectutionId, Integer questionId, Integer userId, ClarificationRequestDto clarificationRequestDto){

        Question question = getQuestion(questionId);

        User user = getUser(this.userService.findUserById(userId));

        CourseExecution courseExecution = getCourseExecution(courseExectutionId);

        checkClarificationRequest(clarificationRequestDto.getTitle(), ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMTPY);

        checkClarificationRequest(clarificationRequestDto.getText(), ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMTPY);

        List<Question> questions = getQuestions(user, courseExecution);

        checkQuestionAnswer(question, questions);

        checkForDuplicateClarificationRequest(clarificationRequestDto);

        if(clarificationRequestDto.getKey() == null)
            clarificationRequestDto.setKey(getMaxClarificationRequestKey()+1);

        ClarificationRequest clarificationRequest = createClarificationRequest(clarificationRequestDto, question, user);

        return new ClarificationRequestDto(clarificationRequest);
    }

    private ClarificationRequest createClarificationRequest(ClarificationRequestDto clarificationRequestDto, Question question, User user) {
        ClarificationRequest clarificationRequest = new ClarificationRequest(user, question, clarificationRequestDto);
        this.clarificationRequestRepository.save(clarificationRequest);
        question.addClarificationRequest(clarificationRequest);
        user.addClarificationRequest(clarificationRequest);
        return clarificationRequest;
    }

    private void checkForDuplicateClarificationRequest(ClarificationRequestDto clarificationRequestDto) {
        if(this.clarificationRequestRepository.findByKey(clarificationRequestDto.getKey()).isPresent())
            throw new TutorException(ErrorMessage.CLARIFICATION_REQUEST_ALREADY_EXISTS);
    }

    private void checkQuestionAnswer(Question question, List<Question> questions) {
        if(!questions.contains(question))
            throw new TutorException(ErrorMessage.QUESTION_ANSWER_NOT_FOUND);
    }

    private List<Question> getQuestions(User user, CourseExecution courseExecution) {
        return this.statementService.getSolvedQuizzes(user.getUsername(), courseExecution.getId())
                    .stream()
                    .map(SolvedQuizDto::getStatementQuiz)
                    .map(StatementQuizDto::getQuizAnswerId)
                    .map(this.quizAnswerRepository::findById)
                    .map(Optional::get)
                    .map(QuizAnswer::getQuiz)
                    .map(Quiz::getQuizQuestions)
                    .flatMap(Collection::stream)
                    .map(QuizQuestion::getQuestion)
                    .collect(Collectors.toList());
    }

    private void checkClarificationRequest(String title, ErrorMessage clarificationRequestTitleIsEmtpy) {
        if (title == null || title.trim().equals(""))
            throw new TutorException(clarificationRequestTitleIsEmtpy);
    }

    private CourseExecution getCourseExecution(Integer courseExectutionId) {
        return this.courseExecutionRepository.findById(courseExectutionId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND));
    }

    private User getUser(Optional<User> userById) {
        return userById
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));
    }

    private Question getQuestion(Integer questionId) {
        return this.questionRepository.findById(questionId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));
    }




    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(Integer userId, Integer clarificationRequestId, ClarificationDto clarificationDto){

        User teacher = getUser(userRepository.findById(userId));
        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationRequestId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND));

        List<Course> courses = teacher.getCourseExecutions()
                .stream()
                .map(CourseExecution::getCourse)
                .collect(Collectors.toList());

        if(!courses.contains(clarificationRequest.getQuestion().getCourse())){
            throw new TutorException(ErrorMessage.TEACHER_COURSE_EXECUTION_MISMATCH,
                    teacher.getId(), clarificationRequest.getQuestion().getCourse().getId());
        }

        if(clarificationDto.getText() == null) {
            throw new TutorException(ErrorMessage.CLARIFICATION_NOT_CONSISTENT, "Text");
        }

        else if (clarificationDto.getText().trim().equals("")){
            throw  new TutorException(ErrorMessage.CLARIFICATION_TEXT_IS_EMPTY);
        }

        Clarification clarification = new Clarification(teacher, clarificationRequest, clarificationDto);
        clarification.setKey(getMaxClarificationKey()+1);
        entityManager.persist(clarification);
        return new ClarificationDto(clarification);
    }

    public Integer getMaxClarificationRequestKey(){
        Integer key = clarificationRequestRepository.findMaxKey();
        return key != null ? key : 0;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationRequestDto> getClarificationRequestsByStudent(Integer userId){
        return this.clarificationRequestRepository.findByUserId(userId)
                .stream().map(ClarificationRequestDto::new).collect(Collectors.toList());
    }

    public Clarification findByKey(Integer key) {
        return this.discussionRepository.findByKey(key);
    }

    public Integer getMaxClarificationKey() {
        Integer result = discussionRepository.getMaxClarificationKey();
        return result != null ? result : 0;
    }
}
