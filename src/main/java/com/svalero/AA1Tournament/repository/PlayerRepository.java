package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAll();
}
