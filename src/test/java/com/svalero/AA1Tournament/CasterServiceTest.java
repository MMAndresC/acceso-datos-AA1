package com.svalero.AA1Tournament;

import com.svalero.AA1Tournament.domain.Caster;
import com.svalero.AA1Tournament.domain.dto.caster.CasterInDto;
import com.svalero.AA1Tournament.domain.dto.caster.CasterPatchDto;
import com.svalero.AA1Tournament.exception.CasterNotFoundException;
import com.svalero.AA1Tournament.exception.TeamNotFoundException;
import com.svalero.AA1Tournament.repository.CasterRepository;
import com.svalero.AA1Tournament.service.CasterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CasterServiceTest {

    @Mock
    private CasterRepository casterRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CasterService casterService;

    private final List<Caster> mockCasters = List.of(
            new Caster(1, "Matt Morello", "Mr X", "623145698", 3, "english", LocalDate.parse("2023-06-02"), null),
            new Caster(2, "Harry Pollit", "LEGDAY", "623145987", 3, "english, spanish", LocalDate.parse("2023-06-27"), null)
    );

    @Test
    public void testGetAll() {
        when(casterRepository.findAll()).thenReturn(mockCasters);

        List<Caster> result = casterService.getAll(null, null, null);

        assertEquals(2, result.size());
        assertEquals("Mr X", result.getFirst().getAlias());
        assertEquals("LEGDAY", result.getLast().getAlias());

        verify(casterRepository, times(1)).findAll();
        verify(casterRepository, times(0)).filterCastersByRegionLanguageHireDate(null, null, null);
    }

    @Test
    public void testGetAllByRegion(){
        int region = 3;

        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> caster.getRegion() == region)
                .toList();

        when(casterRepository.filterCastersByRegionLanguageHireDate(region, null, null)).thenReturn(filteredCasters);

        List<Caster> result = casterService.getAll(null, region, null);

        assertEquals(2, result.size());
        assertEquals("Mr X", result.getFirst().getAlias());
        assertEquals("LEGDAY", result.getLast().getAlias());

        verify(casterRepository, times(0)).findAll();
        verify(casterRepository, times(1)).filterCastersByRegionLanguageHireDate(region, null, null);
    }

    @Test
    public void testGetAllByLanguage(){
        String language  = "spanish";

        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> caster.getLanguages().contains(language))
                .toList();

        when(casterRepository.filterCastersByRegionLanguageHireDate(null, language, null)).thenReturn(filteredCasters);

        List<Caster> result = casterService.getAll(language, null, null);

        assertEquals(1, result.size());
        assertEquals("LEGDAY", result.getFirst().getAlias());

        verify(casterRepository, times(0)).findAll();
        verify(casterRepository, times(1)).filterCastersByRegionLanguageHireDate(null, language, null);
    }

    @Test
    public void testGetAllByHireDate(){
        LocalDate hireDate = LocalDate.parse("2023-06-20");

        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster -> (caster.getHireDate().isAfter(hireDate) || caster.getHireDate().isEqual(hireDate)))
                .toList();

        when(casterRepository.filterCastersByRegionLanguageHireDate(null, null, hireDate)).thenReturn(filteredCasters);

        List<Caster> result = casterService.getAll(null, null, hireDate);

        assertEquals(1, result.size());
        assertEquals("LEGDAY", result.getFirst().getAlias());

        verify(casterRepository, times(0)).findAll();
        verify(casterRepository, times(1)).filterCastersByRegionLanguageHireDate(null, null, hireDate);
    }

    @Test
    public void testGetAllWithParams(){
        LocalDate hireDate = LocalDate.parse("2023-06-20");
        String language  = "english";
        int region = 3;

        List<Caster> filteredCasters = mockCasters.stream()
                .filter(caster ->
                        (caster.getHireDate().isAfter(hireDate) || caster.getHireDate().isEqual(hireDate))
                                && caster.getLanguages().contains(language) && caster.getRegion() == region)
                .toList();

        when(casterRepository.filterCastersByRegionLanguageHireDate(region, language, hireDate)).thenReturn(filteredCasters);

        List<Caster> result = casterService.getAll(language, region, hireDate);

        assertEquals(1, result.size());
        assertEquals("LEGDAY", result.getFirst().getAlias());
        assertEquals(LocalDate.parse("2023-06-27"), result.getFirst().getHireDate());
        assertEquals(3, result.getFirst().getRegion());

        verify(casterRepository, times(0)).findAll();
        verify(casterRepository, times(1)).filterCastersByRegionLanguageHireDate(region, language, hireDate);
    }

    @Test
    public void testGetById() throws CasterNotFoundException {
        long id = 2;

        when(casterRepository.findById(id)).thenReturn(Optional.of(mockCasters.get((int) id - 1)));

        Caster result = casterService.getById(id);

        assertEquals(mockCasters.get((int) id - 1).getName(), result.getName());

        verify(casterRepository, times(1)).findById(id);
    }

    //Version not emulating modelMapper, mocking response only
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testAdd() throws TeamNotFoundException {
        long teamId = 1;

        CasterInDto casterInDto = new CasterInDto(
                "Jennifer Pichette", "LemonKiwi", "675421236", 3,
                "english", LocalDate.parse("2024-09-12")
        );

        Caster casterToSave = new Caster(
                0, "Jennifer Pichette", "LemonKiwi", "675421236", 3,
                "english", LocalDate.parse("2024-09-12"), null
        );

        Caster completeRegister = new Caster(
                3, "Jennifer Pichette", "LemonKiwi", "675421236", 3,
                "english", LocalDate.parse("2024-09-12"), null
        );

        when(modelMapper.map(casterInDto, Caster.class)).thenReturn(casterToSave);
        when(casterRepository.save(casterToSave)).thenReturn(completeRegister);

        Caster result = casterService.add(casterInDto);

        assertEquals(3, result.getId());
        assertEquals("LemonKiwi", result.getAlias());

        verify(casterRepository, times(1)).save(casterToSave);
    }

    @Test
    public void testDelete() throws CasterNotFoundException {
        long id = 2;

        Caster casterToDelete = mockCasters.getLast();

        when(casterRepository.findById(id)).thenReturn(Optional.of(mockCasters.getLast()));
        doNothing().when(casterRepository).delete(casterToDelete);

        Caster result = casterService.getById(id);

        assertEquals(casterToDelete, result);

        casterService.delete(id);

        verify(casterRepository, times(1)).delete(casterToDelete);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void testModify() throws CasterNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        casterService.setModelMapper(realModelMapper);

        long id = 2;

        Caster casterToModify = mockCasters.getLast();

        CasterInDto updatedData = new CasterInDto(
                "Harry Pollit", "LEGDAY", "699999999",
                4, "english, spanish, french", LocalDate.parse("2023-06-27")
        );

        Caster updatedCaster = new Caster(
                2, "Harry Pollit", "LEGDAY", "699999999",
                4, "english, spanish, french", LocalDate.parse("2023-06-27"), null
        );

        when(casterRepository.findById(id)).thenReturn(Optional.of(casterToModify));
        when(casterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Caster result = casterService.modify(2,updatedData);

        assertEquals(updatedCaster.getAlias(), result.getAlias());
        assertEquals(updatedCaster.getPhone(), result.getPhone());
        assertEquals(updatedCaster.getLanguages(), result.getLanguages());
        assertEquals(updatedCaster.getRegion(), result.getRegion());

        verify(casterRepository, times(1)).findById(id);
        verify(casterRepository, times(1)).save(updatedCaster);
    }

    @Test
    public void testUpdate() throws CasterNotFoundException {
        //Inject real model mapper to avoid null pointer exception, problems faking setSkipNullEnabled
        ModelMapper realModelMapper = new ModelMapper();
        realModelMapper.getConfiguration().setSkipNullEnabled(true);
        casterService.setModelMapper(realModelMapper);

        long id = 1;

        Caster casterToUpdate = mockCasters.getFirst();

        CasterPatchDto updatedData = new CasterPatchDto(
               "Mr XXX", "699999999", 5, "english, spanish"
        );

        Caster updatedCaster = new Caster(
               1, "Matt Morello", "Mr XXX", "699999999", 5, "english, spanish",
                LocalDate.parse("2023-06-02"), null
        );

        when(casterRepository.findById(id)).thenReturn(Optional.of(casterToUpdate));

        when(casterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Caster result = casterService.update(id, updatedData);

        assertEquals(updatedCaster.getAlias(), result.getAlias());
        assertEquals(updatedCaster.getPhone(), result.getPhone());
        assertEquals(updatedCaster.getRegion(), result.getRegion());
        assertEquals(updatedCaster.getLanguages(), result.getLanguages());


        verify(casterRepository, times(1)).findById(id);
        verify(casterRepository, times(1)).save(updatedCaster);
    }

    @Test
    public void testCasterNotFoundException() {
        long id = 79;

        when(casterRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CasterNotFoundException.class, () -> {
            casterService.getById(id);
        });
    }

}
