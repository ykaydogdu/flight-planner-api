package com.flightplanner.api.flight.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequestDTO {
    private LocalDateTime departureTime;
    private double price;
    private int emptySeats;
    private String airlineCode;
    private String srcAirportCode;
    private String destAirportCode;
}
