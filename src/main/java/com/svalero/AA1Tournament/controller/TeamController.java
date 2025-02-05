package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAll(){
        List<Team> allTeams = this.teamService.getAll();
        return new ResponseEntity<>(allTeams, HttpStatus.OK);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable long id) throws TeamNotFoundException {
        Team team = this.teamService.getById(id);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> add(@Valid @RequestBody TeamInDto teamInDto){
        Team team = this.teamService.add(teamInDto);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) throws TeamNotFoundException {
        String result = this.teamService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<Team> modify(@PathVariable long id, @Valid @RequestBody TeamInDto teamInDto) throws TeamNotFoundException {
        Team modifiedTeam = this.teamService.modify(id, teamInDto);
        return new ResponseEntity<>(modifiedTeam, HttpStatus.OK);
    }

    //Excepciones

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarNotFoundException(TeamNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        //logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> MethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        //logger.error(exception.getMessage(), exception);

        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse error = ErrorResponse.generalError(500, "Internal Server Error");
        //logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
