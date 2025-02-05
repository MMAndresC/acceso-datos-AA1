package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    List<Team> findAll();

    List<Team> findByRegion(int region);

    List<Team> findByPartner(boolean partner);

}
