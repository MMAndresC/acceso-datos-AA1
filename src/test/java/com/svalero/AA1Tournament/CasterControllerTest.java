package com.svalero.AA1Tournament;

import com.svalero.AA1Tournament.domain.Caster;
import com.svalero.AA1Tournament.domain.dto.caster.CasterInDto;
import com.svalero.AA1Tournament.domain.dto.caster.CasterPatchDto;
import com.svalero.AA1Tournament.exception.CasterNotFoundException;
import com.svalero.AA1Tournament.security.JwtUtil;
import com.svalero.AA1Tournament.service.CasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CasterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CasterService casterService;

    private String token;

    private final List<Caster> mockCasters = List.of(
            new Caster(1, "Matt Morello", "Mr X", "623145698", 3, "english", LocalDate.parse("2023-06-02"), null),
            new Caster(2, "Harry Pollit", "LEGDAY", "623145987", 3, "english, spanish", LocalDate.parse("2023-06-27"), null)
    );

    @BeforeEach
    void setUp() throws CasterNotFoundException {
        JwtUtil jwtUtil = new JwtUtil();
        this.token = "Bearer " + jwtUtil.generateToken("visitor");
    }

    //Only response HTTP 200
    @Test
    void getAllCasters_ShouldReturnOK() throws Exception {
        when(casterService.getAll(null, null, null)).thenReturn(mockCasters);

        mockMvc.perform(get("/api/v1/casters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Matt Morello")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Harry Pollit")));

        verify(casterService).getAll(null, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllCastersByRegion_ShouldReturnOK() throws Exception {
        int region = 1;
        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> caster.getRegion() == region)
                .toList();
        when(casterService.getAll(null, region, null)).thenReturn(filteredCasters);

        mockMvc.perform(get("/api/v1/casters?region=" + region))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(casterService).getAll(null, region, null);
    }

    //Only response HTTP 200
    @Test
    void getAllCastersByLanguage_ShouldReturnOK() throws Exception {
        String language  = "spanish";
        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> caster.getLanguages().contains(language))
                .toList();

        when(casterService.getAll(language, null, null)).thenReturn(filteredCasters);

        mockMvc.perform(get("/api/v1/casters?language=" + language))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].alias", is("LEGDAY")));

        verify(casterService).getAll(language, null, null);
    }

    //Only response HTTP 200
    @Test
    void getAllCastersByHireDate_ShouldReturnOK() throws Exception {
        LocalDate hireDate = LocalDate.parse("2023-06-20");
        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> (caster.getHireDate().isAfter(hireDate) || caster.getHireDate().isEqual(hireDate)))
                .toList();

        when(casterService.getAll(null, null, hireDate)).thenReturn(filteredCasters);

        mockMvc.perform(get("/api/v1/casters?hireDate=" + hireDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].alias", is("LEGDAY")));

        verify(casterService).getAll(null, null, hireDate);
    }

    //Only response HTTP 200
    @Test
    void getAllCastersWithAllParams_ShouldReturnOK() throws Exception {
        LocalDate hireDate = LocalDate.parse("2023-06-20");
        String language  = "english";
        int region = 3;
        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster ->
                        (caster.getHireDate().isAfter(hireDate) || caster.getHireDate().isEqual(hireDate))
                                && caster.getLanguages().contains(language) && caster.getRegion() == region)
                .toList();

        when(casterService.getAll(language, region, hireDate)).thenReturn(filteredCasters);

        mockMvc.perform(get("/api/v1/casters?region=" + region + "&language=" + language + "&hireDate=" + hireDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].alias", is("LEGDAY")));

        verify(casterService).getAll(language, region, hireDate);
    }

    //Response HTTP 200
    @Test
    void getCasterById_WhenExists_ShouldReturnOK() throws Exception {
        when(casterService.getById(1)).thenReturn(mockCasters.getFirst());

        mockMvc.perform(get("/api/v1/casters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Matt Morello")))
                .andExpect(jsonPath("$.alias", is("Mr X")));

        verify(casterService).getById(1);
    }

    //Response HTTP 404
    @Test
    void getCasterById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        long id = 79;
        when(casterService.getById(id)).thenThrow(new CasterNotFoundException());

        mockMvc.perform(get("/api/v1/casters/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Caster not found")));

        verify(casterService).getById(id);
    }

    //Response HTTP 201 Created
    @Test
    void addCaster_ShouldReturnOK() throws Exception {
        Caster newCaster = new Caster(
                3, "Jennifer Pichette", "LemonKiwi", "675421236", 3,
                "english", LocalDate.parse("2024-09-12"), null
        );
        String requestBody = """
                {
                    "name": "Jennifer Pichette",
                    "alias": "LemonKiwi",
                    "phone": "675421236",
                    "region": 4,
                    "language": "english",
                    "hireDate": "2023-01-01"
                }
                """;

        when(casterService.add(any(CasterInDto.class))).thenReturn(newCaster);

        mockMvc.perform(post("/api/v1/casters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Jennifer Pichette")))
                .andExpect(jsonPath("$.alias", is("LemonKiwi")));

        verify(casterService).add(any(CasterInDto.class));
    }

    //Response HTTP 400 Bad request
    @Test
    void addCaster_ShouldReturnKO() throws Exception {
        String invalidRequestBody = """
                {
                    "alias": "LemonKiwi",
                    "phone": "675421236",
                    "region": 9,
                    "language": "english",
                    "hireDate": "2023-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/casters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists())
                .andExpect(jsonPath("$.errorMessages.name").exists());

        // Service not called
        verify(casterService, never()).add(any(CasterInDto.class));
    }

    //Response HTTP 204 No content
    @Test
    void deleteCaster_WhenExistsAndLogged_ShouldReturnOK() throws Exception {
        mockMvc.perform(delete("/api/v1/casters/1")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());

        verify(casterService).delete(1);
    }

    //Response HTTP 401 Unauthorized
    @Test
    void deleteCaster_WhenNotLogged_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/casters/1"))
                .andExpect(status().isUnauthorized());

        verify(casterService, never()).delete(1);
    }

    //Response HTTP 404 Not Found
    @Test
    void deleteCaster_WhenNotExistsAndLogged_ShouldReturnNotFound() throws Exception {
        long id = 79;
        doThrow(new CasterNotFoundException()).when(casterService).delete(id);
        mockMvc.perform(delete("/api/v1/casters/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Caster not found")));

        verify(casterService).delete(id);
    }

    //Response HTTP 200
    @Test
    void modifyCaster_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Caster modifiedCaster = new Caster(1, "Pepito", "Perez", "699999999",
                2, "english, spanish, french", LocalDate.parse("2023-02-02"), null);

        when(casterService.modify(eq(id), any(CasterInDto.class))).thenReturn(modifiedCaster);

        String requestBody = """
                {
                    "name": "Pepito",
                    "alias": "Perez",
                    "phone": "699999999",
                    "region": 2,
                    "language": "english, spanish, french",
                    "hireDate": "2023-02-02"
                }
                """;

        mockMvc.perform(put("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pepito")))
                .andExpect(jsonPath("$.alias", is("Perez")));

        verify(casterService).modify(eq(id), any(CasterInDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void modifyCaster_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(casterService.modify(eq(id), any(CasterInDto.class)))
                .thenThrow(new CasterNotFoundException());

        String requestBody = """
                {
                    "name": "Pepito",
                    "alias": "Perez",
                    "phone": "699999999",
                    "region": 2,
                    "language": "english, spanish, french",
                    "hireDate": "2023-02-02"
                }
                """;

        mockMvc.perform(put("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(status().isNotFound());

        verify(casterService).modify(eq(id), any(CasterInDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void modifyCaster_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                    "name": "Pepito",
                    "phone": "699999999",
                    "region": 9,
                    "language": "english, spanish, french",
                    "hireDate": "2023-02-02"
                }
                """;

        mockMvc.perform(put("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists())
                .andExpect(jsonPath("$.errorMessages.alias").exists());

        verify(casterService, never()).modify(eq(id), any(CasterInDto.class));
    }

    //Response HTTP 200
    @Test
    void updateCaster_WhenExists_ShouldReturnOK() throws Exception {
        long id = 1;
        Caster updatedCaster = new Caster(1, "Matt Morello", "Mr XXX", "623145987",
                5, "english, spanish", LocalDate.parse("2023-06-02"), null);

        when(casterService.update(eq(id), any(CasterPatchDto.class))).thenReturn(updatedCaster);

        String requestBody = """
                {
                    "alias": "Mr XXX",
                    "languages": "english, spanish",
                    "region": 5
                }
                """;

        mockMvc.perform(patch("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.alias", is("Mr XXX")))
                .andExpect(jsonPath("$.region", is(5)))
                .andExpect(jsonPath("$.languages", is("english, spanish")));

        verify(casterService).update(eq(id), any(CasterPatchDto.class));
    }

    //Response HTTP 404 Not Found
    @Test
    void updateCaster_WhenNotExists_ShouldReturnKO() throws Exception {
        long id = 79;

        when(casterService.update(eq(id), any(CasterPatchDto.class)))
                .thenThrow(new CasterNotFoundException());

        String requestBody = """
                {
                    "alias": "Mr XXX",
                    "languages": "english, spanish",
                    "region": 5
                }
                """;

        mockMvc.perform(patch("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(casterService).update(eq(id), any(CasterPatchDto.class));
    }

    //Response HTTP 400 Bad Request
    @Test
    void updateCaster_BadRequest_ShouldReturnKO() throws Exception {
        long id = 1;

        String invalidRequestBody = """
                {
                    "alias": "Mr XXX",
                    "languages": "english, spanish",
                    "region": 9
                }
                """;

        mockMvc.perform(patch("/api/v1/casters/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorMessages").exists())
                .andExpect(jsonPath("$.errorMessages.region").exists());

        verify(casterService, never()).update(eq(id), any(CasterPatchDto.class));
    }
}