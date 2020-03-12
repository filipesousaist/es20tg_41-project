import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import spock.lang.Specification

class CreateQuestionEvaluationServiceSpockTest extends Specification {
    @Autowired
    def studentQuestionService

    def setup() {
        studentQuestionService = new StudentQuestionService()
    }

    def "studentQuestion exists and justification is filled in"() {
        expect: false
    }

    def "studentQuestion does not exist"() {
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
}
