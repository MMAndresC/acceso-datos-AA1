package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.DetailsMatch;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.detailsMatch.DetailsMatchInDto;
import com.svalero.AA1Tournament.domain.dto.detailsMatch.DetailsMatchPatchDto;
import com.svalero.AA1Tournament.exception.*;
import com.svalero.AA1Tournament.service.DetailsMatchService;
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
public class DetailsMatchController {
    private final Logger logger = LoggerFactory.getLogger(DetailsMatchController.class);
    @Autowired
    private DetailsMatchService detailsMatchService;

    @GetMapping("/details-match")
    public ResponseEntity<List<DetailsMatch>> getAll(){
        this.logger.info("Listing all details match...");
        List<DetailsMatch> allDetailsMatch = this.detailsMatchService.getAll();
        this.logger.info("End listing all details match");
        return new ResponseEntity<>(allDetailsMatch, HttpStatus.OK);
    }

    @GetMapping("/details-match/{id}")
    public ResponseEntity<DetailsMatch> getById(@PathVariable long id) throws DetailsMatchNotFoundException {
        this.logger.info("Searching details match by Id...");
        DetailsMatch detailsMatch = this.detailsMatchService.getById(id);
        this.logger.info("Details match found");
        return new ResponseEntity<>(detailsMatch, HttpStatus.OK);
    }

    @PostMapping("/match/{idMatch}/team/{idTeam}/details")
    public ResponseEntity<DetailsMatch> add(@PathVariable long idMatch, @PathVariable long idTeam, @Valid @RequestBody DetailsMatchInDto detailsMatchInDto) throws TeamNotFoundException, MatchNotFoundException, DetailsMatchAlreadyExistException {
        this.logger.info("Adding a new details match...");
        DetailsMatch detailsMatch = this.detailsMatchService.add(detailsMatchInDto, idMatch, idTeam);
        this.logger.info("Details match added");
        return new ResponseEntity<>(detailsMatch, HttpStatus.CREATED);
    }

    @DeleteMapping("/details-match/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws DetailsMatchNotFoundException {
        this.logger.info("Deleting a details match by id...");
        this.detailsMatchService.delete(id);
        this.logger.info("Details match deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/details-match/{id}")
    public ResponseEntity<DetailsMatch> modify(@PathVariable long id, @Valid @RequestBody DetailsMatchInDto detailsMatchInDto) throws DetailsMatchNotFoundException {
        this.logger.info("Modifying a details match...");
        DetailsMatch modifiedDetails = this.detailsMatchService.modify(id, detailsMatchInDto);
        this.logger.info("Details match modified");
        return new ResponseEntity<>(modifiedDetails, HttpStatus.OK);
    }

    @GetMapping("/details/filters")
    public ResponseEntity<List<DetailsMatch>> filter(
            @RequestParam(required = false) Boolean winner,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) Integer kills
    ) throws FilterCriteriaNotFoundException{
        this.logger.info("Apply filters to match details...");
        List<DetailsMatch> detailsMatches = this.detailsMatchService.filter(winner, score, kills);
        this.logger.info("End filtering match details");
        return new ResponseEntity<>(detailsMatches, HttpStatus.OK);
    }

    @PatchMapping("/details-match/{id}")
    public ResponseEntity<DetailsMatch> update(@PathVariable long id, @Valid @RequestBody DetailsMatchPatchDto detailsMatchPatchDto) throws DetailsMatchNotFoundException{
        this.logger.info("Updating a detail match...");
        DetailsMatch updatedDetailsMatch = this.detailsMatchService.update(id, detailsMatchPatchDto);
        this.logger.info("Detail match updated");
        return new ResponseEntity<>(updatedDetailsMatch, HttpStatus.OK);
    }


    //Excepciones
    @ExceptionHandler(DetailsMatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDetailsMatchNotFoundException(DetailsMatchNotFoundException exception) {
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

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMatchNotFoundException(MatchNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DetailsMatchAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleDetailsMatchAlreadyExistException(DetailsMatchAlreadyExistException exception) {
        ErrorResponse error = ErrorResponse.generalError(409, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
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
