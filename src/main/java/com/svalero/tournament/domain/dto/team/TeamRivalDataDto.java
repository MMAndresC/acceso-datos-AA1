package com.svalero.tournament.domain.dto.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRivalDataDto {

    String rivalName;

    int rivalScore;
}
