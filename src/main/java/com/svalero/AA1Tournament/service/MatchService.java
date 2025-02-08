package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.*;
import com.svalero.AA1Tournament.domain.dto.caster.CasterPatchDto;
import com.svalero.AA1Tournament.domain.dto.match.MatchInDto;
import com.svalero.AA1Tournament.domain.dto.match.MatchPatchDto;
import com.svalero.AA1Tournament.exception.*;
import com.svalero.AA1Tournament.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private CasterRepository casterRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Match> getAll(){
        return this.matchRepository.findAll();
    }

    public Match getById(long id) throws MatchNotFoundException {
        return this.matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
    }

    public Match add(MatchInDto newMatch, long idTournament) throws TournamentNotFoundException, CasterNotFoundException {
        Tournament tournament = this.tournamentRepository.findById(idTournament).orElseThrow(TournamentNotFoundException::new);
        long idCaster = newMatch.getIdCaster();
        Caster caster = this.casterRepository.findById(idCaster).orElseThrow(CasterNotFoundException::new);
        //Prevent match id overwrite
        modelMapper.typeMap(MatchInDto.class, Match.class)
                .addMappings(mapper -> mapper.skip(Match::setId));
        Match match = modelMapper.map(newMatch, Match.class);
        match.setCaster(caster);
        match.setTournament(tournament);
        return this.matchRepository.save(match);
    }

    public void delete(long id) throws MatchNotFoundException {
        Match match = this.matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        this.matchRepository.delete(match);
    }

    public Match modify(long id, MatchInDto matchInDto) throws CasterNotFoundException, MatchNotFoundException {
        Match match = this.matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        //if match changes caster
        long idCaster = matchInDto.getIdCaster();
        if(match.getCaster().getId() != idCaster){
            Caster caster = this.casterRepository.findById(idCaster).orElseThrow(CasterNotFoundException::new);
            match.setCaster(caster);
        }
        //prevents it from trying to set the idCaster as the match id
        //for some reason it was mapping the idCaster as match id
        modelMapper.typeMap(MatchInDto.class, Match.class)
                .addMappings(mapper -> mapper.skip(Match::setId));
        modelMapper.map(matchInDto, match);
        this.matchRepository.save(match);
        return match;
    }

    public List<Match> filter(String mapName, Integer duration, LocalTime hour) throws FilterCriteriaNotFoundException {
        if(mapName == null && duration == null && hour == null){
            throw new FilterCriteriaNotFoundException("No matches filters found");
        }else {
            return this.matchRepository.filterMatchesByMapNameDurationHour(mapName, duration, hour);
        }
    }

    public Match update(long id, MatchPatchDto matchPatchDto) throws  MatchNotFoundException{
        Match match = this.matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(matchPatchDto, match);
        this.matchRepository.save(match);
        return match;
    }
}
