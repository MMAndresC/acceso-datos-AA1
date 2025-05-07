package com.svalero.tournament;

import com.svalero.tournament.domain.Player;
import com.svalero.tournament.domain.Team;
import com.svalero.tournament.domain.dto.player.PlayerInDto;
import com.svalero.tournament.domain.dto.player.PlayerModifyDto;
import com.svalero.tournament.domain.dto.player.PlayerPatchDto;
import com.svalero.tournament.domain.dto.player.PlayerStatisticsDto;
import com.svalero.tournament.exception.PlayerNotFoundException;
import com.svalero.tournament.exception.TeamNotFoundException;
import com.svalero.tournament.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    private final String token = "Bearer testtoken";

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


    //Only response HTTP 200
    @Test
    void getAllPlayers_ShouldReturnOK() throws Exception {

        when(playerService.getAll(null, null, null)).thenReturn(mockPlayers);

        mockMvc.perform(get("/api/v1/players")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].alias", is("Khenail")))
                .andExpect(jsonPath("$[5].id", is(6)))
                .andExpect(jsonPath("$[5].alias", is("Vestola")));

        verify(playerService).getAll(null, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllPlayersByBirthdate_ShouldReturnOK() throws Exception {
        LocalDate birthDate = LocalDate.parse("2004-01-01");

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.getBirthDate().isAfter(birthDate) || player.getBirthDate().isEqual(birthDate))
                .toList();

        when(playerService.getAll(birthDate, null, null)).thenReturn(filteredPlayers);

        mockMvc.perform(get("/api/v1/players?birthDate=" + birthDate)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].alias", is("Khenail")))
                .andExpect(jsonPath("$[2].id", is(4)))
                .andExpect(jsonPath("$[2].alias", is("Galaa")));

        verify(playerService).getAll(birthDate, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllPlayersByMainRoster_ShouldReturnOK() throws Exception {
        boolean mainRoster = true;

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.isMainRoster() == mainRoster)
                .toList();

        when(playerService.getAll(null, mainRoster, null)).thenReturn(filteredPlayers);

        mockMvc.perform(get("/api/v1/players?mainRoster=" + mainRoster)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].alias", is("Khenail")))
                .andExpect(jsonPath("$[5].id", is(6)))
                .andExpect(jsonPath("$[5].alias", is("Vestola")));

        verify(playerService).getAll(null, mainRoster, null);
    }

    //Only response HTTP 200
    @Test
    void getAllPlayersByPosition_ShouldReturnOK() throws Exception {
        String position = "support";

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player -> player.getPosition().equals(position))
                .toList();

        when(playerService.getAll(null, null, position)).thenReturn(filteredPlayers);

        mockMvc.perform(get("/api/v1/players?position=" + position)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].alias", is("Khenail")))
                .andExpect(jsonPath("$[1].id", is(4)))
                .andExpect(jsonPath("$[1].alias", is("Galaa")));

        verify(playerService).getAll(null, null, position);
    }

    //Only response HTTP 200
    @Test
    void getAllMatchesWithAllParams_ShouldReturnOK() throws Exception {
        LocalDate birthDate = LocalDate.parse("2004-01-01");
        boolean mainRoster = true;
        String position = "support";

        List<Player> filteredPlayers = mockPlayers.stream()
                .filter(player ->
                        (player.getBirthDate().isAfter(birthDate) || player.getBirthDate().isEqual(birthDate))
                                && player.isMainRoster() == mainRoster && player.getPosition().equals(position))
                .toList();

        when(playerService.getAll(birthDate, mainRoster, position)).thenReturn(filteredPlayers);

        mockMvc.perform(get("/api/v1/players?birthDate=" + birthDate + "&mainRoster=" + mainRoster + "&position=" + position)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].alias", is("Khenail")))
                .andExpect(jsonPath("$[1].id", is(4)))
                .andExpect(jsonPath("$[1].alias", is("Galaa")));

        verify(playerService).getAll(birthDate, mainRoster, position);
    }

    //Response HTTP 200
    @Test
    void getPlayerById_WhenExists_ShouldReturnOK() throws Exception {
        when(playerService.getById(1)).thenReturn(mockPlayers.getFirst());

        mockMvc.perform(get("/api/v1/players/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Christian Ríos")))
                .andExpect(jsonPath("$.alias", is("Khenail")));

        verify(playerService).getById(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void getPlayerById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        long id = 79;
        when(playerService.getById(id)).thenThrow(new PlayerNotFoundException());

        mockMvc.perform(get("/api/v1/players/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Player not found")));

        verify(playerService).getById(id);
    }

    //Response HTTP 201 Created
    @Test
    void addMatch_WhenTeamExists_ShouldReturnOK() throws Exception {
        long teamId = 1;
        Player newPlayer = new Player(
                7, "Scott Kennedy", "Custa", "688794512",
                LocalDate.parse("1996-09-15"), "tank", true,
                mockTeams.getFirst(), null
        );
        String requestBody = """
                {
                     "name": "Scott Kennedy",
                     "alias": "Custa",
                     "phone": "688794512",
                     "position": "tank",
                     "birthDate": "1996-09-15",
                     "mainRoster": true
                }
                """;

        when(playerService.add(any(PlayerInDto.class), eq(teamId))).thenReturn(newPlayer);

        mockMvc.perform(post("/api/v1/teams/" + teamId + "/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.name", is("Scott Kennedy")))
                .andExpect(jsonPath("$.alias", is("Custa")));

        verify(playerService).add(any(PlayerInDto.class), eq(teamId));
    }

    //Response HTTP 404 Not Found Team
    @Test
    void addMatch_WhenTeamNotExists_ShouldReturnKO() throws Exception {
        long teamId = 79;

        String requestBody = """
                {
                     "name": "Scott Kennedy",
                     "alias": "Custa",
                     "phone": "688794512",
                     "position": "tank",
                     "birthDate": "1996-09-15",
                     "mainRoster": true
                }
                """;

        when(playerService.add(any(PlayerInDto.class), eq(teamId))).thenThrow(new TeamNotFoundException());

        mockMvc.perform(post("/api/v1/teams/" + teamId + "/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(playerService).add(any(PlayerInDto.class), eq(teamId));
    }

    //Response HTTP 400 Bad request
    @Test
    void addMatch_BadRequest_ShouldReturnKO() throws Exception {
        long teamId = 1;

        String invalidRequestBody = """
                {
                     "phone": "688794512",
                     "position": "tank",
                     "birthDate": "1996-09-15",
                     "mainRoster": true
                }
                """;

        mockMvc.perform(post("/api/v1/teams/" + teamId + "/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists())
                .andExpect(jsonPath("$.errorMessages.alias").exists());

        verify(playerService, never()).add(any(PlayerInDto.class), eq(teamId));
    }

    //Response HTTP 204 No content
    @Test
    void deletePlayer_WhenExistsAndLogged_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/api/v1/players/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(playerService).delete(1);
    }

    //Response HTTP 401 Unauthorized
    @Test
    void deletePlayer_WhenNotLogged_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/players/1"))
                .andExpect(status().isUnauthorized());

        verify(playerService, never()).delete(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void deletePlayer_WhenNotExistsAndLogged_ShouldReturnNotFound() throws Exception {
        long id = 79;
        doThrow(new PlayerNotFoundException()).when(playerService).delete(id);
        mockMvc.perform(delete("/api/v1/players/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Player not found")));

        verify(playerService).delete(id);
    }

    //Response HTTP 200
    @Test
    void modifyPlayer_WhenExists_ShouldReturnOK() throws Exception {
        long id = 6;
        Player modifyPlayer = new Player(
                6, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "tank", false,
                mockTeams.getFirst(), null
        );

        when(playerService.modify(eq(id), any(PlayerModifyDto.class))).thenReturn(modifyPlayer);

        String requestBody = """
                {
                     "name": "Ilari Vestola",
                     "alias": "Vestola",
                     "phone": "699999999",
                     "position": "tank",
                     "birthDate": "2000-04-07",
                     "mainRoster": false,
                     "idTeam": 1
                }
                """;

        mockMvc.perform(put("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.name", is("Ilari Vestola")))
                .andExpect(jsonPath("$.phone", is("699999999")))
                .andExpect(jsonPath("$.mainRoster", is(false)))
                .andExpect(jsonPath("$.alias", is("Vestola")));

        verify(playerService).modify(eq(id), any(PlayerModifyDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void modifyPlayer_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        String requestBody = """
                {
                     "name": "Ilari Vestola",
                     "alias": "Vestola",
                     "phone": "699999999",
                     "position": "tank",
                     "birthDate": "2000-04-07",
                     "mainRoster": false,
                     "idTeam": 1
                }
                """;

        when(playerService.modify(eq(id), any(PlayerModifyDto.class))).thenThrow(new PlayerNotFoundException());

        mockMvc.perform(put("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Player not found")));

        verify(playerService).modify(eq(id), any(PlayerModifyDto.class));
    }

    //Response HTTP 404 Not Found Team
    @Test
    void modifyPlayer_WhenTeamNotExists_ShouldReturnKO() throws Exception {
        long id = 6;

        String requestBody = """
                {
                     "name": "Ilari Vestola",
                     "alias": "Vestola",
                     "phone": "699999999",
                     "position": "tank",
                     "birthDate": "2000-04-07",
                     "mainRoster": false,
                     "idTeam": 79
                }
                """;

        when(playerService.modify(eq(id), any(PlayerModifyDto.class))).thenThrow(new TeamNotFoundException());

        mockMvc.perform(put("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Team not found")));

        verify(playerService).modify(eq(id), any(PlayerModifyDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void modifyPlayer_BadRequest_ShouldReturnKO() throws Exception {
        long id = 6;

        String invalidRequestBody = """
                {
                     "phone": "699999999",
                     "position": "tank",
                     "birthDate": "2000-04-07",
                     "mainRoster": false,
                     "idTeam": 79
                }
                """;

        mockMvc.perform(put("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists())
                .andExpect(jsonPath("$.errorMessages.alias").exists());

        verify(playerService, never()).modify(eq(id), any(PlayerModifyDto.class));
    }

    //Response HTTP 200
    @Test
    void updatePlayer_WhenExists_ShouldReturnOK() throws Exception {
        long id = 6;
        Player updatePlayer = new Player(
                6, "Ilari Vestola", "Vestola", "699999999",
                LocalDate.parse("2000-04-07"), "support", false,
                mockTeams.getFirst(), null
        );

        when(playerService.update(eq(id), any(PlayerPatchDto.class))).thenReturn(updatePlayer);

        String requestBody = """
                {
                    "phone": "699999999",
                    "position": "support",
                    "mainRoster": false
                }
                """;

        mockMvc.perform(patch("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.phone", is("699999999")))
                .andExpect(jsonPath("$.position", is("support")))
                .andExpect(jsonPath("$.alias", is("Vestola")))
                .andExpect(jsonPath("$.mainRoster", is(false)));

        verify(playerService).update(eq(id), any(PlayerPatchDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void updatePlayer_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(playerService.update(eq(id), any(PlayerPatchDto.class)))
                .thenThrow(new PlayerNotFoundException());

        String requestBody = """
                {
                    "phone": "699999999",
                    "position": "support",
                    "mainRoster": false
                }
                """;

        mockMvc.perform(patch("/api/v1/players/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Player not found")));

        verify(playerService).update(eq(id), any(PlayerPatchDto.class));
    }

    //Response HTTP 200
    @Test
    void getMvpStatisticsPlayer_WhenExists_ShouldReturnOK() throws Exception {
        long id = 2;

        List<PlayerStatisticsDto> playerHighlights = List.of(
                new PlayerStatisticsDto(
                        2, "FACEIT league Season 3 - EMEA masters", Date.valueOf("2025-10-07"),
                        "Watchpoint:Gibraltar", true, "final", 30, 15, 12
                )
        );

        when(playerService.getMvpStatisticsPlayer(eq(id))).thenReturn(playerHighlights);

        mockMvc.perform(get("/api/v1/players/" + id + "/highlights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTournament", is(2)))
                .andExpect(jsonPath("$[0].kills", is(30)))
                .andExpect(jsonPath("$[0].mapName", is("Watchpoint:Gibraltar")))
                .andExpect(jsonPath("$[0].name", is("FACEIT league Season 3 - EMEA masters")))
                .andExpect(jsonPath("$[0].typeMatch", is("final")));

        verify(playerService).getMvpStatisticsPlayer(eq(id));
    }

    //Response HTTP 404 Not Found
    @Test
    void getMvpStatisticsPlayer_WhenNotExists_ShouldReturnOK() throws Exception {
        long id = 79;

        when(playerService.getMvpStatisticsPlayer(eq(id)))
                .thenThrow(new PlayerNotFoundException());

        mockMvc.perform(get("/api/v1/players/" + id + "/highlights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Player not found")));

        verify(playerService).getMvpStatisticsPlayer(eq(id));
    }
}
