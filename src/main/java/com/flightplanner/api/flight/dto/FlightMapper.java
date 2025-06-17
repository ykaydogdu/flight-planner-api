package com.flightplanner.api.flight.dto;

import com.flightplanner.api.flight.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

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
        dto.setAirlineCode(flight.getAirlineCode());
        dto.setSrcAirportCode(flight.getSrcAirportCode());
        dto.setDestAirportCode(flight.getDestAirportCode());
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
        Flight flight = new Flight();
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setAirlineCode(dto.getAirlineCode());
        flight.setSrcAirportCode(dto.getSrcAirportCode());
        flight.setDestAirportCode(dto.getDestAirportCode());
        return flight;
    }

    /**
     * Updates an existing Flight entity from a FlightRequestDTO.
     */
    public void updateEntityFromDto(FlightRequestDTO dto, Flight flight) {
        if (dto == null) {
            return;
        }
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setAirlineCode(dto.getAirlineCode());
        flight.setSrcAirportCode(dto.getSrcAirportCode());
        flight.setDestAirportCode(dto.getDestAirportCode());
    }
}
