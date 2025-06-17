package com.flightplanner.api.flight;

import com.flightplanner.api.flight.dto.FlightRequestDTO;
import com.flightplanner.api.flight.dto.FlightResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<FlightResponseDTO> createFlight(@RequestBody FlightRequestDTO requestDTO) {
        FlightResponseDTO responseDTO = flightService.createFlight(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // Single Item Ops
    @GetMapping("/{id}")
    Flight getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @PutMapping("/{id}")
    ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable Long id, @RequestBody FlightRequestDTO dto) {
        FlightResponseDTO responseDTO = flightService.updateFlight(id, dto);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }
}
