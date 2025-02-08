package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Player;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.player.PlayerInDto;
import com.svalero.AA1Tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.AA1Tournament.domain.dto.player.PlayerPatchDto;
import com.svalero.AA1Tournament.exception.FilterCriteriaNotFoundException;
import com.svalero.AA1Tournament.exception.PlayerNotFoundException;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.service.PlayerService;
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
public class PlayerController {

    private final Logger logger = LoggerFactory.getLogger(PlayerController.class);
    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAll(){
        this.logger.info("Listing all players...");
        List<Player> allPlayers = this.playerService.getAll();
        this.logger.info("End listing all players");
        return new ResponseEntity<>(allPlayers, HttpStatus.OK);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getById(@PathVariable long id) throws PlayerNotFoundException {
        this.logger.info("Searching player by Id...");
        Player player = this.playerService.getById(id);
        this.logger.info("Player found");
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/teams/{idTeam}/players")
    public ResponseEntity<Player> add(@PathVariable long idTeam, @Valid @RequestBody PlayerInDto playerInDto) throws TeamNotFoundException {
        this.logger.info("Adding a new player...");
        Player player = this.playerService.add(playerInDto, idTeam);
        this.logger.info("Player added");
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws PlayerNotFoundException {
        this.logger.info("Deleting a player by id...");
        this.playerService.delete(id);
        this.logger.info("Player deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<Player> modify(@PathVariable long id, @Valid @RequestBody PlayerModifyDto playerModifyDto) throws PlayerNotFoundException, TeamNotFoundException {
        this.logger.info("Modifying a player...");
        Player modifiedPlayer = this.playerService.modify(id, playerModifyDto);
        this.logger.info("Player modified");
        return new ResponseEntity<>(modifiedPlayer, HttpStatus.OK);
    }

    @GetMapping("/players/filters")
    public ResponseEntity<List<Player>> filter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) Boolean mainRoster,
            @RequestParam(required = false) String position
    ) throws FilterCriteriaNotFoundException{
        this.logger.info("Apply filters to players...");
        List<Player> players = this.playerService.filter(birthDate, mainRoster, position);
        this.logger.info("End filtering players");
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PatchMapping("/players/{id}")
    public ResponseEntity<Player> update(@PathVariable long id, @Valid @RequestBody PlayerPatchDto playerPatchDto) throws PlayerNotFoundException {
        this.logger.info("Updating a player...");
        Player modifiedPlayer = this.playerService.update(id, playerPatchDto);
        this.logger.info("Player updated");
        return new ResponseEntity<>(modifiedPlayer, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotFoundException(PlayerNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamNotFoundException(TeamNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FilterCriteriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFilterCriteriaNotFoundException(FilterCriteriaNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
