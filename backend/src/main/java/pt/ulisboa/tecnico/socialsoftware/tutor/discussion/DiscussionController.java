package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ClarificationRequestDto;

import javax.validation.Valid;

@RestController
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;



    @PostMapping("clarificationRequests/{clarificationRequestId}/clarifications")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#clarificationRequestId, 'CLARIFICATION.REQUEST.ACCESS')")
    public ClarificationDto createClarification(@PathVariable int clarificationRequestId, @Valid @RequestBody ClarificationDto clarification){
        return this.discussionService.createClarification(clarificationRequestId, clarification);
    }

    @GetMapping("clarifications/{clarificationId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS')")
    public ClarificationDto getClarification(@PathVariable int clarificationId) {
        return this.discussionService.findClarificationById(clarificationId);
    }

}
