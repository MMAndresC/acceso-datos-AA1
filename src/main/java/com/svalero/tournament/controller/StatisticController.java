package com.svalero.tournament.controller;

import com.svalero.tournament.domain.Statistic;
import com.svalero.tournament.domain.dto.ErrorResponse;
import com.svalero.tournament.domain.dto.statistics.StatisticsInDto;
import com.svalero.tournament.domain.dto.statistics.StatisticsPatchDto;
import com.svalero.tournament.exception.*;
import com.svalero.tournament.service.StatisticService;
import com.svalero.tournament.utils.CreateCsv;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public ResponseEntity<List<Statistic>> getAll(
            @RequestParam(required = false) Boolean mvp,
            @RequestParam(required = false) Integer kills,
            @RequestParam(required = false) Long idPlayer
    ){
        this.logger.info("Listing statistics...");
        List<Statistic> allStatistics = this.statisticService.getAll(mvp, kills, idPlayer);
        this.logger.info("End listing statistics");
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

    @PatchMapping("/statistics/{id}")
    public ResponseEntity<Statistic> update(@PathVariable long id, @Valid @RequestBody StatisticsPatchDto statisticsPatchDto) throws StatisticsNotFoundException{
        this.logger.info("Updating a statistics...");
        Statistic updatedStatistics = this.statisticService.update(id, statisticsPatchDto);
        this.logger.info("Statistics updated");
        return new ResponseEntity<>(updatedStatistics, HttpStatus.OK);
    }

    @GetMapping("/statistics/{idPlayer}/download")
    public ResponseEntity<Resource> download(@PathVariable long idPlayer) throws PlayerNotFoundException, IOException{
        this.logger.info("Getting statistics...");
        List<Statistic> statistics = this.statisticService.download(idPlayer);
        this.logger.info("Collecting statistics end");
        this.logger.info("Generating statistics csv...");
        String filePath = "statistics-" + idPlayer + ".csv";
        File csvFile = CreateCsv.exportStatisticsToCsv(statistics, filePath);
        InputStreamResource body = new InputStreamResource(new FileInputStream(csvFile));
        this.logger.info("Statistics csv generated");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics-" + idPlayer + ".csv" )
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvFile.length())
                .body(body);
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

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOEException(IOException exception) {
        ErrorResponse error = ErrorResponse.generalError(500, "Error generating csv file");
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse error = ErrorResponse.generalError(500, "Internal Server Error");
        this.logger.error(exception.getMessage(), exception);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
