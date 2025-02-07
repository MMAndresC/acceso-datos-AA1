package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Caster;
import com.svalero.AA1Tournament.domain.dto.caster.CasterInDto;
import com.svalero.AA1Tournament.exception.CasterNotFoundException;
import com.svalero.AA1Tournament.exception.FilterCriteriaNotFoundException;
import com.svalero.AA1Tournament.repository.CasterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CasterService {
    @Autowired
    private CasterRepository casterRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Caster> getAll(){
        return this.casterRepository.findAll();
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

    public List<Caster> filter(Integer region, String language, LocalDate hireDate) throws FilterCriteriaNotFoundException {
        if(language == null && region == null && hireDate == null){
            throw new FilterCriteriaNotFoundException("No caster filters found");
        }else {
            return this.casterRepository.filterCastersByRegionLanguageHireDate(region, language, hireDate);
        }
    }
}
