package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.airport.AirportRepository;
import com.flightplanner.api.flight.dto.*;
import com.flightplanner.api.timezone.TimezoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlightMapperTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private TimezoneService timezoneService;

    @InjectMocks
    private FlightMapper flightMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToEntity_ValidInput() {
        FlightRequestDTO dto = new FlightRequestDTO();
        dto.setAirlineCode("AL123");
        dto.setOriginAirportCode("OR123");
        dto.setDestinationAirportCode("DS123");
        dto.setDepartureTime(LocalDateTime.of(2025, 7, 1, 10, 0));
        dto.setDuration(120);

        Airline airline = new Airline();
        Airport originAirport = new Airport();
        originAirport.setLatitude(40.7128);
        originAirport.setLongitude(-74.0060);
        Airport destinationAirport = new Airport();
        destinationAirport.setLatitude(34.0522);
        destinationAirport.setLongitude(-118.2437);

        when(airlineRepository.findById("AL123")).thenReturn(Optional.of(airline));
        when(airportRepository.findById("OR123")).thenReturn(Optional.of(originAirport));
        when(airportRepository.findById("DS123")).thenReturn(Optional.of(destinationAirport));
        when(timezoneService.getTimezone(40.7128, -74.0060)).thenReturn(TimeZone.getTimeZone("America/New_York"));
        when(timezoneService.getTimezone(34.0522, -118.2437)).thenReturn(TimeZone.getTimeZone("America/Los_Angeles"));
        when(timezoneService.convertLocalDateTimeToUtc(any(), any())).thenReturn(LocalDateTime.of(2025, 7, 1, 14, 0));

        Flight flight = flightMapper.toEntity(dto);

        assertNotNull(flight);
        assertEquals(120, flight.getDuration());
        assertEquals(airline, flight.getAirline());
        assertEquals(originAirport, flight.getOriginAirport());
        assertEquals(destinationAirport, flight.getDestinationAirport());
    }

    @Test
    void testToEntity_AirlineNotFound() {
        FlightRequestDTO dto = new FlightRequestDTO();
        dto.setAirlineCode("AL123");

        when(airlineRepository.findById("AL123")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> flightMapper.toEntity(dto));
    }

    @Test
    void testFixTimeZone() {
        FlightDTO dto = new FlightDTO();
        Airport originAirport = new Airport();
        originAirport.setLatitude(40.7128);
        originAirport.setLongitude(-74.0060);
        Airport destinationAirport = new Airport();
        destinationAirport.setLatitude(34.0522);
        destinationAirport.setLongitude(-118.2437);
        dto.setOriginAirport(originAirport);
        dto.setDestinationAirport(destinationAirport);
        dto.setDepartureTime(LocalDateTime.of(2025, 7, 1, 14, 0));
        dto.setArrivalTime(LocalDateTime.of(2025, 7, 1, 16, 0));

        when(timezoneService.getTimezone(40.7128, -74.0060)).thenReturn(TimeZone.getTimeZone("America/New_York"));
        when(timezoneService.getTimezone(34.0522, -118.2437)).thenReturn(TimeZone.getTimeZone("America/Los_Angeles"));
        when(timezoneService.convertUtcToLocalDateTime(any(), any())).thenReturn(LocalDateTime.of(2025, 7, 1, 10, 0));

        flightMapper.fixTimeZone(dto);

        assertEquals(LocalDateTime.of(2025, 7, 1, 10, 0), dto.getDepartureTime());
    }
}
