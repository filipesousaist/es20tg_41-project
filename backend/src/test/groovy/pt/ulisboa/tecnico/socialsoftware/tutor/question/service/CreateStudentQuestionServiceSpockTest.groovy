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
        //the student creates an empty question
        expect: false
    }

    def "user not consistent"(){
        //One student creates a studentQuestion, but student details in studentQuestion are from another student
        expect: false
    }
}