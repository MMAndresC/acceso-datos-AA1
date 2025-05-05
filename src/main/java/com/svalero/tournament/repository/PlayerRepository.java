package com.svalero.tournament.repository;

import com.svalero.tournament.domain.Player;
import com.svalero.tournament.domain.dto.player.PlayerStatisticsDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAll();

    List<Player> findByBirthDate(LocalDate birthDate);

    List<Player> findByMainRoster(boolean mainRoster);

    List<Player> findByPosition(String position);

    List<Player> findByBirthDateAndMainRoster(LocalDate birthDate, boolean mainRoster);

    List<Player> findByBirthDateAndPosition(LocalDate birthDate, String position);

    List<Player> findByMainRosterAndPosition(boolean mainRoster, String position);

    List<Player> findByBirthDateAndMainRosterAndPosition(LocalDate birthDate, boolean mainRoster, String position);

    //JPQL
    @Query( "SELECT p FROM player p WHERE "
            + "(:birthDate IS NULL OR p.birthDate >= :birthDate) AND "
            + "(:mainRoster IS NULL OR p.mainRoster = :mainRoster) AND "
            + "(:position IS NULL OR p.position = :position)")
    List<Player> filterPlayerByBirthDateMainRosterPosition(LocalDate birthDate, Boolean mainRoster, String position);

    //SQL
    @Query(
            value = "SELECT t.id, t.name, t.init_date, m.map_name, s.mvp, m.type, s.kills, s.deaths, s.assists "
                    + "FROM player p "
                    + "INNER JOIN statistics_tournament_player s ON p.id = s.player_id "
                    + "INNER JOIN match_t m ON s.match_id = m.id "
                    + "INNER JOIN tournament t ON t.id = m.tournament_id "
                    + "WHERE s.mvp = true AND p.id = :id",
            nativeQuery = true
    )
    List<PlayerStatisticsDto> getMvpStatisticsPlayer(long id);
}
