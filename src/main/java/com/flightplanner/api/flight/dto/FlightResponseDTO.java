package com.flightplanner.api.flight.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlightResponseDTO {
    private long id;
    private LocalDateTime departureTime;
    private String airlineCode;
    private String srcAirportCode;
    private String destAirportCode;
}
