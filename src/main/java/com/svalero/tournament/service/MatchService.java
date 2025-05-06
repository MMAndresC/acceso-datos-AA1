package com.svalero.tournament.service;

import com.svalero.tournament.domain.Caster;
import com.svalero.tournament.domain.Match;
import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.match.MatchInDto;
import com.svalero.tournament.domain.dto.match.MatchPatchDto;
import com.svalero.tournament.exception.CasterNotFoundException;
import com.svalero.tournament.exception.MatchNotFoundException;
import com.svalero.tournament.exception.TournamentNotFoundException;
import com.svalero.tournament.repository.CasterRepository;
import com.svalero.tournament.repository.MatchRepository;
import com.svalero.tournament.repository.TournamentRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Data
@Service
public class MatchService {

    private MatchRepository matchRepository;

    private CasterRepository casterRepository;

    private TournamentRepository tournamentRepository;

    private ModelMapper modelMapper;

    public MatchService(MatchRepository matchRepository, CasterRepository casterRepository, TournamentRepository tournamentRepository, ModelMapper modelMapper) {
        this.matchRepository = matchRepository;
        this.casterRepository = casterRepository;
        this.tournamentRepository = tournamentRepository;
        this.modelMapper = modelMapper;
    }

    public List<Match> getAll(String mapName, Integer duration, LocalTime hour){
        if(mapName == null && duration == null && hour == null){
            return this.matchRepository.findAll();
        }else {
            return this.matchRepository.filterMatchesByMapNameDurationHour(mapName, duration, hour);
        }
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

    public Match update(long id, MatchPatchDto matchPatchDto) throws  MatchNotFoundException{
        Match match = this.matchRepository.findById(id).orElseThrow(MatchNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(matchPatchDto, match);
        this.matchRepository.save(match);
        return match;
    }
}
