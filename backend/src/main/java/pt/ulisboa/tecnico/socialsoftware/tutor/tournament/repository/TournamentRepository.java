package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query(value = "SELECT * FROM tournaments t WHERE t.id = :tournamentId", nativeQuery = true)
    Optional<Tournament> findTournamentById(int tournamentId);

    @Query(value = "SELECT * FROM tournaments t WHERE t.name = :tournamentName", nativeQuery = true)
    Optional<Tournament> findTournamentByName(String tournamentName);
}

