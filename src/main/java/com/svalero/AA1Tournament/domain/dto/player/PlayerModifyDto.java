package com.svalero.AA1Tournament.domain.dto.player;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModifyDto extends PlayerInDto{

    @NotNull(message = "Team id required")
    private long idTeam;
}
