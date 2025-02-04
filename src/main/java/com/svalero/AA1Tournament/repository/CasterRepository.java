package com.svalero.AA1Tournament.repository;

import com.svalero.AA1Tournament.domain.Caster;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CasterRepository extends CrudRepository<Caster, Long> {

    List<Caster> findAll();
}
