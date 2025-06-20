package com.flightplanner.api.booking.exception;

public class NotEnoughSeatsException extends RuntimeException {
    public NotEnoughSeatsException(long flightId, int requestedSeats, int availableSeats) {
        super("Not enough seats available for this flight with id: " + flightId + "\n" +
              "Requested seats: " + requestedSeats + " Available seats: " + availableSeats);
    }
}
