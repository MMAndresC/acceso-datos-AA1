package com.svalero.tournament;

import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.tournament.TournamentInDto;
import com.svalero.tournament.domain.dto.tournament.TournamentOutDto;
import com.svalero.tournament.domain.dto.tournament.TournamentPatchDto;
import com.svalero.tournament.exception.TournamentNotFoundException;
import com.svalero.tournament.repository.TournamentRepository;
import com.svalero.tournament.service.TournamentService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Unit tests tournament service
@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TournamentService tournamentService;

    private final List<Tournament> mockTournaments = List.of(
            new Tournament(1, "Dragons community open tournament #9", LocalDate.parse("2025-03-26"), LocalDate.parse("2025-03-31"), 500.8F, "Paris, France", "Clara Moreau", 48.85661400, 2.35222190, null),
            new Tournament(2, "FACEIT league Season 3 - EMEA masters", LocalDate.parse("2025-10-07"), LocalDate.parse("2025-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52000660, 13.40495400, null),
            new Tournament(3, "Prove your luck", LocalDate.parse("2024-10-07"), LocalDate.parse("2024-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52018660, 13.40495500, null)
    );

    @Test
    public void testGetAll(){
        when(tournamentRepository.findAll()).thenReturn(mockTournaments);

        //Without params
        List<Tournament> tournaments = tournamentService.getAll(null, null, null);
        assertEquals(3, tournaments.size());
        assertEquals(mockTournaments.getFirst().getName(), tournaments.getFirst().getName());
        assertEquals(mockTournaments.getLast().getName(), tournaments.getLast().getName());

        verify(tournamentRepository, times(1)).findAll();
        verify(tournamentRepository, times(0)).filterTournamentByInitDateManagerPrize(null, null, null);
    }

    @Test
    public void testGetAllByInitDate(){
        LocalDate initDate = LocalDate.parse("2025-01-01");

        //Expected result
        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getInitDate().isAfter(initDate) || tournament.getInitDate().isEqual(initDate))
                .toList();

        when(tournamentRepository.filterTournamentByInitDateManagerPrize(initDate, null, null)).thenReturn(filteredTournaments);
        List<Tournament> result = tournamentService.getAll(initDate, null, null);

        assertEquals(2, result.size());
        assertEquals(mockTournaments.getFirst().getName(), result.getFirst().getName());
        assertEquals(mockTournaments.get(1).getName(), result.getLast().getName());

        verify(tournamentRepository, times(0)).findAll();
        verify(tournamentRepository, times(1)).filterTournamentByInitDateManagerPrize(initDate, null, null);
    }

    @Test
    public void testGetAllByManager(){
        String manager = "Clara Moreau";

        //Expected result
        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getManager().contains(manager))
                .toList();

        when(tournamentRepository.filterTournamentByInitDateManagerPrize(null, manager, null)).thenReturn(filteredTournaments);
        List<Tournament> result = tournamentService.getAll(null, manager, null);

        assertEquals(1, result.size());
        assertEquals(mockTournaments.getFirst().getName(), result.getFirst().getName());
        assertEquals(mockTournaments.getFirst().getPrize(), result.getFirst().getPrize());

        verify(tournamentRepository, times(0)).findAll();
        verify(tournamentRepository, times(1)).filterTournamentByInitDateManagerPrize(null, manager, null);
    }

    @Test
    public void testGetAllByPrize(){
        float prize = 1000;

        //Expected result
        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getPrize() >= prize)
                .toList();

        when(tournamentRepository.filterTournamentByInitDateManagerPrize(null, null, prize)).thenReturn(filteredTournaments);
        List<Tournament> result = tournamentService.getAll(null, null, prize);

        assertEquals(2, result.size());
        assertEquals(mockTournaments.get(1).getName(), result.getFirst().getName());
        assertEquals(mockTournaments.getLast().getName(), result.getLast().getName());

        verify(tournamentRepository, times(0)).findAll();
        verify(tournamentRepository, times(1)).filterTournamentByInitDateManagerPrize(null, null, prize);
    }

    @Test
    public void testGetAllWithParams(){
        LocalDate initDate = LocalDate.parse("2025-01-01");
        String manager = "ar";
        float prize = 50;

        //Expected result
        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament ->
                        (tournament.getInitDate().isAfter(initDate) || tournament.getInitDate().isEqual(initDate))
                    && tournament.getManager().contains(manager) && tournament.getPrize() >= prize)
                .toList();

        when(tournamentRepository.filterTournamentByInitDateManagerPrize(initDate, manager, prize)).thenReturn(filteredTournaments);
        List<Tournament> result = tournamentService.getAll(initDate, manager, prize);

        assertEquals(2, result.size());
        assertEquals(mockTournaments.getFirst().getName(), result.getFirst().getName());
        assertEquals(mockTournaments.get(1).getName(), result.getLast().getName());

        verify(tournamentRepository, times(0)).findAll();
        verify(tournamentRepository, times(1)).filterTournamentByInitDateManagerPrize(initDate, manager, prize);
    }

    @Test
    public void testGetById() throws TournamentNotFoundException {
        long id = 2;

        when (tournamentRepository.findById(id)).thenReturn(Optional.ofNullable(mockTournaments.get((int) (id-1))));
        Tournament result = tournamentService.getById(id);

        assertEquals(mockTournaments.get((int) (id-1)), result);

        verify(tournamentRepository, times(1)).findById((id));
    }

    @Test
    public void testAdd(){
        TournamentInDto tournamentToSave = new TournamentInDto(
                "New tournament",
                LocalDate.parse("2025-06-05"),
                LocalDate.parse("2025-06-15"),
                500, "Athens, Greece", "Pedro Gutierrez", 37.98376, 23.72784
        );

        Tournament mappedTournament = new Tournament(
                0, "New tournament",
                LocalDate.parse("2025-06-05"),
                LocalDate.parse("2025-06-15"),
                500, "Athens, Greece", "Pedro Gutierrez", 37.98376, 23.72784, null
        );


        Tournament savedTournament = new Tournament(
                4, "New tournament",
                LocalDate.parse("2025-06-05"),
                LocalDate.parse("2025-06-15"),
                500, "Athens, Greece", "Pedro Gutierrez", 37.98376, 23.72784, null
        );

        when(modelMapper.map(eq(tournamentToSave), eq(Tournament.class))).thenReturn(mappedTournament);
        when(tournamentRepository.save(mappedTournament)).thenReturn(savedTournament);

        Tournament result = tournamentService.add(tournamentToSave);

        assertEquals(savedTournament, result);
        verify(modelMapper).map(eq(tournamentToSave), eq(Tournament.class));
        verify(tournamentRepository, times(1)).save(mappedTournament);
    }

    @Test
    public void testDelete() throws TournamentNotFoundException {
        long id = 3;
        Tournament tournamentToDelete = mockTournaments.get((int) (id - 1));

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournamentToDelete));
        doNothing().when(tournamentRepository).delete(tournamentToDelete);

        Tournament result = tournamentService.getById(id);
        assertEquals(tournamentToDelete, result);

        tournamentService.delete(id);

        verify(tournamentRepository, times(1)).delete(tournamentToDelete);
    }

    @Test
    public void testModify() throws TournamentNotFoundException {
        long id = 1;
        Tournament tournamentToModify = mockTournaments.getFirst();

        TournamentInDto updatedData = new TournamentInDto(
                "Updated Tournament", LocalDate.parse("2025-06-01"), LocalDate.parse("2025-06-10"),
                1750, "Barcelona, Spain", "Jon Vazquez", 41.3851, 2.1734
        );

        Tournament updatedTournament = new Tournament(
                1, "Updated Tournament", LocalDate.parse("2025-06-01"), LocalDate.parse("2025-06-10"),
                1750, "Barcelona, Spain", "Jon Vazquez", 41.3851, 2.1734, null
        );

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournamentToModify));

        doAnswer(invocation -> {
            TournamentInDto dto = invocation.getArgument(0);
            Tournament target = invocation.getArgument(1);
            target.setName(dto.getName());
            target.setInitDate(dto.getInitDate());
            target.setEndDate(dto.getEndDate());
            target.setPrize(dto.getPrize());
            target.setAddress(dto.getAddress());
            target.setManager(dto.getManager());
            target.setLatitude(dto.getLatitude());
            target.setLongitude(dto.getLongitude());
            return null;
        }).when(modelMapper).map(updatedData, tournamentToModify);

        when(tournamentRepository.save(tournamentToModify)).thenReturn(updatedTournament);

        Tournament result = tournamentService.modify(id, updatedData);

        assertEquals("Updated Tournament", result.getName());
        assertEquals("Jon Vazquez", result.getManager());
        assertEquals("Barcelona, Spain", result.getAddress());
        assertEquals(1750, result.getPrize());

        verify(tournamentRepository, times(1)).findById(id);
        verify(modelMapper).map(eq(updatedData), eq(tournamentToModify));
        verify(tournamentRepository, times(1)).save(tournamentToModify);
    }

    @Test
    public void testUpdate() throws TournamentNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        tournamentService.setModelMapper(realModelMapper);

        long id = 1;
        Tournament tournamentToUpdate = mockTournaments.getFirst();

        TournamentPatchDto updatedData = new TournamentPatchDto(
                null, LocalDate.parse("2025-06-01"), LocalDate.parse("2025-06-10"),
                1750f, null, null, null, null
        );

        Tournament updatedTournament = new Tournament(
                1, tournamentToUpdate.getName(), LocalDate.parse("2025-06-01"), LocalDate.parse("2025-06-10"),
                1750, tournamentToUpdate.getAddress(), tournamentToUpdate.getManager(),
                tournamentToUpdate.getLatitude(), tournamentToUpdate.getLongitude(), null
        );

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournamentToUpdate));

        when(tournamentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Tournament result = tournamentService.update(id, updatedData);

        assertEquals(updatedTournament.getInitDate(), result.getInitDate());
        assertEquals(updatedTournament.getEndDate(), result.getEndDate());
        assertEquals(updatedTournament.getPrize(), result.getPrize());
        assertEquals(updatedTournament.getName(), result.getName());


        verify(tournamentRepository, times(1)).findById(id);
        verify(tournamentRepository, times(1)).save(updatedTournament);
    }

    @Test
    public void testGetAllTeamsWinnersMatches() throws TournamentNotFoundException {
        long id = 1;

        List<TournamentOutDto> mockTournamentsWinnersMatches = List.of(new TournamentOutDto(
                1, "Dragons community open tournament #9", Date.valueOf("2025-01-30"),
                5,  "quarter-finals", "Gen.G"));



        when(tournamentRepository.findById(id)).thenReturn(Optional.of(mockTournaments.getFirst()));
        when(tournamentRepository.getAllTeamsWinnersMatches(id)).thenReturn(mockTournamentsWinnersMatches);

        List<TournamentOutDto> results = tournamentService.getAllTeamsWinnersMatches(id);

        assertEquals(mockTournaments.getFirst().getName(), results.getFirst().getNameTournament());
        verify(tournamentRepository, times(1)).findById(id);
        verify(tournamentRepository, times(1)).getAllTeamsWinnersMatches(id);
    }

    @Test
    public void testTournamentNotFoundException() throws TournamentNotFoundException {
        long id = 79;

        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.getById(id);
        });
    }
}
