package com.flightplanner.api.booking.dto;

import com.flightplanner.api.flight.classes.FlightClassEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPassengerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private FlightClassEnum flightClass;
    private double priceAtBooking;
}
