package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.DetailsMatch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DetailsMatchRepository extends CrudRepository<DetailsMatch, Long> {

    List<DetailsMatch> findAll();

    //JPQL to check if already exist a team detail match
    @Query("select dmt FROM detail_match_team dmt WHERE dmt.match.id = :idMatch AND dmt.team.id = :idTeam")
    DetailsMatch getDetailMatchByTeam(long idMatch, long idTeam);
}
