package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;

@Service
public class DiscussionService {
    public ClarificationRequestDto createClarificationRequest(Integer questionId, Integer userId, ClarificationRequestDto clarificationRequestDto){

        return new ClarificationRequestDto();
    }

    public ClarificationDto createClarification(Integer userId, Integer clarificationRequestId, ClarificationDto clarificationDto){

        return new ClarificationDto();
    }

    public Integer getMaxClarificationRequestKey(){
        return 0;
    }
}
