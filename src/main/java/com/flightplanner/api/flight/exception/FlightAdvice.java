package com.flightplanner.api.flight.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlightAdvice {

    @ExceptionHandler(FlightLimitExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String flightLimitExceeded(FlightLimitExceededException ex) {
        return ex.getMessage();
    }
}
