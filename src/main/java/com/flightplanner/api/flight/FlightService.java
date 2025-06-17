package com.flightplanner.api.flight;

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
    private static final int MAX_DAILY_FLIGHTS = 3;

    @Autowired
    public FlightService(final FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight getFlightById(final Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));
    }

    @Transactional
    public Flight createFlight(final Flight flight) {
        validateFlightLimit(flight);
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlight(final Long id, final Flight updatedFlight) {
        // If the new flight violates the flight limit, we should not perform the action
        // Fetch current flight
        Flight existingFlight = getFlightById(id);

        boolean isValidationRequired = hasFlightAttrChanged(existingFlight, updatedFlight);

        if (isValidationRequired) {
            validateFlightLimit(updatedFlight);
        }

        existingFlight.setAirline(updatedFlight.getAirline());
        existingFlight.setSrcAirport(updatedFlight.getSrcAirport());
        existingFlight.setDestAirport(updatedFlight.getDestAirport());
        existingFlight.setDepartureTime(updatedFlight.getDepartureTime());

        return flightRepository.save(existingFlight);
    }

    /**
     * Checks if the core attributes (airline, route, date) of a flight have changed.
     * @param existingFlight The existing record in the database
     * @param updatedFlight Updated record
     * @return If the core attributes are changed or not
     */
    private boolean hasFlightAttrChanged(final Flight existingFlight, final Flight updatedFlight) {
        boolean dateChanged = !existingFlight.getDepartureTime().toLocalDate()
                .equals(updatedFlight.getDepartureTime().toLocalDate());
        boolean airlineChanged = !existingFlight.getAirline().equals(updatedFlight.getAirline());
        boolean sourceChanged = !existingFlight.getSrcAirport().equals(updatedFlight.getSrcAirport());
        boolean destChanged = !existingFlight.getDestAirport().equals(updatedFlight.getDestAirport());

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
                flight.getAirlineCode(),
                flight.getSrcAirportCode(),
                flight.getDestAirportCode(),
                startOfDay,
                endOfDay
        );

        // Enforce business rule
        if (existingFlightsCount >= MAX_DAILY_FLIGHTS) {
            throw new FlightLimitExceededException(
                    MAX_DAILY_FLIGHTS,
                    flight.getSrcAirportCode(),
                    flight.getDestAirportCode(),
                    flight.getAirlineCode()
            );
        }
    }

    public void deleteFlight(final Long id) {
        flightRepository.deleteById(id);
    }
}
