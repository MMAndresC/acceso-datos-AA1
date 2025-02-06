package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Statistic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StatisticsRepository extends CrudRepository<Statistic, Long> {

    List<Statistic> findAll();
}
