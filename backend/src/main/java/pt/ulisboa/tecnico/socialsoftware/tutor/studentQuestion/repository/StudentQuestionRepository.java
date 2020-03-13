package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion;

import java.util.List;

@Repository
@Transactional
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Integer> {
    @Query(value = "SELECT * FROM studentQuestions s WHERE s.user_id = :userId", nativeQuery = true)
    List<StudentQuestion> findStudentQuestions(int userId);
}
