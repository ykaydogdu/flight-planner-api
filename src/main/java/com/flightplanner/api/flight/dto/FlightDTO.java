package com.flightplanner.api.flight.dto;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private long id;
    private double minPrice;
    private long seatCount;
    private long emptySeats;
    private LocalDateTime departureTime;
    private long duration;
    private LocalDateTime arrivalTime;
    private Airline airline;
    private Airport originAirport;
    private Airport destinationAirport;
}
