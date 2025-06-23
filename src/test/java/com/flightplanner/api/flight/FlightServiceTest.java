package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.UnauthorizedActionException;
import com.flightplanner.api.airline.Airline; // Assuming these are your entity classes
import com.flightplanner.api.airport.Airport; // Assuming these are your entity classes
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.user.Role;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks // Inject the mocks into the FlightService instance
    private FlightService flightService;

    // Common test data
    private LocalDateTime testDepartureTime;
    private Flight flightEntity;
    private FlightRequestDTO flightRequestDTO;
    private FlightResponseDTO flightResponseDTO;
    private Airline airlineEntity;
    private Airport srcAirportEntity;
    private Airport destAirportEntity;
    private User userEntity;

    @BeforeEach
    void setUp() {
        testDepartureTime = LocalDateTime.now().plusDays(10);

        // Entities with necessary fields for validation/mapping
        airlineEntity = new Airline("THY", "Turkish Airlines");
        String username = "testUser";
        userEntity = new User(username, "password", "test@test.com", Role.ROLE_AIRLINE_STAFF, airlineEntity);

        srcAirportEntity = new Airport("IST", "Istanbul Airport");
        destAirportEntity = new Airport("CDG", "Charles de Gaulle Airport");

        // Flight entity as it would be if returned from repository or created
        flightEntity = new Flight(1L, testDepartureTime, 100, 100, airlineEntity, srcAirportEntity, destAirportEntity);

        // DTOs for request and response
        flightRequestDTO = new FlightRequestDTO(testDepartureTime, 100, 100, "THY", "IST", "CDG");
        flightResponseDTO = new FlightResponseDTO(1L, 100, 100, testDepartureTime, "THY", "IST", "CDG");
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
    void getFlightById_shouldThrowNotFoundException_whenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.getFlightById(nonExistentId));
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
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
        FlightResponseDTO result = flightService.createFlight(flightRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightMapper, times(1)).toEntity(flightRequestDTO); // Mapper converts request DTO to entity
        verify(flightRepository, times(1)).dailyFlightCount(
                eq(flightEntity.getAirline().getCode()),
                eq(flightEntity.getOriginAirport().getCode()),
                eq(flightEntity.getDestinationAirport().getCode()),
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
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO(newDepartureTime, 100, 100, "THY", "IST", "CDG");
        Flight updatedFlightEntity = new Flight(
                flightId,
                newDepartureTime,
                100, 100,
                airlineEntity,
                srcAirportEntity,
                destAirportEntity);

        FlightResponseDTO updatedResponseDTO = new FlightResponseDTO(flightId, 100, 100, newDepartureTime, "THY", "IST", "CDG");

        // Mock existing flight from repository
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // Mock daily flight count to be within limit for the *new* attributes
        when(flightRepository.dailyFlightCount(
                eq("THY"), eq("IST"), eq("CDG"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(0L); // Valid for new attributes

        // Mock mapper behavior for update
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntity); // for hasFlightAttrChanged/validateFlightLimit
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(updatedResponseDTO);

        // mock auth
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(flightRepository.save(updatedFlightEntity)).thenReturn(updatedFlightEntity);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, updatedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedResponseDTO, result);

        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toEntity(updatedRequestDTO); // Called to build entity for validation
        verify(flightRepository, times(1)).dailyFlightCount(
                eq("THY"), eq("IST"), eq("CDG"), any(LocalDateTime.class), any(LocalDateTime.class)
        ); // Validation check is performed
        verify(flightMapper, times(1)).toResponseDto(updatedFlightEntity); // And converted
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenNoAttrsChanged() {
        // Arrange
        Long flightId = 1L;
        // Request DTO with same attributes as existing flight, so hasFlightAttrChanged returns false
        FlightRequestDTO unchangedRequestDTO = new FlightRequestDTO(testDepartureTime, 100, 100, "THY", "IST", "CDG");

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toEntity(unchangedRequestDTO)).thenReturn(flightEntity);
        when(flightMapper.toResponseDto(any(Flight.class))).thenReturn(flightResponseDTO);

        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(flightRepository.save(flightEntity)).thenReturn(flightEntity);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, unchangedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);

        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toEntity(any(FlightRequestDTO.class)); // Mapper.toEntity not called if no change for validation
        verify(flightRepository, never()).dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        ); // No validation check performed
        verify(flightMapper, times(1)).toResponseDto(flightEntity); // And converted
    }


    @Test
    void updateFlight_shouldThrowFlightLimitExceededException_whenAttrsChangedAndLimitExceeded() {
        // Arrange
        Long flightId = 2L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1);
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO(newDepartureTime, 100, 100, "DL", "LAX", "SEA");
        Flight updatedFlightEntityForValidation = new Flight(); // Entity for validation check
        updatedFlightEntityForValidation.setId(flightId);
        updatedFlightEntityForValidation.setDepartureTime(newDepartureTime);
        updatedFlightEntityForValidation.setAirline(new Airline("THY", "Delta"));
        updatedFlightEntityForValidation.setOriginAirport(new Airport("LAX", "Los Angeles"));
        updatedFlightEntityForValidation.setDestinationAirport(new Airport("SEA", "Seattle"));

        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntityForValidation); // For hasFlightAttrChanged/validateFlightLimit

        // Mock dailyFlightCount to exceed limit for the *new* attributes
        when(flightRepository.dailyFlightCount(
                eq("THY"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(3L); // Limit exceeded

        // Act & Assert
        assertThrows(FlightLimitExceededException.class,
                () -> flightService.updateFlight(flightId, updatedRequestDTO));

        // Verify calls
        verify(flightRepository, times(1)).findById(flightId);
        verify(flightMapper, times(1)).toEntity(updatedRequestDTO); // Called for validation
        verify(flightRepository, times(1)).dailyFlightCount(
                eq("THY"), eq("LAX"), eq("SEA"), any(LocalDateTime.class), any(LocalDateTime.class)
        );
        verify(flightRepository, never()).save(any(Flight.class)); // No save
    }

    @Test
    void updateFlight_shouldThrowFlightNotFoundException_whenUpdatingNonExistentFlight() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.updateFlight(nonExistentId, flightRequestDTO));

        // Verify no further calls
        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightMapper, never()).toEntity(any(FlightRequestDTO.class));
        verify(flightRepository, never()).dailyFlightCount(anyString(), anyString(), anyString(), any(), any());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void deleteFlight_shouldDeleteFlight_whenExists() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // authorized user
        String username = userEntity.getUsername();
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        // Act
        flightService.deleteFlight(flightId);

        // Assert
        verify(flightRepository, times(1)).deleteById(flightId);
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsNotAirlineStaff() {
        // Arrange
        String username = "notStaffUser";
        User user = new User(username, "password", "test@test.com", Role.ROLE_USER, null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(UnauthorizedActionException.class, () -> flightService.validateAirlineStaffAuthorization(airlineEntity.getCode()));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsFromOtherAirline() {
        Airline testAirline = new Airline("DL", "Delta Airlines");
        String username = "diffAirlineUser";
        User user = new User(username, "password", "test@test.com", Role.ROLE_AIRLINE_STAFF, testAirline);
        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        // Mock security context and authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        String otherAirlineCode = "AA"; // Different airline code
        assertThrows(UnauthorizedActionException.class, () -> flightService.validateAirlineStaffAuthorization(otherAirlineCode));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldPass_whenUserIsAuthorizedAirlineStaff() {
        String username = userEntity.getUsername();
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        // Mock security context and authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        SecurityContextHolder.setContext(securityContext);
        assertDoesNotThrow(() -> flightService.validateAirlineStaffAuthorization(airlineEntity.getCode()));
    }
}
