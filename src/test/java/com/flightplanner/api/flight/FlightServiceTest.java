package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.UnauthorizedActionException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.flight.classes.FlightClass;
import com.flightplanner.api.flight.classes.FlightClassEnum;
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
import static org.mockito.ArgumentMatchers.*;
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

    // Test data
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
        testDepartureTime = LocalDateTime.of(2025, 12, 25, 10, 0);

        // Setup mock entities
        airlineEntity = new Airline();
        airlineEntity.setCode("THY");
        airlineEntity.setName("Turkish Airlines");

        userEntity = new User();
        userEntity.setUsername("staff1");
        userEntity.setRole(com.flightplanner.api.user.Role.ROLE_AIRLINE_STAFF);
        userEntity.setAirline(airlineEntity);

        srcAirportEntity = new Airport();
        srcAirportEntity.setCode("IST");
        srcAirportEntity.setName("Istanbul Airport");

        destAirportEntity = new Airport();
        destAirportEntity.setCode("CDG");
        destAirportEntity.setName("Charles de Gaulle Airport");

        // Flight entity as it would be if returned from repository or created
        flightEntity = new Flight();
        flightEntity.setId(1L);
        flightEntity.setDepartureTime(testDepartureTime);
        flightEntity.setArrivalTime(testDepartureTime.plusHours(2));
        flightEntity.setDuration(120);
        flightEntity.setAirline(airlineEntity);
        flightEntity.setOriginAirport(srcAirportEntity);
        flightEntity.setDestinationAirport(destAirportEntity);
        
        // Create flight classes
        FlightClass economyClass = new FlightClass(flightEntity, FlightClassEnum.ECONOMY, 80, 150.0);
        FlightClass businessClass = new FlightClass(flightEntity, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> flightClasses = Arrays.asList(economyClass, businessClass);
        flightEntity.setClasses(flightClasses);

        // DTOs for request and response - using constructor and setters available
        economyClass = new FlightClass(null, FlightClassEnum.ECONOMY, 80, 150.0);
        businessClass = new FlightClass(null, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> requestFlightClasses = Arrays.asList(economyClass, businessClass);
        
        flightRequestDTO = new FlightRequestDTO();
        flightRequestDTO.setDepartureTime(testDepartureTime);
        flightRequestDTO.setDuration(120);
        flightRequestDTO.setAirlineCode("THY");
        flightRequestDTO.setOriginAirportCode("IST");
        flightRequestDTO.setDestinationAirportCode("CDG");
        flightRequestDTO.setFlightClasses(requestFlightClasses);

        flightResponseDTO = new FlightResponseDTO();
        flightResponseDTO.setId(1L);
        flightResponseDTO.setMinPrice(150.0);
        flightResponseDTO.setSeatCount(100L);
        flightResponseDTO.setEmptySeats(100L);
        flightResponseDTO.setDepartureTime(testDepartureTime);
        flightResponseDTO.setArrivalTime(testDepartureTime.plusHours(2));
        flightResponseDTO.setDuration(120L);
        flightResponseDTO.setAirline(airlineEntity);
        flightResponseDTO.setOriginAirport(srcAirportEntity);
        flightResponseDTO.setDestinationAirport(destAirportEntity);
    }

    @Test
    void getAllFlights_shouldReturnListOfFlightResponseDTOs() {
        // Arrange
        List<FlightResponseDTO> flights = Arrays.asList(flightResponseDTO, new FlightResponseDTO()); // Mock 2 entities
        when(flightRepository.findAllWithEmptySeats()).thenReturn(flights); // Repository returns entities

        // Act
        List<FlightResponseDTO> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(flightResponseDTO, result.getFirst()); // Assuming equals/hashCode for DTO
        verify(flightRepository, times(1)).findAllWithEmptySeats();
        verify(flightMapper, times(2)).fixTimeZone(any(FlightResponseDTO.class)); // Called twice for 2 entities
    }

    @Test
    void getFlightById_shouldReturnFlightResponseDTO_whenFound() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findByIdWithEmptySeats(flightId)).thenReturn(Optional.of(flightResponseDTO));

        // Act
        FlightResponseDTO result = flightService.getFlightById(flightId);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, times(1)).findByIdWithEmptySeats(flightId);
        verify(flightMapper, times(1)).fixTimeZone(flightResponseDTO);
    }

    @Test
    void getFlightById_shouldThrowNotFoundException_whenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findByIdWithEmptySeats(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.getFlightById(nonExistentId));
        verify(flightRepository, times(1)).findByIdWithEmptySeats(nonExistentId);
        verify(flightMapper, never()).fixTimeZone(any(FlightResponseDTO.class)); // Mapper should not be called
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
        when(flightRepository.findByIdWithEmptySeats(any(Long.class))).thenReturn(Optional.of(flightResponseDTO));

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
                eq(flightEntity.getAirlineCode()),
                eq(flightEntity.getOriginAirport().getCode()),
                eq(flightEntity.getDestinationAirport().getCode()),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        ); // Validate limit check is done
        verify(flightRepository, times(1)).save(flightEntity); // Flight is saved
        verify(flightMapper, times(2)).fixTimeZone(flightResponseDTO); // Saved entity converted to response DTO
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
        verify(flightMapper, never()).fixTimeZone(any(FlightResponseDTO.class)); // No response DTO conversion
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenAttrsChangedAndWithinLimit() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1); // Date changed
        
        // Create flight classes for the updated request
        FlightClass economyClass = new FlightClass(null, FlightClassEnum.ECONOMY, 80, 150.0);
        FlightClass businessClass = new FlightClass(null, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> updatedFlightClasses = Arrays.asList(economyClass, businessClass);
        
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO();
        updatedRequestDTO.setDepartureTime(newDepartureTime);
        updatedRequestDTO.setDuration(120);
        updatedRequestDTO.setAirlineCode("THY");
        updatedRequestDTO.setOriginAirportCode("IST");
        updatedRequestDTO.setDestinationAirportCode("CDG");
        updatedRequestDTO.setFlightClasses(updatedFlightClasses);

        Flight updatedFlightEntity = new Flight();
        updatedFlightEntity.setId(flightId);
        updatedFlightEntity.setDepartureTime(newDepartureTime);
        updatedFlightEntity.setArrivalTime(newDepartureTime.plusHours(2));
        updatedFlightEntity.setDuration(120);
        updatedFlightEntity.setAirline(airlineEntity);
        updatedFlightEntity.setOriginAirport(srcAirportEntity);
        updatedFlightEntity.setDestinationAirport(destAirportEntity);
        updatedFlightEntity.setClasses(updatedFlightClasses);

        FlightResponseDTO updatedResponseDTO = new FlightResponseDTO();
        updatedResponseDTO.setId(flightId);
        updatedResponseDTO.setMinPrice(150.0);
        updatedResponseDTO.setSeatCount(100L);
        updatedResponseDTO.setEmptySeats(100L);
        updatedResponseDTO.setDepartureTime(newDepartureTime);
        updatedResponseDTO.setArrivalTime(newDepartureTime.plusHours(2));
        updatedResponseDTO.setDuration(120L);
        updatedResponseDTO.setAirline(airlineEntity);
        updatedResponseDTO.setOriginAirport(srcAirportEntity);
        updatedResponseDTO.setDestinationAirport(destAirportEntity);

        // Mock existing flight from repository
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // Mock daily flight count to be within limit for the *new* attributes
        when(flightRepository.dailyFlightCount(
                eq("THY"), eq("IST"), eq("CDG"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(0L); // Valid for new attributes

        // Mock mapper behavior for update
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntity); // for hasFlightAttrChanged/validateFlightLimit
        when(flightMapper.updateEntity(flightEntity, updatedRequestDTO)).thenReturn(updatedFlightEntity);
        when(flightRepository.save(updatedFlightEntity)).thenReturn(updatedFlightEntity);
        when(flightRepository.findByIdWithEmptySeats(flightId)).thenReturn(Optional.of(updatedResponseDTO));

        // mock auth
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        SecurityContextHolder.setContext(securityContext);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, updatedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedResponseDTO, result);
        verify(flightRepository, times(1)).findById(flightId); // Fetch existing flight
        verify(flightMapper, times(1)).toEntity(updatedRequestDTO); // Used for comparison and validation
        verify(flightRepository, times(1)).dailyFlightCount(
                eq("THY"), eq("IST"), eq("CDG"), any(LocalDateTime.class), any(LocalDateTime.class)
        ); // Validate limit for new attributes
        verify(flightRepository, times(1)).findByIdWithEmptySeats(flightId); // Final fetch for response
        verify(flightMapper, times(2)).fixTimeZone(updatedResponseDTO); // Timezone fix applied
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenNoAttrsChanged() {
        // Arrange
        Long flightId = 1L;

        // Mock existing flight from repository
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // Mock mapper behavior for update (no changes detected)
        when(flightMapper.updateEntity(flightEntity, flightRequestDTO)).thenReturn(flightEntity);
        when(flightRepository.save(flightEntity)).thenReturn(flightEntity);
        when(flightRepository.findByIdWithEmptySeats(flightId)).thenReturn(Optional.of(flightResponseDTO));

        // mock auth
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        SecurityContextHolder.setContext(securityContext);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, flightRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, times(1)).findById(flightId); // Fetch existing flight
        // No toEntity call because hasFlightAttrChanged uses entity fields directly
        // No daily flight count check because no attributes changed
        verify(flightRepository, never()).dailyFlightCount(anyString(), anyString(), anyString(), any(), any());
        verify(flightRepository, times(1)).findByIdWithEmptySeats(flightId); // Final fetch for response
        verify(flightMapper, times(2)).fixTimeZone(flightResponseDTO); // Timezone fix applied
    }

    @Test
    void updateFlight_shouldThrowFlightLimitExceededException_whenAttrsChangedAndLimitExceeded() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1); // Date changed
        
        // Create flight classes for the updated request
        FlightClass economyClass = new FlightClass(null, FlightClassEnum.ECONOMY, 80, 150.0);
        FlightClass businessClass = new FlightClass(null, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> updatedFlightClasses = Arrays.asList(economyClass, businessClass);
        
        FlightRequestDTO updatedRequestDTO = new FlightRequestDTO();
        updatedRequestDTO.setDepartureTime(newDepartureTime);
        updatedRequestDTO.setDuration(120);
        updatedRequestDTO.setAirlineCode("THY");
        updatedRequestDTO.setOriginAirportCode("IST");
        updatedRequestDTO.setDestinationAirportCode("CDG");
        updatedRequestDTO.setFlightClasses(updatedFlightClasses);

        Flight updatedFlightEntityForValidation = new Flight();
        updatedFlightEntityForValidation.setId(flightId);
        updatedFlightEntityForValidation.setDepartureTime(newDepartureTime);
        updatedFlightEntityForValidation.setArrivalTime(newDepartureTime.plusHours(2));
        updatedFlightEntityForValidation.setDuration(120);
        updatedFlightEntityForValidation.setAirline(airlineEntity);
        updatedFlightEntityForValidation.setOriginAirport(srcAirportEntity);
        updatedFlightEntityForValidation.setDestinationAirport(destAirportEntity);
        updatedFlightEntityForValidation.setClasses(updatedFlightClasses);

        // Mock existing flight from repository
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // Mock daily flight count to exceed limit for new attributes
        when(flightRepository.dailyFlightCount(
                eq("THY"), eq("IST"), eq("CDG"), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(3L); // Limit exceeded

        // Mock mapper behavior for update
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntityForValidation); // for hasFlightAttrChanged/validateFlightLimit

        // mock auth
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(FlightLimitExceededException.class, () -> flightService.updateFlight(flightId, updatedRequestDTO));

        // Verify no final fetch occurred (exception should be thrown before this)
        verify(flightRepository, never()).findByIdWithEmptySeats(flightId);
    }

    @Test
    void updateFlight_shouldThrowFlightNotFoundException_whenUpdatingNonExistentFlight() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.updateFlight(nonExistentId, flightRequestDTO));

        // Verify no other operations are performed
        verify(flightMapper, never()).toEntity(any(FlightRequestDTO.class));
        verify(flightRepository, never()).dailyFlightCount(anyString(), anyString(), anyString(), any(), any());
        verify(flightRepository, never()).findByIdWithEmptySeats(any());
    }

    @Test
    void deleteFlight_shouldDeleteFlight_whenExists() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));

        // mock auth
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        SecurityContextHolder.setContext(securityContext);

        // Act
        flightService.deleteFlight(flightId);

        // Assert
        verify(flightRepository, times(1)).findById(flightId); // Fetch flight to delete
        verify(flightRepository, times(1)).deleteById(flightId); // Delete the flight
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsNotAirlineStaff() {
        // Arrange
        User nonStaffUser = new User();
        nonStaffUser.setUsername("regularUser");
        nonStaffUser.setRole(com.flightplanner.api.user.Role.ROLE_USER);
        nonStaffUser.setAirline(null); // Not staff

        String username = nonStaffUser.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(nonStaffUser));

        SecurityContextHolder.setContext(securityContext);

        // Need to mock mapper to avoid NullPointerException
        when(flightMapper.toEntity(any(FlightRequestDTO.class))).thenReturn(flightEntity);

        // Act & Assert
        assertThrows(UnauthorizedActionException.class, () -> flightService.createFlight(flightRequestDTO));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsFromOtherAirline() {
        // Arrange
        Airline otherAirline = new Airline();
        otherAirline.setCode("BA");
        otherAirline.setName("British Airways");

        User otherStaffUser = new User();
        otherStaffUser.setUsername("otherStaff");
        otherStaffUser.setRole(com.flightplanner.api.user.Role.ROLE_AIRLINE_STAFF);
        otherStaffUser.setAirline(otherAirline); // Different airline

        String username = otherStaffUser.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(otherStaffUser));

        SecurityContextHolder.setContext(securityContext);

        // Need to mock mapper to avoid NullPointerException
        when(flightMapper.toEntity(any(FlightRequestDTO.class))).thenReturn(flightEntity);

        // Act & Assert
        assertThrows(UnauthorizedActionException.class, () -> flightService.createFlight(flightRequestDTO));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldPass_whenUserIsAuthorizedAirlineStaff() {
        // Arrange - using setup userEntity which has matching airline
        when(flightRepository.dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(1L); // Within limit
        when(flightRepository.save(any(Flight.class))).thenReturn(flightEntity);
        when(flightMapper.toEntity(any(FlightRequestDTO.class))).thenReturn(flightEntity);
        when(flightRepository.findByIdWithEmptySeats(any(Long.class))).thenReturn(Optional.of(flightResponseDTO));

        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

        SecurityContextHolder.setContext(securityContext);

        // Act
        assertDoesNotThrow(() -> flightService.createFlight(flightRequestDTO));

        // Verify that flight creation proceeded normally
        verify(flightRepository, times(1)).save(flightEntity);
    }
}
