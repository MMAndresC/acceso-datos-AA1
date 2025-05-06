package com.svalero.tournament.service;

import com.svalero.tournament.domain.Team;
import com.svalero.tournament.domain.dto.team.TeamConsultWinsDto;
import com.svalero.tournament.domain.dto.team.TeamInDto;
import com.svalero.tournament.domain.dto.team.TeamPatchDto;
import com.svalero.tournament.domain.dto.team.TeamRivalDataDto;
import com.svalero.tournament.exception.TeamNotFoundException;
import com.svalero.tournament.repository.TeamRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class TeamService {

    private TeamRepository teamRepository;

    private ModelMapper modelMapper;

    public TeamService(TeamRepository teamRepository, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    public List<Team> getAll(Integer region, Boolean partner, LocalDate registrationDate){
        if(region == null && partner == null && registrationDate == null) {
            return this.teamRepository.findAll();
        }else{
            return this.teamRepository.filterTeamByRegionPartnerRegistrationDate(region, partner, registrationDate);
        }
    }

    public Team getById(long id) throws TeamNotFoundException {
        return this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
    }

    public Team add(TeamInDto newTeam){
        Team team = modelMapper.map(newTeam, Team.class);
        team.setRegistrationDate(LocalDate.now());
        return this.teamRepository.save(team);
    }

    public void delete(long id) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        this.teamRepository.delete(team);
    }

    public Team modify(long id, TeamInDto teamInDto) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        modelMapper.map(teamInDto, team);
        this.teamRepository.save(team);
        return team;
    }

    public Team update(long id, TeamPatchDto teamPatchDto) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(teamPatchDto, team);
        this.teamRepository.save(team);
        return team;
    }

    public List<TeamConsultWinsDto> getAllTournamentWins(long id) throws TeamNotFoundException{
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        List<TeamConsultWinsDto> listWins = this.teamRepository.getAllTournamentWins(id);
       for(TeamConsultWinsDto win : listWins){
           Optional<TeamRivalDataDto> rivalData = this.teamRepository.getFinalRival(win.getIdMatch());
           String name = rivalData.map(TeamRivalDataDto::getRivalName).orElse(" ");
           int score = rivalData.map(TeamRivalDataDto::getRivalScore).orElse(0);
           win.setRivalName(name);
           win.setRivalScore(score);
       }
       return listWins;
    }
}
