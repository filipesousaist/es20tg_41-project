package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiscussionService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository;

    @Autowired
    DiscussionRepository discussionRepository;

    @PersistenceContext
    EntityManager entityManager;


    public ClarificationRequestDto createClarificationRequest(Integer questionId, Integer userId, ClarificationRequestDto clarificationRequestDto){

        return new ClarificationRequestDto();
    }

    public ClarificationDto createClarification(Integer userId, Integer clarificationRequestId, ClarificationDto clarificationDto){

        User teacher = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));
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
        return 0;
    }

    public Clarification findByKey(Integer key) {
        return this.discussionRepository.findByKey(key);
    }

    public Integer getMaxClarificationKey() {
        Integer result = discussionRepository.getMaxClarificationKey();
        return result != null ? result : 0;
    }
}
