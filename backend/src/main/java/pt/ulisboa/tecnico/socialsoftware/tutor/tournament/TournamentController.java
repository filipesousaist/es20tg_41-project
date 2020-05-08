package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/courseExecution/{courseExecutionId}/tournament/createTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal, @PathVariable Integer courseExecutionId, @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.createNewTournament(user.getId(), courseExecutionId, tournamentDto);
    }

    @PostMapping("/courseExecution/{courseExecutionId}/tournament/{tournamentId}/enrollTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public TournamentDto enrollTournament(Principal principal, @PathVariable Integer courseExecutionId, @PathVariable Integer tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.enrollTournament(user.getId(), tournamentId);
    }

    @PostMapping("/courseExecution/{courseExecutionId}/tournament/{tournamentId}/unenrollTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public ResponseEntity unenrollTournament(Principal principal, @PathVariable Integer courseExecutionId, @PathVariable Integer tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.unenrollTournament(user.getId(), tournamentId);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/courseExecution/{courseExecutionId}/tournament/{tournamentId}/deleteTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public ResponseEntity removeTournament(Principal principal, @PathVariable Integer courseExecutionId, @PathVariable Integer tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.removeTournament(user.getId(), tournamentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/tournament/getAllTournaments")
    public List<TournamentDto> getAllTournaments(Principal principal) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.getAllTournaments();
    }

    @GetMapping("/tournament/getAllOpenTournament")
    public List<TournamentDto> getAllOpenTournament(Principal principal) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.getAllOpenTournament();
    }
}
