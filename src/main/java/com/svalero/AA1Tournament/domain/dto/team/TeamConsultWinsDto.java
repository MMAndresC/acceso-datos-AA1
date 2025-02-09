package com.svalero.AA1Tournament.domain.dto.team;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TeamConsultWinsDto {

    long idTournament;

    String nameTournament;

    LocalDate dateFinal;

    int score;

    float prize;

    long idMatch;

    String rivalName;

    int rivalScore;

    public TeamConsultWinsDto(
            long idTournament,
            String nameTournament,
            Date dateFinal,
            int score,
            float prize,
            long idMatch
    ){
        this.idTournament = idTournament;
        this.nameTournament = nameTournament;
        this.dateFinal = dateFinal.toLocalDate();
        this.score = score;
        this.prize = prize;
        this.idMatch = idMatch;
    }

}
