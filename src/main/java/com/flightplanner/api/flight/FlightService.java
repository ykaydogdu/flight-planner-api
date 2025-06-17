package com.flightplanner.api.flight;

import com.flightplanner.api.flight.dto.FlightMapper;
import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import com.flightplanner.api.flight.exception.FlightLimitExceededException;
import com.flightplanner.api.flight.exception.FlightNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private static final int MAX_DAILY_FLIGHTS = 3;

    @Autowired
    public FlightService(final FlightRepository flightRepository, final FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(final Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));
    }

    @Transactional
    public FlightResponseDTO createFlight(final FlightRequestDTO requestDTO) {
        Flight flight = flightMapper.toEntity(requestDTO);
        validateFlightLimit(flight);
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toResponseDto(savedFlight);
    }

    @Transactional
    public FlightResponseDTO updateFlight(final Long id, final FlightRequestDTO requestDTO) {
        // If the new flight violates the flight limit, we should not perform the action
        // Fetch current flight
        Flight existingFlight = getFlightById(id);

        boolean isValidationRequired = hasFlightAttrChanged(existingFlight, requestDTO);

        if (isValidationRequired) {
            Flight updatedFlight = flightMapper.toEntity(requestDTO);
            validateFlightLimit(updatedFlight);
        }

        flightMapper.updateEntityFromDto(requestDTO, existingFlight);
        return flightMapper.toResponseDto(existingFlight);
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
        boolean sourceChanged = !existingFlight.getSourceAirport().getCode().equals(updatedFlight.getSrcAirportCode());
        boolean destChanged = !existingFlight.getDestinationAirport().getCode().equals(updatedFlight.getDestAirportCode());

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
                flight.getSourceAirport().getCode(),
                flight.getDestinationAirport().getCode(),
                startOfDay,
                endOfDay
        );

        // Enforce business rule
        if (existingFlightsCount >= MAX_DAILY_FLIGHTS) {
            throw new FlightLimitExceededException(
                    MAX_DAILY_FLIGHTS,
                    flight.getAirline().getCode(),
                    flight.getSourceAirport().getCode(),
                    flight.getDestinationAirport().getCode()
            );
        }
    }

    public void deleteFlight(final Long id) {
        flightRepository.deleteById(id);
    }
}
