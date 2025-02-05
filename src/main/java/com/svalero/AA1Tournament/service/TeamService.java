package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Team> getAll(){
        return this.teamRepository.findAll();
    }

    public Team getById(long id) throws TeamNotFoundException {
        return this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
    }

    public Team add(TeamInDto newTeam){
        Team team = modelMapper.map(newTeam, Team.class);
        team.setRegistrationDate(LocalDate.now());
        return this.teamRepository.save(team);
    }

    public String delete(long id) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        this.teamRepository.delete(team);
        return "1 row deleted";
    }

    public Team modify(long id, TeamInDto teamInDto) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(id).orElseThrow(TeamNotFoundException::new);
        modelMapper.map(teamInDto, team);
        this.teamRepository.save(team);
        return team;
    }
}
