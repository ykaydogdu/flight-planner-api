package com.flightplanner.api.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPassengerResponseDTO {
    private int passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String flightClass;
    private double priceAtBooking;
}
