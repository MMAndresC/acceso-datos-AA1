package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
