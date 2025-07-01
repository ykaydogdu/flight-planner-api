package com.flightplanner.api.booking.dto;

import com.flightplanner.api.flight.classes.FlightClassEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPassengerResponseDTO {
    private int passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private FlightClassEnum flightClass;
    private double priceAtBooking;
}
