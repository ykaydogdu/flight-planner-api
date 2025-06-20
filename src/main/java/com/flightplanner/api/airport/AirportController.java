package com.flightplanner.api.airport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all airports", description = "Retrieves a list of all airports.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of airports"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public List<Airport> getAllAirports() {
        return airportService.getAllAirports();
    }

    @PostMapping("/")
    @Operation(summary = "Create a new airport", description = "Creates a new airport with the provided details. (Only admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public Airport createAirport(@RequestBody Airport airport) {
        return airportService.createAirport(airport);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get airport by code", description = "Retrieves an airport by its unique code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airport"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public Airport getAirportByCode(@PathVariable String code) {
        return airportService.getAirportByCode(code);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete airport by code", description = "Deletes an airport by its unique code. (Only admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Airport deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Airport not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public void deleteAirport(@PathVariable String code) {
        airportService.deleteAirport(code);
    }
}
