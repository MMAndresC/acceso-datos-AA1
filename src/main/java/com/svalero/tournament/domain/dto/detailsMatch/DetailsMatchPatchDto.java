package com.svalero.tournament.domain.dto.detailsMatch;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsMatchPatchDto {
    @Min(0)
    private Integer score;

    private Boolean winner;

    @Min(0)
    private Integer kills;

    @Min(0)
    private Integer deaths;

    @Min(0)
    private Integer assists;
}
