package com.flightplanner.api.airport.exception;

public class AirportNotFoundException extends RuntimeException {
    public AirportNotFoundException(String code) {
        super("Airport " + code + " not found");
    }
}
