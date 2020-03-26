package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.ClarificationRequest;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ClarificationRequestRepository extends JpaRepository<ClarificationRequest, Integer> {

    @Query(value = "SELECT * FROM clarification_requests WHERE user_id = :userId AND question_id = :questionId", nativeQuery = true)
    Optional<ClarificationRequest> findByUserIdAndQuestionId(Integer userId, Integer questionId);

    @Query(value = "SELECT  MAX(key) FROM clarification_requests", nativeQuery = true)
    Integer findMaxKey();

    @Query(value = "SELECT * FROM clarification_requests WHERE user_id = :userId", nativeQuery = true)
    List<ClarificationRequest> findByUserId(Integer userId);
}

