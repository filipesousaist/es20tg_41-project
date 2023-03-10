package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.dto.QuestionEvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;

import javax.persistence.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_EVALUATION_MISSING_JUSTIFICATION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_QUESTION_TEACHER_NOT_IN_COURSE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;


@Entity
@Table(name = "question_evaluations")
public class QuestionEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean approved;

    private String justification;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "student_question_id")
    private StudentQuestion studentQuestion;


    public QuestionEvaluation() {

    }

    public QuestionEvaluation(User teacher, StudentQuestion studentQuestion, QuestionEvaluationDto questionEvaluationDto) {
        String just = checkAndGetJustification(questionEvaluationDto);

        checkTeacherInCourse(teacher, studentQuestion);

        this.teacher = teacher;
        teacher.addQuestionEvaluation(this);
        addStudentQuestion(studentQuestion, questionEvaluationDto.isApproved());

        this.approved = questionEvaluationDto.isApproved();
        this.justification = just;
    }

    private void addStudentQuestion(StudentQuestion studentQuestion, boolean isApproved) {
        this.studentQuestion = studentQuestion;
        studentQuestion.addQuestionEvaluation(this);

        this.studentQuestion.setStatus(
            isApproved ? StudentQuestion.Status.ACCEPTED : StudentQuestion.Status.REJECTED
        );
    }

    private String checkAndGetJustification(QuestionEvaluationDto questionEvaluationDto) {
        String just = questionEvaluationDto.getJustification();
        if (just == null || just.trim().isEmpty()) {
            throw new TutorException(QUESTION_EVALUATION_MISSING_JUSTIFICATION);
        }
        return just;
    }

    private void checkTeacherInCourse(User teacher, StudentQuestion studentQuestion) {
        Course questionCourse = studentQuestion.getQuestion().getCourse();
        if (teacher.getCourseExecutions().stream()
                .noneMatch(courseExecution -> courseExecution.getCourse().getId().equals(questionCourse.getId()))) {
            throw new TutorException(STUDENT_QUESTION_TEACHER_NOT_IN_COURSE);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public StudentQuestion getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(StudentQuestion studentQuestion) {
        this.studentQuestion = studentQuestion;
    }
}
