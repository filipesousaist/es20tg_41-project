package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;

@RestController
public class DiscussionController {
    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/questions/{questionId}/clarification_request")
    public ClarificationRequestDto submitClarificationRequest(@PathVariable int questionId, @RequestBody ClarificationRequestDto clarificationRequestDto){
        return discussionService.submitClarificationRequest(questionId, clarificationRequestDto);
    }
}
