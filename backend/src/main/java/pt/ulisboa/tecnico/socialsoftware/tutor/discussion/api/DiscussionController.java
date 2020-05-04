package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;

import javax.validation.Valid;

@RestController
public class DiscussionController {
    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/questions/{questionId}/clarification_request")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'QUESTION.ACCESS') and hasPermission(#questionId, 'QUESTION.ANSWERED')")
    public ClarificationRequestDto submitClarificationRequest(@PathVariable int questionId, @RequestBody ClarificationRequestDto clarificationRequestDto){
        return discussionService.submitClarificationRequest(questionId, clarificationRequestDto);

    }

    @PostMapping("clarificationRequests/{clarificationRequestId}/clarifications")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#clarificationRequestId, 'CLARIFICATION.REQUEST.ACCESS')")
    public ClarificationDto createClarification(@PathVariable int clarificationRequestId, @Valid @RequestBody ClarificationDto clarification){
        return this.discussionService.createClarification(clarificationRequestId, clarification);
    }

    @GetMapping("/clarificationRequests/{clarificationRequestId}/clarification")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#clarificationRequestId, 'CLARIFICATION.ACCESS')")
    public ClarificationDto getClarification(@PathVariable int clarificationRequestId){
        return this.discussionService.getClarificationByRequest(clarificationRequestId);
    }

}
