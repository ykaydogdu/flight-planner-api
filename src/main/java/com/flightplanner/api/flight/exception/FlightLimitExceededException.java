package com.flightplanner.api.flight.exception;

public class FlightLimitExceededException extends RuntimeException {
    public FlightLimitExceededException(int limit, String airlineCode, String srcCode, String destCode) {
        super("Cannot schedule the flight: The maximum of " + limit +
                " daily flights for the route " + srcCode + " to " + destCode +
                " by airline " + airlineCode + " has been reached."
        );
    }
}
