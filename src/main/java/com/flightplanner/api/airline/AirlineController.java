package com.flightplanner.api.airline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @Autowired
    AirlineController(final AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping("/")
    public List<Airline> getAllAirlines() {
        return airlineService.getAllAirlines();
    }

    @PostMapping("/")
    public Airline addAirline(@RequestBody Airline airline) {
        return airlineService.addAirline(airline);
    }

    @GetMapping("/{code}")
    public Airline getAirlineByCode(@PathVariable String code) {
        return airlineService.getAirlineByCode(code);
    }

    @DeleteMapping("/{code}")
    public void deleteAirline(@PathVariable String code) {
        airlineService.deleteAirline(code);
    }
}
