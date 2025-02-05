package com.svalero.AA1Tournament.domain.dto.match;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchInDto {

    @NotNull(message = "Match date required")
    private LocalDate date;

    @NotNull(message = "Match date required")
    private LocalTime hour;

    private String mapName;

    @Min(0)
    private int duration;

    @Min(1)
    private int day;

    private long idCaster;

}
