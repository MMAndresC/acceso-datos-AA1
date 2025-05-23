package com.svalero.tournament.controller;

import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.ErrorResponse;
import com.svalero.tournament.domain.dto.tournament.TournamentInDto;
import com.svalero.tournament.domain.dto.tournament.TournamentOutDto;
import com.svalero.tournament.domain.dto.tournament.TournamentPatchDto;
import com.svalero.tournament.exception.TournamentNotFoundException;
import com.svalero.tournament.service.TournamentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TournamentController {
    private final Logger logger = LoggerFactory.getLogger(TournamentController.class);
    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/tournaments")
    public ResponseEntity<List<Tournament>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate initDate,
            @RequestParam(required = false) String manager,
            @RequestParam(required = false) Float prize
    ){
        this.logger.info("Listing tournaments...");
        List<Tournament> allTournaments = this.tournamentService.getAll(initDate, manager, prize);
        this.logger.info("End listing tournaments");
        return new ResponseEntity<>(allTournaments, HttpStatus.OK);
    }

    @GetMapping("/tournaments/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable long id) throws TournamentNotFoundException {
        this.logger.info("Searching tournament by Id...");
        Tournament tournament = this.tournamentService.getById(id);
        this.logger.info("tournament found");
        return new ResponseEntity<>(tournament, HttpStatus.OK);
    }

    @PostMapping("/tournaments")
    public ResponseEntity<Tournament> add(@Valid @RequestBody TournamentInDto tournamentInDto){
        this.logger.info("Adding a new tournament...");
        Tournament tournament = this.tournamentService.add(tournamentInDto);
        this.logger.info("Tournament added");
        return new ResponseEntity<>(tournament, HttpStatus.CREATED);
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws TournamentNotFoundException {
        this.logger.info("Deleting a tournament by id...");
        this.tournamentService.delete(id);
        this.logger.info("Tournament deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<Tournament> modify(@PathVariable long id, @Valid @RequestBody TournamentInDto tournamentInDto) throws TournamentNotFoundException {
        this.logger.info("Modifying a tournament...");
        Tournament modifiedTournament = this.tournamentService.modify(id, tournamentInDto);
        this.logger.info("Tournament modified");
        return new ResponseEntity<>(modifiedTournament, HttpStatus.OK);
    }

    @PatchMapping("/tournaments/{id}")
    public ResponseEntity<Tournament> update(@PathVariable long id, @Valid @RequestBody TournamentPatchDto tournamentPatchDto) throws TournamentNotFoundException {
        this.logger.info("Updating a tournament...");
        Tournament updatedTournament = this.tournamentService.update(id, tournamentPatchDto);
        this.logger.info("Tournament updated");
        return new ResponseEntity<>(updatedTournament, HttpStatus.OK);
    }

    @GetMapping("/tournaments/{id}/match-winners")
    public ResponseEntity<List<TournamentOutDto>> getAllTeamsWinnersMatches(@PathVariable long id) throws TournamentNotFoundException{
        this.logger.info("Getting match winners consult...");
        List<TournamentOutDto> listMatchWinners = this.tournamentService.getAllTeamsWinnersMatches(id);
        this.logger.info("Match winners consult done");
        return new ResponseEntity<>(listMatchWinners, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTournamentNotFoundException(TournamentNotFoundException exception) {
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
