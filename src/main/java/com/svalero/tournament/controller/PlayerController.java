package com.svalero.tournament.controller;

import com.svalero.tournament.domain.Player;
import com.svalero.tournament.domain.dto.ErrorResponse;
import com.svalero.tournament.domain.dto.player.PlayerInDto;
import com.svalero.tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.tournament.domain.dto.player.PlayerPatchDto;
import com.svalero.tournament.domain.dto.player.PlayerStatisticsDto;
import com.svalero.tournament.exception.PlayerNotFoundException;
import com.svalero.tournament.exception.TeamNotFoundException;
import com.svalero.tournament.service.PlayerService;
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
    public ResponseEntity<List<Player>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) Boolean mainRoster,
            @RequestParam(required = false) String position
    ){
        this.logger.info("Listing players...");
        List<Player> allPlayers = this.playerService.getAll(birthDate, mainRoster, position);
        this.logger.info("End listing players");
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

    @PatchMapping("/players/{id}")
    public ResponseEntity<Player> update(@PathVariable long id, @Valid @RequestBody PlayerPatchDto playerPatchDto) throws PlayerNotFoundException {
        this.logger.info("Updating a player...");
        Player updatedPlayer = this.playerService.update(id, playerPatchDto);
        this.logger.info("Player updated");
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }

    @GetMapping("/players/{id}/highlights")
    public ResponseEntity<List<PlayerStatisticsDto>> getMvpStatisticsPlayer(@PathVariable long id) throws PlayerNotFoundException {
        this.logger.info("Getting player highlights...");
        List<PlayerStatisticsDto> highlightsPlayer = this.playerService.getMvpStatisticsPlayer(id);
        this.logger.info("Player highlights done");
        return new ResponseEntity<>(highlightsPlayer, HttpStatus.OK);
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
