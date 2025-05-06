package com.svalero.tournament;

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
import com.svalero.tournament.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlayerService playerService;

    private final List<Team> mockTeams = List.of(
            new Team(1, "Virtus Pro", "Aapo Vartiainen", "654123987", true, LocalDate.parse("2023-05-14"), "Armenia", 3, null, null),
            new Team(2, "Gen.G", "Kent Wakeford", "678456123", true, LocalDate.parse("2023-05-05"), "South Korea", 3, null, null),
            new Team(3, "Sakura", "Kent Wakeford", "678456123", false, LocalDate.parse("2023-05-05"), "France", 3, null, null)
    );

    private final List<Player> mockPlayers = List.of(
            new Player(1, "Christian Ríos", "Khenail", "654789321", LocalDate.parse("2005-08-20"), "support", true, mockTeams.get(1), null),
            new Player(2, "Paavo Ulmanen", "Sauna", "647888956", LocalDate.parse("2004-10-03"), "DPS", true, mockTeams.get(1), null),
            new Player(3, "Kim Tae-sung", "Mag", "622365894", LocalDate.parse("2002-06-12"), "tank", true, mockTeams.get(1), null),
            new Player(4, "Jesus Nuñez Lopez", "Galaa", "688741235", LocalDate.parse("2004-04-09"), "support", true, mockTeams.getFirst(), null),
            new Player(5, "Niclas Smidt Jensen", "sHockWave", "685125795", LocalDate.parse("2000-07-25"), "DPS", true, mockTeams.getFirst(), null),
            new Player(6, "Ilari Vestola", "Vestola", "699854123", LocalDate.parse("2000-04-07"), "tank", true, mockTeams.getFirst(), null)
    );

    @Test
    public void testGetAll(){
        when(playerRepository.findAll()).thenReturn(mockPlayers);

        List<Player> result = playerService.getAll(null, null, null);

        assertEquals(6, result.size());
        assertEquals("Khenail", result.getFirst().getAlias());
        assertEquals("Vestola", result.getLast().getAlias());

        verify(playerRepository, times(1)).findAll();
        verify(playerRepository, times(0)).filterPlayerByBirthDateMainRosterPosition(null, null, null);
    }

    @Test
    public void testGetAllByBirthDate(){
        LocalDate birthDate = LocalDate.parse("2004-01-01");

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.getBirthDate().isAfter(birthDate) || player.getBirthDate().isEqual(birthDate))
                .toList();

        when(playerRepository.filterPlayerByBirthDateMainRosterPosition(birthDate, null, null)).thenReturn(filteredPlayers);

        List<Player> result = playerService.getAll(birthDate, null, null);

        assertEquals(3, result.size());
        assertEquals("Khenail", result.getFirst().getAlias());
        assertEquals(LocalDate.parse("2005-08-20"), result.getFirst().getBirthDate());
        assertEquals("Galaa", result.getLast().getAlias());
        assertEquals(LocalDate.parse("2004-04-09"), result.getLast().getBirthDate());

        verify(playerRepository, times(0)).findAll();
        verify(playerRepository, times(1)).filterPlayerByBirthDateMainRosterPosition(birthDate, null, null);
    }

    @Test
    public void testGetAllByMainRoster(){
        boolean mainRoster = true;

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.isMainRoster() == mainRoster)
                .toList();

        when(playerRepository.filterPlayerByBirthDateMainRosterPosition(null, mainRoster, null)).thenReturn(filteredPlayers);

        List<Player> result = playerService.getAll(null, mainRoster, null);

        assertEquals(6, result.size());
        assertEquals("Khenail", result.getFirst().getAlias());
        assertEquals(LocalDate.parse("2005-08-20"), result.getFirst().getBirthDate());
        assertEquals("Vestola", result.getLast().getAlias());
        assertEquals(LocalDate.parse("2000-04-07"), result.getLast().getBirthDate());

        verify(playerRepository, times(0)).findAll();
        verify(playerRepository, times(1)).filterPlayerByBirthDateMainRosterPosition(null, mainRoster, null);
    }

    @Test
    public void testGetAllByPosition(){
        String position = "support";

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.getPosition().equals(position))
                .toList();

        when(playerRepository.filterPlayerByBirthDateMainRosterPosition(null, null, position)).thenReturn(filteredPlayers);

        List<Player> result = playerService.getAll(null, null, position);

        assertEquals(2, result.size());
        assertEquals("Khenail", result.getFirst().getAlias());
        assertEquals(LocalDate.parse("2005-08-20"), result.getFirst().getBirthDate());
        assertEquals("Galaa", result.getLast().getAlias());
        assertEquals(LocalDate.parse("2004-04-09"), result.getLast().getBirthDate());

        verify(playerRepository, times(0)).findAll();
        verify(playerRepository, times(1)).filterPlayerByBirthDateMainRosterPosition(null, null, position);
    }

    @Test
    public void testGetAllWithParams(){
        LocalDate birthDate = LocalDate.parse("2004-01-01");
        boolean mainRoster = true;
        String position = "support";

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player ->
                        (player.getBirthDate().isAfter(birthDate) || player.getBirthDate().isEqual(birthDate))
                                && player.isMainRoster() == mainRoster && player.getPosition().equals(position))
                .toList();

        when(playerRepository.filterPlayerByBirthDateMainRosterPosition(birthDate, mainRoster, position)).thenReturn(filteredPlayers);

        List<Player> result = playerService.getAll(birthDate, mainRoster, position);

        assertEquals(2, result.size());
        assertEquals("Khenail", result.getFirst().getAlias());
        assertEquals(LocalDate.parse("2005-08-20"), result.getFirst().getBirthDate());
        assertEquals("Galaa", result.getLast().getAlias());
        assertEquals(LocalDate.parse("2004-04-09"), result.getLast().getBirthDate());

        verify(playerRepository, times(0)).findAll();
        verify(playerRepository, times(1)).filterPlayerByBirthDateMainRosterPosition(birthDate, mainRoster, position);
    }

    @Test
    public void testGetById() throws PlayerNotFoundException {
        long id = 2;

        when(playerRepository.findById(id)).thenReturn(Optional.of(mockPlayers.get((int) id - 1)));

        Player result = playerService.getById(id);

        assertEquals(mockPlayers.get((int) id - 1).getName(), result.getName());

        verify(playerRepository, times(1)).findById(id);
    }

    //Version not emulating modelMapper, mocking response only
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAdd() throws TeamNotFoundException {
        long teamId = 1;

        PlayerInDto playerInDto = new PlayerInDto(
                "Jane Austen", "Eskay", "675421236", LocalDate.parse("2000-09-12"),
                "support", false
        );

        Player playerToSave = new Player(
                0, "Jane Austen", "Eskay", "675421236",
                LocalDate.parse("2000-09-12"), "support", false,
                mockTeams.getFirst(), null
        );

        Player completeRegister = new Player(
                7, "Jane Austen", "Eskay", "675421236",
                LocalDate.parse("2000-09-12"), "support", false,
                mockTeams.getFirst(), null
        );

        when(teamRepository.findById(teamId)).thenReturn(Optional.ofNullable(mockTeams.getFirst()));
        when(modelMapper.map(playerInDto, Player.class)).thenReturn(playerToSave);
        when(playerRepository.save(playerToSave)).thenReturn(completeRegister);

        Player result = playerService.add(playerInDto, teamId);

        assertEquals(7, result.getId());
        assertEquals("Eskay", result.getAlias());
        assertEquals(1, result.getTeam().getId());

        verify(teamRepository, times(1)).findById(teamId);
        verify(playerRepository, times(1)).save(playerToSave);
    }

    @Test
    public void testDelete() throws PlayerNotFoundException {
        long id = 6;

        Player playerToDelete = mockPlayers.getLast();

        when(playerRepository.findById(id)).thenReturn(Optional.ofNullable(mockPlayers.getLast()));
        assert playerToDelete != null;
        doNothing().when(playerRepository).delete(playerToDelete);

        Player result = playerService.getById(id);

        assertEquals(playerToDelete, result);

        playerService.delete(id);

        verify(playerRepository, times(1)).delete(playerToDelete);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testModifyNoChangingTeam() throws PlayerNotFoundException, TeamNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        playerService.setModelMapper(realModelMapper);

        long id = 6;
        Player playerToModify = mockPlayers.getLast();

        PlayerModifyDto updatedData = new PlayerModifyDto(
                playerToModify.getTeam().getId(), "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "tank", false
        );

        Player updatedPlayer = new Player(
                6, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "tank", false,
                mockTeams.getFirst(), null
        );

        when(playerRepository.findById(id)).thenReturn(Optional.of(playerToModify));
        when(teamRepository.findById(playerToModify.getTeam().getId())).thenReturn(Optional.of(mockTeams.getFirst()));
        when(playerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.modify(6,updatedData);

        assertEquals(updatedPlayer.getAlias(), result.getAlias());
        assertEquals(updatedPlayer.getPhone(), result.getPhone());
        assertEquals(updatedPlayer.isMainRoster(), result.isMainRoster());
        assertEquals(updatedPlayer.getTeam().getId(), result.getTeam().getId());


        verify(teamRepository, times(0)).findById(Objects.requireNonNull(mockTeams.getFirst()).getId());
        verify(playerRepository, times(1)).findById(id);
        verify(playerRepository, times(1)).save(updatedPlayer);
    }

    @Test
    public void testModifyChangingTeam() throws PlayerNotFoundException, TeamNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        playerService.setModelMapper(realModelMapper);

        long id = 6;
        long idTeam = 2;
        Player playerToModify = mockPlayers.getLast();

        PlayerModifyDto updatedData = new PlayerModifyDto(
                idTeam, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "tank", false
        );

        Player updatedPlayer = new Player(
                6, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "tank", false,
                mockTeams.get(1), null
        );

        when(playerRepository.findById(id)).thenReturn(Optional.of(playerToModify));
        when(teamRepository.findById(idTeam)).thenReturn(Optional.of(mockTeams.get(1)));
        when(playerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.modify(6,updatedData);

        assertEquals(updatedPlayer.getAlias(), result.getAlias());
        assertEquals(updatedPlayer.getPhone(), result.getPhone());
        assertEquals(updatedPlayer.isMainRoster(), result.isMainRoster());
        assertEquals(idTeam, result.getTeam().getId());

        // Only activated when new data player team not equal to saved team
        verify(teamRepository, times(1)).findById(Objects.requireNonNull(mockTeams.get(1)).getId());
        verify(playerRepository, times(1)).findById(id);
        verify(playerRepository, times(1)).save(updatedPlayer);
    }

    @Test
    public void testUpdate() throws PlayerNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        playerService.setModelMapper(realModelMapper);

        long id = 6;
        Player playerToUpdate = mockPlayers.getLast();

        PlayerPatchDto updatedData = new PlayerPatchDto(
                "699999999", "support", false
        );

        Player updatedPlayer = new Player(
                6, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "support", false,
                mockTeams.getFirst(), null
        );

        when(playerRepository.findById(id)).thenReturn(Optional.of(playerToUpdate));
        when(playerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Player result = playerService.update(6,updatedData);

        assertEquals(updatedPlayer.getAlias(), result.getAlias());
        assertEquals(updatedPlayer.getPhone(), result.getPhone());
        assertEquals(updatedPlayer.isMainRoster(), result.isMainRoster());
        assertEquals(updatedPlayer.getPosition(), result.getPosition());
        assertEquals(updatedPlayer.getTeam().getId(), result.getTeam().getId());

        verify(playerRepository, times(1)).findById(id);
        verify(playerRepository, times(1)).save(updatedPlayer);
    }

    @Test
    public void testGetMvpStatisticsPlayer() throws PlayerNotFoundException {
        long id = 2;

        List<PlayerStatisticsDto> playerStatisticsDto = List.of(
                new PlayerStatisticsDto(
                2, "FACEIT league Season 3 - EMEA masters", Date.valueOf("2025-10-07"),
                "Watchpoint:Gibraltar", true, "final", 30, 15, 12
                )
        );

        when(playerRepository.findById(id)).thenReturn(Optional.of(mockPlayers.get((int) id - 1)));
        when(playerRepository.getMvpStatisticsPlayer(id)).thenReturn(playerStatisticsDto);

        List<PlayerStatisticsDto> result = playerService.getMvpStatisticsPlayer(id);

        assertEquals(1, result.size());
        assertEquals("FACEIT league Season 3 - EMEA masters", result.getFirst().getName());
        assertTrue(result.getFirst().isMvp());

        verify(playerRepository, times(1)).findById(id);
        verify(playerRepository, times(1)).getMvpStatisticsPlayer(id);
    }

    @Test
    public void testPlayerNotFoundException() throws PlayerNotFoundException {
        long id = 79;

        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.getById(id);
        });
    }
}
