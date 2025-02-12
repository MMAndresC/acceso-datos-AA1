package com.svalero.AA1Tournament.domain.dto.tournament;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentInDto {

    @NotNull(message = "Tournament name is a mandatory field")
    private String name;

    @NotNull(message = "Tournament initio date is a mandatory field")
    private LocalDate initDate;

    private LocalDate endDate;

    @NotNull(message = "Tournament prize is a mandatory field")
    @Min(0)
    private float prize;

    private String address;

    private String manager;

    private float latitude;

    private float longitude;
}
