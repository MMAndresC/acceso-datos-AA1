package com.svalero.AA1Tournament.domain.dto.statistics;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsInDto {

    @NotNull(message = "mvp field required")
    private boolean mvp;

    @Min(0)
    @NotNull(message = "kills field required")
    private int kills;

    @Min(0)
    @NotNull(message = "deaths field required")
    private int deaths;

    @Min(0)
    @NotNull(message = "assists field required")
    private int assists;

}
