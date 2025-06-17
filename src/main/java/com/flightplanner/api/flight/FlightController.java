package com.flightplanner.api.flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/")
    List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @PostMapping("/")
    Flight createFlight(@RequestBody Flight flight) {
        return flightService.createFlight(flight);
    }

    // Single Item Ops
    @GetMapping("/{id}")
    Flight getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @PutMapping("/{id}")
    Flight updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        return flightService.updateFlight(id, flight);
    }

    @DeleteMapping("/{id}")
    void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }
}
