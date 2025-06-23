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
        dto.setAirlineCode(flight.getAirline().getCode());
        dto.setSrcAirportCode(flight.getSrcAirport().getCode());
        dto.setDestAirportCode(flight.getDestAirport().getCode());
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
        Airport srcAirport = airportRepository.findById(dto.getSrcAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getSrcAirportCode());
                }}));
        Airport destAirport = airportRepository.findById(dto.getDestAirportCode())
                .orElseThrow(() -> new NotFoundException("Airport", new HashMap<>(){{
                    put("code", dto.getDestAirportCode());
                }}));

        return new Flight(
                dto.getDepartureTime(),
                dto.getPrice(),
                dto.getSeatCount(),
                airline,
                srcAirport,
                destAirport
        );
    }

}
