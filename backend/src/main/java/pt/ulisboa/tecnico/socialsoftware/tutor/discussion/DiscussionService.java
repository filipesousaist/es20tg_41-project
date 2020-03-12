package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

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
    public ClarificationRequestDto createClarificationRequest(Integer courseExectutionId, Integer questionId, Integer userId, ClarificationRequestDto clarificationRequestDto){

        Question question = this.questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));

        User user = this.userService.findUserById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        CourseExecution courseExecution = this.courseExecutionRepository.findById(courseExectutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND));

        if(clarificationRequestDto.getTitle() == null || clarificationRequestDto.getTitle().trim().equals(""))
            throw new TutorException(ErrorMessage.CLARIFICATION_REQUEST_TITLE_IS_EMTPY);

        if(clarificationRequestDto.getText() == null || clarificationRequestDto.getText().trim().equals(""))
            throw new TutorException(ErrorMessage.CLARIFICATION_REQUEST_TEXT_IS_EMTPY);

        List<Question> questions = this.statementService.getSolvedQuizzes(user.getUsername(), courseExecution.getId())
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

        if(!questions.contains(question))
            throw new TutorException(ErrorMessage.QUESTION_ANSWER_NOT_FOUND);

        if(this.clarificationRequestRepository.findByKey(clarificationRequestDto.getKey()).isPresent())
            throw new TutorException(ErrorMessage.CLARIFICATION_REQUEST_ALREADY_EXISTS);

        if(clarificationRequestDto.getKey() == null)
            clarificationRequestDto.setKey(getMaxClarificationRequestKey()+1);

        ClarificationRequest clarificationRequest = new ClarificationRequest(user, question, clarificationRequestDto);

        this.clarificationRequestRepository.save(clarificationRequest);
        question.addClarificationRequest(clarificationRequest);
        user.addClarificationRequest(clarificationRequest);

        return new ClarificationRequestDto(clarificationRequest);
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


}
