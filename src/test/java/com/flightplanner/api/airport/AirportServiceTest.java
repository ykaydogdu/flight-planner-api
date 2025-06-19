package com.flightplanner.api.airport;

import com.flightplanner.api.airport.exception.AirportNotFoundException;
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

class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportService airportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAirports() {
        List<Airport> airports = Arrays.asList(new Airport("JFK", "John F. Kennedy International Airport"),
                                               new Airport("LAX", "Los Angeles International Airport"));

        when(airportRepository.findAll()).thenReturn(airports);

        List<Airport> result = airportService.getAllAirports();

        assertEquals(2, result.size());
        assertEquals("JFK", result.get(0).getCode());
        assertEquals("John F. Kennedy International Airport", result.get(0).getName());
        assertEquals("LAX", result.get(1).getCode());
        assertEquals("Los Angeles International Airport", result.get(1).getName());

        verify(airportRepository).findAll();
    }

    @Test
    void testGetAirportByCodeSuccess() {
        Airport airport = new Airport("JFK", "John F. Kennedy International Airport");

        when(airportRepository.findById("JFK")).thenReturn(Optional.of(airport));

        Airport result = airportService.getAirportByCode("JFK");

        assertNotNull(result);
        assertEquals("JFK", result.getCode());
        assertEquals("John F. Kennedy International Airport", result.getName());

        verify(airportRepository).findById("JFK");
    }

    @Test
    void testGetAirportByCodeThrowsException() {
        when(airportRepository.findById("JFK")).thenReturn(Optional.empty());

        assertThrows(AirportNotFoundException.class, () -> airportService.getAirportByCode("JFK"));

        verify(airportRepository).findById("JFK");
    }

    @Test
    void testCreateAirport() {
        Airport airport = new Airport("JFK", "John F. Kennedy International Airport");

        when(airportRepository.save(airport)).thenReturn(airport);

        Airport result = airportService.createAirport(airport);

        assertNotNull(result);
        assertEquals("JFK", result.getCode());
        assertEquals("John F. Kennedy International Airport", result.getName());

        verify(airportRepository).save(airport);
    }

    @Test
    void testDeleteAirport() {
        doNothing().when(airportRepository).deleteById("JFK");

        airportService.deleteAirport("JFK");

        verify(airportRepository).deleteById("JFK");
    }
}
