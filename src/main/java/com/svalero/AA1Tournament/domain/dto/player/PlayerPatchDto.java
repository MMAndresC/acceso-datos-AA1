package com.svalero.AA1Tournament.domain.dto.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPatchDto {

    private String phone;

    private String position;

    private Boolean mainRoster;
}
