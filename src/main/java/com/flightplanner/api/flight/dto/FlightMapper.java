package com.flightplanner.api.flight.dto;

import com.flightplanner.api.NotFoundException;
import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.airport.AirportRepository;
import com.flightplanner.api.flight.Flight;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FlightMapper {

    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    public FlightMapper(AirlineRepository airlineRepository, AirportRepository airportRepository) {
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
    }

    /**
     * Converts a Flight entity to a FlightResponseDTO
     */
    public FlightResponseDTO toResponseDto(Flight flight) {
        if (flight == null) {
            return null;
        }
        FlightResponseDTO dto = new FlightResponseDTO();
        dto.setId(flight.getId());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setAirline(flight.getAirline());
        dto.setOriginAirport(flight.getOriginAirport());
        dto.setDestinationAirport(flight.getDestinationAirport());
        return dto;
    }

    /**
     * Converts a FlightRequestDTO to a Flight entity.
     */
    public Flight toEntity(FlightRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Airline airline = airlineRepository.findById(dto.getAirlineCode())
                .orElseThrow(() -> new NotFoundException("Airline", new HashMap<>(){{
                    put("code", dto.getAirlineCode());
                }}));
        Airport originAirport = airportRepository.findById(dto.getOriginAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getOriginAirportCode());
                }}));
        Airport destinationAirport = airportRepository.findById(dto.getDestinationAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getDestinationAirportCode());
                }}));

        return new Flight(
                dto.getDepartureTime(),
                dto.getPrice(),
                dto.getSeatCount(),
                airline,
                originAirport,
                destinationAirport
        );
    }

}
