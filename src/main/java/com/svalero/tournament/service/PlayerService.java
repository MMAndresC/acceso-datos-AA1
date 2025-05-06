package com.svalero.tournament.service;

import com.svalero.tournament.domain.Player;
import com.svalero.tournament.domain.Team;
import com.svalero.tournament.domain.dto.player.PlayerInDto;
import com.svalero.tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.tournament.domain.dto.player.PlayerPatchDto;
import com.svalero.tournament.domain.dto.player.PlayerStatisticsDto;
import com.svalero.tournament.exception.PlayerNotFoundException;
import com.svalero.tournament.exception.TeamNotFoundException;
import com.svalero.tournament.repository.PlayerRepository;
import com.svalero.tournament.repository.TeamRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Data
@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    private TeamRepository teamRepository;

    private ModelMapper modelMapper;

    public PlayerService(ModelMapper modelMapper,PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
    }

    public List<Player> getAll(LocalDate birthDate, Boolean mainRoster, String position){
        if(birthDate == null && mainRoster == null && position == null){
            return this.playerRepository.findAll();
        }else {
            return this.playerRepository.filterPlayerByBirthDateMainRosterPosition(birthDate, mainRoster, position);
        }
    }

    public Player getById(long id) throws PlayerNotFoundException {
        return this.playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
    }

    public Player add(PlayerInDto newPlayer, long idTeam) throws TeamNotFoundException {
        Team team = this.teamRepository.findById(idTeam).orElseThrow(TeamNotFoundException::new);
        Player player = modelMapper.map(newPlayer, Player.class);
        player.setTeam(team);
        return this.playerRepository.save(player);
    }

    public void delete(long id) throws PlayerNotFoundException {
        Player player = this.playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
        this.playerRepository.delete(player);
    }

    public Player modify(long id, PlayerModifyDto playerModifyDto) throws PlayerNotFoundException, TeamNotFoundException {
        Player player = this.playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
        //if player changes team
        long idTeam = playerModifyDto.getIdTeam();
        if(player.getTeam().getId() != idTeam){
            Team team = this.teamRepository.findById(idTeam).orElseThrow(TeamNotFoundException::new);
            player.setTeam(team);
        }
        //prevents it from trying to set the idTeam as the player id
        //for some reason it was mapping the idTeam as player id
        modelMapper.typeMap(PlayerModifyDto.class, Player.class)
                .addMappings(mapper -> mapper.skip(Player::setId));
        modelMapper.map(playerModifyDto, player);
        this.playerRepository.save(player);
        return player;
    }

    public Player update(long id, PlayerPatchDto playerPatchDto) throws PlayerNotFoundException {
        Player player = this.playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
        //if attribute is null, skip it in modelMapper
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(playerPatchDto, player);
        this.playerRepository.save(player);
        return player;
    }

    public List<PlayerStatisticsDto> getMvpStatisticsPlayer(long id) throws PlayerNotFoundException{
        Player player = this.playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
        return this.playerRepository.getMvpStatisticsPlayer(id);
    }
}
