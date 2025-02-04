package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.DetailMatchTeam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DetailMatchTeamRepository extends CrudRepository<DetailMatchTeam, Long> {

    List<DetailMatchTeam> findAll();
}
