package com.flightplanner.api.flight;

import com.flightplanner.api.airline.Airline; // Assuming these are your entity classes
import com.flightplanner.api.airport.Airport; // Assuming these are your entity classes
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.flight.exception.FlightNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class FlightServiceTest {

    @Mock // Mock the FlightRepository dependency
    private FlightRepository flightRepository;

    @Mock // Mock the FlightMapper dependency
    private FlightMapper flightMapper;

    @InjectMocks // Inject the mocks into the FlightService instance
    private FlightService flightService;

    // Common test data
    private FlightRequestDTO flightRequestDTO;
    private Flight flightEntity;
    private FlightResponseDTO flightResponseDTO;
    private LocalDateTime testDepartureTime;

    @BeforeEach
    void setUp() {
        testDepartureTime = LocalDateTime.now().plusDays(10);

        // Entities with necessary fields for validation/mapping
        Airline testAirline = new Airline("THY", "Turkish Airlines");
        Airport testSrcAirport = new Airport("IST", "Istanbul Airport");
        Airport testDestAirport = new Airport("CDG", "Charles de Gaulle Airport");

        // Flight entity as it would be if returned from repository or created
        flightEntity = new Flight(1L, testDepartureTime, testAirline, testSrcAirport, testDestAirport);

        // DTOs for request and response
        flightRequestDTO = new FlightRequestDTO(testDepartureTime, "THY", "IST", "CDG");
        flightResponseDTO = new FlightResponseDTO(1L, testDepartureTime, "THY", "IST", "CDG");
    }

    @Test
    void getAllFlights_shouldReturnListOfFlightResponseDTOs() {
        // Arrange
        List<Flight> flights = Arrays.asList(flightEntity, new Flight()); // Mock 2 entities
        when(flightRepository.findAll()).thenReturn(flights); // Repository returns entities
        when(flightMapper.toResponseDto(any(Flight.class))) // Mapper converts each entity
                .thenReturn(flightResponseDTO); // For simplicity, returning the same DTO for both

        // Act
        List<FlightResponseDTO> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(flightResponseDTO, result.getFirst()); // Assuming equals/hashCode for DTO
        verify(flightRepository, times(1)).findAll();
        verify(flightMapper, times(2)).toResponseDto(any(Flight.class)); // Called twice for 2 entities
    }

    @Test
    void getFlightById_shouldReturnFlightResponseDTO_whenFound() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(flightResponseDTO);

        // Act
        FlightResponseDTO result = flightService.getFlightById(flightId);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toResponseDto(flightEntity);
    }

    @Test
    void getFlightById_shouldThrowFlightNotFoundException_whenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(FlightNotFoundException.class, () -> flightService.getFlightById(nonExistentId));
        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightMapper, never()).toResponseDto(any(Flight.class)); // Mapper should not be called
    }

    @Test
    void createFlight_shouldSaveFlightAndReturnResponseDTO_withinLimit() {
        // Arrange
        // Assume dailyFlightCount returns less than MAX_DAILY_FLIGHTS
        when(flightRepository.dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(1L); // Current flights: 1 (less than 3)
        when(flightRepository.save(any(Flight.class))).thenReturn(flightEntity); // Repository saves and returns the entity
        when(flightMapper.toEntity(any(FlightRequestDTO.class))).thenReturn(flightEntity);
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(flightResponseDTO);

        // Act
        FlightResponseDTO result = flightService.createFlight(flightRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightMapper, times(1)).toEntity(flightRequestDTO); // Mapper converts request DTO to entity
        verify(flightRepository, times(1)).dailyFlightCount(
                eq(flightEntity.getAirline().getCode()),
                eq(flightEntity.getSrcAirport().getCode()),
                eq(flightEntity.getDestAirport().getCode()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        ); // Validate limit check is done
        verify(flightRepository, times(1)).save(flightEntity); // Flight is saved
        verify(flightMapper, times(1)).toResponseDto(flightEntity); // Saved entity converted to response DTO
    }

    @Test
    void createFlight_shouldThrowFlightLimitExceededException_whenLimitExceeded() {
        // Arrange
        when(flightRepository.dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(3L); // Current flights: 3 (equal to MAX_DAILY_FLIGHTS, so limit exceeded)
        when(flightMapper.toEntity(any(FlightRequestDTO.class))).thenReturn(flightEntity);

        // Act & Assert
        assertThrows(FlightLimitExceededException.class, () -> flightService.createFlight(flightRequestDTO));

        // Verify no save operation occurred
        verify(flightRepository, never()).save(any(Flight.class));
        verify(flightMapper, times(1)).toEntity(flightRequestDTO); // Mapper is called before validation
        verify(flightMapper, never()).toResponseDto(any(Flight.class)); // No response DTO conversion
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenAttrsChangedAndWithinLimit() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1); // Date changed
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO(newDepartureTime, "DL", "LAX", "SEA");
        Flight updatedFlightEntity = new Flight(
                flightId,
                newDepartureTime,
                new Airline("DL", "Delta"),
                new Airport("LAX", "Los Angeles"),
                new Airport("SEA", "Seattle")); // Simulate the updated entity

        FlightResponseDTO updatedResponseDTO = new FlightResponseDTO(flightId, newDepartureTime, "DL", "LAX", "SEA");

        // Mock existing flight from repository
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // Mock daily flight count to be within limit for the *new* attributes
        when(flightRepository.dailyFlightCount(
                eq("DL"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(0L); // Valid for new attributes

        // Mock mapper behavior for update
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntity); // for hasFlightAttrChanged/validateFlightLimit
        doNothing().when(flightMapper).updateEntityFromDto(any(FlightRequestDTO.class), any(Flight.class)); // The actual update method
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(updatedResponseDTO);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, updatedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedResponseDTO, result);

        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toEntity(updatedRequestDTO); // Called to build entity for validation
        verify(flightRepository, times(1)).dailyFlightCount(
                eq("DL"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        ); // Validation check is performed
        verify(flightMapper, times(1)).updateEntityFromDto(updatedRequestDTO, flightEntity); // Entity is updated
        verify(flightMapper, times(1)).toResponseDto(flightEntity); // And converted
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenNoAttrsChanged() {
        // Arrange
        Long flightId = 1L;
        // Request DTO with same attributes as existing flight, so hasFlightAttrChanged returns false
        FlightRequestDTO unchangedRequestDTO = new FlightRequestDTO(testDepartureTime, "THY", "IST", "CDG");

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        doNothing().when(flightMapper).updateEntityFromDto(any(FlightRequestDTO.class), any(Flight.class));
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(flightResponseDTO);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, unchangedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);

        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, never()).toEntity(any(FlightRequestDTO.class)); // Mapper.toEntity not called if no change for validation
        verify(flightRepository, never()).dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        ); // No validation check performed
        verify(flightMapper, times(1)).updateEntityFromDto(unchangedRequestDTO, flightEntity); // Entity is updated
        verify(flightMapper, times(1)).toResponseDto(flightEntity); // And converted
    }


    @Test
    void updateFlight_shouldThrowFlightLimitExceededException_whenAttrsChangedAndLimitExceeded() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1);
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO(newDepartureTime, "DL", "LAX", "SEA");
        Flight updatedFlightEntityForValidation = new Flight(); // Entity for validation check
        updatedFlightEntityForValidation.setId(flightId);
        updatedFlightEntityForValidation.setDepartureTime(newDepartureTime);
        updatedFlightEntityForValidation.setAirline(new Airline("DL", "Delta"));
        updatedFlightEntityForValidation.setSrcAirport(new Airport("LAX", "Los Angeles"));
        updatedFlightEntityForValidation.setDestAirport(new Airport("SEA", "Seattle"));

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntityForValidation); // For hasFlightAttrChanged/validateFlightLimit

        // Mock dailyFlightCount to exceed limit for the *new* attributes
        when(flightRepository.dailyFlightCount(
                eq("DL"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(3L); // Limit exceeded

        // Act & Assert
        assertThrows(FlightLimitExceededException.class,
                () -> flightService.updateFlight(flightId, updatedRequestDTO));

        // Verify calls
        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toEntity(updatedRequestDTO); // Called for validation
        verify(flightRepository, times(1)).dailyFlightCount(
                eq("DL"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        );
        verify(flightMapper, never()).updateEntityFromDto(any(FlightRequestDTO.class), any(Flight.class)); // No update
        verify(flightRepository, never()).save(any(Flight.class)); // No save
    }

    @Test
    void updateFlight_shouldThrowFlightNotFoundException_whenUpdatingNonExistentFlight() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(FlightNotFoundException.class, () -> flightService.updateFlight(nonExistentId, flightRequestDTO));

        // Verify no further calls
        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightMapper, never()).toEntity(any(FlightRequestDTO.class));
        verify(flightRepository, never()).dailyFlightCount(anyString(), anyString(), anyString(), any(), any());
        verify(flightMapper, never()).updateEntityFromDto(any(FlightRequestDTO.class), any(Flight.class));
        verify(flightRepository, never()).save(any(Flight.class));
    }


    @Test
    void deleteFlight_shouldDeleteFlight_whenExists() {
        // Arrange
        Long flightId = 1L;

        // Act
        flightService.deleteFlight(flightId);

        // Assert
        verify(flightRepository, times(1)).deleteById(flightId);
    }
}