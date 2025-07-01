package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.UnauthorizedActionException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.flight.classes.FlightClass;
import com.flightplanner.api.flight.classes.FlightClassEnum;
import com.flightplanner.api.flight.classes.FlightClassRepository;
import com.flightplanner.api.flight.dto.*;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations for JUnit 5
class FlightServiceTest {

    @Mock // Mock the FlightRepository dependency
    private FlightRepository flightRepository;
    @Mock
    private FlightClassRepository flightClassRepository;

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
    private FlightDTO flightDTO;
    private Airline airlineEntity;
    private Airport srcAirportEntity;
    private Airport destAirportEntity;
    private User userEntity;
    private List<FlightClassDTO> flightClassDTOs;

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
        List<FlightClass> flightClasses = List.of(economyClass, businessClass);
        flightEntity.setClasses(flightClasses);

        // DTOs for request and response - using constructor and setters available
        economyClass = new FlightClass(null, FlightClassEnum.ECONOMY, 80, 150.0);
        businessClass = new FlightClass(null, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> requestFlightClasses = List.of(economyClass, businessClass);

        flightRequestDTO = new FlightRequestDTO();
        flightRequestDTO.setDepartureTime(testDepartureTime);
        flightRequestDTO.setDuration(120);
        flightRequestDTO.setAirlineCode("THY");
        flightRequestDTO.setOriginAirportCode("IST");
        flightRequestDTO.setDestinationAirportCode("CDG");
        flightRequestDTO.setFlightClasses(requestFlightClasses);

        flightDTO = new FlightDTO(1L, 150.0, 100L, 100L, testDepartureTime, 120, testDepartureTime.plusHours(2), airlineEntity, srcAirportEntity, destAirportEntity);
        flightClassDTOs = List.of(new FlightClassDTO(FlightClassEnum.ECONOMY, 80, 80, 150.0, 1L));
        flightResponseDTO = new FlightResponseDTO(1L, 150.0, 100L, 100L, testDepartureTime, 120, testDepartureTime.plusHours(2), airlineEntity, srcAirportEntity, destAirportEntity, flightClassDTOs);
    }

