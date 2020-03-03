package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto



import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import spock.lang.Specification

class createClarificationRequestServiceSpockTest extends Specification {

    static final String QUESTION_1_TITLE = "question1"
    static final String QUESTION_KEY = 1
    static final String QUESTION_1_CONTENT = "o que é uma classe?"

    DiscussionService discussionService

    def "the question exists and create Clarification Request" (){
        // creates a ClarificationRequest
        given: "a question"
        def question = new Question()
        question.setTitle(QUESTION_1_TITLE)
        question.setKey(QUESTION_KEY)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        and: "a questionDto"
        def questionDto = new QuestionDto()
        when:
        def result = discussionService.createClarificationRequest(questionDto)
    }

    def "the question does not exist" (){
        // throws exception
        expect: false
    }

    def "the question has not been answered by the logged in student" (){
        // throws exception
        expect: false
    }
}
