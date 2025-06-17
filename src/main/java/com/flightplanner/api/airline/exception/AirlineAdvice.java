package com.flightplanner.api.airline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AirlineAdvice {

    @ExceptionHandler(AirlineNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String airlineNotFoundException(final AirlineNotFoundException e) {
        return e.getMessage();
    }
}
