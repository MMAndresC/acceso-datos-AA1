package com.svalero.tournament.domain.dto.statistics;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsPatchDto {

    private Boolean mvp;

    @Min(0)
    private Integer kills;

    @Min(0)
    private Integer deaths;

    @Min(0)
    private Integer assists;
}
