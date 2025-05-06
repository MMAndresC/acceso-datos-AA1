package com.svalero.tournament.repository;

import com.svalero.tournament.domain.Team;
import com.svalero.tournament.domain.dto.team.TeamConsultWinsDto;
import com.svalero.tournament.domain.dto.team.TeamRivalDataDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    List<Team> findAll();

    List<Team> findByRegion(int region);

    List<Team> findByPartner(boolean partner);

    List<Team> findByRegistrationDate(LocalDate registrationDate);

    List<Team> findByRegionAndPartner(int region, boolean partner);

    List<Team> findByRegionAndRegistrationDate(int region, LocalDate registrationDate);

    List<Team> findByPartnerAndRegistrationDate(boolean partner, LocalDate registrationDate);

    List<Team> findByRegionAndPartnerAndRegistrationDate(int region, boolean partner, LocalDate registrationDate);

    //JPQL
    @Query( "SELECT t FROM team t WHERE "
            + "(:region IS NULL OR t.region = :region) AND "
            + "(:partner IS NULL OR t.partner = :partner) AND "
            + "(:registrationDate IS NULL OR t.registrationDate >= :registrationDate)")
    List<Team> filterTeamByRegionPartnerRegistrationDate(Integer region, Boolean partner, LocalDate registrationDate);

    //SQL
    @Query(
            value = "SELECT t.id, t.name, t.end_date, dm.score, t.prize, m.id "
                    + "FROM team tm "
                    + "INNER JOIN detail_match_team dm ON dm.id = tm.id "
                    + "INNER JOIN match_t m ON m.id = dm.id "
                    + "INNER JOIN tournament t ON t.id = m.id "
                    + "WHERE m.type = 'final' AND tm.id = :idTeam",
            nativeQuery = true
    )
    List<TeamConsultWinsDto> getAllTournamentWins(long idTeam);

    @Query(
            value = "SELECT t.name, dm.score FROM match_t m "
                    + "INNER JOIN detail_match_team dm ON dm.id = m.id "
                    + "INNER JOIN team t ON dm.team_id = t.id "
                    + "WHERE m.id = :idMatch AND dm.winner = false",
            nativeQuery = true
    )
    Optional<TeamRivalDataDto> getFinalRival(long idMatch);
}
