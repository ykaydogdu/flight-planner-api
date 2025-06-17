package com.flightplanner.api.flight.exception;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(Long id) {
        super("Flight " + id + " not found");
    }
}
