package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.DetailsMatch;
import com.svalero.AA1Tournament.domain.Match;
import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.detailsMatch.DetailsMatchInDto;
import com.svalero.AA1Tournament.exception.*;
import com.svalero.AA1Tournament.repository.DetailsMatchRepository;
import com.svalero.AA1Tournament.repository.MatchRepository;
import com.svalero.AA1Tournament.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailsMatchService {
    @Autowired
    private DetailsMatchRepository detailsMatchRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<DetailsMatch> getAll(){
        return this.detailsMatchRepository.findAll();
    }

    public DetailsMatch getById(long id) throws DetailsMatchNotFoundException {
        return this.detailsMatchRepository.findById(id).orElseThrow(DetailsMatchNotFoundException::new);
    }

    public DetailsMatch add(DetailsMatchInDto newDetailsMatch, long idMatch, long idTeam) throws TeamNotFoundException, MatchNotFoundException, DetailsMatchAlreadyExistException {
        Team team = this.teamRepository.findById(idTeam).orElseThrow(TeamNotFoundException::new);
        Match match = this.matchRepository.findById(idMatch).orElseThrow(MatchNotFoundException::new);
        //Not add if already exist
        DetailsMatch existingRegister = this.detailsMatchRepository.getDetailMatchByTeam(idMatch, idTeam);
        if(existingRegister != null) throw new DetailsMatchAlreadyExistException();
        DetailsMatch detailsMatch = modelMapper.map(newDetailsMatch, DetailsMatch.class);
        detailsMatch.setTeam(team);
        detailsMatch.setMatch(match);
        return this.detailsMatchRepository.save(detailsMatch);
    }

    public void delete(long id) throws DetailsMatchNotFoundException {
        DetailsMatch detailsMatch = this.detailsMatchRepository.findById(id).orElseThrow(DetailsMatchNotFoundException::new);
        this.detailsMatchRepository.delete(detailsMatch);
    }

    public DetailsMatch modify(long id, DetailsMatchInDto detailsMatchInDto) throws DetailsMatchNotFoundException {
        DetailsMatch detailsMatch = this.detailsMatchRepository.findById(id).orElseThrow(DetailsMatchNotFoundException::new);
        modelMapper.map(detailsMatchInDto, detailsMatch);
        this.detailsMatchRepository.save(detailsMatch);
        return detailsMatch;
    }
}
