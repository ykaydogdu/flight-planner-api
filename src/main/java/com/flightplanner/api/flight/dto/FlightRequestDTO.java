package com.flightplanner.api.flight.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlightRequestDTO {
    private LocalDateTime departureTime;
    private String airlineCode;
    private String srcAirportCode;
    private String destAirportCode;
}
