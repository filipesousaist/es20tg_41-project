package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.QuestionEvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.QuestionEvaluationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateQuestionEvaluationServiceSpockPerformanceTest extends Specification {
    private static final String COURSE_NAME = "Engenharia de Software";
    private static final String ACRONYM = "ES";
    private static final String ACADEMIC_TERM = "2 SEM";
    private static final String QUESTION_TITLE = "Question 1";
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    private static final String OPTION1_CONTENT = "Option 1";
    private static final String OPTION2_CONTENT = "Option 2";
    private static final String STUDENT_NAME = "Student Name";
    private static final String STUDENT_USERNAME = "Student Username";
    private static final int STUDENT_KEY = 1;
    private static final String JUSTIFICATION = "Good question";
    private static final String TEACHER_NAME = "Teacher Name";
    private static final String TEACHER_USERNAME = "Teacher Username";
    private static final int TEACHER_KEY = 2;

    @Autowired
    StudentQuestionService studentQuestionService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentQuestionRepository studentQuestionRepository;

    @Autowired
    QuestionEvaluationRepository questionEvaluationRepository;

    def studentQuestion
    def studentQuestionID
    def teacher
    def teacherID
    def questionEvaluationDto

    def setup() {
        // Create course and course execution
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        // Create question
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter))

        // Create 2 options, and add them to question
        def option1Dto = new OptionDto()
        option1Dto.setContent(OPTION1_CONTENT)
        option1Dto.setCorrect(true)
        def option2Dto = new OptionDto()
        option2Dto.setContent(OPTION2_CONTENT)
        option2Dto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(option1Dto)
        options.add(option2Dto)
        questionDto.setOptions(options)

        // Create student
        def student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        // Create teacher, and get ID
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, TEACHER_KEY, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)
        teacherID = userRepository.findByKey(TEACHER_KEY).id

        // Create student question, and get ID
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setQuestionDto(questionDto)
        studentQuestion = new StudentQuestion(course, student, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionID = studentQuestionRepository.findAll().get(0).id
    }

    def "performance test to create 10000 question evaluations"() {
        given: "a QuestionEvaluationDto"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(JUSTIFICATION)
        questionEvaluationDto.setApproved(true)

        when: "a question evaluation is created"
        1.upto(10000, {studentQuestionService.createQuestionEvaluation(
            teacherID, studentQuestionID, questionEvaluationDto)});
        /*def i;
        for (i = 0; i < 10000; i ++) {
            questionEvaluationDto.setApproved(!questionEvaluationDto.isApproved())
            studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)
        }*/

        then:
        true
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
