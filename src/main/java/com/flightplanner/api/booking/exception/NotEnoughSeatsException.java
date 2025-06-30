package com.flightplanner.api.booking.exception;

public class NotEnoughSeatsException extends RuntimeException {
    public NotEnoughSeatsException(long flightId) {
        super("Not enough seats available for this flight with id: " + flightId);
//              "Requested seats: " + requestedSeats + " Available seats: " + availableSeats);
    }
}
