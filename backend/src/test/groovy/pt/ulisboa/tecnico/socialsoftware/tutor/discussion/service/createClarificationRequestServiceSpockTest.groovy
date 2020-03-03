package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto



import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class createClarificationRequestServiceSpockTest extends Specification {

    static final String QUESTION_1_TITLE = "question1"
    static final String QUESTION_1_CONTENT = "o que é uma classe?"

    static final String CLARIFICATION_TEXT = "Não percebi porque é que a opção correta é 1)."

    DiscussionService discussionService

    def "the question exists, the student exists, the student answered the question and create Clarification Request" (){
        // creates a ClarificationRequest
        expect: false
    }

    def "the question does not exist" (){
        // throws exception
        expect: false
    }

    def "the student is null" (){
        //throws exception
        expect: false
    }

    def "the question has not been answered by the student" (){
        // throws exception
        expect: false
    }
    
    def "the clarification text is null" (){
        //throws exception
        expect: false
    }

    def "the clarification text is blank" (){
        //throws exception
        expect: false
    }

    def "there is a clarification already created for a certain question by a certain student" (){
        // throws exception
        expect: false
    }
}
