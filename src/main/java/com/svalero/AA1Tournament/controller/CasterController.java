package com.svalero.AA1Tournament.controller;

import com.svalero.AA1Tournament.domain.Caster;
import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.ErrorResponse;
import com.svalero.AA1Tournament.domain.dto.caster.CasterInDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.exception.CasterNotFoundException;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.service.CasterService;
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
public class CasterController {
    private final Logger logger = LoggerFactory.getLogger(CasterController.class);
    @Autowired
    private CasterService casterService;

    @GetMapping("/casters")
    public ResponseEntity<List<Caster>> getAll(){
        this.logger.info("Listing all casters...");
        List<Caster> allCasters = this.casterService.getAll();
        this.logger.info("End listing all casters");
        return new ResponseEntity<>(allCasters, HttpStatus.OK);
    }

    @GetMapping("/casters/{id}")
    public ResponseEntity<Caster> getById(@PathVariable long id) throws CasterNotFoundException {
        this.logger.info("Searching caster by Id...");
        Caster caster = this.casterService.getById(id);
        this.logger.info("Caster found");
        return new ResponseEntity<>(caster, HttpStatus.OK);
    }

    @PostMapping("/casters")
    public ResponseEntity<Caster> add(@Valid @RequestBody CasterInDto casterInDto){
        this.logger.info("Adding a new caster...");
        Caster caster = this.casterService.add(casterInDto);
        this.logger.info("Caster added");
        return new ResponseEntity<>(caster, HttpStatus.CREATED);
    }

    @DeleteMapping("/casters/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws CasterNotFoundException {
        this.logger.info("Deleting a caster by id...");
        this.casterService.delete(id);
        this.logger.info("Caster deleted");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/casters/{id}")
    public ResponseEntity<Caster> modify(@PathVariable long id, @Valid @RequestBody CasterInDto casterInDto) throws CasterNotFoundException {
        this.logger.info("Modifying a caster...");
        Caster modifiedCaster = this.casterService.modify(id, casterInDto);
        this.logger.info("Caster modified");
        return new ResponseEntity<>(modifiedCaster, HttpStatus.OK);
    }

    //Excepciones
    @ExceptionHandler(CasterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCasterNotFoundException(CasterNotFoundException exception) {
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
