package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Clarification;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Clarification, Integer> {

}
