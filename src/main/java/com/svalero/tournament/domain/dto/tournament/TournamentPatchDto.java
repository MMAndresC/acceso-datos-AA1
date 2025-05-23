package com.svalero.tournament.domain.dto.tournament;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentPatchDto {

    private String name;

    private LocalDate initDate;

    private LocalDate endDate;

    @Min(0)
    private Float prize;

    private String address;

    private String manager;

    private Double latitude;

    private Double longitude;
}
