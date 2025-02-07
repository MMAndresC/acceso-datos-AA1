package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.DetailsMatch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DetailsMatchRepository extends CrudRepository<DetailsMatch, Long> {

    List<DetailsMatch> findAll();

    List<DetailsMatch> findByWinner(boolean winner);

    List<DetailsMatch> findByScore(int score);

    List<DetailsMatch> findByKills(int kills);

    List<DetailsMatch> findByWinnerAndScore(boolean winner, int score);

    List<DetailsMatch> findByWinnerAndKills(boolean winner, int kills);

    List<DetailsMatch> findByScoreAndKills(int score, int kills);

    List<DetailsMatch> findByWinnerAndScoreAndKills(boolean winner, int score, int kills);

    //JPQL to check if already exist a team detail match
    @Query("SELECT dmt FROM detail_match_team dmt WHERE dmt.match.id = :idMatch AND dmt.team.id = :idTeam")
    DetailsMatch getDetailMatchByTeam(long idMatch, long idTeam);

    //JPQL to filter
    @Query( "SELECT dmt FROM detail_match_team dmt WHERE "
            + "(:winner IS NULL OR dmt.winner = :winner) AND "
            + "(:score IS NULL OR dmt.score >= :score) AND "
            + "(:kills IS NULL OR dmt.kills >= :kills)")
    List<DetailsMatch> filterDetailsByWinnerScoreKills (Boolean winner, Integer score, Integer kills);
}
