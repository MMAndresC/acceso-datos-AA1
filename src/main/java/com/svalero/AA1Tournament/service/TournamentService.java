package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Tournament;
import com.svalero.AA1Tournament.domain.dto.tournament.TournamentInDto;
import com.svalero.AA1Tournament.exception.FilterCriteriaNotFoundException;
import com.svalero.AA1Tournament.exception.TournamentNotFoundException;
import com.svalero.AA1Tournament.repository.TournamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Tournament> getAll(){
        return this.tournamentRepository.findAll();
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
    public List<Tournament> filter(LocalDate initDate, String manager, Float prize) throws FilterCriteriaNotFoundException {
        if(initDate == null && manager == null && prize == null){
            throw new FilterCriteriaNotFoundException("No tournament filters found");
        }else {
            return this.tournamentRepository.filterTournamentByRegionManagerPrize(initDate, manager, prize);
        }
    }
}
