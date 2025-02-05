package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.service.TeamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(TeamController.class);
    @Autowired
    private TeamService teamService;

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAll(){
        this.logger.info("Listing all teams...");
        List<Team> allTeams = this.teamService.getAll();
        this.logger.info("End listing all teams");
        return new ResponseEntity<>(allTeams, HttpStatus.OK);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getById(@PathVariable long id) throws TeamNotFoundException {
        this.logger.info("Searching team by Id...");
        Team team = this.teamService.getById(id);
        this.logger.info("Team found");
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> add(@Valid @RequestBody TeamInDto teamInDto){
        this.logger.info("Adding a new team...");
        Team team = this.teamService.add(teamInDto);
        this.logger.info("Team added");
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws TeamNotFoundException {
        this.logger.info("Deleting a team by id...");
        this.teamService.delete(id);
        this.logger.info("Team deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<Team> modify(@PathVariable long id, @Valid @RequestBody TeamInDto teamInDto) throws TeamNotFoundException {
        this.logger.info("Modifying a team...");
        Team modifiedTeam = this.teamService.modify(id, teamInDto);
        this.logger.info("Team modified");
        return new ResponseEntity<>(modifiedTeam, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCarNotFoundException(TeamNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
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
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse error = ErrorResponse.generalError(500, "Internal Server Error");
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
