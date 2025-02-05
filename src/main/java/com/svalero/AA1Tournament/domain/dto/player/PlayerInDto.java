package com.svalero.AA1Tournament.domain.dto.player;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInDto {

    @NotNull(message = "Player name is a mandatory field")
    private String name;

    @NotNull(message = "Player alias is a mandatory field")
    private String alias;

    private String phone;

    @Past
    private LocalDate birthDate;

    private String position;

    private boolean mainRoster;

}
