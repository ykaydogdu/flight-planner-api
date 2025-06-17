package com.flightplanner.api.airport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airports")
public class AirportController {

    private final AirportService airportService;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping("/")
    public List<Airport> getAllAirports() {
        return airportService.getAllAirports();
    }

    @PostMapping("/")
    public Airport createAirport(@RequestBody Airport airport) {
        return airportService.createAirport(airport);
    }

    @GetMapping("/{code}")
    public Airport getAirportByCode(@PathVariable String code) {
        return airportService.getAirportByCode(code);
    }

    @DeleteMapping("/{code}")
    public void deleteAirport(@PathVariable String code) {
        airportService.deleteAirport(code);
    }
}
