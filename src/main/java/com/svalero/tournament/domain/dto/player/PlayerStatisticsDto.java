package com.svalero.tournament.domain.dto.player;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PlayerStatisticsDto {

    long idTournament;

    String name;

    LocalDate tournamentDate;

    String mapName;

    boolean mvp;

    String typeMatch;

    int kills;

    int deaths;

    int assists;

    public PlayerStatisticsDto(
            long idTournament,
            String name,
            Date tournamentDate,
            String mapName,
            boolean mvp,
            String typeMatch,
            int kills,
            int deaths,
            int assists
    ){
        this.idTournament = idTournament;
        this.name = name;
        this.tournamentDate = tournamentDate.toLocalDate();
        this.mapName = mapName;
        this.mvp = mvp;
        this.typeMatch = typeMatch;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
    }
}
