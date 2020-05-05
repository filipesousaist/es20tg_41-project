package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class UpdateStudentQuestionServiceSpockTest extends Specification{
    private static final String COURSE_NAME = "Engenharia de Software"
    private static final String ACRONYM = "ES"
    private static final String ACADEMIC_TERM = "2 SEM"
    private static final String QUESTION_TITLE = "Question 1"
    public static final String NEW_QUESTION_TITLE = 'new question title'
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    private static final String OPTION1_CONTENT = "Option 1"
    public static final String NEW_OPTION_CONTENT = "new optionId content"
    private static final String OPTION2_CONTENT = "Option 2"
    private static final String STUDENT_NAME = "Student Name"
    private static final String STUDENT_USERNAME = "Student Username"
    private static final int STUDENT_KEY = 1

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    OptionRepository optionRepository

    def studentQuestion
    def studentQuestionDto
    def student
    def option1
    def option2
    def question

    def setup() {
        // Create course and course execution
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create question
        question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.DISABLED)

        // Create 2 options, and add them to question
        option1 = new Option()
        option1.setContent(OPTION1_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(0)
        option1.setQuestion(question)
        optionRepository.save(option1)
        option2 = new Option()
        option2.setContent(OPTION2_CONTENT)
        option2.setCorrect(false)
        option2.setSequence(1)
        option2.setQuestion(question)
        optionRepository.save(option2)
        questionRepository.save(question)

        // Create student
        student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // Create student question
        studentQuestion = new StudentQuestion()
        studentQuestion.setStatus(StudentQuestion.Status.ACCEPTED)
        studentQuestion.setQuestion(question)
        studentQuestion.setUser(student)
        studentQuestionRepository.save(studentQuestion)
    }

    def "change accepted student question title and option 1 content"(){
        given: "a changed StudentQuestionDto"
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
        studentQuestionDto.getQuestionDto().setTitle(NEW_QUESTION_TITLE)
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(option1)
        optionDto.setContent(NEW_OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(option2)
        optionDto.setCorrect(true)
        options.add(optionDto)
        studentQuestionDto.getQuestionDto().setOptions(options)

        when: "a student question is changed"
        studentQuestionService.updateStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "the returned data are correct"
        def result = this.studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == StudentQuestion.Status.ACCEPTED
        def resQuestion = result.getQuestion()
        resQuestion != null
        resQuestion.getKey() == 1
        resQuestion.getStatus() == Question.Status.DISABLED
        and: "student question title is changed"
        resQuestion.getTitle() == NEW_QUESTION_TITLE
        resQuestion.getContent() == QUESTION_CONTENT
        resQuestion.getImage() == null
        resQuestion.getOptions().size() == 2
        resQuestion.getCourse().getName() == COURSE_NAME
        def resOption = resQuestion.getOptions().get(0)
        resOption.getContent() == NEW_OPTION_CONTENT
        !resOption.getCorrect()
        def resOption2 = resQuestion.getOptions().get(1)
        resOption2.getContent() == OPTION2_CONTENT
        resOption2.getCorrect()
    }

    def "make question title empty"(){
        given: "a changed studentQuestionDto"
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
        studentQuestionDto.getQuestionDto().setTitle('     ')
        when: "a student question is changed"
        studentQuestionService.updateStudentQuestion(studentQuestion.getId(), studentQuestionDto)
        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "cannot update a student question that isn't accepted"(){
        given: "an unaccepted studentQuestion"
        studentQuestion.setStatus(StudentQuestion.Status.PROPOSED)
        and: "a changed studentQuestionDto"
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
        studentQuestionDto.getQuestionDto().setTitle(NEW_QUESTION_TITLE)

        when: "a student question is changed"
        studentQuestionService.updateStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_QUESTION_NEEDS_ACCEPTANCE

    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
