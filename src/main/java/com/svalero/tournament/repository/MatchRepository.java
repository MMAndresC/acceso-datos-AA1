package com.svalero.tournament.repository;

import com.svalero.tournament.domain.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalTime;
import java.util.List;

public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> findAll();

    List<Match> findByMapName(String mapName);

    List<Match> findByDuration(int duration);

    List<Match> findByHour(LocalTime hour);

    List<Match> findByMapNameAndDuration(String mapName, int duration);
    List<Match> findByMapNameAndHour(String mapName, LocalTime hour);

    List<Match> findByDurationAndHour(int duration, LocalTime hour);

    List<Match> findByMapNameAndDurationAndHour(String mapName, int duration, LocalTime hour);

    //JPQL to filter matches
    //entity name & class attributes names
    @Query( "SELECT m FROM match m WHERE "
            + "(:mapName IS NULL OR m.mapName = :mapName) AND "
            + "(:duration IS NULL OR m.duration >= :duration) AND "
            + "(:hour IS NULL OR m.hour = :hour)")
    List<Match> filterMatchesByMapNameDurationHour(String mapName, Integer duration, LocalTime hour);

}
