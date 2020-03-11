package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import spock.lang.Specification

class createClarificationServiceSpockTest extends Specification {
    def discussionService

    def setup(){
        discussionService = new DiscussionService()
    }

    def "create Clarification" (){
        // Creates a Clarification
        expect: false
    }

    def "the request does not exist" (){
        // Throws Exception
        expect: false
    }

    def "Clarification with null string as clarification" (){
        //Throws Exception
        expect: false
    }

    def "Clarification with empty/only blank spaces" (){
        //Throws Exception
        expect: false
    }
}

