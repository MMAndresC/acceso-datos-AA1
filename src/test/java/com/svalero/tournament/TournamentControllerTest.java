package com.svalero.tournament;

import com.svalero.tournament.domain.Tournament;
import com.svalero.tournament.domain.dto.tournament.TournamentInDto;
import com.svalero.tournament.domain.dto.tournament.TournamentOutDto;
import com.svalero.tournament.domain.dto.tournament.TournamentPatchDto;
import com.svalero.tournament.exception.TournamentNotFoundException;
import com.svalero.tournament.security.JwtUtil;
import com.svalero.tournament.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    private String token;

    private final List<Tournament> mockTournaments = List.of(
            new Tournament(1, "Dragons community open tournament #9", LocalDate.parse("2025-03-26"), LocalDate.parse("2025-03-31"), 500.8F, "Paris, France", "Clara Moreau", 48.85661400, 2.35222190, null),
            new Tournament(2, "FACEIT league Season 3 - EMEA masters", LocalDate.parse("2025-10-07"), LocalDate.parse("2025-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52000660, 13.40495400, null),
            new Tournament(3, "Prove your luck", LocalDate.parse("2024-10-07"), LocalDate.parse("2024-12-15"), 17000, "Berlin, Germany", "Mark Salling", 52.52018660, 13.40495500, null)
    );

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil();
        this.token = "Bearer " + jwtUtil.generateToken("visitor");
    }

    //Only response HTTP 200
    @Test
    void getAllTournaments_ShouldReturnOK() throws Exception {

        when(tournamentService.getAll(null, null, null)).thenReturn(mockTournaments);

        mockMvc.perform(get("/api/v1/tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Prove your luck")));

        verify(tournamentService).getAll(null, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTournamentsByInitDate_ShouldReturnOK() throws Exception {
        LocalDate initDate = LocalDate.parse("2025-01-01");

        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getInitDate().isAfter(initDate) || tournament.getInitDate().isEqual(initDate))
                .toList();

        when(tournamentService.getAll(initDate, null, null)).thenReturn(filteredTournaments);

        mockMvc.perform(get("/api/v1/tournaments?initDate=" + initDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("FACEIT league Season 3 - EMEA masters")));

        verify(tournamentService).getAll(initDate, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTournamentsByManager_ShouldReturnOK() throws Exception {
        String manager = "Clara Moreau";

        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getManager().contains(manager))
                .toList();

        when(tournamentService.getAll(null, manager, null)).thenReturn(filteredTournaments);

        mockMvc.perform(get("/api/v1/tournaments?manager=" + manager))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$[0].prize", is(500.8)));

        verify(tournamentService).getAll(null, manager, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTournamentsByPrize_ShouldReturnOK() throws Exception {
        float prize = 1000;

        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament -> tournament.getPrize() >= prize)
                .toList();

        when(tournamentService.getAll(null, null, prize)).thenReturn(filteredTournaments);

        mockMvc.perform(get("/api/v1/tournaments?prize=" + prize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("FACEIT league Season 3 - EMEA masters")))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].name", is("Prove your luck")));

        verify(tournamentService).getAll(null, null, prize);
    }

    @Test
    public void testGetAllWithParams_ShouldReturnOK() throws Exception {
        LocalDate initDate = LocalDate.parse("2025-01-01");
        String manager = "ar";
        float prize = 50;

        List<Tournament> filteredTournaments = mockTournaments.stream()
                .filter(tournament ->
                        (tournament.getInitDate().isAfter(initDate) || tournament.getInitDate().isEqual(initDate))
                                && tournament.getManager().contains(manager) && tournament.getPrize() >= prize)
                .toList();

        when(tournamentService.getAll(initDate, manager, prize)).thenReturn(filteredTournaments);

        mockMvc.perform(get("/api/v1/tournaments?initDate=" + initDate + "&manager=" + manager + "&prize=" + prize))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("FACEIT league Season 3 - EMEA masters")));

        verify(tournamentService).getAll(initDate, manager, prize);
    }

    //Response HTTP 200
    @Test
    void getTournamentById_WhenExists_ShouldReturnOK() throws Exception {
        when(tournamentService.getById(1)).thenReturn(mockTournaments.getFirst());

        mockMvc.perform(get("/api/v1/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$.prize", is(500.8)));

        verify(tournamentService).getById(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void getTournamentsById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        long id = 79;
        when(tournamentService.getById(id)).thenThrow(new TournamentNotFoundException());

        mockMvc.perform(get("/api/v1/tournaments/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(tournamentService).getById(id);
    }

    //Response HTTP 201 Created
    @Test
    void addTournament_ShouldReturnOK() throws Exception {

        Tournament newTournament = new Tournament(
                4, "Spring Recall",
                LocalDate.parse("2025-02-13"), LocalDate.parse("2025-02-15"),
                1000.00f, "Athens, Greece", "Annita Smith",
                -123.23,  85.15, null
        );

        String requestBody = """
                {
                     "name": "Spring Recall",
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": 1000.00,
                     "manager": "Annita Smith",
                     "address": "Athens, Greece",
                     "latitude": -123.23,
                     "longitude": 85.15
                }
                """;

        when(tournamentService.add(any(TournamentInDto.class))).thenReturn(newTournament);

        mockMvc.perform(post("/api/v1/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("Spring Recall")))
                .andExpect(jsonPath("$.latitude", is(-123.23)));

        verify(tournamentService).add(any(TournamentInDto.class));
    }

    //Response HTTP 400 Bad request
    @Test
    void addTournament_BadRequest_ShouldReturnKO() throws Exception {

        String invalidRequestBody = """
                {
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": -1000.00,
                     "manager": "Annita Smith",
                     "address": "Athens, Greece",
                     "latitude": -123.23,
                     "longitude": 85.15
                }
                """;

        mockMvc.perform(post("/api/v1/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.prize").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists());

        verify(tournamentService, never()).add(any(TournamentInDto.class));
    }

    //Response HTTP 204 No content
    @Test
    void deleteTournament_WhenExistsAndLogged_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/api/v1/tournaments/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(tournamentService).delete(1);
    }

    //Response HTTP 401 Unauthorized
    @Test
    void deleteTournament_WhenNotLogged_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/tournaments/1"))
                .andExpect(status().isUnauthorized());

        verify(tournamentService, never()).delete(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void deleteTournament_WhenNotExistsAndLogged_ShouldReturnNotFound() throws Exception {
        long id = 79;
        doThrow(new TournamentNotFoundException()).when(tournamentService).delete(id);
        mockMvc.perform(delete("/api/v1/tournaments/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(tournamentService).delete(id);
    }

    //Response HTTP 200
    @Test
    void modifyTournament_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Tournament modifiedTournament = new Tournament(
                1, "Spring Recall",
                LocalDate.parse("2025-02-13"), LocalDate.parse("2025-02-15"),
                1000.00f, "Athens, Greece", "Annita Smith",
                -123.23,  85.15, null
        );

        String requestBody = """
                {
                     "name": "Spring Recall",
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": 1000.00,
                     "manager": "Annita Smith",
                     "address": "Athens, Greece",
                     "latitude": -123.23,
                     "longitude": 85.15
                }
                """;

        when(tournamentService.modify(eq(id), any(TournamentInDto.class))).thenReturn(modifiedTournament);

        mockMvc.perform(put("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Spring Recall")))
                .andExpect(jsonPath("$.manager", is("Annita Smith")));

        verify(tournamentService).modify(eq(id), any(TournamentInDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void modifyTournament_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        String requestBody = """
                {
                     "name": "Spring Recall",
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": 1000.00,
                     "manager": "Annita Smith",
                     "address": "Athens, Greece",
                     "latitude": -123.23,
                     "longitude": 85.15
                }
                """;

        when(tournamentService.modify(eq(id), any(TournamentInDto.class)))
                .thenThrow(new TournamentNotFoundException());

        mockMvc.perform(put("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(tournamentService).modify(eq(id), any(TournamentInDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void modifyTournament_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": -1000.00,
                     "manager": "Annita Smith",
                     "address": "Athens, Greece",
                     "latitude": -123.23,
                     "longitude": 85.15
                }
                """;

        mockMvc.perform(put("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.prize").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists());

        verify(tournamentService, never()).modify(eq(id), any(TournamentInDto.class));
    }

    //Response HTTP 200
    @Test
    void updateTournaments_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Tournament updatedTournament = new Tournament(
                1, "Spring Recall",
                LocalDate.parse("2025-02-13"), LocalDate.parse("2025-02-15"),
                1000.00f, "Athens, Greece", "Annita Smith",
                -123.23,  85.15, null
        );

        when(tournamentService.update(eq(id), any(TournamentPatchDto.class))).thenReturn(updatedTournament);

        String requestBody = """
                {
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": 1000.00
                }
                """;

        mockMvc.perform(patch("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Spring Recall")))
                .andExpect(jsonPath("$.initDate", is("2025-02-13")))
                .andExpect(jsonPath("$.prize", is(1000.00)));

        verify(tournamentService).update(eq(id), any(TournamentPatchDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void updateTournaments_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(tournamentService.update(eq(id), any(TournamentPatchDto.class)))
                .thenThrow(new TournamentNotFoundException());

        String requestBody = """
                {
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": 1000.00
                }
                """;

        mockMvc.perform(patch("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(tournamentService).update(eq(id), any(TournamentPatchDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void updateTournaments_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String requestBody = """
                {
                     "initDate": "2025-02-13",
                     "endDate": "2025-02-15",
                     "prize": -1000.00
                }
                """;

        mockMvc.perform(patch("/api/v1/tournaments/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.prize").exists());

        verify(tournamentService, never()).update(eq(id), any(TournamentPatchDto.class));
    }

    //Response HTTP 200
    @Test
    void getAllTeamsWinnersMatches_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        List<TournamentOutDto> mockTournamentsWinnersMatches = List.of(new TournamentOutDto(
                1, "Dragons community open tournament #9", Date.valueOf("2025-01-30"),
                5,  "quarter-finals", "Gen.G"));

        when(tournamentService.getAllTeamsWinnersMatches(eq(id))).thenReturn(mockTournamentsWinnersMatches);

        mockMvc.perform(get("/api/v1/tournaments/" + id + "/match-winners")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nameTournament", is("Dragons community open tournament #9")))
                .andExpect(jsonPath("$[0].nameTeam", is("Gen.G")))
                .andExpect(jsonPath("$[0].day", is(5)));

        verify(tournamentService).getAllTeamsWinnersMatches(eq(id));
    }

    //Response HTTP 404 NotFound
    @Test
    void getAllTeamsWinnersMatches_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 1;

        when(tournamentService.getAllTeamsWinnersMatches(eq(id)))
                .thenThrow(new TournamentNotFoundException());

        mockMvc.perform(get("/api/v1/tournaments/" + id + "/match-winners")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Tournament not found")));

        verify(tournamentService).getAllTeamsWinnersMatches(eq(id));
    }
}
