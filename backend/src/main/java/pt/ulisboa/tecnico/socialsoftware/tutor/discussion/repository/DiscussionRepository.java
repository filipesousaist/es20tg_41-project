package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.Optional;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Clarification, Integer> {

    @Query(value = "SELECT * FROM clarifications WHERE user_id = :user_id AND clarification_request_id = :clarification_request_id", nativeQuery = true)
    Optional<Clarification> findByUserIdAndClarificationRequestId(Integer user_id, Integer clarification_request_id);
}
