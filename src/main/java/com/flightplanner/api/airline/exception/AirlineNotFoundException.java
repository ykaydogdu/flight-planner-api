package com.flightplanner.api.airline.exception;

public class AirlineNotFoundException extends RuntimeException {
    public AirlineNotFoundException(String code) {
        super("Airline with code " + code + " not found");
    }
}
