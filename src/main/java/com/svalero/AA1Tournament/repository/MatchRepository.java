package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Match;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> findAll();
}
