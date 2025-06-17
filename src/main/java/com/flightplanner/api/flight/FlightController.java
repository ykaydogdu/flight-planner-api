package com.flightplanner.api.flight;

import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<List<FlightResponseDTO>> getAllFlights() {
        return new ResponseEntity<>(flightService.getAllFlights(), HttpStatus.OK);
    }

    @PostMapping("/")
    ResponseEntity<FlightResponseDTO> createFlight(@RequestBody FlightRequestDTO requestDTO) {
        return new ResponseEntity<>(flightService.createFlight(requestDTO), HttpStatus.CREATED);
    }

    // Single Item Ops
    @GetMapping("/{id}")
    ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        return new ResponseEntity<>(flightService.getFlightById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable Long id, @RequestBody FlightRequestDTO dto) {
        return new ResponseEntity<>(flightService.updateFlight(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
