package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
//import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.StudentQuestionRepository
import spock.lang.Specification



@DataJpaTest
class CreateStudentQuestionTest extends Specification{

    //@Autowired
    //StudentQuestionRepository StudentQuestionRepository


    def studentQuestion


    def setup(){

        studentQuestion = new StudentQuestion()
    }

    def "create studentQuestion"(){
        //A studentQuestion is created
        expect: false
    }

    def "student question is not created"(){
        //an exception is thrown
        expect: false
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}