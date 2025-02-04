package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.StatisticsTournamentPlayer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatisticsTournamentPlayerRepository extends CrudRepository<StatisticsTournamentPlayer, Long> {

    List<StatisticsTournamentPlayer> findAll();
}
