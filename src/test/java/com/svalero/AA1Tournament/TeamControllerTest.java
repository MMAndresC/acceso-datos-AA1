package com.svalero.AA1Tournament;

import com.svalero.AA1Tournament.domain.Team;
import com.svalero.AA1Tournament.domain.dto.team.TeamConsultWinsDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamInDto;
import com.svalero.AA1Tournament.domain.dto.team.TeamPatchDto;
import com.svalero.AA1Tournament.exception.CasterNotFoundException;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.security.JwtUtil;
import com.svalero.AA1Tournament.service.TeamService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    private String token;

    private final List<Team> mockTeams = List.of(
            new Team(1, "Virtus Pro", "Aapo Vartiainen", "654123987", true, LocalDate.parse("2023-05-14"), "Armenia", 3, null, null),
            new Team(2, "Gen.G", "Kent Wakeford", "678456123", true, LocalDate.parse("2023-05-05"), "South Korea", 3, null, null),
            new Team(3, "Sakura", "Kent Wakeford", "678456123", false, LocalDate.parse("2023-05-05"), "France", 3, null, null)
    );

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil();
        this.token = "Bearer " + jwtUtil.generateToken("visitor");
    }

    //Only response HTTP 200
    @Test
    void getAllTeams_ShouldReturnOK() throws Exception {

        when(teamService.getAll(null, null, null)).thenReturn(mockTeams);

        mockMvc.perform(get("/api/v1/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Virtus Pro")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Sakura")));

        verify(teamService).getAll(null, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTeamsByRegion_ShouldReturnOK() throws Exception {
        int region = 3;

        List<Team> filteredTeams = mockTeams.stream()
                .filter(team -> team.getRegion() == region)
                .toList();

        when(teamService.getAll(region, null, null)).thenReturn(filteredTeams);

        mockMvc.perform(get("/api/v1/teams?region=" + region))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Virtus Pro")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Sakura")));

        verify(teamService).getAll(region, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTeamsByPartner_ShouldReturnOK() throws Exception {
        boolean partner = true;

        List<Team> filteredTeams = mockTeams.stream()
                .filter(team -> team.isPartner() == partner)
                .toList();

        when(teamService.getAll(null, partner, null)).thenReturn(filteredTeams);

        mockMvc.perform(get("/api/v1/teams?partner=" + partner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Virtus Pro")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Gen.G")));

        verify(teamService).getAll(null, partner, null);
    }

    //Only response HTTP 200
    @Test
    void getAllTeamsByRegistrationDate_ShouldReturnOK() throws Exception {
        LocalDate registrationDate = LocalDate.parse("2023-05-10");

        List<Team> filteredTeams = mockTeams.stream()
                .filter(team -> team.getRegistrationDate().isAfter(registrationDate) || team.getRegistrationDate().isEqual(registrationDate))
                .toList();

        when(teamService.getAll(null, null, registrationDate)).thenReturn(filteredTeams);

        mockMvc.perform(get("/api/v1/teams?registrationDate=" + registrationDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Virtus Pro")));

        verify(teamService).getAll(null, null, registrationDate);
    }

    //Only response HTTP 200
    @Test
    void getAllTeamsWithParams_ShouldReturnOK() throws Exception {
        LocalDate registrationDate = LocalDate.parse("2023-01-01");
        boolean partner = true;
        int region = 3;

        List<Team> filteredTeams = mockTeams.stream()
                .filter(team ->
                        (team.getRegistrationDate().isAfter(registrationDate) || team.getRegistrationDate().isEqual(registrationDate))
                                && team.isPartner() == partner && team.getRegion() == region)
                .toList();

        when(teamService.getAll(region, partner, registrationDate)).thenReturn(filteredTeams);

        mockMvc.perform(get("/api/v1/teams?registrationDate=" + registrationDate + "&partner=" + partner + "&region=" + region))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Virtus Pro")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Gen.G")));

        verify(teamService).getAll(region, partner, registrationDate);
    }

    //Response HTTP 200
    @Test
    void getTeamById_WhenExists_ShouldReturnOK() throws Exception {
        when(teamService.getById(1)).thenReturn(mockTeams.getFirst());

        mockMvc.perform(get("/api/v1/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Virtus Pro")))
                .andExpect(jsonPath("$.phone", is("654123987")));

        verify(teamService).getById(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void getTeamById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        long id = 79;
        when(teamService.getById(id)).thenThrow(new TeamNotFoundException());

        mockMvc.perform(get("/api/v1/teams/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(teamService).getById(id);
    }

    //Response HTTP 201 Created
    @Test
    void addTeam_ShouldReturnOK() throws Exception {

        Team newTeam = new Team(
                4, "G2 esports", "Carlos Rodriguez", "698532471",
                true, LocalDate.now() ,"Awrwefwe 11,Berlin,Germany",
                3, null, null
        );

        String requestBody = """
                {
                    "name": "G2 esports",
                    "representative": "Carlos Rodriguez",
                    "phone": "698532471",
                    "partner": true,
                    "address": "Awrwefwe 11,Berlin,Germany",
                    "region": 3
                }
                """;

        when(teamService.add(any(TeamInDto.class))).thenReturn(newTeam);

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("G2 esports")))
                .andExpect(jsonPath("$.region", is(3)));

        verify(teamService).add(any(TeamInDto.class));
    }

    //Response HTTP 400 Bad request
    @Test
    void addTeam_BadRequest_ShouldReturnKO() throws Exception {

        String invalidRequestBody = """
                {
                    "representative": "Carlos Rodriguez",
                    "phone": "698532471",
                    "partner": true,
                    "address": "Awrwefwe 11,Berlin,Germany",
                    "region": 9
                }
                """;

        mockMvc.perform(post("/api/v1/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists());

        verify(teamService, never()).add(any(TeamInDto.class));
    }

    //Response HTTP 204 No content
    @Test
    void deleteTeam_WhenExistsAndLogged_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/api/v1/teams/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(teamService).delete(1);
    }

    //Response HTTP 401 Unauthorized
    @Test
    void deleteTeam_WhenNotLogged_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/teams/1"))
                .andExpect(status().isUnauthorized());

        verify(teamService, never()).delete(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void deleteTeam_WhenNotExistsAndLogged_ShouldReturnNotFound() throws Exception {
        long id = 79;
        doThrow(new TeamNotFoundException()).when(teamService).delete(id);
        mockMvc.perform(delete("/api/v1/teams/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(teamService).delete(id);
    }

    //Response HTTP 200
    @Test
    void modifyTeam_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Team modifiedTeam = new Team(
                1, "Virtus Pro", "Aapo Vartiainen", "69999999",
                false, LocalDate.parse("2023-05-14"), "Armenia", 3, null, null
        );

        when(teamService.modify(eq(id), any(TeamInDto.class))).thenReturn(modifiedTeam);

        String requestBody = """
                {
                    "name": "Virtus Pro",
                    "representative": "Aapo Vartiainen",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Armenia",
                    "region": 3
                }
                """;

        mockMvc.perform(put("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Virtus Pro")))
                .andExpect(jsonPath("$.phone", is("69999999")));

        verify(teamService).modify(eq(id), any(TeamInDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void modifyTeam_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(teamService.modify(eq(id), any(TeamInDto.class)))
                .thenThrow(new TeamNotFoundException());

        String requestBody = """
                {
                    "name": "Virtus Pro",
                    "representative": "Aapo Vartiainen",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Armenia",
                    "region": 3
                }
                """;

        mockMvc.perform(put("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(teamService).modify(eq(id), any(TeamInDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void modifyTeam_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                    "representative": "Aapo Vartiainen",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Armenia",
                    "region": 9
                }
                """;

        mockMvc.perform(put("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists());

        verify(teamService, never()).modify(eq(id), any(TeamInDto.class));
    }

    //Response HTTP 200
    @Test
    void updateTeam_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Team updateTeam = new Team(
                1, "Virtus Pro", "Juan Perez", "69999999",
                false, LocalDate.parse("2023-05-14"), "Spain", 3, null, null
        );

        when(teamService.update(eq(id), any(TeamPatchDto.class))).thenReturn(updateTeam);

        String requestBody = """
                {
                    "representative": "Juan Perez",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Spain",
                     "region": 3
                }
                """;

        mockMvc.perform(patch("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Virtus Pro")))
                .andExpect(jsonPath("$.address", is("Spain")))
                .andExpect(jsonPath("$.phone", is("69999999")));

        verify(teamService).update(eq(id), any(TeamPatchDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void updateTeam_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(teamService.update(eq(id), any(TeamPatchDto.class)))
                .thenThrow(new TeamNotFoundException());

        String requestBody = """
                {
                    "representative": "Juan Perez",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Spain",
                     "region": 3
                }
                """;

        mockMvc.perform(patch("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(teamService).update(eq(id), any(TeamPatchDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void updateTeam_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                    "representative": "Juan Perez",
                    "phone": "69999999",
                    "partner": false,
                    "address": "Spain",
                    "region": 9
                }
                """;

        mockMvc.perform(patch("/api/v1/teams/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists());

        verify(teamService, never()).update(eq(id), any(TeamPatchDto.class));
    }

    //Response HTTP 200
    @Test
    void getAllTournamentWins_WhenExists_ShouldReturnOK() throws Exception {
        long idTeam = 2;
        long idMatch = 2;

        List<TeamConsultWinsDto> mockListWins = List.of(
                new TeamConsultWinsDto(
                        2, "FACEIT league Season 3 - EMEA masters",
                        Date.valueOf("2025-12-15"), 1, 17000.0f, idMatch
                )
        );

        when(teamService.getAllTournamentWins(eq(idTeam))).thenReturn(mockListWins);

        mockMvc.perform(get("/api/v1/teams/" + idTeam + "/wins")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTournament", is(2)))
                .andExpect(jsonPath("$[0].score", is(1)))
                .andExpect(jsonPath("$[0].nameTournament", is("FACEIT league Season 3 - EMEA masters")))
                .andExpect(jsonPath("$[0].prize", is(17000.0)));

        verify(teamService).getAllTournamentWins(eq(idTeam));
    }

    //Response HTTP 404 NotFound
    @Test
    void getAllTournamentWins_WhenNotExists_ShouldReturnOK() throws Exception {
        long idTeam = 79;

        when(teamService.getAllTournamentWins(eq(idTeam)))
                .thenThrow(new TeamNotFoundException());

        mockMvc.perform(get("/api/v1/teams/" + idTeam + "/wins")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(teamService).getAllTournamentWins(eq(idTeam));
    }
}
