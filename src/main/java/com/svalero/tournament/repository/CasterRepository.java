package com.svalero.tournament.repository;

import com.svalero.tournament.domain.Caster;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CasterRepository extends CrudRepository<Caster, Long> {

    List<Caster> findAll();

    List<Caster> findByRegion(int region);

    List<Caster> findByLanguages(String language);

    List<Caster> findByHireDate(LocalDate hireDate);

    List<Caster> findByRegionAndLanguages(int region, String language);

    List<Caster> findByRegionAndHireDate(int region, LocalDate hireDate);

    List<Caster> findByLanguagesAndHireDate(String language, LocalDate hireDate);

    List<Caster> findByRegionAndLanguagesAndHireDate(int region, String language, LocalDate hireDate);

    //Filter JPQL
    //entity name, in this case entity name = table name
    //IS NULL to discard when a parameter is null
    @Query( "SELECT c FROM caster c WHERE "
            + "(:region IS NULL OR c.region = :region) AND "
            + "(:language IS NULL OR c.languages LIKE %:language%) AND "
            + "(:hireDate IS NULL OR c.hireDate = :hireDate)")
    List<Caster> filterCastersByRegionLanguageHireDate(Integer region, String language, LocalDate hireDate);

}
