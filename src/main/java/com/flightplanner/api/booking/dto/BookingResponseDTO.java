package com.flightplanner.api.booking.dto;

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
    private String username;
    private double price;
    private int numberOfSeats;
    private String airlineCode;
    private String srcAirportCode;
    private String destAirportCode;
    private LocalDateTime departureTime;
}
