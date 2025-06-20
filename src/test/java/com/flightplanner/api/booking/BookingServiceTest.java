package com.flightplanner.api.booking;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import com.flightplanner.api.flight.Flight;
import com.flightplanner.api.flight.FlightRepository;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO bookingRequestDTO;
    private BookingResponseDTO bookingResponseDTO;
    private Flight flight;
    private User user;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setAirline(new Airline("THY", "Turkish Airlines"));
        flight.setSrcAirport(new Airport("SAW", "Sabiha Gokcen Airport"));
        flight.setDestAirport(new Airport("IST", "Istanbul Airport"));
        flight.setId(1L);
        flight.setSeatCount(100);

        user = new User();
        user.setUsername("testUser");

        bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setFlightId(1L);
        bookingRequestDTO.setUsername("testUser");
        bookingRequestDTO.setNumberOfSeats(2);

        bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setFlightId(1L);
        bookingResponseDTO.setUsername("testUser");
        bookingResponseDTO.setNumberOfSeats(2);
    }

    @Test
    void createBooking_shouldSaveBookingAndReturnResponseDTO_whenSeatsAvailable() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        when(bookingRepository.countBookedSeatsForFlight(1L)).thenReturn(50);
        when(bookingRepository.save(any())).thenReturn(new Booking(1L, flight, user, 2));

        // Act
        BookingResponseDTO result = bookingService.createBooking(bookingRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(bookingResponseDTO.getFlightId(), result.getFlightId());
        assertEquals(bookingResponseDTO.getUsername(), result.getUsername());
        assertEquals(bookingResponseDTO.getNumberOfSeats(), result.getNumberOfSeats());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenSeatsNotAvailable() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.of(user));
        when(bookingRepository.countBookedSeatsForFlight(1L)).thenReturn(99);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenFlightNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingRequestDTO));
        verify(bookingRepository, never()).save(any());
    }
}
