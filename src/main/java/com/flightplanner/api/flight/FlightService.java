package com.flightplanner.api.flight;

import com.flightplanner.api.flight.exception.FlightNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

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

    public Flight createFlight(final Flight flight) {
        return flightRepository.save(flight);
    }

    public Flight updateFlight(final Long id, final Flight flight) {
        return flightRepository.findById(id)
                .map(f -> {
                    f.setAirline(flight.getAirline());
                    f.setSrcAirport(flight.getSrcAirport());
                    f.setDestAirport(flight.getDestAirport());
                    return flightRepository.save(f);
                })
                .orElseGet(() -> flightRepository.save(flight));
    }

    public void deleteFlight(final Long id) {
        flightRepository.deleteById(id);
    }
}
