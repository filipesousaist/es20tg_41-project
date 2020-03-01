package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import spock.lang.Specification

class createClarificationRequestServiceSpockTest extends Specification {
    def discussionService

    def setup(){
        discussionService = new DiscussionService()
    }

    def "the question exists and create Clarification Request" (){
        // creates a ClarificationRequest
        expect: false
    }

    def "the question does not exist" (){
        // throws exception
        expect: false
    }

    def "the question has not been answered by the logged in student" (){
        // throws excption
        expect: false
    }
}
