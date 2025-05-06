package com.svalero.tournament.domain.dto.match;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPatchDto {

    private LocalDate date;

    private LocalTime hour;

    private String mapName;

    @Min(0)
    private Integer duration;

    @Min(1)
    private Integer day;

}
