package com.flightplanner.api.airline;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.dto.AirlineWithStaffCountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAirlines() {
        List<AirlineWithStaffCountDTO> airlines = Arrays.asList(
            new AirlineWithStaffCountDTO("AA", "American Airlines", 100),
            new AirlineWithStaffCountDTO("DL", "Delta Airlines", 200)
        );

        when(airlineRepository.findAllAirlinesWithStaffCount()).thenReturn(airlines);

        List<AirlineWithStaffCountDTO> result = airlineService.getAllAirlines();

        assertEquals(2, result.size());
        assertEquals("AA", Objects.requireNonNull(result.stream().findFirst().orElse(null)).getCode());
        assertEquals("American Airlines", Objects.requireNonNull(result.stream().findFirst().orElse(null)).getName());
        assertEquals(100, Objects.requireNonNull(result.stream().findFirst().orElse(null)).getStaffCount());
        assertEquals("DL", result.get(1).getCode());
        assertEquals("Delta Airlines", result.get(1).getName());
        assertEquals(200, result.get(1).getStaffCount());

        verify(airlineRepository).findAllAirlinesWithStaffCount();
    }

    @Test
    void testGetAirlineByCodeSuccess() {
        AirlineWithStaffCountDTO airline = new AirlineWithStaffCountDTO("AA", "American Airlines", 100);

        when(airlineRepository.findAllAirlinesWithStaffCount()).thenReturn(List.of(airline));

        AirlineWithStaffCountDTO result = airlineService.getAirlineByCode("AA");

        assertNotNull(result);
        assertEquals("AA", result.getCode());
        assertEquals("American Airlines", result.getName());
        assertEquals(100, result.getStaffCount());

        verify(airlineRepository).findAllAirlinesWithStaffCount();
    }

    @Test
    void testGetAirlineByCodeThrowsException() {
        when(airlineRepository.findAllAirlinesWithStaffCount()).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> airlineService.getAirlineByCode("AA"));

        verify(airlineRepository).findAllAirlinesWithStaffCount();
    }

    @Test
    void testAddAirline() {
        Airline airline = new Airline("AA", "American Airlines");
        AirlineWithStaffCountDTO savedAirline = new AirlineWithStaffCountDTO("AA", "American Airlines", 100);

        when(airlineRepository.save(airline)).thenReturn(airline);
        when(airlineRepository.findAllAirlinesWithStaffCount()).thenReturn(List.of(savedAirline));

        AirlineWithStaffCountDTO result = airlineService.addAirline(airline);

        assertNotNull(result);
        assertEquals("AA", result.getCode());
        assertEquals("American Airlines", result.getName());
        assertEquals(100, result.getStaffCount());

        verify(airlineRepository).save(airline);
        verify(airlineRepository).findAllAirlinesWithStaffCount();
    }

    @Test
    void testDeleteAirline() {
        doNothing().when(airlineRepository).deleteById("AA");

        airlineService.deleteAirline("AA");

        verify(airlineRepository).deleteById("AA");
    }
}
