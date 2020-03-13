package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Clarification, Integer> {
    @Query(value = "select * from clarifications u where u.key = :key", nativeQuery = true)
    Clarification findByKey(Integer key);

    @Query(value = "select MAX(id) from clarifications", nativeQuery = true)
    Integer getMaxClarificationKey();


}
