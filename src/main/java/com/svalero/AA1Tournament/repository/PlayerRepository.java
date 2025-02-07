package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAll();

    List<Player> findByBirthDate(LocalDate birthDate);

    List<Player> findByMainRoster(boolean mainRoster);

    List<Player> findByPosition(String position);

    List<Player> findByBirthDateAndMainRoster(LocalDate birthDate, boolean mainRoster);

    List<Player> findByBirthDateAndPosition(LocalDate birthDate, String position);

    List<Player> findByMainRosterAndPosition(boolean mainRoster, String position);

    List<Player> findByBirthDateAndMainRosterAndPosition(LocalDate birthDate, boolean mainRoster, String position);

    //JPQL
    @Query( "SELECT p FROM player p WHERE "
            + "(:birthDate IS NULL OR p.birthDate >= :birthDate) AND "
            + "(:mainRoster IS NULL OR p.mainRoster = :mainRoster) AND "
            + "(:position IS NULL OR p.position = :position)")
    List<Player> filterPlayerByBirthDateMainRosterPosition(LocalDate birthDate, Boolean mainRoster, String position);

}
