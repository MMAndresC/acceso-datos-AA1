package com.svalero.tournament.service;

import com.svalero.tournament.domain.*;
import com.svalero.tournament.domain.dto.statistics.StatisticsInDto;
import com.svalero.tournament.domain.dto.statistics.StatisticsPatchDto;
import com.svalero.tournament.exception.*;
import com.svalero.tournament.repository.MatchRepository;
import com.svalero.tournament.repository.PlayerRepository;
import com.svalero.tournament.repository.StatisticsRepository;
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

    public List<Statistic> getAll(Boolean mvp, Integer kills, Long idPlayer){
        if(mvp == null && kills == null && idPlayer == null){
            return this.statisticsRepository.findAll();
        }else {
            return this.statisticsRepository.filterStatisticByMvpKillsPlayer(mvp, kills, idPlayer);
        }
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

    public Statistic update(long id, StatisticsPatchDto statisticsPatchDto) throws  StatisticsNotFoundException{
        Statistic statistic = this.statisticsRepository.findById(id).orElseThrow(StatisticsNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(statisticsPatchDto, statistic);
        this.statisticsRepository.save(statistic);
        return statistic;
    }

    public List<Statistic> download(long idPlayer) throws PlayerNotFoundException {
        Player player = this.playerRepository.findById(idPlayer).orElseThrow(PlayerNotFoundException::new);
        return this.statisticsRepository.findByPlayerId(idPlayer);
    }
}
