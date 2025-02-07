package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Statistic;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.statistics.StatisticsInDto;
import com.svalero.AA1Tournament.exception.*;
import com.svalero.AA1Tournament.service.StatisticService;
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
public class StatisticController {
    private final Logger logger = LoggerFactory.getLogger(StatisticController.class);
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/statistics")
    public ResponseEntity<List<Statistic>> getAll(){
        this.logger.info("Listing all statistics...");
        List<Statistic> allStatistics = this.statisticService.getAll();
        this.logger.info("End listing all statistics");
        return new ResponseEntity<>(allStatistics, HttpStatus.OK);
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<Statistic> getById(@PathVariable long id) throws StatisticsNotFoundException {
        this.logger.info("Searching statistic by Id...");
        Statistic statistic = this.statisticService.getById(id);
        this.logger.info("Statistic found");
        return new ResponseEntity<>(statistic, HttpStatus.OK);
    }

    @PostMapping("/match/{idMatch}/player/{idPlayer}/statistic")
    public ResponseEntity<Statistic> add(@PathVariable long idMatch, @PathVariable long idPlayer, @Valid @RequestBody StatisticsInDto statisticsInDto) throws MatchNotFoundException, PlayerNotFoundException {
        this.logger.info("Adding a new statistic...");
        Statistic statistic = this.statisticService.add(statisticsInDto, idMatch, idPlayer);
        this.logger.info("Statistic added");
        return new ResponseEntity<>(statistic, HttpStatus.CREATED);
    }

    @DeleteMapping("/statistics/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws StatisticsNotFoundException {
        this.logger.info("Deleting a statistic by id...");
        this.statisticService.delete(id);
        this.logger.info("Statistic deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/statistics/{id}")
    public ResponseEntity<Statistic> modify(@PathVariable long id, @Valid @RequestBody StatisticsInDto statisticsInDto) throws StatisticsNotFoundException, TeamNotFoundException {
        this.logger.info("Modifying a statistic...");
        Statistic modifiedStatistic = this.statisticService.modify(id, statisticsInDto);
        this.logger.info("Statistic modified");
        return new ResponseEntity<>(modifiedStatistic, HttpStatus.OK);
    }

    @GetMapping("/statistics/filters")
    public ResponseEntity<List<Statistic>> filter(
            @RequestParam(required = false) Boolean mvp,
            @RequestParam(required = false) Integer kills,
            @RequestParam(required = false) Long idPlayer
    ) throws FilterCriteriaNotFoundException{
        this.logger.info("Apply filters to statistics...");
        List<Statistic> statistics = this.statisticService.filter(mvp, kills, idPlayer);
        this.logger.info("End filtering statistics");
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(StatisticsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatisticNotFoundException(StatisticsNotFoundException exception) {
        ErrorResponse error = ErrorResponse.generalError(404, exception.getMessage());
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlayerNotFoundException(PlayerNotFoundException exception) {
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
