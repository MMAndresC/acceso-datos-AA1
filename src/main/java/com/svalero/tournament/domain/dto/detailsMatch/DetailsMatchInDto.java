package com.svalero.tournament.domain.dto.detailsMatch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsMatchInDto {

    @Min(0)
    @NotNull(message = "Score in details match is required")
    private int score;

    @NotNull(message = "Winner in details match is required")
    private boolean winner;

    @Min(0)
    private int kills;

    @Min(0)
    private int deaths;

    @Min(0)
    private int assists;

}
