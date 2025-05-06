package com.svalero.tournament;

import com.svalero.tournament.domain.Caster;
import com.svalero.tournament.domain.Match;
import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.match.MatchInDto;
import com.svalero.tournament.domain.dto.match.MatchPatchDto;
import com.svalero.tournament.exception.*;
import com.svalero.tournament.repository.CasterRepository;
import com.svalero.tournament.repository.MatchRepository;
import com.svalero.tournament.repository.TournamentRepository;
import com.svalero.tournament.service.MatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private CasterRepository casterRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MatchService matchService;

    private final List<Tournament> mockTournaments = List.of(
            new Tournament(1, "Dragons community open tournament #9", LocalDate.parse("2025-03-26"), LocalDate.parse("2025-03-31"), 500.8F, "Paris, France", "Clara Moreau", 48.85661400, 2.35222190, null),
            new Tournament(2, "FACEIT league Season 3 - EMEA masters", LocalDate.parse("2025-10-07"), LocalDate.parse("2025-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52000660, 13.40495400, null),
            new Tournament(3, "Prove your luck", LocalDate.parse("2024-10-07"), LocalDate.parse("2024-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52018660, 13.40495500, null)
    );

    private final List<Caster> mockCasters = List.of(
            new Caster(1, "Matt Morello", "Mr X", "623145698", 3, "english", LocalDate.parse("2023-06-02"), null),
            new Caster(2, "Harry Pollit", "LEGDAY", "623145987", 3, "english, spanish", LocalDate.parse("2023-06-27"), null)
    );

    private final List<Match> mockMatches = List.of(
        new Match(1, LocalDate.parse("2024-10-17"), LocalTime.parse("18:00"), "group stage", "Samoa", 21, 2, mockCasters.getLast(), mockTournaments.get(1), null, null),
        new Match( 2, LocalDate.parse("2024-12-15"), LocalTime.parse("18:00"), "final", "Watchpoint:Gibraltar", 28, 12, mockCasters.getFirst(), mockTournaments.get(1), null, null),
        new Match(3, LocalDate.parse("2025-01-30"), LocalTime.parse("19:00"), "quarter-finals", "Suravasa", 19, 5, mockCasters.getLast(), mockTournaments.getFirst(), null, null)
    );

    @Test
    public void testGetAll(){
        when(matchRepository.findAll()).thenReturn(mockMatches);

        List<Match> result = matchService.getAll(null, null, null);

        assertEquals(3, result.size());
        assertEquals("Samoa", result.getFirst().getMapName());
        assertEquals(19, result.getLast().getDuration());

        verify(matchRepository, times(1)).findAll();
        verify(matchRepository, times(0)).filterMatchesByMapNameDurationHour(null, null, null);
    }

    @Test
    public void testGetAllByMapName(){
        String mapName = "Suravasa";

        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> match.getMapName().equals(mapName))
                .toList();

        when(matchRepository.filterMatchesByMapNameDurationHour(mapName, null, null)).thenReturn(filteredMatches);

        List<Match> result = matchService.getAll(mapName, null, null);

        assertEquals(1, result.size());
        assertEquals(mockMatches.getLast().getDate(), result.getFirst().getDate());
        assertEquals(mockMatches.getLast().getType(), result.getFirst().getType());

        verify(matchRepository, times(0)).findAll();
        verify(matchRepository, times(1)).filterMatchesByMapNameDurationHour(mapName, null, null);
    }

    @Test
    public void testGetAllByDuration(){
        int duration = 19;

        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> match.getDuration() == duration)
                .toList();

        when(matchRepository.filterMatchesByMapNameDurationHour(null, duration, null)).thenReturn(filteredMatches);

        List<Match> result = matchService.getAll(null, duration, null);

        assertEquals(1, result.size());
        assertEquals(mockMatches.getLast().getDate(), result.getFirst().getDate());
        assertEquals(mockMatches.getLast().getType(), result.getFirst().getType());

        verify(matchRepository, times(0)).findAll();
        verify(matchRepository, times(1)).filterMatchesByMapNameDurationHour(null, duration, null);
    }

    @Test
    public void testGetAllByHour(){
        LocalTime hour = LocalTime.parse("16:00");

        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> match.getHour().isAfter(hour) || match.getHour().equals(hour))
                .toList();

        when(matchRepository.filterMatchesByMapNameDurationHour(null, null, hour)).thenReturn(filteredMatches);

        List<Match> result = matchService.getAll(null, null, hour);

        assertEquals(3, result.size());
        assertEquals(mockMatches.getFirst().getDate(), result.getFirst().getDate());
        assertEquals(mockMatches.getLast().getType(), result.getLast().getType());

        verify(matchRepository, times(0)).findAll();
        verify(matchRepository, times(1)).filterMatchesByMapNameDurationHour(null, null, hour);
    }

    @Test
    public void testGetAllWithParams(){
        LocalTime hour = LocalTime.parse("16:00");
        String mapName = "Suravasa";
        int duration = 19;

        List<Match> filteredMatches = mockMatches.stream()
                .filter(match ->
                        (match.getHour().isAfter(hour) || match.getHour().equals(hour))
                                && match.getMapName().equals(mapName) && match.getDuration() == duration)
                .toList();

        when(matchRepository.filterMatchesByMapNameDurationHour(mapName, duration, hour)).thenReturn(filteredMatches);

        List<Match> result = matchService.getAll(mapName, duration, hour);

        assertEquals(1, result.size());
        assertEquals(mockMatches.getLast().getDate(), result.getFirst().getDate());
        assertEquals(mockMatches.getLast().getType(), result.getFirst().getType());

        verify(matchRepository, times(0)).findAll();
        verify(matchRepository, times(1)).filterMatchesByMapNameDurationHour(mapName, duration, hour);
    }

    @Test
    public void testGetById() throws MatchNotFoundException {
        long id = 2;

        when(matchRepository.findById(id)).thenReturn(Optional.of(mockMatches.get((int) id - 1)));

        Match result = matchService.getById(id);

        assertEquals(mockMatches.get((int) id - 1).getTournament().getId(), result.getTournament().getId());

        verify(matchRepository, times(1)).findById(id);
    }

    @Test
    public void testAdd() throws CasterNotFoundException, TournamentNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        matchService.setModelMapper(realModelMapper);

        long tournamentId = 1;
        long casterId = 1;

        MatchInDto matchInDto = new MatchInDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, casterId
        );

        Match matchToSave = new Match(
                0, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, mockCasters.getFirst(), mockTournaments.getFirst(), null, null
        );

        Match completeRegister = new Match(
                4, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, mockCasters.getFirst(), mockTournaments.getFirst(), null, null
        );

        when(casterRepository.findById(casterId)).thenReturn(Optional.of(mockCasters.getFirst()));
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(mockTournaments.getFirst()));
        when(matchRepository.save(matchToSave)).thenReturn(completeRegister);

        Match result = matchService.add(matchInDto, tournamentId);

        assertEquals(4, result.getId());
        assertEquals("Oasis", result.getMapName());
        assertEquals(1, result.getTournament().getId());

        verify(casterRepository, times(1)).findById(casterId);
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(matchRepository, times(1)).save(matchToSave);
    }

    @Test
    public void testAddNotExistingTournament()  {
        long tournamentId = 9;
        long casterId = 1;

        MatchInDto matchInDto = new MatchInDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, casterId
        );

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class, () -> {
            matchService.add(matchInDto,tournamentId);
        });
    }

    @Test
    public void testAddNotExistingCaster()  {
        long tournamentId = 1;
        long casterId = 9;

        MatchInDto matchInDto = new MatchInDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, casterId
        );

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(mockTournaments.getFirst()));
        when(casterRepository.findById(casterId)).thenReturn(Optional.empty());

        assertThrows(CasterNotFoundException.class, () -> {
            matchService.add(matchInDto,tournamentId);
        });
    }

    @Test
    public void testDelete() throws MatchNotFoundException {
        long id = 3;

        Match matchToDelete = mockMatches.getLast();

        when(matchRepository.findById(id)).thenReturn(Optional.of(mockMatches.getLast()));
        doNothing().when(matchRepository).delete(matchToDelete);

        Match result = matchService.getById(id);

        assertEquals(matchToDelete, result);

        matchService.delete(id);

        verify(matchRepository, times(1)).delete(matchToDelete);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testModifyNoChangingCaster() throws CasterNotFoundException, MatchNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        matchService.setModelMapper(realModelMapper);

        long id = 3;

        Match matchToModify = mockMatches.getLast();

        MatchInDto updatedData = new MatchInDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, matchToModify.getCaster().getId()
        );

        Match updatedMatch = new Match(
                id, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5,
                matchToModify.getCaster(), matchToModify.getTournament(), null, null
        );

        when(matchRepository.findById(id)).thenReturn(Optional.of(matchToModify));
        when(matchRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Match result = matchService.modify(id, updatedData);

        assertEquals(updatedMatch.getType(), result.getType());
        assertEquals(updatedMatch.getMapName(), result.getMapName());
        assertEquals(updatedMatch.getCaster().getId(), result.getCaster().getId());


        verify(casterRepository, times(0)).findById(Objects.requireNonNull(mockMatches.getLast()).getId());
        verify(matchRepository, times(1)).findById(id);
        verify(matchRepository, times(1)).save(updatedMatch);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testModifyWithChangingCaster() throws CasterNotFoundException, MatchNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        matchService.setModelMapper(realModelMapper);

        long id = 3;
        long casterId = 1;

        Match matchToModify = mockMatches.getLast();

        MatchInDto updatedData = new MatchInDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, casterId
        );

        Match updatedMatch = new Match(
                id, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, mockCasters.getFirst(),
                matchToModify.getTournament(), null, null
        );

        when(casterRepository.findById(casterId)).thenReturn(Optional.of(mockCasters.getFirst()));
        when(matchRepository.findById(id)).thenReturn(Optional.of(matchToModify));
        when(matchRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Match result = matchService.modify(id, updatedData);

        assertEquals(updatedMatch.getType(), result.getType());
        assertEquals(updatedMatch.getMapName(), result.getMapName());
        assertEquals(updatedMatch.getCaster().getId(), result.getCaster().getId());


        verify(casterRepository, times(1)).findById(casterId);
        verify(matchRepository, times(1)).findById(id);
        verify(matchRepository, times(1)).save(updatedMatch);
    }

    @Test
    public void testUpdate() throws MatchNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        matchService.setModelMapper(realModelMapper);

        long id = 3;

        Match matchToUpdate = mockMatches.getLast();

        MatchPatchDto updatedData = new MatchPatchDto(
                LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                 "Oasis", 22, 5
        );

        Match updatedMatch = new Match(
                id, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, mockCasters.getLast(),
                matchToUpdate.getTournament(), null, null
        );

        when(matchRepository.findById(id)).thenReturn(Optional.of(matchToUpdate));
        when(matchRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Match result = matchService.update(id,updatedData);

        assertEquals(updatedData.getDate(), result.getDate());
        assertEquals(updatedData.getHour(), result.getHour());
        assertEquals(updatedData.getMapName(), result.getMapName());
        assertEquals(updatedData.getDuration(), result.getDuration());

        verify(matchRepository, times(1)).findById(id);
        verify(matchRepository, times(1)).save(updatedMatch);
    }

    @Test
    public void testMatchNotFoundException() {
        long id = 79;

        when(matchRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MatchNotFoundException.class, () -> {
            matchService.getById(id);
        });
    }
}
