package com.svalero.AA1Tournament.domain.dto.tournament;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TournamentOutDto {

    long idTournament;

    String nameTournament;

    LocalDate date;

    int day;

    String nameTeam;

    String type;

    public TournamentOutDto(long id, String nameTournament, Date date, int day, String type, String nameTeam){
        this.idTournament = id;
        this.nameTournament = nameTournament;
        this.date = date.toLocalDate();
        this.day = day;
        this.type = type;
        this.nameTeam = nameTeam;
    }
}
