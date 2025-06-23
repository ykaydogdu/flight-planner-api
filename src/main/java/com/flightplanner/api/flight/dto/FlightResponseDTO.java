package com.flightplanner.api.flight.dto;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;

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
    private Airline airline;
    private Airport originAirport;
    private Airport destinationAirport;
}
