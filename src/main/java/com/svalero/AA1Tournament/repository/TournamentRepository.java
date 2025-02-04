package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {

    List<Tournament> findAll();
}
