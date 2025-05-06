package com.svalero.tournament.domain.dto.team;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInDto {

    @NotNull(message = "Team name is a mandatory field")
    private String name;

    @NotNull(message = "Team representative is a mandatory field")
    private String representative;

    private String phone;

    private boolean partner;

    private String address;

    @NotNull(message = "Team has to be registered in a region")
    @Min(value = 1)
    @Max(value = 5)
    private int region;
}
