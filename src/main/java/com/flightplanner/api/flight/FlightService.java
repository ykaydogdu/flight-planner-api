package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseClassDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.UnauthorizedActionException;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import com.flightplanner.api.flight.classes.FlightClassRepository;
import com.flightplanner.api.flight.classes.FlightClass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightClassRepository flightClassRepository;
    private final FlightMapper flightMapper;
    private static final int MAX_DAILY_FLIGHTS = 3;

    private final UserRepository userRepository;

    @Autowired
    public FlightService(final FlightRepository flightRepository, final FlightClassRepository flightClassRepository, final FlightMapper flightMapper, UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.flightClassRepository = flightClassRepository;
        this.flightMapper = flightMapper;
        this.userRepository = userRepository;
    }

    public List<FlightResponseClassDTO> getAllFlights(String airlineCode,
                                                 String originAirportCode,
                                                 String destinationAirportCode,
                                                 LocalDate departureDate) {
        List<FlightResponseDTO> flights = flightRepository.findFilteredFlights(
                airlineCode,
                originAirportCode,
                destinationAirportCode,
                departureDate != null ? departureDate.atStartOfDay() : null,
                departureDate != null ? departureDate.atTime(LocalTime.MAX) : null
        );
        flights.forEach(flightMapper::fixTimeZone);
        return flights.stream().map(flight -> {
            List<FlightClass> flightClasses = flightClassRepository.findByFlightId(flight.getId());
            return flightMapper.toResponseClassDTO(flight, flightClasses);
        }).toList();
    }

    public List<FlightResponseDTO> getAllFlights() {
        List<FlightResponseDTO> flights = flightRepository.findAllWithEmptySeats();
        flights.forEach(flightMapper::fixTimeZone);
        return flights;
    }

    public FlightResponseDTO getFlightById(final Long id) {
        FlightResponseDTO flight = flightRepository.findByIdWithEmptySeats(id)
                .orElseThrow(() -> new NotFoundException("Flight", new HashMap<>(){{put("id", id);}}));
        flightMapper.fixTimeZone(flight);
        return flight;
    }

    @Transactional
    public FlightResponseDTO createFlight(final FlightRequestDTO requestDTO) {
        Flight flight = flightMapper.toEntity(requestDTO);
        validateFlightLimit(flight);
        validateAirlineStaffAuthorization(flight.getAirlineCode());
        Flight createdFlight = flightRepository.save(flight);
        FlightResponseDTO createdFlightResponse = getFlightById(createdFlight.getId());
        flightMapper.fixTimeZone(createdFlightResponse);
        return createdFlightResponse;
    }

    @Transactional
    public FlightResponseDTO updateFlight(final Long id, final FlightRequestDTO requestDTO) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight", new HashMap<>(){{put("id", id);}}));

        // Validate that the user is authorized to update this flight
        validateAirlineStaffAuthorization(existingFlight.getAirlineCode());

        if (hasFlightAttrChanged(existingFlight, requestDTO)) {
            Flight flightFromRequest = flightMapper.toEntity(requestDTO);
            validateFlightLimit(flightFromRequest);
        }

        // If airline is changed, reject
        if (!existingFlight.getAirlineCode().equals(requestDTO.getAirlineCode())) {
            throw new UnauthorizedActionException("You cannot change airline of a flight.");
        }

        Flight updatedFlight = flightMapper.updateEntity(existingFlight, requestDTO);

        Flight savedFlight = flightRepository.save(updatedFlight);
        FlightResponseDTO updatedFlightResponse = getFlightById(savedFlight.getId());
        flightMapper.fixTimeZone(updatedFlightResponse);
        return updatedFlightResponse;
    }

    @Transactional
    public void deleteFlight(final Long id) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight", new HashMap<>(){{put("id", id);}}));
        validateAirlineStaffAuthorization(existingFlight.getAirlineCode());
        flightRepository.deleteById(id);
    }

    /**
     * Checks if the core attributes (airline, route, date) of a flight have changed.
     * @param existingFlight The existing record in the database
     * @param updatedFlight Updated record
     * @return If the core attributes are changed or not
     */
    private boolean hasFlightAttrChanged(final Flight existingFlight, final FlightRequestDTO updatedFlight) {
        boolean dateChanged = !existingFlight.getDepartureTime().toLocalDate()
                .equals(updatedFlight.getDepartureTime().toLocalDate());
        boolean airlineChanged = !existingFlight.getAirlineCode().equals(updatedFlight.getAirlineCode());
        boolean originChanged = !existingFlight.getOriginAirport().getCode().equals(updatedFlight.getOriginAirportCode());
        boolean destinationChanged = !existingFlight.getDestinationAirport().getCode().equals(updatedFlight.getDestinationAirportCode());

        return dateChanged || airlineChanged || originChanged || destinationChanged;
    }

    /**
     * Validates the daily flight limit is obeyed when trying to add a new flight record
     * Throws FlightLimitExceeded exception if not valid.
     * @param flight The flight record that is trying to be inserted.
     */
    private void validateFlightLimit(Flight flight) {
        LocalDateTime departureDateTime = flight.getDepartureTime();
        LocalDate departureDate = departureDateTime.toLocalDate();

        // Define the start and the end of the day
        LocalDateTime startOfDay = departureDate.atStartOfDay();
        LocalDateTime endOfDay = departureDate.atTime(LocalTime.MAX);

        // Check flight number
        long existingFlightsCount = flightRepository.dailyFlightCount(
                flight.getAirlineCode(),
                flight.getOriginAirport().getCode(),
                flight.getDestinationAirport().getCode(),
                startOfDay,
                endOfDay
        );

        // Enforce business rule
        if (existingFlightsCount >= MAX_DAILY_FLIGHTS) {
            throw new FlightLimitExceededException(
                    MAX_DAILY_FLIGHTS,
                    flight.getAirlineCode(),
                    flight.getOriginAirport().getCode(),
                    flight.getDestinationAirport().getCode()
            );
        }
    }

    protected void validateAirlineStaffAuthorization(String airlineCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findById(username)
                .orElseThrow(() -> new NotFoundException("User", new HashMap<>(){{put("username", username);}}));
        if (!user.getRole().name().equals("ROLE_AIRLINE_STAFF") || !user.getAirline().getCode().equals(airlineCode)) {
            throw new UnauthorizedActionException("You cannot alter the flights of another airline.");
        }
    }
}
