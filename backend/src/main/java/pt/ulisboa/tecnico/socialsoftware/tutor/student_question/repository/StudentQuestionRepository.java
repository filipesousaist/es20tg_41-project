package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.domain.StudentQuestion;

@Repository
@Transactional
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Integer> {
    /*@Query(value = "SELECT * FROM studentQuestions s WHERE s.user_id = :userId", nativeQuery = true)
    List<StudentQuestion> findStudentQuestions(int userId);*/
}
