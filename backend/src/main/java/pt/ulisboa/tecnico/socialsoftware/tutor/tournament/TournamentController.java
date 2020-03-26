package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/courseExecution/{courseExecutionId}/tournament/createtournament")
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

/*
    @DeleteMapping("/courseExecution/{courseExecutionId}/tournament/{tournamentId}/deleteTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public ResponseEntity removeTournament(Principal principal, @PathVariable Integer courseExecutionId, @PathVariable Integer tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.removeTournament(tournamentId);

        return ResponseEntity.ok().build();
    }
*/
    @GetMapping("/tournament/getAllOpenTournament")
    public List<TournamentDto> getAllOpenTournament(Principal principal) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.getAllOpenTournament();
    }
}
