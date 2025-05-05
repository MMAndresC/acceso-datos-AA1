package com.svalero.tournament.domain.dto.team;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamPatchDto {

    private String representative;

    private String phone;

    private Boolean partner;

    private String address;

    @Min(value = 1)
    @Max(value = 5)
    private Integer region;
}
