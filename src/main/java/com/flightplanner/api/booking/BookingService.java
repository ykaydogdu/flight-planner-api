package com.flightplanner.api.booking;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.booking.exception.NotEnoughSeatsException;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import com.flightplanner.api.booking.dto.BookingRequestDTO;
import com.flightplanner.api.booking.dto.BookingResponseDTO;
import com.flightplanner.api.flight.Flight;
import com.flightplanner.api.flight.FlightRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        Flight flight = flightRepository.findById(bookingRequestDTO.getFlightId())
                .orElseThrow(() -> new NotFoundException("Flight"));
        User user = userRepository.findById(bookingRequestDTO.getUsername())
                .orElseThrow(() -> new NotFoundException("User"));
        if (bookingRequestDTO.getNumberOfSeats() <= 0) {
            throw new IllegalArgumentException("Invalid number of seats requested");
        }

        // check for available seats
        int availableSeats = flight.getSeatCount() - bookingRepository.countBookedSeatsForFlight(bookingRequestDTO.getFlightId());
        if (availableSeats <= bookingRequestDTO.getNumberOfSeats()) {
            throw new NotEnoughSeatsException(flight.getId(), bookingRequestDTO.getNumberOfSeats(), availableSeats);
        }

        Booking booking = new Booking(flight, user, bookingRequestDTO.getNumberOfSeats());
        Booking savedBooking = bookingRepository.save(booking);

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

    private BookingResponseDTO getBookingResponseDTO(Booking booking) {
        Flight flight = booking.getFlight();
        User user = booking.getUser();

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .flightId(flight.getId())
                .username(user.getUsername())
                .price(flight.getPrice())
                .numberOfSeats(booking.getNumberOfSeats())
                .airlineCode(flight.getAirline().getCode())
                .originAirportCode(flight.getOriginAirport().getCode())
                .destinationAirportCode(flight.getDestinationAirport().getCode())
                .departureTime(flight.getDepartureTime())
                .build();
    }
}
