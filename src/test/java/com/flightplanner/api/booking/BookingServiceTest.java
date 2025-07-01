package com.flightplanner.api.booking;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import com.flightplanner.api.booking.dto.BookingPassengerRequestDTO;
import com.flightplanner.api.booking.passenger.BookingPassenger;
import com.flightplanner.api.booking.passenger.BookingPassengerRepository;
import com.flightplanner.api.flight.Flight;
import com.flightplanner.api.flight.FlightRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BookingPassengerRepository bookingPassengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO bookingRequestDTO;
    private Flight flight;
    private User user;
    private List<FlightClass> flightClasses;

    @BeforeEach
    void setUp() {
        Airline airline = new Airline("THY", "Turkish Airlines");
        Airport originAirport = new Airport("SAW", "Sabiha Gokcen Airport");
        Airport destinationAirport = new Airport("IST", "Istanbul Airport");
        
        flight = new Flight();
        flight.setId(1L);
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setDuration(120);
        flight.setAirline(airline);
        flight.setOriginAirport(originAirport);
        flight.setDestinationAirport(destinationAirport);
        
        // Create flight classes
        FlightClass economyClass = new FlightClass(flight, FlightClassEnum.ECONOMY, 80, 100.0);
        economyClass.setAvailableSeats(80);
        FlightClass businessClass = new FlightClass(flight, FlightClassEnum.BUSINESS, 20, 300.0);
        businessClass.setAvailableSeats(20);
        flightClasses = Arrays.asList(economyClass, businessClass);
        flight.setClasses(flightClasses);

        user = new User();
        user.setUsername("testUser");

        // Create passenger request DTOs
        BookingPassengerRequestDTO passenger1 = BookingPassengerRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .flightClass(FlightClassEnum.ECONOMY)
                .priceAtBooking(100.0)
                .build();
        
        BookingPassengerRequestDTO passenger2 = BookingPassengerRequestDTO.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .flightClass(FlightClassEnum.ECONOMY)
                .priceAtBooking(100.0)
                .build();
        
        List<BookingPassengerRequestDTO> passengers = Arrays.asList(passenger1, passenger2);

        bookingRequestDTO = BookingRequestDTO.builder()
                .flightId(1L)
                .username("testUser")
                .passengers(passengers)
                .build();
    }

    @Test
    void bookFlight_shouldSaveBookingAndReturnResponseDTO_whenSeatsAvailable() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setFlight(flight);
        savedBooking.setUser(user);
        
        // Create actual BookingPassenger entities for the saved booking
        List<BookingPassenger> savedPassengers = Arrays.asList(
                BookingPassenger.builder()
                        .id(1L)
                        .booking(savedBooking)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@example.com")
                        .flightClass(FlightClassEnum.ECONOMY)
                        .priceAtBooking(100.0)
                        .build(),
                BookingPassenger.builder()
                        .id(2L)
                        .booking(savedBooking)
                        .firstName("Jane")
                        .lastName("Doe")
                        .email("jane.doe@example.com")
                        .flightClass(FlightClassEnum.ECONOMY)
                        .priceAtBooking(100.0)
                        .build()
        );
        savedBooking.setPassengers(savedPassengers);
        
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingPassengerRepository.saveAll(anyList())).thenReturn(List.of());
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        // Act
        BookingResponseDTO result = bookingService.bookFlight(1L, bookingRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        assertEquals(flight.getOriginAirport(), result.getOriginAirport());
        assertEquals(flight.getDestinationAirport(), result.getDestinationAirport());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingPassengerRepository, times(1)).saveAll(anyList());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void bookFlight_shouldThrowException_whenSeatsNotAvailable() {
        // Arrange
        // Make economy class fully booked
        flightClasses.getFirst().setAvailableSeats(1); // Only 1 seat available but 2 requested
        
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.bookFlight(1L, bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
        verify(bookingPassengerRepository, never()).saveAll(anyList());
    }

    @Test
    void bookFlight_shouldThrowException_whenFlightNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.bookFlight(1L, bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
        verify(bookingPassengerRepository, never()).saveAll(anyList());
    }

    @Test
    void bookFlight_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.bookFlight(1L, bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
        verify(bookingPassengerRepository, never()).saveAll(anyList());
    }
}
