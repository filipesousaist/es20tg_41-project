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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("course/{courseId}/tournament/createtournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public TournamentDto createTournament(Principal principal, @PathVariable Integer courseId, @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.createNewTournament(tournamentDto.getUserDto().getId(), tournamentDto);
    }

    @PostMapping("course/{courseId}/tournament/enrollTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public TournamentDto enrollTournament(Principal principal, @PathVariable Integer courseId, @RequestBody TournamentDto tournamentDto) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.enrollTournament(tournamentDto.getUserDto().getId(), tournamentDto);
    }

    @DeleteMapping("course/{courseId}/tournament/deleteTournament/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public ResponseEntity removeTournament(Principal principal, @PathVariable Integer tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || !user.getRole().equals(User.Role.STUDENT)){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.removeTournament(tournamentId);

        return ResponseEntity.ok().build();
    }
}
