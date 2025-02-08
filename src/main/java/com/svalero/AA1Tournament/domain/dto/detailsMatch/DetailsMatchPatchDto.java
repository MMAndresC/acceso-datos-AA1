package com.svalero.AA1Tournament.domain.dto.detailsMatch;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsMatchPatchDto {
    @Min(0)
    private int score;

    private boolean winner;

    @Min(0)
    private int kills;

    @Min(0)
    private int deaths;

    @Min(0)
    private int assists;
}
