package com.svalero.AA1Tournament;

import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.team.TeamConsultWinsDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamPatchDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamRivalDataDto;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.repository.TeamRepository;
import com.svalero.AA1Tournament.service.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TeamService teamService;

    private final List<Team> mockTeams = List.of(
        new Team(1, "Virtus Pro", "Aapo Vartiainen", "654123987", true, LocalDate.parse("2023-05-14"), "Armenia", 3, null, null),
        new Team(2, "Gen.G", "Kent Wakeford", "678456123", true, LocalDate.parse("2023-05-05"), "South Korea", 3, null, null),
        new Team(3, "Sakura", "Kent Wakeford", "678456123", false, LocalDate.parse("2023-05-05"), "France", 3, null, null)
    );

    @Test
    public void testGetAll(){
        when(teamRepository.findAll()).thenReturn(mockTeams);

        List<Team> result = teamService.getAll(null, null, null);

        assertEquals(3, result.size());
        assertEquals(mockTeams.getFirst().getName(), result.getFirst().getName());
        assertEquals(mockTeams.getLast().getName(), result.getLast().getName());

        verify(teamRepository, times(1)).findAll();
        verify(teamRepository, times(0)).filterTeamByRegionPartnerRegistrationDate(null, null, null);
    }

    @Test
    public void testGetAllWithParams(){
        LocalDate registrationDate = LocalDate.parse("2023-01-01");
        boolean partner = true;
        int region = 3;

        List<Team> filteredTeams = mockTeams.stream()
                .filter(team ->
                        (team.getRegistrationDate().isAfter(registrationDate) || team.getRegistrationDate().isEqual(registrationDate))
                                && team.isPartner() == partner && team.getRegion() == region)
                .toList();

        when(teamRepository.filterTeamByRegionPartnerRegistrationDate(region, partner, registrationDate)).thenReturn(filteredTeams);
        List<Team> result = teamService.getAll(region, partner, registrationDate);

        assertEquals(2, result.size());
        assertEquals(mockTeams.getFirst().getName(), result.getFirst().getName());
        assertEquals(mockTeams.get(1).getName(), result.getLast().getName());

        verify(teamRepository, times(0)).findAll();
        verify(teamRepository, times(1)).filterTeamByRegionPartnerRegistrationDate(region, partner, registrationDate);
    }

    @Test
    public void testGetById() throws TeamNotFoundException {
        long id = 2;

        when(teamRepository.findById(id)).thenReturn(Optional.ofNullable(mockTeams.get((int) id - 1)));
        Team result = teamService.getById(id);

        assertEquals(mockTeams.get((int) id - 1).getName(), result.getName());

        verify(teamRepository, times(1)).findById(id);
    }

    @Test
    public void testAdd(){
        TeamInDto teamToSave = new TeamInDto(
                "Sakura", "Kent Wakeford", "678456123",
                false, "France", 3
        );

        Team mappedTeam = new Team(
                0, "Sakura", "Kent Wakeford", "678456123",
                false, LocalDate.now() ,"France", 3, null, null
        );

        Team savedTeam = new Team(
                4, "Sakura", "Kent Wakeford", "678456123",
                false, LocalDate.now() ,"France", 3, null, null
        );

        when(modelMapper.map(teamToSave, Team.class)).thenReturn(mappedTeam);
        when(teamRepository.save(mappedTeam)).thenReturn(savedTeam);

        Team result = teamService.add(teamToSave);

        assertEquals(savedTeam.getName(), result.getName());
        verify(modelMapper).map(eq(teamToSave), eq(Team.class));
        verify(teamRepository, times(1)).save(mappedTeam);
    }

    @Test
    public void testDelete() throws TeamNotFoundException {
        long id = 3;
        Team teamToDelete = mockTeams.get((int) id - 1);

        when(teamRepository.findById(id)).thenReturn(Optional.ofNullable(mockTeams.get((int) id - 1)));
        doNothing().when(teamRepository).delete(teamToDelete);

        Team result = teamService.getById(id);

        assertEquals(teamToDelete, result);

        teamService.delete(id);

        verify(teamRepository, times(1)).delete(teamToDelete);
    }

    @Test
    public void testModify() throws TeamNotFoundException {
        long id = 1;
        Team teamToModify = new Team(
                1, "Virtus Pro", "Aapo Vartiainen",
                "654123987", true, LocalDate.parse("2023-05-14"),
                "Armenia", 3, null, null
        );

        TeamInDto updatedData = new TeamInDto(
                "Virtus Pro", "Aapo Vartiainen", "69999999",
                false, "Armenia", 3
        );

        Team updatedTeam = new Team(
                1, "Virtus Pro", "Aapo Vartiainen", "69999999",
                false, LocalDate.parse("2023-05-14"), "Armenia", 3, null, null
        );

        when(teamRepository.findById(id)).thenReturn(Optional.ofNullable(teamToModify));

        doAnswer(invocation -> {
            TeamInDto dto = invocation.getArgument(0);
            Team target = invocation.getArgument(1);
            target.setName(dto.getName());
            target.setAddress(dto.getAddress());
            target.setPartner(dto.isPartner());
            target.setPhone(dto.getPhone());
            target.setRepresentative(dto.getRepresentative());
            target.setRegion(dto.getRegion());
            return null;
        }).when(modelMapper).map(updatedData, teamToModify);

        when(teamRepository.save(teamToModify)).thenReturn(updatedTeam);

        Team result = teamService.modify(id,updatedData);

        assertEquals("Virtus Pro", result.getName());
        assertEquals("69999999", result.getPhone());
        assertEquals("Armenia", result.getAddress());
        assertFalse(result.isPartner());

        verify(teamRepository, times(1)).findById(id);
        verify(modelMapper).map(eq(updatedData), eq(teamToModify));
        verify(teamRepository, times(1)).save(teamToModify);
    }

    @Test
    public void testUpdate() throws TeamNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        teamService.setModelMapper(realModelMapper);

        long id = 1;
        Team teamToUpdate = mockTeams.getFirst();

        TeamPatchDto updatedData = new TeamPatchDto(
                "Aapo Vartiainen", "69999999",
                false, "Armenia", 3
        );

        Team updatedTeam = new Team(
                1, teamToUpdate.getName(), teamToUpdate.getRepresentative(), "69999999",
                false, teamToUpdate.getRegistrationDate(), "Armenia",
                3, null, null
        );

        when(teamRepository.findById(id)).thenReturn(Optional.of(teamToUpdate));

        when(teamRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Team result = teamService.update(id, updatedData);

        assertEquals(updatedTeam.getAddress(), result.getAddress());
        assertEquals(updatedTeam.getPhone(), result.getPhone());
        assertEquals(updatedTeam.getRegion(), result.getRegion());
        assertEquals(updatedTeam.isPartner(), result.isPartner());
        assertEquals(updatedTeam.getName(), result.getName());


        verify(teamRepository, times(1)).findById(id);
        verify(teamRepository, times(1)).save(updatedTeam);
    }

    @Test
    public void testGetAllTournamentWins() throws TeamNotFoundException {
        long idTeam = 2;
        long idMatch = 2;

        List<TeamConsultWinsDto> mockListWins = List.of(
                new TeamConsultWinsDto(
                        2, "FACEIT league Season 3 - EMEA masters",
                        Date.valueOf("2025-12-15"), 1, 17000.0f, idMatch
                )
        );

        TeamRivalDataDto opponentData = new TeamRivalDataDto("Gen.G", 1);

        when(teamRepository.findById(idTeam)).thenReturn(Optional.of(mockTeams.get((int) idTeam - 1)));
        when(teamRepository.getAllTournamentWins(idTeam)).thenReturn(mockListWins);
        when(teamRepository.getFinalRival(idMatch)).thenReturn(Optional.of(opponentData));

        List<TeamConsultWinsDto> results = teamService.getAllTournamentWins(idTeam);

        assertEquals("FACEIT league Season 3 - EMEA masters", results.getFirst().getNameTournament());
        assertEquals("Gen.G", results.getFirst().getRivalName());

        verify(teamRepository, times(1)).findById(idTeam);
        verify(teamRepository, times(1)).getAllTournamentWins(idTeam);
        verify(teamRepository, times(1)).getFinalRival(idMatch);
    }

    @Test
    public void testTeamNotFoundException() throws TeamNotFoundException {
        long id = 79;

        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> {
            teamService.getById(id);
        });
    }
}
