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
public class FlightResponseDTO {
    private long id;
    private double price;
    private int emptySeats;
    private LocalDateTime departureTime;
    private String airlineCode;
    private String originAirportCode;
    private String destinationAirportCode;
}
