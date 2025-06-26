package com.flightplanner.api.booking.dto;

import com.flightplanner.api.airline.Airline;
import com.flightplanner.api.airport.Airport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private long id;
    private long flightId;
    private double price;
    private int numberOfSeats;
    private Airline airline;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private int flightDuration;
    private LocalDateTime arrivalTime;
}
