package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class GetPublicClarificationRequestsServiceSpockTest extends Specification {
    private static String QUESTION_TITLE = "Question title"
    private static String QUESTION_CONTENT = "Question content";

    private static String REQUEST_TITLE1 = "Request Title 1"
    private static String REQUEST_TITLE2 = "Request Title 2"

    private static String REQUEST_TEXT1 = "Request Text 1"
    private static String REQUEST_TEXT2 = "Request Text 2"

    private static String CLARIFICATION_TEXT1 = "Clarification Text 1"
    private static String CLARIFICATION_TEXT2 = "Clarification Text 2"

    private static String CLARIFICATION_SUMMARY1 = "Clarification Summary 1"
    private static String CLARIFICATION_SUMMARY2 = "Clarification Summary 2"

    private static String USERNAME1 = "username1"
    private static String USERNAME2 = "username2"
    private static String USERNAME3 = "username3"


    @Autowired
    QuestionRepository questionRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    DiscussionService discussionService


    User student1
    User student2

    User teacher

    Question question
    ClarificationRequest request1
    ClarificationRequest request2
    ClarificationRequest request3


    Clarification clarification1
    Clarification clarification2
    Clarification clarification3




    def setup(){
        student1 = new User()
        student1.setRole(User.Role.STUDENT)
        student1.setUsername(USERNAME1)
        student1.setKey(1)
        userRepository.save(student1)

        student2 = new User()
        student2.setRole(User.Role.STUDENT)
        student2.setUsername(USERNAME2)
        student2.setKey(2)
        userRepository.save(student2)


        teacher = new User()
        teacher.setRole(User.Role.TEACHER)
        teacher.setUsername(USERNAME3)
        teacher.setKey(3)
        userRepository.save(teacher)


        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        questionRepository.save(question)

        request1 = new ClarificationRequest()
        request1.setTitle(REQUEST_TITLE1)
        request1.setText(REQUEST_TEXT1)
        request1.setPrivacy(true)
        request1.setQuestion(question)
        request1.setStudent(student1)
        clarificationRequestRepository.save(request1)
        question.addClarificationRequest(request1)

        request2 = new ClarificationRequest()
        request2.setTitle(REQUEST_TITLE1)
        request2.setText(REQUEST_TEXT1)
        request2.setPrivacy(true)
        request2.setQuestion(question)
        request2.setStudent(student2)
        clarificationRequestRepository.save(request2)
        question.addClarificationRequest(request2)


        // only this request should to student1 appear because it was not asked by him and it's public
        request3 = new ClarificationRequest()
        request3.setTitle(REQUEST_TITLE2)
        request3.setText(REQUEST_TEXT2)
        request3.setPrivacy(false)
        request3.setQuestion(question)
        request3.setStudent(student2)
        clarificationRequestRepository.save(request3)
        question.addClarificationRequest(request3)


        clarification1 = new Clarification()
        clarification1.setText(CLARIFICATION_TEXT1)
        clarification1.setClarificationRequest(request1)
        clarification1.setSummary(CLARIFICATION_SUMMARY1)
        clarification1.setTeacher(teacher)
        clarificationRepository.save(clarification1)

        clarification2 = new Clarification()
        clarification2.setText(CLARIFICATION_TEXT2)
        clarification2.setClarificationRequest(request2)
        clarification2.setSummary(CLARIFICATION_SUMMARY2)
        clarification1.setTeacher(teacher)
        clarificationRepository.save(clarification2)

        clarification3 = new Clarification()
        clarification3.setText(CLARIFICATION_TEXT2)
        clarification3.setClarificationRequest(request3)
        clarification3.setSummary(CLARIFICATION_SUMMARY2)
        clarification3.setTeacher(teacher)
        clarificationRepository.save(clarification3)

    }

    def "the user exists, the questions exists and retrieve all public clarification requests, minus the ones created by the user" (){
        given:" a user id"
        int userId = student1.getId()

        and: "a question id"
        int questionId = question.getId()

        when:
        def result = discussionService.getPublicClarificationRequests(questionId)

        then:
        result.size() == 1
        result.get(0).getId() == request3.getId()
        result.get(0).getTitle() == request3.getTitle()
        result.get(0).getText() == request3.getText()


    }


    def "the question does not exist" (){
        given:" a user id"
        int userId = student1.getId()

        and: "an invalid question id"
        int questionId = -1

        when:
        discussionService.getPublicClarificationRequests(questionId)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.QUESTION_NOT_FOUND
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
