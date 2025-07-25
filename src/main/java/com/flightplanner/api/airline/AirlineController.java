package com.flightplanner.api.airline;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightplanner.api.airline.dto.AirlineWithStaffCountDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @Autowired
    AirlineController(final AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @GetMapping("")
    @Operation(summary = "Get all airlines", description = "Retrieves a list of all airlines.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of airlines"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public ResponseEntity<List<AirlineWithStaffCountDTO>> getAllAirlines() {
        return ResponseEntity.ok(airlineService.getAllAirlines());
    }

    @PostMapping("")
    @Operation(summary = "Add a new airline", description = "Creates a new airline with the provided details. (Only admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Airline created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public ResponseEntity<AirlineWithStaffCountDTO> addAirline(@RequestBody Airline airline) {
        return new ResponseEntity<>(airlineService.addAirline(airline), HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get airline by code", description = "Retrieves an airline by its unique code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved airline"),
            @ApiResponse(responseCode = "404", description = "Airline not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public ResponseEntity<AirlineWithStaffCountDTO> getAirlineByCode(@PathVariable String code) {
        return ResponseEntity.ok(airlineService.getAirlineByCode(code));
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete airline by code", description = "Deletes an airline by its unique code. (Only admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Airline deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Airline not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
    })
    public ResponseEntity<Void> deleteAirline(@PathVariable String code) {
        airlineService.deleteAirline(code);
        return ResponseEntity.noContent().build();
    }
}
