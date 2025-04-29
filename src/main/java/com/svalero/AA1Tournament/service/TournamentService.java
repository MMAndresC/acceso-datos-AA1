package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Tournament;
import com.svalero.AA1Tournament.domain.dto.tournament.TournamentInDto;
import com.svalero.AA1Tournament.domain.dto.tournament.TournamentOutDto;
import com.svalero.AA1Tournament.domain.dto.tournament.TournamentPatchDto;
import com.svalero.AA1Tournament.exception.TournamentNotFoundException;
import com.svalero.AA1Tournament.repository.TournamentRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Data
@Service
public class TournamentService {

    private TournamentRepository tournamentRepository;

    private ModelMapper modelMapper;

    public TournamentService(TournamentRepository tournamentRepository, ModelMapper modelMapper) {
        this.tournamentRepository = tournamentRepository;
        this.modelMapper = modelMapper;
    }

    public List<Tournament> getAll(LocalDate initDate, String manager, Float prize){
        if(initDate == null && manager == null && prize == null){
            return this.tournamentRepository.findAll();
        }else {
            return this.tournamentRepository.filterTournamentByRegionManagerPrize(initDate, manager, prize);
        }
    }

    public Tournament getById(long id) throws TournamentNotFoundException {
        return this.tournamentRepository.findById(id).orElseThrow(TournamentNotFoundException::new);
    }

    public Tournament add(TournamentInDto newTournament){
        Tournament tournament = modelMapper.map(newTournament, Tournament.class);
        return this.tournamentRepository.save(tournament);
    }

    public void delete(long id) throws TournamentNotFoundException {
        Tournament tournament = this.tournamentRepository.findById(id).orElseThrow(TournamentNotFoundException::new);
        this.tournamentRepository.delete(tournament);
    }

    public Tournament modify(long id, TournamentInDto tournamentInDto) throws TournamentNotFoundException {
        Tournament tournament = this.tournamentRepository.findById(id).orElseThrow(TournamentNotFoundException::new);
        modelMapper.map(tournamentInDto, tournament);
        this.tournamentRepository.save(tournament);
        return tournament;
    }

    public Tournament update(long id, TournamentPatchDto tournamentPatchDto) throws TournamentNotFoundException {
        Tournament tournament = this.tournamentRepository.findById(id).orElseThrow(TournamentNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(tournamentPatchDto, tournament);
        this.tournamentRepository.save(tournament);
        return tournament;
    }

    public List<TournamentOutDto> getAllTeamsWinnersMatches(long id) throws TournamentNotFoundException{
        Tournament tournament = this.tournamentRepository.findById(id).orElseThrow(TournamentNotFoundException::new);
        return this.tournamentRepository.getAllTeamsWinnersMatches(id);
    }
}
