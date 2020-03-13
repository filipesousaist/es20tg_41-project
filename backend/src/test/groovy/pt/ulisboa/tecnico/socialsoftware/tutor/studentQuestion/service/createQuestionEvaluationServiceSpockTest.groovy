import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto.QuestionEvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.StudentQuestionRepository
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
    private static final int STUDENT_KEY = 1;
    private static final String JUSTIFICATION = "Good question";
    private static final String TEACHER_NAME = "Teacher Name";
    private static final String TEACHER_USERNAME = "Teacher Username";
    private static final int TEACHER_KEY = 2;


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

        // Create student
        def student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT);
        student.getCourseExecutions().add(courseExecution);
        courseExecution.getUsers().add(student);
        userRepository.save(student);

        // Create teacher, and get ID
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, TEACHER_KEY, User.Role.TEACHER);
        teacher.getCourseExecutions().add(courseExecution);
        courseExecution.getUsers().add(teacher);
        userRepository.save(teacher);
        teacherID = userRepository.findByKey(TEACHER_KEY).id;

        // Create student question, and get ID
        def studentQuestionDto = new StudentQuestionDto();
        studentQuestionDto.setQuestionDto(questionDto);
        studentQuestion = new StudentQuestion(course, student, studentQuestionDto);
        studentQuestionRepository.save(studentQuestion);
        studentQuestionID = studentQuestionRepository.findAll().get(0).id;
    }

    def "studentQuestion and teacher exist and justification is filled in"() {
        given: "a QuestionEvaluationDto"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(JUSTIFICATION)
        questionEvaluationDto.setApproved(true)

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)

        then: "the returned data are correct"
        def result = questionEvaluationRepository.findAll().get(0)
        result != null
        result.getId() != null
        result.getApproved() == true
        result.getJustification == JUSTIFICATION
        and: "returned references are also correct"
        result.teacher == teacher
        result.studentQuestion == studentQuestion
    }

    def "studentQuestion does not exist"() {
        given: "a QuestionEvaluationDto"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(JUSTIFICATION)
        questionEvaluationDto.setApproved(true)
        and: "student question is deleted"
        studentQuestionRepository.delete(studentQuestion)

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_QUESTION_NOT_FOUND
        and: "the question evaluation is not created"
        questionEvaluationRepository.size() == 0L
    }

    def "teacher does not exist"() {
        given: "a QuestionEvaluationDto"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(JUSTIFICATION)
        questionEvaluationDto.setApproved(true)
        and: "teacher is deleted"
        userRepository.delete(teacher)

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
        and: "the question evaluation is not created"
        questionEvaluationRepository.size() == 0L
    }

    def "justification is null"() {
        given: "a QuestionEvaluationDto with null justification"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(null)
        questionEvaluationDto.setApproved(true)

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_EVALUATION_MISSING_JUSTIFICATION
        and: "the question evaluation is not created"
        questionEvaluationRepository.size() == 0
    }

    def "justification is empty"() {
        given: "a QuestionEvaluationDto with empty justification"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification("")
        questionEvaluationDto.setApproved(true)

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(teacherID, studentQuestionID, questionEvaluationDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_EVALUATION_MISSING_JUSTIFICATION
        and: "the question evaluation is not created"
        questionEvaluationRepository.size() == 0
    }

    def "teacher does not teach the course of the question"() {
        given: "a QuestionEvaluationDto"
        questionEvaluationDto = new QuestionEvaluationDto()
        questionEvaluationDto.setJustification(JUSTIFICATION)
        questionEvaluationDto.setApproved(true)
        and: "a teacher who is not in the course"
        def otherTeacher = new User("Other Teacher Name", "Other Teacher Username", TEACHER_KEY + 1, User.Role.TEACHER)
        userRepository.add(otherTeacher)
        def otherTeacherID = userRepository.findByKey(TEACHER_KEY + 1).id

        when: "a question evaluation is created"
        studentQuestionService.createQuestionEvaluation(otherTeacherID, studentQuestionID, questionEvaluationDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_QUESTION_TEACHER_NOT_IN_COURSE
        and: "the question evaluation is not created"
        questionEvaluationRepository.size() == 0
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
