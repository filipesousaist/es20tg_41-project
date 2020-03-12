import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

class CreateQuestionEvaluationServiceSpockTest extends Specification {
    private static final String QUESTION_TITLE = "Question 1";
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    @Autowired
    private StudentQuestionService studentQuestionService;

    def setup() {

    }

    def "studentQuestion exists and justification is filled in"() {
        expect: false
    }

    def "studentQuestion is null"() {
        expect: false
    }

    def "justification is null"() {
        expect: false
    }

    def "justification is empty"() {
        expect: false
    }

    def "teacher does not teach the course of the question"() {
        expect: false
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
