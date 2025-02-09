package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Statistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatisticsRepository extends CrudRepository<Statistic, Long> {

    List<Statistic> findAll();

    List<Statistic> findByMvp(boolean mvp);

    List<Statistic> findByKills(int kills);

    List<Statistic> findByDeaths(int deaths);

    List<Statistic> findByMvpAndKills(boolean mvp, int kills);

    List<Statistic> findByMvpAndDeaths(boolean mvp, int deaths);

    List<Statistic> findByKillsAndDeaths(int kills, int deaths);

    List<Statistic> findByMvpAndKillsAndDeaths(boolean mvp, int kills, int deaths);

    List<Statistic> findByPlayerId(long idPlayer);

    //JPQL
    @Query( "SELECT s FROM statistics_tournament_player s WHERE "
            + "(:mvp IS NULL OR s.mvp = :mvp) AND "
            + "(:kills IS NULL OR s.kills >= :kills) AND "
            + "(:idPlayer IS NULL OR s.player.id = :idPlayer)")
    List<Statistic> filterStatisticByMvpKillsPlayer(Boolean mvp, Integer kills, Long idPlayer);

}
