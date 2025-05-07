package com.svalero.tournament;

import com.svalero.tournament.domain.Caster;
import com.svalero.tournament.domain.Match;
import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.match.MatchInDto;
import com.svalero.tournament.domain.dto.match.MatchPatchDto;
import com.svalero.tournament.exception.CasterNotFoundException;
import com.svalero.tournament.exception.MatchNotFoundException;
import com.svalero.tournament.exception.TournamentNotFoundException;
import com.svalero.tournament.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
            new Match(3, LocalDate.parse("2025-01-30"), LocalTime.parse("17:00"), "quarter-finals", "Suravasa", 19, 5, mockCasters.getLast(), mockTournaments.getFirst(), null, null)
    );

    private final String token = "Bearer testtoken";


    //Only response HTTP 200
    @Test
    void getAllMatches_ShouldReturnOK() throws Exception {
        when(matchService.getAll(null, null, null)).thenReturn(mockMatches);

        mockMvc.perform(get("/api/v1/matches")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].mapName", is("Samoa")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].mapName", is("Suravasa")));

        verify(matchService).getAll(null, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllMatchesByMapName_ShouldReturnOK() throws Exception {
        String mapName = "Samoa";
        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> Objects.equals(match.getMapName(), mapName))
                .toList();
        when(matchService.getAll(mapName, null, null)).thenReturn(filteredMatches);

        mockMvc.perform(get("/api/v1/matches?mapName=" + mapName)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("group stage")));

        verify(matchService).getAll(mapName, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllMatchesByDuration_ShouldReturnOK() throws Exception {
        int duration = 21;
        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> match.getDuration() == duration)
                .toList();

        when(matchService.getAll(null, duration, null)).thenReturn(filteredMatches);

        mockMvc.perform(get("/api/v1/matches?duration=" + duration)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].mapName", is("Samoa")));

        verify(matchService).getAll(null, duration, null);
    }

    //Only response HTTP 200
    @Test
    void getAllMatchesByHour_ShouldReturnOK() throws Exception {
        LocalTime hour = LocalTime.parse("18:00");
        List<Match> filteredMatches = mockMatches.stream()
                .filter(match -> (match.getHour().isAfter(hour) || match.getHour().equals(hour)))
                .toList();

        when(matchService.getAll(null, null, hour)).thenReturn(filteredMatches);

        mockMvc.perform(get("/api/v1/matches?hour=" + hour)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].mapName", is("Samoa")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].mapName", is("Watchpoint:Gibraltar")));

        verify(matchService).getAll(null, null, hour);
    }

    //Only response HTTP 200
    @Test
    void getAllMatchesWithAllParams_ShouldReturnOK() throws Exception {
        LocalTime hour = LocalTime.parse("18:00");
        int duration = 21;
        String mapName = "Samoa";
        List<Match> filteredMatches = mockMatches.stream()
                .filter(match ->
                        (match.getHour().isAfter(hour) || match.getHour().equals(hour))
                                && match.getMapName().contains(mapName) && match.getDuration() == duration)
                .toList();

        when(matchService.getAll(mapName, duration, hour)).thenReturn(filteredMatches);

        mockMvc.perform(get("/api/v1/matches?mapName=" + mapName + "&duration=" + duration + "&hour=" + hour)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].day", is(2)));

        verify(matchService).getAll(mapName, duration, hour);
    }

    //Response HTTP 200
    @Test
    void getMatchById_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        when(matchService.getById(id)).thenReturn(mockMatches.getFirst());

        mockMvc.perform(get("/api/v1/matches/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mapName", is("Samoa")))
                .andExpect(jsonPath("$.duration", is(21)));

        verify(matchService).getById(id);
    }

    //Response HTTP 404 Not Found
    @Test
    void getMatchById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        long id = 79;
        when(matchService.getById(id)).thenThrow(new MatchNotFoundException());

        mockMvc.perform(get("/api/v1/matches/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Match not found")));

        verify(matchService).getById(id);
    }

    //Response HTTP 201 Created
    @Test
    void addMatch_ShouldReturnOK() throws Exception {
        long tournamentId = 1;
        Match newMatch = new Match(
                4, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5, mockCasters.getFirst(), mockTournaments.getFirst(), null, null
        );
        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 1
                }
                """;

        when(matchService.add(any(MatchInDto.class), eq(tournamentId))).thenReturn(newMatch);

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.mapName", is("Oasis")))
                .andExpect(jsonPath("$.day", is(5)));

        verify(matchService).add(any(MatchInDto.class), eq(tournamentId));
    }

    //Response HTTP 404 Not Found Tournament
    @Test
    void addMatch_WhenNotExistTournament_ShouldReturnKO() throws Exception {
        long tournamentId = 79;

        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 1
                }
                """;

        when(matchService.add(any(MatchInDto.class), eq(tournamentId)))
                .thenThrow(new TournamentNotFoundException());

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(matchService).add(any(MatchInDto.class), eq(tournamentId));
    }

    //Response HTTP 404 Not Found Caster
    @Test
    void addMatch_WhenNotExistCaster_ShouldReturnKO() throws Exception {
        long tournamentId = 1;

        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 79
                }
                """;

        when(matchService.add(any(MatchInDto.class), eq(tournamentId)))
                .thenThrow(new CasterNotFoundException());

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Caster not found")));

        verify(matchService).add(any(MatchInDto.class), eq(tournamentId));
    }

    //Response HTTP 400 Bad request
    @Test
    void addMatch_BadRequest_ShouldReturnKO() throws Exception {
        long tournamentId = 1;
        String invalidRequestBody = """
                {
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": -22,
                   "day": 5,
                   "idCaster": 1
                }
                """;

        mockMvc.perform(post("/api/v1/tournaments/" + tournamentId + "/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.date").exists())
                .andExpect(jsonPath("$.errorMessages.duration").exists())
                .andExpect(jsonPath("$.errorMessages.hour").exists());

        // Service not called
        verify(matchService, never()).add(any(MatchInDto.class), eq(tournamentId));
    }

    //Response HTTP 204 No content
    @Test
    void deleteMatch_WhenExistsAndLogged_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(matchService).delete(1);
    }

    //Response HTTP 401 Unauthorized
    @Test
    void deleteMatch_WhenNotLogged_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/1"))
                .andExpect(status().isUnauthorized());

        verify(matchService, never()).delete(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void deleteMatch_WhenNotExistsAndLogged_ShouldReturnNotFound() throws Exception {
        long id = 79;
        doThrow(new MatchNotFoundException()).when(matchService).delete(id);
        mockMvc.perform(delete("/api/v1/matches/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Match not found")));

        verify(matchService).delete(id);
    }

    //Response HTTP 200
    @Test
    void modifyMatch_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Match modifiedMatch = new Match(
                4, LocalDate.parse("2025-01-30"), LocalTime.parse("20:00"),
                "quarter-finals", "Oasis", 22, 5,
                mockCasters.getFirst(), mockTournaments.getFirst(), null, null
        );

        when(matchService.modify(eq(id), any(MatchInDto.class))).thenReturn(modifiedMatch);

        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 1
                }
                """;

        mockMvc.perform(put("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.type", is("quarter-finals")))
                .andExpect(jsonPath("$.duration", is(22)));

        verify(matchService).modify(eq(id), any(MatchInDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void modifyMatch_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(matchService.modify(eq(id), any(MatchInDto.class)))
                .thenThrow(new MatchNotFoundException());

        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 1
                }
                """;

        mockMvc.perform(put("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)));

        verify(matchService).modify(eq(id), any(MatchInDto.class));
    }

    //Response HTTP 404 Not Found Caster
    @Test
    void modifyMatch_WhenCasterNotExists_ShouldReturnKO() throws Exception {
        long id = 1;

        when(matchService.modify(eq(id), any(MatchInDto.class)))
                .thenThrow(new CasterNotFoundException());

        String requestBody = """
                {
                   "date": "2025-01-30",
                   "hour": "20:00",
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 79
                }
                """;

        mockMvc.perform(put("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)));

        verify(matchService).modify(eq(id), any(MatchInDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void modifyMatch_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                   "mapName": "Oasis",
                   "type": "quarter-finals",
                   "duration": "22",
                   "day": 5,
                   "idCaster": 1
                }
                """;

        mockMvc.perform(put("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.date").exists())
                .andExpect(jsonPath("$.errorMessages.hour").exists());

        verify(matchService, never()).modify(eq(id), any(MatchInDto.class));
    }

    //Response HTTP 200
    @Test
    void updateMatch_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Match updatedMatch = new Match(
                1, LocalDate.parse("2025-06-01"), LocalTime.parse("19:00"),
                "group stage", "Samoa", 21, 2,
                mockCasters.getLast(), mockTournaments.get(1), null, null
        );

        when(matchService.update(eq(id), any(MatchPatchDto.class))).thenReturn(updatedMatch);

        String requestBody = """
                {
                   "date": "2025-06-01",
                   "hour": "19:00"
                }
                """;

        mockMvc.perform(patch("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mapName", is("Samoa")))
                .andExpect(jsonPath("$.date", is("2025-06-01")))
                .andExpect(jsonPath("$.hour", is("19:00:00")));

        verify(matchService).update(eq(id), any(MatchPatchDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void updateMatch_WhenNotExists_ShouldReturnOK() throws Exception {
        long id = 79;

        when(matchService.update(eq(id), any(MatchPatchDto.class)))
                .thenThrow(new MatchNotFoundException());

        String requestBody = """
                {
                   "date": "2025-06-01",
                   "hour": "19:00"
                }
                """;

        mockMvc.perform(patch("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound());

        verify(matchService).update(eq(id), any(MatchPatchDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void updateMatch_BadRequest_ShouldReturnOK() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                   "date": "2025-06-01",
                   "hour": "19:00",
                   "day" : 0,
                   "duration": -15
                }
                """;

        mockMvc.perform(patch("/api/v1/matches/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.duration").exists())
                .andExpect(jsonPath("$.errorMessages.day").exists());

        verify(matchService, never()).update(eq(id), any(MatchPatchDto.class));
    }
}
