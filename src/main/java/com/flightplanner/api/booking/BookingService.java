package com.flightplanner.api.booking;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.booking.dto.BookingPassengerResponseDTO;
import com.flightplanner.api.booking.exception.NotEnoughSeatsException;
import com.flightplanner.api.booking.passenger.BookingPassenger;
import com.flightplanner.api.booking.passenger.BookingPassengerRepository;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import com.flightplanner.api.flight.Flight;
import com.flightplanner.api.flight.FlightRepository;
import com.flightplanner.api.flight.dto.FlightClassSeatCountDTO;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final BookingPassengerRepository bookingPassengerRepository;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserRepository userRepository, BookingPassengerRepository bookingPassengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
    }

    public BookingResponseDTO bookFlight(BookingRequestDTO bookingRequestDTO) {
        Long flightId = bookingRequestDTO.getFlightId();
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NotFoundException("Flight"));
        User user = userRepository.findById(bookingRequestDTO.getUsername())
                .orElseThrow(() -> new NotFoundException("User"));

        if (bookingRequestDTO.getNumberOfSeats() <= 0) {
            throw new IllegalArgumentException("Invalid number of seats requested");
        }

        List<BookingPassenger> passengers = bookingRequestDTO.getPassengers().stream()
                .map(passenger -> BookingPassenger.builder()
                        .firstName(passenger.getFirstName())
                        .lastName(passenger.getLastName())
                        .email(passenger.getEmail())
                        .flightClass(passenger.getFlightClass())
                        .priceAtBooking(passenger.getPriceAtBooking())
                        .build())
                .toList();

        // check for available seats
        if (!flight.checkAvailability(passengers)) {
            throw new NotEnoughSeatsException(flight.getId());
        }

        Booking booking = new Booking(flight, user, passengers);
        Booking savedBooking = bookingRepository.save(booking);
        bookingPassengerRepository.saveAll(passengers);

        // Sum seat counts for each flight class
        List<FlightClassSeatCountDTO> flightClassSeatCounts = passengers.stream()
                .map(passenger -> FlightClassSeatCountDTO.builder()
                        .flightClass(passenger.getFlightClass())
                        .seatCount(1)
                        .build())
                .toList();
        // Decrease the available seats
        flight.decreaseAvailableSeats(flightClassSeatCounts);
        flightRepository.save(flight); // cascades to flight classes

        return getBookingResponseDTO(savedBooking);
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking"));

        return getBookingResponseDTO(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking"));
        bookingRepository.delete(booking);
    }

    public List<BookingResponseDTO> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User"));
        return bookingRepository.findAllByUsername(username)
                .stream()
                .map(this::getBookingResponseDTO)
                .toList();
    }

    private BookingResponseDTO getBookingResponseDTO(Booking booking) {
        Flight flight = booking.getFlight();

        List<BookingPassengerResponseDTO> passengerDTOs = new ArrayList<>();
        for (BookingPassenger passenger : booking.getPassengers()) {
            BookingPassengerResponseDTO passengerDTO = BookingPassengerResponseDTO.builder()
                    .firstName(passenger.getFirstName())
                    .lastName(passenger.getLastName())
                    .email(passenger.getEmail())
                    .flightClass(passenger.getFlightClass())
                    .priceAtBooking(passenger.getPriceAtBooking())
                    .build();
            passengerDTOs.add(passengerDTO);
        }

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .airline(flight.getAirline())
                .originAirport(flight.getOriginAirport())
                .destinationAirport(flight.getDestinationAirport())
                .departureTime(flight.getDepartureTime())
                .flightDuration(flight.getDuration())
                .arrivalTime(flight.getArrivalTime())
                .passengers(passengerDTOs)
                .bookingDate(booking.getBookingDate())
                .build();
    }
}
