package com.svalero.tournament.domain.dto.player;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModifyDto extends PlayerInDto{

    @NotNull(message = "Team id required")
    private long idTeam;

    public PlayerModifyDto(long idTeam, String name, String alias, String phone, LocalDate birthDate, String position, boolean mainRoster) {
        super(name, alias, phone, birthDate, position, mainRoster);
        this.idTeam = idTeam;
    }
}
