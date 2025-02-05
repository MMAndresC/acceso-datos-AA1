package com.svalero.AA1Tournament.service;

import com.svalero.AA1Tournament.domain.Player;
import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.player.PlayerInDto;
import com.svalero.AA1Tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.AA1Tournament.exception.PlayerNotFoundException;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.repository.PlayerRepository;
import com.svalero.AA1Tournament.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Player> getAll(){
        return this.playerRepository.findAll();
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
}
