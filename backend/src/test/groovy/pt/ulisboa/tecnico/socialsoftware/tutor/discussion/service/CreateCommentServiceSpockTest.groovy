package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Comment
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.CommentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.CommentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CreateCommentServiceSpockTest extends Specification{

    static final String NAME_1 = "John"
    static final String NAME_2 = "Bob"
    static final String NAME_3 = "Alice"
    static final String USERNAME_1 = "John"
    static final String USERNAME_2 = "Bob"
    static final String USERNAME_3 = "Alice"

    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"

    static final String COMMENT_TEXT1 = "Comment text1"
    static final String COMMENT_TEXT2 = "Comment text2"


    @Autowired
    DiscussionService discussionService

    @Autowired
    UserService userService

    @Autowired
    CommentRepository commentRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    QuestionRepository questionRepository



    User u1
    User u2
    User u3

    Question question

    ClarificationRequest clarificationRequest
    Clarification clarification
    Comment comment
    CommentDto commentDto
    CommentDto commentDto1

    def setup(){
        u1 = userService.createUser(NAME_1, USERNAME_1, User.Role.STUDENT);
        u2 = userService.createUser(NAME_2, USERNAME_2, User.Role.STUDENT);
        u3 = userService.createUser(NAME_3, USERNAME_3, User.Role.TEACHER);

        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        questionRepository.save(question)

    }

    def "the clarification exists, the user created the clarifiction request and create a Comment" () {
        given: "a clarification request"
        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setQuestion(question)
        clarificationRequest.setStudent(u1)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification"
        clarification = new Clarification()
        clarification.setTeacher(u3)
        clarification.setClarificationRequest(clarificationRequest)
        clarificationRepository.save(clarification)


        and: "a comment dto"
        commentDto = new CommentDto()
        commentDto.setUserId(u1.getId())
        commentDto.setText(COMMENT_TEXT1)

        when:
        discussionService.createComment(clarification.getId(), commentDto)

        then: "the comment is added to the repository"
        commentRepository.findAll().size() == 1
        def result = commentRepository.findAll().get(0)

        and: "the comment values are correct"
        result.getUser().getId() == u1.getId()
        result.getText() == COMMENT_TEXT1

        and: "the comment gets added to the clarification"
        clarification.getComments() != null
        clarification.getComments().size() == 1
        def comm = clarification.getComments().get(0)
        comm.getUser().getId() == u1.getId()
        comm.getText() == COMMENT_TEXT1
    }

    def "the clarification exists, the user created the clarifiction and create a Comment" () {
        given: "a clarification request"
        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setQuestion(question)
        clarificationRequest.setStudent(u1)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification"
        clarification = new Clarification()
        clarification.setTeacher(u3)
        clarification.setClarificationRequest(clarificationRequest)
        clarificationRepository.save(clarification)


        and: "a comment dto"
        commentDto = new CommentDto()
        commentDto.setUserId(u3.getId())
        commentDto.setText(COMMENT_TEXT1)

        when:
        discussionService.createComment(clarification.getId(), commentDto)

        then: "the comment is added to the repository"
        commentRepository.findAll().size() == 1
        def result = commentRepository.findAll().get(0)

        and: "the comment values are correct"
        result.getUser().getId() == u3.getId()
        result.getText() == COMMENT_TEXT1

        and: "the comment gets added to the clarification"
        clarification.getComments() != null
        clarification.getComments().size() == 1
        def comm = clarification.getComments().get(0)
        comm.getUser().getId() == u3.getId()
        comm.getText() == COMMENT_TEXT1
    }

    def "the clarification does not exist" () {
        given: "a comment dto"
        commentDto = new CommentDto()
        commentDto.setUserId(u3.getId())
        commentDto.setText(COMMENT_TEXT1)

        when:
        discussionService.createComment(-1, commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_NOT_FOUND

        and: "the comment is not added to the repository"
        commentRepository.count() == 0L
    }

    def "the user has not asked the clarification request or answered with a clarification" () {
        given: "a clarification request"
        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setQuestion(question)
        clarificationRequest.setStudent(u1)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification"
        clarification = new Clarification()
        clarification.setTeacher(u3)
        clarification.setClarificationRequest(clarificationRequest)
        clarificationRepository.save(clarification)



        and: "a comment dto"
        commentDto = new CommentDto()
        commentDto.setUserId(u2.getId())
        commentDto.setText(COMMENT_TEXT1)

        when:
        discussionService.createComment(clarification.getId(), commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_INVALID_USER

        and: "the comment is not added to the repository"
        commentRepository.count() == 0L

        and: "the comment is not added to the clarification request"
        clarification.getComments().size() == 0


    }

    @Unroll
    def "invalid arguments:text=#text" () {
        given: "a clarification request"
        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setQuestion(question)
        clarificationRequest.setStudent(u1)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification"
        clarification = new Clarification()
        clarification.setTeacher(u3)
        clarification.setClarificationRequest(clarificationRequest)
        clarificationRepository.save(clarification)

        and: "a comment dto"
        commentDto = new CommentDto()
        commentDto.setUserId(u1.getId())
        commentDto.setText(text)

        when:
        discussionService.createComment(clarification.getId(), commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage

        and: "the comment is not added to the repository"
        commentRepository.count() == 0L

        and: "the comment is not added to the clarification request"
        clarification.getComments().size() == 0

        where:
        text                   || errorMessage
        "   "                  || ErrorMessage.COMMENT_EMPTY_TEXT
        null                   || ErrorMessage.COMMENT_EMPTY_TEXT

    }



    @TestConfiguration
    static class ServiceImplTestContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        DiscussionService discussionService() {
            return new DiscussionService()
        }
    }
}
