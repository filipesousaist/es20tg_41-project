package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

class CreateStudentQuestionServiceSpockTest extends Specification{

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

    def "user not consistent"(){
        //an exception is thrown
        expect: false
    }
}