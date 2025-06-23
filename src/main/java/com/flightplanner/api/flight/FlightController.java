package com.flightplanner.api.flight;

import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/")
    @Operation(summary = "Get all flights", description = "Retrieves a list of all available flights.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of flights"),
    })
    ResponseEntity<List<FlightResponseDTO>> getAllFlights(
            @RequestParam(required = false) String airlineCode,
            @RequestParam(required = false) String originAirportCode,
            @RequestParam(required = false) String destinationAirportCode,
            @RequestParam(required = false) LocalDate departureDate
    ) {
        List<FlightResponseDTO> flights = flightService.getAllFlights(airlineCode, originAirportCode, destinationAirportCode, departureDate);
        return ResponseEntity.ok(flights);
    }

    @PostMapping("/")
    @Operation(summary = "Create a new flight", description = "Creates a new flight with the provided details. (Only for airline staff)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid flight request data"),
    })
    ResponseEntity<FlightResponseDTO> createFlight(@RequestBody FlightRequestDTO requestDTO) {
        return new ResponseEntity<>(flightService.createFlight(requestDTO), HttpStatus.CREATED);
    }

    // Single Item Ops
    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieves a flight with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found"),
            @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        return new ResponseEntity<>(flightService.getFlightById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flight by ID", description = "Updates the flight with the specified ID using the provided details. (Only for airline staff)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid flight request data"),
            @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable Long id, @RequestBody FlightRequestDTO dto) {
        return new ResponseEntity<>(flightService.updateFlight(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flight by ID", description = "Deletes the flight with the specified ID. (Only for airline staff)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flight not found"),
    })
    ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
