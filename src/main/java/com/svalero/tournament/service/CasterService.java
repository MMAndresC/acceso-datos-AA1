package com.svalero.tournament.service;

import com.svalero.tournament.domain.Caster;
import com.svalero.tournament.domain.dto.caster.CasterInDto;
import com.svalero.tournament.domain.dto.caster.CasterPatchDto;
import com.svalero.tournament.exception.CasterNotFoundException;
import com.svalero.tournament.repository.CasterRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Data
@Service
public class CasterService {

    private CasterRepository casterRepository;

    private ModelMapper modelMapper;

    public CasterService(CasterRepository casterRepository, ModelMapper modelMapper) {
        this.casterRepository = casterRepository;
        this.modelMapper = modelMapper;
    }

    public List<Caster> getAll(String language, Integer region, LocalDate hireDate){
        if(language == null && region == null && hireDate == null){
            return this.casterRepository.findAll();
        }else {
            return this.casterRepository.filterCastersByRegionLanguageHireDate(region, language, hireDate);
        }
    }

    public Caster getById(long id) throws CasterNotFoundException {
        return this.casterRepository.findById(id).orElseThrow(CasterNotFoundException::new);
    }

    public Caster add(CasterInDto newCaster){
        Caster caster = modelMapper.map(newCaster, Caster.class);
        return this.casterRepository.save(caster);
    }

    public void delete(long id) throws CasterNotFoundException {
        Caster caster = this.casterRepository.findById(id).orElseThrow(CasterNotFoundException::new);
        this.casterRepository.delete(caster);
    }

    public Caster modify(long id, CasterInDto casterInDto) throws CasterNotFoundException {
        Caster caster = this.casterRepository.findById(id).orElseThrow(CasterNotFoundException::new);
        modelMapper.map(casterInDto, caster);
        this.casterRepository.save(caster);
        return caster;
    }

    public Caster update(long id, CasterPatchDto casterPatchDto) throws  CasterNotFoundException{
        Caster caster = this.casterRepository.findById(id).orElseThrow(CasterNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(casterPatchDto, caster);
        this.casterRepository.save(caster);
        return caster;
    }
}
