package com.flightplanner.api.flight;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.UnauthorizedActionException;
import com.flightplanner.api.user.User;
import com.flightplanner.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private static final int MAX_DAILY_FLIGHTS = 3;

    private final UserRepository userRepository;

    @Autowired
    public FlightService(final FlightRepository flightRepository, final FlightMapper flightMapper, UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.userRepository = userRepository;
    }

    public List<FlightResponseDTO> getAllFlights(String airlineCode,
                                                 String srcAirportCode,
                                                 String destAirportCode,
                                                 LocalDate departureDate) {
        List<Flight> flights = flightRepository.findFilteredFlights(
                airlineCode,
                srcAirportCode,
                destAirportCode,
                departureDate != null ? departureDate.atStartOfDay() : null,
                departureDate != null ? departureDate.atTime(LocalTime.MAX) : null
        );
        return flights.stream()
                .map(flightMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public FlightResponseDTO getFlightById(final Long id) {
        return flightMapper.toResponseDto(
                flightRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Flight", new HashMap<>(){{put("id", id);}})));
    }

    @Transactional
    public FlightResponseDTO createFlight(final FlightRequestDTO requestDTO) {
        Flight flight = flightMapper.toEntity(requestDTO);
        validateFlightLimit(flight);
        validateAirlineStaffAuthorization(flight.getAirlineCode());
        Flight createdFlight = flightRepository.save(flight);
        return flightMapper.toResponseDto(createdFlight);
    }

    @Transactional
    public FlightResponseDTO updateFlight(final Long id, final FlightRequestDTO requestDTO) {
        Flight existingFlight = flightRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Flight", new HashMap<>(){{put("id", id);}}));

        Flight updatedFlight = flightMapper.toEntity(requestDTO);
        // Validate that the user is authorized to update this flight
        validateAirlineStaffAuthorization(existingFlight.getAirlineCode());
        if (hasFlightAttrChanged(existingFlight, requestDTO)) {
            validateFlightLimit(updatedFlight);
        }

        // If airline is changed, reject
        if (!existingFlight.getAirlineCode().equals(requestDTO.getAirlineCode())) {
            throw new UnauthorizedActionException("You cannot change airline of a flight.");
        }

        updatedFlight.setId(id);
        Flight savedFlight = flightRepository.save(updatedFlight);
        return flightMapper.toResponseDto(savedFlight);
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
        boolean airlineChanged = !existingFlight.getAirline().getCode().equals(updatedFlight.getAirlineCode());
        boolean sourceChanged = !existingFlight.getSrcAirport().getCode().equals(updatedFlight.getSrcAirportCode());
        boolean destChanged = !existingFlight.getDestAirport().getCode().equals(updatedFlight.getDestAirportCode());

        return dateChanged || airlineChanged || sourceChanged || destChanged;
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
                flight.getAirline().getCode(),
                flight.getSrcAirport().getCode(),
                flight.getDestAirport().getCode(),
                startOfDay,
                endOfDay
        );

        // Enforce business rule
        if (existingFlightsCount >= MAX_DAILY_FLIGHTS) {
            throw new FlightLimitExceededException(
                    MAX_DAILY_FLIGHTS,
                    flight.getAirline().getCode(),
                    flight.getSrcAirport().getCode(),
                    flight.getDestAirport().getCode()
            );
        }
    }

    protected void validateAirlineStaffAuthorization(String airlineCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        if (!user.getRole().name().equals("ROLE_AIRLINE_STAFF") || !user.getAirline().getCode().equals(airlineCode)) {
            throw new UnauthorizedActionException("You cannot alter the flights of another airline.");
        }
    }
}
