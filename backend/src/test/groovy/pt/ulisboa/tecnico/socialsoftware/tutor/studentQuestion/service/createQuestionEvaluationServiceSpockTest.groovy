import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

class CreateQuestionEvaluationServiceSpockTest extends Specification {
    private static final String COURSE_NAME = "Engenharia de Software";
    private static final String ACRONYM = "ES";
    private static final String ACADEMIC_TERM = "2 SEM";
    private static final String QUESTION_TITLE = "Question 1";
    private static final String QUESTION_CONTENT = "What is the answer to this question?";
    private static final String OPTION1_CONTENT = "Option 1";
    private static final String OPTION2_CONTENT = "Option 2";
    private static final String STUDENT_NAME = "Student Name";
    private static final String STUDENT_USERNAME = "Student Username";


    @Autowired
    QuestionService questionService;

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

    def setup() {
        // Create course and course execution
        def course = new Course(COURSE_NAME, Course.Type.TECNICO);
        courseRepository.save(course);
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution);

        // Create question
        def questionDto = new QuestionDto();
        questionDto.setKey(1);
        questionDto.setTitle(QUESTION_TITLE);
        questionDto.setContent(QUESTION_CONTENT);
        questionDto.setStatus(Question.Status.AVAILABLE.name());

        // Create 2 options, and add them to question
        def option1Dto = new OptionDto();
        option1Dto.setContent(OPTION1_CONTENT);
        option1Dto.setCorrect(true);
        def option2Dto = new OptionDto();
        option2Dto.setContent(OPTION2_CONTENT);
        option2Dto.setCorrect(false);
        def options = new ArrayList<OptionDto>();
        options.add(option1Dto);
        options.add(option2Dto);
        questionDto.setOptions(options);

        // Add question
        questionService.createQuestion(course.getId(), questionDto);

        // Create student
        def student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT);
        student.getCourseExecutions().add(courseExecution);
        courseExecution.getUsers().add(student);
        userRepository.save(student);

        def studentQuestion = new StudentQuestion()

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
        QuestionService questionService() {
            return new QuestionService()
        }
        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
