package com.flightplanner.api.flight.dto;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airline.AirlineRepository;
import com.flightplanner.api.airline.exception.AirlineNotFoundException;
import com.flightplanner.api.airport.Airport;
import com.flightplanner.api.airport.AirportRepository;
import com.flightplanner.api.airport.exception.AirportNotFoundException;
import com.flightplanner.api.flight.Flight;
import org.springframework.stereotype.Component;

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
        dto.setSrcAirportCode(flight.getSourceAirport().getCode());
        dto.setDestAirportCode(flight.getDestinationAirport().getCode());
        return dto;
    }

    /**
     * Converts a FlightRequestDTO to a Flight entity.
     * Note: This does not set the ID, as that is generated upon creation.
     */
    public Flight toEntity(FlightRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Airline airline = airlineRepository.findById(dto.getAirlineCode())
                .orElseThrow(() -> new AirlineNotFoundException(dto.getAirlineCode()));
        Airport srcAirport = airportRepository.findById(dto.getSrcAirportCode())
                .orElseThrow(() -> new AirportNotFoundException(dto.getSrcAirportCode()));
        Airport destAirport = airportRepository.findById(dto.getDestAirportCode())
                .orElseThrow(() -> new AirportNotFoundException(dto.getDestAirportCode()));

        return new Flight(
                dto.getDepartureTime(),
                airline,
                srcAirport,
                destAirport
        );
    }

    /**
     * Updates an existing Flight entity from a FlightRequestDTO.
     */
    public void updateEntityFromDto(FlightRequestDTO dto, Flight flight) {
        if (dto == null || flight == null) {
            return;
        }

        flight.setDepartureTime(dto.getDepartureTime());

        // Set codes safely (assuming the related objects are non-null)
        if (flight.getAirline() == null) {
            flight.setAirline(new Airline());
        }
        flight.getAirline().setCode(dto.getAirlineCode());

        if (flight.getSourceAirport() == null) {
            flight.setSourceAirport(new Airport());
        }
        flight.getSourceAirport().setCode(dto.getSrcAirportCode());

        if (flight.getDestinationAirport() == null) {
            flight.setDestinationAirport(new Airport());
        }
        flight.getDestinationAirport().setCode(dto.getDestAirportCode());
    }
}