    @Test
    void getAllFlights_shouldReturnListOfFlightResponseDTOs() {
        // Arrange
        List<FlightDTO> flights = List.of(flightDTO); // Mock 1 entity
        when(flightRepository.findFilteredFlights(any(), any(), any(), any(), any(), anyBoolean(), any(), any(), any())).thenReturn(flights);
        when(flightClassRepository.findByFlightIds(anyList())).thenReturn(flightClassDTOs);
        when(flightMapper.toResponseDTO(any(FlightDTO.class), anyList())).thenReturn(flightResponseDTO);

        // Act
        List<FlightResponseDTO> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(flightResponseDTO, result.getFirst()); // Assuming equals/hashCode for DTO
        verify(flightRepository, times(1)).findFilteredFlights(isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), eq(0), eq(0), eq(0));
        verify(flightMapper, times(1)).fixTimeZone(any(FlightDTO.class)); // Called once for 1 entity
    }

    @Test
    void getFlightById_shouldReturnFlightResponseDTO_whenFound() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findByIdWithEmptySeats(flightId)).thenReturn(Optional.of(flightDTO));
        when(flightClassRepository.findByFlightId(flightId)).thenReturn(flightClassDTOs);
        when(flightMapper.toResponseDTO(flightDTO, flightClassDTOs)).thenReturn(flightResponseDTO);

        // Act
        FlightResponseDTO result = flightService.getFlightById(flightId);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, times(1)).findByIdWithEmptySeats(flightId);
        verify(flightMapper, times(1)).fixTimeZone(flightDTO);
    }

    @Test
    void getFlightById_shouldThrowNotFoundException_whenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findByIdWithEmptySeats(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.getFlightById(nonExistentId));
        verify(flightRepository, times(1)).findByIdWithEmptySeats(nonExistentId);
        verify(flightMapper, never()).fixTimeZone(any(FlightDTO.class)); // Mapper should not be called
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
        // a little bit of cheating here since getFlightById is called internally
        when(flightRepository.findByIdWithEmptySeats(any(Long.class))).thenReturn(Optional.of(flightDTO));
        when(flightClassRepository.findByFlightId(anyLong())).thenReturn(flightClassDTOs);
        when(flightMapper.toResponseDTO(flightDTO, flightClassDTOs)).thenReturn(flightResponseDTO);

        // Act
        String username = userEntity.getUsername();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findById(username)).thenReturn(Optional.of(userEntity));

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
        verify(flightMapper, times(1)).fixTimeZone(flightResponseDTO); // Saved entity converted to response DTO
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
    }

    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenAttrsChangedAndWithinLimit() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1); // Date changed

        // Create flight classes for the updated request
        FlightClass economyClass = new FlightClass(null, FlightClassEnum.ECONOMY, 80, 150.0);
        FlightClass businessClass = new FlightClass(null, FlightClassEnum.BUSINESS, 20, 400.0);
        List<FlightClass> updatedFlightClasses = List.of(economyClass, businessClass);

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
        updatedFlightEntity.setOriginAirport(srcAirportEntity);
        updatedFlightEntity.setDestinationAirport(destAirportEntity);
        updatedFlightEntity.setAirline(airlineEntity);

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toEntity(updatedRequestDTO)).thenReturn(updatedFlightEntity);
        when(flightRepository.dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(1L);
        when(flightMapper.updateEntity(any(Flight.class), any(FlightRequestDTO.class))).thenReturn(updatedFlightEntity);
        when(flightRepository.save(any(Flight.class))).thenReturn(updatedFlightEntity);

        // a little bit of cheating here since getFlightById is called internally
        when(flightRepository.findByIdWithEmptySeats(any(Long.class))).thenReturn(Optional.of(flightDTO));
        when(flightClassRepository.findByFlightId(anyLong())).thenReturn(flightClassDTOs);
        when(flightMapper.toResponseDTO(flightDTO, flightClassDTOs)).thenReturn(flightResponseDTO);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);


        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, updatedRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, times(1)).save(updatedFlightEntity);
    }


    @Test
    void updateFlight_shouldUpdateAndReturnResponseDTO_whenNoAttrsChanged() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.updateEntity(flightEntity, flightRequestDTO)).thenReturn(flightEntity);
        when(flightRepository.save(flightEntity)).thenReturn(flightEntity);
        when(flightRepository.findByIdWithEmptySeats(flightId)).thenReturn(Optional.of(flightDTO));
        when(flightClassRepository.findByFlightId(flightId)).thenReturn(flightClassDTOs);
        when(flightMapper.toResponseDTO(flightDTO, flightClassDTOs)).thenReturn(flightResponseDTO);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);

        // Act
        FlightResponseDTO result = flightService.updateFlight(flightId, flightRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(flightResponseDTO, result);
        verify(flightRepository, never()).dailyFlightCount(anyString(), anyString(), anyString(), any(), any());
        verify(flightRepository, times(1)).save(flightEntity);
    }

    @Test
    void updateFlight_shouldThrowFlightLimitExceededException_whenAttrsChangedAndLimitExceeded() {
        // Arrange
        Long flightId = 1L;
        LocalDateTime newDepartureTime = testDepartureTime.plusDays(1);
        flightRequestDTO.setDepartureTime(newDepartureTime);

        Flight flightFromRequest = new Flight();
        flightFromRequest.setDepartureTime(newDepartureTime);
        flightFromRequest.setAirline(airlineEntity);
        flightFromRequest.setOriginAirport(srcAirportEntity);
        flightFromRequest.setDestinationAirport(destAirportEntity);


        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.toEntity(flightRequestDTO)).thenReturn(flightFromRequest);
        when(flightRepository.dailyFlightCount(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(3L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        assertThrows(FlightLimitExceededException.class, () -> flightService.updateFlight(flightId, flightRequestDTO));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void updateFlight_shouldThrowFlightNotFoundException_whenUpdatingNonExistentFlight() {
        // Arrange
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(NotFoundException.class, () -> flightService.updateFlight(nonExistentId, flightRequestDTO));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void deleteFlight_shouldDeleteFlight_whenExists() {
        // Arrange
        Long flightId = 1L;
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        doNothing().when(flightRepository).deleteById(flightId);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);

        // Act
        flightService.deleteFlight(flightId);

        // Assert
        verify(flightRepository, times(1)).deleteById(flightId);
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsNotAirlineStaff() {
        userEntity.setRole(com.flightplanner.api.user.Role.ROLE_USER);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);
        assertThrows(UnauthorizedActionException.class, () -> flightService.validateAirlineStaffAuthorization("THY"));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldThrowUnauthorizedActionException_whenUserIsFromOtherAirline() {
        Airline otherAirline = new Airline();
        otherAirline.setCode("DL");
        userEntity.setAirline(otherAirline);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);
        assertThrows(UnauthorizedActionException.class, () -> flightService.validateAirlineStaffAuthorization("THY"));
    }

    @Test
    void validateAirlineStaffAuthorization_shouldPass_whenUserIsAuthorizedAirlineStaff() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEntity.getUsername());
        when(userRepository.findById(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        SecurityContextHolder.setContext(securityContext);
        assertDoesNotThrow(() -> flightService.validateAirlineStaffAuthorization("THY"));
    }
}
