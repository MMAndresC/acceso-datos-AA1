package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Tournament;
import com.svalero.AA1Tournament.domain.dto.tournament.TournamentOutDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

    List<Tournament> findAll();

    List<Tournament> findByInitDate(LocalDate initDate);

    List<Tournament> findByManager(String manager);

    List<Tournament> findByPrize(float prize);

    List<Tournament> findByInitDateAndManager(LocalDate initDate, String manager);

    List<Tournament> findByInitDateAndPrize(LocalDate initDate, float prize);

    List<Tournament> findByManagerAndPrize(String manager, float prize);

    List<Tournament> findByInitDateAndManagerAndPrize(LocalDate initDate, String manager, float prize);

    //JPQL
    @Query( "SELECT tr FROM tournament tr WHERE "
            + "(:initDate IS NULL OR tr.initDate >= :initDate) AND "
            + "(:manager IS NULL OR tr.manager LIKE %:manager%) AND "
            + "(:prize IS NULL OR tr.prize >= :prize)")
    List<Tournament> filterTournamentByRegionManagerPrize(LocalDate initDate, String manager, Float prize);

    //SQL
    @Query(
            value = "SELECT t.id, m.date, m.day, tm.name FROM tournament t "
                    + "INNER JOIN match_t m ON t.id = m.tournament_id "
                    + "INNER JOIN detail_match_team dm ON m.id = dm.match_id "
                    + "INNER JOIN team tm ON tm.id = dm.team_id "
                    + "WHERE dm.winner = true AND t.id = :idTournament",
            nativeQuery = true)
    List<TournamentOutDto> getAllTeamsWinnersMatches(long idTournament);

}
