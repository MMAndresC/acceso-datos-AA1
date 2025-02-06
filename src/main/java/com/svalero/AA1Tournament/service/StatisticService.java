package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Match;
import com.svalero.AA1Tournament.domain.Player;
import com.svalero.AA1Tournament.domain.Statistic;
import com.svalero.AA1Tournament.domain.Tournament;
import com.svalero.AA1Tournament.domain.dto.statistics.StatisticsInDto;
import com.svalero.AA1Tournament.exception.MatchNotFoundException;
import com.svalero.AA1Tournament.exception.PlayerNotFoundException;
import com.svalero.AA1Tournament.exception.StatisticsNotFoundException;
import com.svalero.AA1Tournament.exception.TournamentNotFoundException;
import com.svalero.AA1Tournament.repository.MatchRepository;
import com.svalero.AA1Tournament.repository.PlayerRepository;
import com.svalero.AA1Tournament.repository.StatisticsRepository;
import com.svalero.AA1Tournament.repository.TournamentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticService {
    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Statistic> getAll(){
        return this.statisticsRepository.findAll();
    }

    public Statistic getById(long id) throws StatisticsNotFoundException {
        return this.statisticsRepository.findById(id).orElseThrow(StatisticsNotFoundException::new);
    }

    public Statistic add(StatisticsInDto newStatistics, long idMatch, long idPlayer) throws MatchNotFoundException, PlayerNotFoundException {
        Player player = this.playerRepository.findById(idPlayer).orElseThrow(PlayerNotFoundException::new);
        Match match = this.matchRepository.findById(idMatch).orElseThrow(MatchNotFoundException::new);
        Statistic statistic = modelMapper.map(newStatistics, Statistic.class);
        statistic.setPlayer(player);
        statistic.setMatch(match);
        return this.statisticsRepository.save(statistic);
    }

    public void delete(long id) throws StatisticsNotFoundException {
        Statistic statistic = this.statisticsRepository.findById(id).orElseThrow(StatisticsNotFoundException::new);
        this.statisticsRepository.delete(statistic);
    }

    public Statistic modify(long id, StatisticsInDto statisticsInDto) throws StatisticsNotFoundException {
        Statistic statistic = this.statisticsRepository.findById(id).orElseThrow(StatisticsNotFoundException::new);
        modelMapper.map(statisticsInDto, statistic);
        this.statisticsRepository.save(statistic);
        return statistic;
    }
}
