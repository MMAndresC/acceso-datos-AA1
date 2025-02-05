package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Match;
import com.svalero.AA1Tournament.domain.Player;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.match.MatchInDto;
import com.svalero.AA1Tournament.domain.dto.player.PlayerInDto;
import com.svalero.AA1Tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.AA1Tournament.exception.*;
import com.svalero.AA1Tournament.service.MatchService;
import com.svalero.AA1Tournament.service.PlayerService;
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
public class MatchController {

    private final Logger logger = LoggerFactory.getLogger(MatchController.class);
    @Autowired
    private MatchService matchService;

    @GetMapping("/matches")
    public ResponseEntity<List<Match>> getAll(){
        this.logger.info("Listing all matches...");
        List<Match> allMatches = this.matchService.getAll();
        this.logger.info("End listing all matches");
        return new ResponseEntity<>(allMatches, HttpStatus.OK);
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<Match> getById(@PathVariable long id) throws MatchNotFoundException {
        this.logger.info("Searching match by Id...");
        Match match = this.matchService.getById(id);
        this.logger.info("Match found");
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PostMapping("/tournaments/{idTournament}/matches")
    public ResponseEntity<Match> add(@PathVariable long idTournament, @Valid @RequestBody MatchInDto matchInDto) throws TournamentNotFoundException, CasterNotFoundException {
        this.logger.info("Adding a new match...");
        Match match = this.matchService.add(matchInDto, idTournament);
        this.logger.info("Match added");
        return new ResponseEntity<>(match, HttpStatus.CREATED);
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws MatchNotFoundException {
        this.logger.info("Deleting a match by id...");
        this.matchService.delete(id);
        this.logger.info("Match deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/matches/{id}")
    public ResponseEntity<Match> modify(@PathVariable long id, @Valid @RequestBody MatchInDto matchInDto) throws CasterNotFoundException, MatchNotFoundException {
        this.logger.info("Modifying a match...");
        Match modifiedMatch = this.matchService.modify(id, matchInDto);
        this.logger.info("Match modified");
        return new ResponseEntity<>(modifiedMatch, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMatchNotFoundException(MatchNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CasterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCasterNotFoundException(CasterNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

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
