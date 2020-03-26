package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.QuestionEvaluation;

@Repository
@Transactional
public interface QuestionEvaluationRepository extends JpaRepository<QuestionEvaluation, Integer> {
}
