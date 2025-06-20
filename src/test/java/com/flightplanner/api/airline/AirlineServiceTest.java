package com.flightplanner.api.airline;

import com.flightplanner.api.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        List<Airline> airlines = Arrays.asList(new Airline("AA", "American Airlines"),
                                               new Airline("DL", "Delta Airlines"));

        when(airlineRepository.findAll()).thenReturn(airlines);

        List<Airline> result = airlineService.getAllAirlines();

        assertEquals(2, result.size());
        assertEquals("AA", result.get(0).getCode());
        assertEquals("American Airlines", result.get(0).getName());
        assertEquals("DL", result.get(1).getCode());
        assertEquals("Delta Airlines", result.get(1).getName());

        verify(airlineRepository).findAll();
    }

    @Test
    void testGetAirlineByCodeSuccess() {
        Airline airline = new Airline("AA", "American Airlines");

        when(airlineRepository.findById("AA")).thenReturn(Optional.of(airline));

        Airline result = airlineService.getAirlineByCode("AA");

        assertNotNull(result);
        assertEquals("AA", result.getCode());
        assertEquals("American Airlines", result.getName());

        verify(airlineRepository).findById("AA");
    }

    @Test
    void testGetAirlineByCodeThrowsException() {
        when(airlineRepository.findById("AA")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> airlineService.getAirlineByCode("AA"));

        verify(airlineRepository).findById("AA");
    }

    @Test
    void testAddAirline() {
        Airline airline = new Airline("AA", "American Airlines");

        when(airlineRepository.save(airline)).thenReturn(airline);

        Airline result = airlineService.addAirline(airline);

        assertNotNull(result);
        assertEquals("AA", result.getCode());
        assertEquals("American Airlines", result.getName());

        verify(airlineRepository).save(airline);
    }

    @Test
    void testDeleteAirline() {
        doNothing().when(airlineRepository).deleteById("AA");

        airlineService.deleteAirline("AA");

        verify(airlineRepository).deleteById("AA");
    }
}
